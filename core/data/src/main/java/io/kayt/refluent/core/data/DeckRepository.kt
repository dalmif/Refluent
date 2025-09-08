package io.kayt.refluent.core.data

import io.kayt.core.model.Deck
import io.kayt.refluent.core.database.AppDatabase
import io.kayt.refluent.core.database.entity.DeckEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val deckDao by lazy { database.deckDao() }

    suspend fun addNewDeck(name: String, colors: Pair<Int, Int>) {
        withContext(Dispatchers.IO) {
            deckDao.newDeck(
                DeckEntity(
                    name = name,
                    color1 = colors.first,
                    color2 = colors.second
                )
            )
        }
    }

    fun getAllDeck(): Flow<List<Deck>> = deckDao
        .getDeckWithCardCounts()
        .map { list ->
            list.map {
                Deck(
                    name = it.name,
                    colors = Pair(it.color1, it.color2),
                    totalCards = it.totalCards,
                    dueCards = it.dueCards
                )
            }
        }
        .flowOn(Dispatchers.IO)
}