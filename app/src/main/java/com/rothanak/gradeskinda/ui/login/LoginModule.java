package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;
import com.rothanak.gradeskinda.ui.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    @Provides @PerActivity LoginPresenter presenter(LoginInteractor interactor) {
        return new LoginPresenter(interactor);
    }

}