package com.rothanak.gradeskinda;

import android.app.Application;

import timber.log.Timber;

public class GradesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
