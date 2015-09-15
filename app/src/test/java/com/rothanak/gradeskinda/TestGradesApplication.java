package com.rothanak.gradeskinda;

import timber.log.Timber;

@SuppressWarnings("unused")
public class TestGradesApplication extends GradesApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Only log Timber messages to the console
        Timber.uprootAll();
        Timber.plant(new ConsoleTree());
    }

    private static class ConsoleTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    }

}