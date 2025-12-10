package io.kayt.refluent.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import io.kayt.refluent.core.database.dao.DeckDao
import io.kayt.refluent.core.database.dictionary.CmuDao
import io.kayt.refluent.core.database.dictionary.Entry
import io.kayt.refluent.core.database.entity.CardEntity
import io.kayt.refluent.core.database.entity.DeckEntity

@Database(
    version = 2,
    entities = [
        DeckEntity::class,
        CardEntity::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
}

@Database(entities = [Entry::class], version = 1, exportSchema = true)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun cmu(): CmuDao
}
