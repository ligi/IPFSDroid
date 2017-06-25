package org.ligi.ipfsdroid;

import android.app.Application;
import org.ligi.tracedroid.TraceDroid;

public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                                      .appModule(new AppModule(this))
                                      .build();
        TraceDroid.init(this);
    }

    public static AppComponent component() {
        return component;
    }
}
