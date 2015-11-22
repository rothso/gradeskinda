package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.data.entity.Credentials;

import rx.Observable;

public interface Authenticator {

    Observable<Boolean> login(Credentials credentials);

}
