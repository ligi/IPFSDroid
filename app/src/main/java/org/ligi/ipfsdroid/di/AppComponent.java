package org.ligi.ipfsdroid.di;

import dagger.Component;
import javax.inject.Singleton;
import org.ligi.ipfsdroid.activities.DetailsActivity;
import org.ligi.ipfsdroid.activities.HashTextAndBarcodeActivity;
import org.ligi.ipfsdroid.activities.MainActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(HashTextAndBarcodeActivity addIPFSContent);

    void inject(DetailsActivity detailsActivity);

}
