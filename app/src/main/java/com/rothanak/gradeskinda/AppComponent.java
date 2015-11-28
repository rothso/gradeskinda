package com.rothanak.gradeskinda;

import com.rothanak.gradeskinda.data.auth.AuthModule;
import com.rothanak.gradeskinda.domain.interactor.InteractorModule;
import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;

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

    LoginInteractor loginInteractor();

}
