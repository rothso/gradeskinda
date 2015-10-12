package com.rothanak.gradeskinda.data.auth;

import rx.Observable;
import rx.Single;

public interface AuthFacade {

    Observable<Boolean> isLoggedIn();

    Single<Boolean> login(String username, String password);
}
