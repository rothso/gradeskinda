package com.rothanak.gradeskinda.data.auth;

import rx.Observable;

public interface AuthFacade {

    Observable<Boolean> login(String username, String password);

}
