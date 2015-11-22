package com.rothanak.gradeskinda;

import com.rothanak.gradeskinda.data.auth.AuthModule;
import com.rothanak.gradeskinda.data.auth.Authenticator;
import com.rothanak.gradeskinda.interactor.InteractorModule;
import com.rothanak.gradeskinda.interactor.LoginInteractor;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                InteractorModule.class,
                AuthModule.class
        }
)
public interface AppComponent {

    Authenticator authFacade();

    LoginInteractor loginInteractor();

}
