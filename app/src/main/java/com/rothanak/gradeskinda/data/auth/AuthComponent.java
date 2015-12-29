package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.data.net.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

/* TODO component inheritance or subcomponent of NetworkComponent */
@Singleton
@Component(modules = {
        AuthModule.class,
        NetworkModule.class
})
public interface AuthComponent {

    LoginService loginService();

}
