package org.ligi.ipfsdroid.di;

import dagger.Component;
import javax.inject.Singleton;
import org.ligi.ipfsdroid.activities.broadcasters.BroadCastersActivity;
import org.ligi.ipfsdroid.activities.HashTextAndBarcodeActivity;
import org.ligi.ipfsdroid.activities.MainActivity;
import org.ligi.ipfsdroid.activities.feed.FeedActivity;
import org.ligi.ipfsdroid.activities.player.PlayerActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(HashTextAndBarcodeActivity addIPFSContent);

    void inject(BroadCastersActivity broadCastersActivity);

    void inject(PlayerActivity playerActivity);

    void inject(FeedActivity feedActivity);
}
