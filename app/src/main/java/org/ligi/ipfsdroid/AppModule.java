package org.ligi.ipfsdroid;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class AppModule {

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Singleton
    @Provides
    IPFSBinaryController provideBinaryController() {
        return new IPFSBinaryController(app);
    }
}
