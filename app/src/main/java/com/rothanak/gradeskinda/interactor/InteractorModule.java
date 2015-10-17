package com.rothanak.gradeskinda.interactor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InteractorModule {

    @Provides @Singleton LoginInteractor loginInteractor() {
        return (user, pass) -> { throw new UnsupportedOperationException(); };
    }

}
