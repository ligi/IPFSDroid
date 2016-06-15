package org.ligi.ipfsdroid;

import dagger.Module;
import dagger.Provides;
import io.ipfs.kotlin.IPFS;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;

@Module
public class AppModule {

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Singleton
    @Provides
    OkHttpClient proviceOkhttp() {
        return new OkHttpClient.Builder().build();
    }

    @Singleton
    @Provides
    IPFS provideIPFS() {
        return new IPFS();
    }

    @Singleton
    @Provides
    State provideState() {
        return new State();
    }
}
