package org.ligi.ipfsdroid.di

import dagger.Module
import dagger.Provides
import io.ipfs.kotlin.IPFS
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    internal fun provideOkhttp() = OkHttpClient.Builder().let { builder ->
        builder.connectTimeout(1000, TimeUnit.SECONDS)
        builder.readTimeout(1000, TimeUnit.SECONDS)
        builder.build()
    }

    @Singleton
    @Provides
    internal fun provideIPFS(providedOkHttp: OkHttpClient)
            = IPFS(okHttpClient = providedOkHttp)

}
