package org.ligi.ipfsdroid;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(IPFSBrowseActivity ipfsBrowseActivity);

    void inject(MainActivity mainActivity);
}
