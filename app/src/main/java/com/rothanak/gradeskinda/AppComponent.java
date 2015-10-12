package com.rothanak.gradeskinda;

import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.data.auth.AuthModule;
import com.rothanak.gradeskinda.ui.dashboard.DashboardActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AuthModule.class)
public interface AppComponent {

    void inject(DashboardActivity activity);

    AuthFacade authResolver();

}
