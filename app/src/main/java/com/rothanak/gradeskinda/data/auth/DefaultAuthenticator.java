package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.service.Authenticator;

import rx.Observable;

class DefaultAuthenticator implements Authenticator {

    private final LoginService loginService;
    private final SessionRepository repository;

    public DefaultAuthenticator(LoginService loginService, SessionRepository repository) {
        this.loginService = loginService;
        this.repository = repository;
    }

    @Override public Observable<Boolean> login(Credentials credentials) {
        return loginService.login(credentials)
                .doOnNext(repository::store)
                .map(session -> true)
                .defaultIfEmpty(false);
    }
}
