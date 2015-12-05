package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.service.Authenticator;

import static org.mockito.Mockito.mock;

public class MockAuthModule extends AuthModule {

    @Override
    Authenticator authenticator(LoginService loginService, AuthRepository authRepository) {
        return mock(Authenticator.class);
    }

}
