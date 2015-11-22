package com.rothanak.gradeskinda.data.auth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {

    @Provides @Singleton Authenticator authenticator() {
        return credentials -> {
            throw new UnsupportedOperationException();
        };
    }

}
