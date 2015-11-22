package com.rothanak.gradeskinda.data.auth;

import static org.mockito.Mockito.mock;

public class MockAuthModule extends AuthModule {

    @Override Authenticator authenticator() {
        return mock(Authenticator.class);
    }

}
