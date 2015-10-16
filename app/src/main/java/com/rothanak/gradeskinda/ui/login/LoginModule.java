package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.ui.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    @Provides @PerActivity LoginPresenter presenter(AuthFacade auth) {
        return new LoginPresenter(auth);
    }

}