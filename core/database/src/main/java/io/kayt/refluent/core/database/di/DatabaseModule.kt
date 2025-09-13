package io.kayt.refluent.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.kayt.refluent.core.database.AppDatabase
import io.kayt.refluent.core.database.DictionaryDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "refluent-db"
        ).build()
    }

    @Provides
    fun provideCmuDicDatabase(@ApplicationContext context: Context): DictionaryDatabase {
        return Room.databaseBuilder(context, DictionaryDatabase::class.java, "cmudict.db")
            .createFromAsset("database/cmu_dict.db")
            .build()
    }
}