package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.service.Authenticator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {

    @Provides @Singleton LoginService loginService() {
        return credentials -> {
            throw new UnsupportedOperationException();
        };
    }

    @Provides @Singleton AuthRepository authRepository() {
        return token -> {
            throw new UnsupportedOperationException();
        };
    }

    @Provides @Singleton
    Authenticator authenticator(LoginService loginService, AuthRepository authRepository) {
        return new DefaultAuthenticator(loginService, authRepository);
    }

}
