package com.rothanak.gradeskinda.interactor;

import com.rothanak.gradeskinda.data.auth.Authenticator;
import com.rothanak.gradeskinda.interactor.scheduler.AddSchedulesTransformer;

import static org.mockito.Mockito.mock;

public class MockInteractorModule extends InteractorModule {

    @Override
    LoginInteractor loginInteractor(Authenticator authenticator, AddSchedulesTransformer scheduler) {
        return mock(LoginInteractor.class);
    }

}
