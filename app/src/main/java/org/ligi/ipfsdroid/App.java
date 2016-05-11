package org.ligi.ipfsdroid;

import android.app.Application;
import org.ligi.tracedroid.TraceDroid;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        TraceDroid.init(this);
    }
}
