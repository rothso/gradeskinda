package com.rothanak.gradeskinda.data.auth;

import static org.mockito.Mockito.mock;

public class MockAuthModule extends AuthModule {

    @Override AuthFacade authResolver() {
        return mock(AuthFacade.class);
    }

}
