package com.rothanak.gradeskinda.interactor;

import static org.mockito.Mockito.mock;

public class MockInteractorModule extends InteractorModule {

    @Override LoginInteractor loginInteractor() {
        return mock(LoginInteractor.class);
    }

}
