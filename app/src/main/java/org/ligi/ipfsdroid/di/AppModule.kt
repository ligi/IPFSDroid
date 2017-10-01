package org.ligi.ipfsdroid.di

import dagger.Module
import dagger.Provides
import io.ipfs.kotlin.IPFS
import okhttp3.OkHttpClient
import org.ligi.ipfsdroid.App
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Singleton
    @Provides
    internal fun proviceOkhttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(1000, TimeUnit.SECONDS)
        builder.readTimeout(1000, TimeUnit.SECONDS)
        return builder.build()
    }

    @Singleton
    @Provides
    internal fun provideIPFS(providedOkHttp: OkHttpClient)
            = IPFS(okHttpClient = providedOkHttp)

}
