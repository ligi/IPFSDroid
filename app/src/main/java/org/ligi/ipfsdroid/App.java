package org.ligi.ipfsdroid;

import android.app.Application;
import com.chibatching.kotpref.Kotpref;
import org.ligi.tracedroid.TraceDroid;

public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        Kotpref.INSTANCE.init(this);
        component = DaggerAppComponent.builder()
                                      .appModule(new AppModule(this))
                                      .build();
        TraceDroid.init(this);
    }

    public static AppComponent component() {
        return component;
    }
}
