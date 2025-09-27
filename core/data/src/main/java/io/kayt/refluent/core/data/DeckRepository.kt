package io.kayt.refluent.core.data

import io.kayt.core.model.Card
import io.kayt.core.model.Deck
import io.kayt.refluent.core.database.AppDatabase
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.DeckEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Singleton
class DeckRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {
    private val deckDataAccess by lazy { appDatabase.deckDao() }

    companion object {
        // knobs you can tweak
        private const val MIN_EASE = 1.30f
        private const val MAX_EASE = 2.50f
        private const val LAPSE_PENALTY = 0.25f          // stronger penalty on fails
        private const val INTERVAL_MODIFIER = 0.85f      // 15% sooner globally
        private const val EASY_BONUS = 1.10f             // small push for perfect recall

        // "learning" steps for very early reviews (sub-day). Interval field will be 0 for these.
        private const val STEP1_MINUTES = 10L
        private const val STEP2_MINUTES = 60L
    }

    suspend fun addNewDeck(name: String, colors: Pair<Int, Int>) {
        withContext(Dispatchers.IO) {
            deckDataAccess.newDeck(
                DeckEntity(
                    name = name,
                    color1 = colors.first,
                    color2 = colors.second
                )
            )
        }
    }

    suspend fun addNewCard(
        deckId: Long,
        frontSide: String,
        backSide: String,
        comment: String,
        phonetic: String
    ) {
        withContext(Dispatchers.IO) {
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

    class AbortFlowException : Exception()
    fun getDeckById(deckId: Long): Flow<Deck> = channelFlow {
        while (currentCoroutineContext().isActive) {
            try {
                deckDataAccess.getDeckById(deckId).collectLatest {
                    send(it)
                    it.nearestNextReview?.plus(1.seconds.inWholeMilliseconds)?.let {
                        val nextUpdate = it - System.currentTimeMillis()
                        if (nextUpdate > 0) {
                            delay(nextUpdate)
                            throw AbortFlowException()
                        }
                    }
                }
            }
            catch (ex : AbortFlowException){ }
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

    fun getAllDeck(): Flow<List<Deck>> = deckDataAccess
        .getDeckWithCardCounts()
        .map { list ->
            list.map {
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

    fun getCardsForDeck(deckId: Long): Flow<List<Card>> = deckDataAccess
        .getCardsForDeck(deckId)
        .map { cards ->
            cards.map {
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
            }
        }

    /**
     * Returns only cards that are due for review for a given deck.
     * A card is due when it is not archived and its nextReview <= now.
     */
    fun getDueCardsForDeck(deckId: Long): Flow<List<Card>> =
        getCardsForDeck(deckId).map { cards ->
            cards.filter { !it.isArchived && it.dueDate <= System.currentTimeMillis() }
        }

    /**
     * Save the spaced-repetition review result for a card using SM-2 algorithm.
     * @param card The current card snapshot (domain model)
     * @param quality The 0..5 quality score (5 = perfect). Values < 3 reset repetition.
     */
    suspend fun saveReviewResult(card: Card, quality: Int) {
        require(quality in 0..5) { "quality must be in 0..5" }

        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()

            // compute updated ease first (SM-2ish but slightly conservative upwards)
            val rawEaseUpdate = if (quality < 3) {
                card.easeFactor - LAPSE_PENALTY
            } else {
                // classic SM-2: EF':= EF + (0.1 − (5−q) * (0.08 + (5−q)*0.02))
                card.easeFactor + (0.10f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
            }
            val newEase = rawEaseUpdate.coerceIn(MIN_EASE, MAX_EASE)

            val millisPerDay = 24L * 60 * 60 * 1000
            val minute = 60L * 1000

            var nextRepetition: Int
            var nextIntervalDays: Int
            var nextReviewAt: Long

            if (quality < 3) {
                // Lapse: reset repetition, do a quick follow-up in 10 minutes
                nextRepetition = 0
                nextIntervalDays = 0
                nextReviewAt = now + STEP1_MINUTES * minute
            } else {
                // Passed
                when (card.repetition) {
                    0 -> {
                        // First successful pass → 10 min check
                        nextRepetition = 1
                        nextIntervalDays = 0
                        nextReviewAt = now + STEP1_MINUTES * minute
                    }

                    1 -> {
                        // Second pass → 1 hour check
                        nextRepetition = 2
                        nextIntervalDays = 0
                        nextReviewAt = now + STEP2_MINUTES * minute
                    }

                    2 -> {
                        // Third pass → 1 day
                        nextRepetition = 3
                        nextIntervalDays = 1
                        nextReviewAt = now + nextIntervalDays * millisPerDay
                    }

                    else -> {
                        // Review phase with global modifier and optional easy bonus
                        nextRepetition = card.repetition + 1
                        val base = (card.interval * newEase * INTERVAL_MODIFIER)
                            .toDouble().roundToInt().coerceAtLeast(1)

                        val withEasyBonus = if (quality == 5) {
                            (base * EASY_BONUS).roundToInt().coerceAtLeast(1)
                        } else base

                        nextIntervalDays = withEasyBonus
                        nextReviewAt = now + nextIntervalDays * millisPerDay
                    }
                }
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
                nextReview = nextReviewAt,     // supports sub-day learning steps
                interval = nextIntervalDays,   // will be 0 for sub-day steps
                repetition = nextRepetition,
                easeFactor = newEase,
                lastReviewed = now,
                createdDateTime = card.createdDateTime
            )

            deckDataAccess.updateCard(updatedEntity)
        }
    }

}