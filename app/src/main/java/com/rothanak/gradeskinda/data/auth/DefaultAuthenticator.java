package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.service.Authenticator;

import rx.Observable;

public class DefaultAuthenticator implements Authenticator {

    private final LoginService loginService;
    private final AuthRepository repository;

    public DefaultAuthenticator(LoginService loginService, AuthRepository repository) {
        this.loginService = loginService;
        this.repository = repository;
    }

    @Override public Observable<Boolean> login(Credentials credentials) {
        return loginService.login(credentials)
                .doOnNext(repository::store)
                .map(token -> true)
                .defaultIfEmpty(false);
    }
}
