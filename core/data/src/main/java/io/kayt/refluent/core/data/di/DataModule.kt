package io.kayt.refluent.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideSocket(): Socket {
        return IO.socket(
            // TODO Update the url
//            "https://live.refluent.app/",
            "http://192.168.0.118:5100",
            IO.Options().apply {
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE
            timeout = 60_000
        })
    }
}