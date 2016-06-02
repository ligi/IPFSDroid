package org.ligi.ipfsdroid;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(AddIPFSContent addIPFSContent);

    void inject(DetailsActivity detailsActivity);
}
