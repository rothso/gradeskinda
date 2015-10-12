package com.rothanak.gradeskinda.data.auth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.Single;

@Module
public class AuthModule {

    @Provides @Singleton AuthFacade authResolver() {
        return new AuthFacade() {
            @Override public Observable<Boolean> isLoggedIn() {
                throw new UnsupportedOperationException();
            }

            @Override public Single<Boolean> login(String username, String password) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
