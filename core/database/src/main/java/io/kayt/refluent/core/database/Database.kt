package io.kayt.refluent.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.kayt.refluent.core.database.dao.DeckDao
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.DeckEntity

@Database(entities = [DeckEntity::class, CardEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
}