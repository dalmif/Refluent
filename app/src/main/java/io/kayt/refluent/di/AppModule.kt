package io.kayt.refluent.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.kayt.core.model.Config
import io.kayt.refluent.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideConfigRepository(): Config {
        return Config(
            versionCode = BuildConfig.VERSION_CODE,
            useLocalIp = BuildConfig.DEBUG
        )
    }
}