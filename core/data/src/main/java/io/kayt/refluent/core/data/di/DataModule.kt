package io.kayt.refluent.core.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.kayt.core.domain.repository.DeckRepository
import io.kayt.core.domain.repository.LiveEditRepository
import io.kayt.core.domain.repository.VocabularyRepository
import io.kayt.core.model.Config
import io.kayt.refluent.core.data.DeckRepositoryImpl
import io.kayt.refluent.core.data.LiveEditRepositoryImpl
import io.kayt.refluent.core.data.VocabularyRepositoryImpl
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    companion object {
        @Provides
        @Singleton
        fun provideSocket(config: Config): Socket {
            return IO.socket(
                if (config.useLocalIp)
                    "http://192.168.0.118:5100/"
                else
                    "https://live.refluent.app/",
                IO.Options().apply {
                    reconnection = true
                    reconnectionAttempts = Int.MAX_VALUE
                    timeout = 60_000
                })
        }
    }

    @Binds
    @Singleton
    abstract fun provideLiveEditRepository(impl: LiveEditRepositoryImpl): LiveEditRepository

    @Binds
    @Singleton
    abstract fun provideDeckRepository(impl: DeckRepositoryImpl): DeckRepository

    @Binds
    @Singleton
    abstract fun provideVocabularyRepository(impl: VocabularyRepositoryImpl): VocabularyRepository

}