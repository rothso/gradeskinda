package com.rothanak.gradeskinda.domain.service;

import com.rothanak.gradeskinda.domain.model.Credentials;

import rx.Observable;

public interface Authenticator {

    Observable<Boolean> login(Credentials credentials);

}
