package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.data.entity.Credentials;

import rx.Observable;

public interface AuthFacade {

    Observable<Boolean> login(Credentials credentials);

}
