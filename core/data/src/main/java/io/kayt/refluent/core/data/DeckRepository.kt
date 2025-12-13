package io.kayt.refluent.core.data

import io.kayt.core.domain.repository.DeckRepository
import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.core.model.SearchResultCard
import io.kayt.refluent.core.database.AppDatabase
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.DeckEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

class DeckRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : DeckRepository {
    private val deckDataAccess by lazy { appDatabase.deckDao() }

    companion object {
        // knobs you can tweak
        private const val MIN_EASE = 1.30f

        // I just changed the ease factor to see the cards more repeatedly, it's a bit aggressive
        // the default one is 2.5f
        private const val MAX_EASE = 1.7f
        private const val LAPSE_PENALTY = 0.25f // stronger penalty on fails
        private const val INTERVAL_MODIFIER = 0.85f // 15% sooner globally
        private const val EASY_BONUS = 1.10f // small push for perfect recall

        // "learning" steps for very early reviews (sub-day). Interval field will be 0 for these.
        private const val STEP1_MINUTES = 10L
        private const val STEP2_MINUTES = 60L
    }

    override suspend fun addNewDeck(name: String, colors: Pair<Int, Int>): Long {
        return withContext(Dispatchers.IO) {
            deckDataAccess.newDeck(
                DeckEntity(
                    name = name,
                    color1 = colors.first,
                    color2 = colors.second
                )
            )
        }
    }

    override suspend fun updateDeck(id: Long, name: String, colors: Pair<Int, Int>) {
        withContext(Dispatchers.IO) {
            // First get the existing card to preserve SRS fields
            val existingCard = deckDataAccess.readDeckById(id)
            deckDataAccess.updateDeck(
                existingCard.copy(
                    name = name,
                    color1 = colors.first,
                    color2 = colors.second
                )
            )
        }
    }

    override suspend fun removeDeck(id: Long) {
        withContext(Dispatchers.IO) {
            // Delete all cards associated with this deck first
            deckDataAccess.deleteCardsByDeckId(id)
            // Then delete the deck
            deckDataAccess.deleteDeckById(id)
        }
    }

    override suspend fun searchCardGlobally(query: String): List<SearchResultCard> {
        return withContext(Dispatchers.IO) {
            deckDataAccess.searchCardsWithDeck(query).map {
                SearchResultCard(
                    card = it.card.let {
                        Card(
                            id = it.uid,
                            front = it.frontSide,
                            back = it.backSide,
                            deckId = it.deckOwnerId,
                            interval = it.interval,
                            repetition = it.repetition,
                            easeFactor = it.easeFactor,
                            dueDate = it.nextReview,
                            lastReviewed = it.lastReviewed ?: 0L,
                            createdDateTime = it.createdDateTime,
                            isArchived = it.isArchived,
                            phonetic = it.phonetic,
                            comment = it.comment,
                            tags = it.tags
                        )
                    },
                    deckColor = it.deck.color1 to it.deck.color2,
                    deckName = it.deck.name
                )
            }
        }
    }

    override suspend fun addNewCard(
        deckId: Long,
        frontSide: String,
        backSide: String,
        comment: String,
        phonetic: String
    ): Long {
        return withContext(Dispatchers.IO) {
            deckDataAccess.insertCard(
                CardEntity(
                    deckOwnerId = deckId,
                    frontSide = frontSide,
                    backSide = backSide,
                    comment = comment,
                    phonetic = phonetic,
                    isArchived = false,
                    tags = ""
                )
            )
        }
    }

    override suspend fun updateCard(
        cardId: Long,
        frontSide: String,
        backSide: String,
        comment: String,
        phonetic: String
    ) {
        withContext(Dispatchers.IO) {
            // First get the existing card to preserve SRS fields
            val existingCard = deckDataAccess.getCardById(cardId)

            if (existingCard != null) {
                deckDataAccess.updateCard(
                    existingCard.copy(
                        frontSide = frontSide,
                        backSide = backSide,
                        comment = comment,
                        phonetic = phonetic
                    )
                )
            }
        }
    }

    override suspend fun deleteCard(cardId: Long) {
        withContext(Dispatchers.IO) {
            deckDataAccess.deleteCardById(cardId)
        }
    }

    class AbortFlowException : Exception()

    @Suppress("EmptyCatchBlock", "SwallowedException")
    override fun getDeckById(deckId: Long): Flow<Deck> = channelFlow {
        while (currentCoroutineContext().isActive) {
            try {
                deckDataAccess.getDeckById(deckId).collectLatest {
                    send(it)
                    it.nearestNextReview?.plus(1.seconds.inWholeMilliseconds)?.let {
                        val nextUpdate = it - System.currentTimeMillis()
                        if (nextUpdate > 0) {
                            delay(nextUpdate)
                        }
                        throw AbortFlowException()
                    }
                }
            } catch (ex: AbortFlowException) {
            }
        }
    }
        .map {
            it.let {
                Deck(
                    id = it.uid,
                    name = it.name,
                    colors = Pair(it.color1, it.color2),
                    totalCards = it.totalCards,
                    dueCards = it.dueCards
                )
            }
        }
        .flowOn(Dispatchers.IO)

    @Suppress("EmptyCatchBlock", "SwallowedException")
    override fun getAllDeck(): Flow<List<Deck>> = channelFlow {
        send(Unit)
        while (currentCoroutineContext().isActive) {
            try {
                deckDataAccess.getTheNearestDueCard()
                    .filterNotNull()
                    .map { it.nextReview }
                    .distinctUntilChanged()
                    .collectLatest {
                        val nextUpdate = (it + 1_000) - System.currentTimeMillis()
                        if (nextUpdate > 0) {
                            delay(nextUpdate)
                        }
                        send(Unit)
                        // When we reach to the nearest one, we need to re-execute the query to get the
                        // next nearest one, as the current one is past now.
                        throw AbortFlowException()
                    }
            } catch (ex: AbortFlowException) {
            }
        }
    }.flatMapLatest {
        deckDataAccess.getDeckWithCardCounts()
    }.map { list ->
        list.map {
            Deck(
                id = it.uid,
                name = it.name,
                colors = Pair(it.color1, it.color2),
                totalCards = it.totalCards,
                dueCards = it.dueCards
            )
        }
    }.flowOn(Dispatchers.IO)

    override fun getAllCards(): Flow<List<Card>> {
        return deckDataAccess.getAllCards().map { it.map { it.toCard() } }
    }

    override fun getCardsForDeck(deckId: Long): Flow<List<Card>> = deckDataAccess
        .getCardsForDeck(deckId)
        .map { cards ->
            cards.map { it.toCard() }
        }

    override suspend fun getCardById(cardId: Long): Card? {
        return withContext(Dispatchers.IO) {
            deckDataAccess.getCardById(cardId)?.let { cardEntity ->
                Card(
                    id = cardEntity.uid,
                    front = cardEntity.frontSide,
                    back = cardEntity.backSide,
                    deckId = cardEntity.deckOwnerId,
                    interval = cardEntity.interval,
                    repetition = cardEntity.repetition,
                    easeFactor = cardEntity.easeFactor,
                    dueDate = cardEntity.nextReview,
                    lastReviewed = cardEntity.lastReviewed ?: 0L,
                    createdDateTime = cardEntity.createdDateTime,
                    isArchived = cardEntity.isArchived,
                    phonetic = cardEntity.phonetic,
                    comment = cardEntity.comment,
                    tags = cardEntity.tags
                )
            }
        }
    }

    /**
     * Returns only cards that are due for review for a given deck.
     * A card is due when it is not archived and its nextReview <= now.
     */
    override fun getDueCardsForDeck(deckId: Long): Flow<List<Card>> =
        getCardsForDeck(deckId).map { cards ->
            cards.filter { !it.isArchived && it.dueDate <= System.currentTimeMillis() }
        }

    /**
     * Save the spaced-repetition review result for a card using SM-2 algorithm.
     * @param card The current card snapshot (domain model)
     * @param remembered Whether the user remembered the card (true) or not (false)
     */
    override suspend fun saveReviewResult(card: Card, remembered: Boolean) {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val quality = if (remembered) 4 else 0 // 5 = perfect recall, 0 = complete failure

            val currentInterval = card.interval
            val currentRepetition = card.repetition
            val currentEase = card.easeFactor

            val (nextIntervalDays, nextRepetition, newEase) = calculateNextReview(
                quality = quality,
                currentInterval = currentInterval,
                currentRepetition = currentRepetition,
                currentEase = currentEase
            )

            // Calculate next review time
            val nextReviewAt = if (nextIntervalDays == 0) {
                // Learning phase - use minutes
                if (nextRepetition == 0) {
                    now + (STEP1_MINUTES * 60 * 1000) // 10 minutes
                } else {
                    now + (STEP2_MINUTES * 60 * 1000) // 60 minutes
                }
            } else {
                // Mature phase - use days
                now + (nextIntervalDays * 24 * 60 * 60 * 1000)
            }

            val updatedEntity = CardEntity(
                uid = card.id,
                deckOwnerId = card.deckId,
                frontSide = card.front,
                backSide = card.back,
                phonetic = card.phonetic,
                comment = card.comment,
                isArchived = card.isArchived,
                tags = card.tags,
                nextReview = nextReviewAt,
                interval = nextIntervalDays,
                repetition = nextRepetition,
                easeFactor = newEase,
                lastReviewed = now,
                createdDateTime = card.createdDateTime
            )

            deckDataAccess.updateCard(updatedEntity)
        }
    }

    /**
     * Calculate the next review parameters using SM-2 algorithm
     * @param quality Quality score (0-5, where 5 is perfect)
     * @param currentInterval Current interval in days
     * @param currentRepetition Current repetition count
     * @param currentEase Current ease factor
     * @return Triple of (nextIntervalDays, nextRepetition, newEase)
     */
    private fun calculateNextReview(
        quality: Int,
        currentInterval: Int,
        currentRepetition: Int,
        currentEase: Float
    ): Triple<Int, Int, Float> {
        var newEase = currentEase
        var nextRepetition = currentRepetition
        var nextIntervalDays: Int

        // Update ease factor
        newEase = newEase + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        newEase = newEase.coerceIn(MIN_EASE, MAX_EASE)

        // Apply lapse penalty if quality < 3
        if (quality < 3) {
            nextRepetition = 0
            newEase -= LAPSE_PENALTY
            newEase = newEase.coerceIn(MIN_EASE, MAX_EASE)
        } else {
            nextRepetition++
        }

        // Calculate next interval
        when (nextRepetition) {
            0 -> nextIntervalDays = 0 // Learning phase
            1 -> nextIntervalDays = 1
            2 -> nextIntervalDays = 6
            else -> {
                nextIntervalDays = (currentInterval * newEase * INTERVAL_MODIFIER).roundToInt()
                // Apply easy bonus for perfect recall
                if (quality == 5) {
                    nextIntervalDays = (nextIntervalDays * EASY_BONUS).roundToInt()
                }
            }
        }

        return Triple(nextIntervalDays, nextRepetition, newEase)
    }

    private fun CardEntity.toCard() = Card(
        id = this.uid,
        front = this.frontSide,
        back = this.backSide,
        deckId = this.deckOwnerId,
        interval = this.interval,
        repetition = this.repetition,
        easeFactor = this.easeFactor,
        dueDate = this.nextReview,
        lastReviewed = this.lastReviewed ?: 0L,
        createdDateTime = this.createdDateTime,
        isArchived = this.isArchived,
        phonetic = this.phonetic,
        comment = this.comment,
        tags = this.tags
    )
}
