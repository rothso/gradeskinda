package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.AppComponent;
import com.rothanak.gradeskinda.ui.PerActivity;

import dagger.Component;

@PerActivity
@Component(
        dependencies = AppComponent.class,
        modules = LoginModule.class
)
public interface LoginComponent {

    void inject(LoginActivity activity);

    LoginPresenter presenter();

}
