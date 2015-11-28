package com.rothanak.gradeskinda.domain.interactor;

import com.rothanak.gradeskinda.domain.interactor.scheduler.AddSchedulesTransformer;
import com.rothanak.gradeskinda.domain.service.Authenticator;

import static org.mockito.Mockito.mock;

public class MockInteractorModule extends InteractorModule {

    @Override
    LoginInteractor loginInteractor(Authenticator authenticator, AddSchedulesTransformer scheduler) {
        return mock(LoginInteractor.class);
    }

}
