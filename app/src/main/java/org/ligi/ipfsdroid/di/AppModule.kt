package org.ligi.ipfsdroid.di

import android.content.Context
import android.support.annotation.NonNull
import dagger.Module
import dagger.Provides
import io.ipfs.kotlin.IPFS
import okhttp3.OkHttpClient
import org.ligi.ipfsdroid.repository.Repository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(val context: Context) {

    @Singleton
    @Provides
    @NonNull
    fun provideContext() : Context {
        return context
    }

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

    @Singleton
    @Provides
    internal fun provideRepository(ipfs: IPFS) = Repository(ipfs = ipfs)

}
