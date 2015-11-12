package com.rothanak.gradeskinda.data.auth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class AuthModule {

    @Provides @Singleton AuthFacade authResolver() {
        return new AuthFacade() {
            @Override public Observable<Boolean> isLoggedIn() {
                throw new UnsupportedOperationException();
            }

            @Override public Observable<Boolean> login(String username, String password) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
