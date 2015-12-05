package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;

import rx.Observable;

public interface LoginService {

    Observable<AuthToken> login(Credentials credentials);

}
