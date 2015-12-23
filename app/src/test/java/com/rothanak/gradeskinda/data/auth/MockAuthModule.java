package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.service.Authenticator;

import static org.mockito.Mockito.mock;

public class MockAuthModule extends AuthModule {

    @Override
    Authenticator authenticator(LoginService loginService, SessionRepository sessionRepository) {
        return mock(Authenticator.class);
    }

}
