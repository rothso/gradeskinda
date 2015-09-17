package com.rothanak.gradeskinda;

import android.app.Application;

import timber.log.Timber;

public class GradesApplication extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public AppComponent component() {
        if (component == null) throw new IllegalStateException();
        return component;
    }

    public void component(AppComponent appComponent) {
        component = appComponent;
    }
}
