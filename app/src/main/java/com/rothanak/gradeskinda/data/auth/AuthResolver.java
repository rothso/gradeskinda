package com.rothanak.gradeskinda.data.auth;

import rx.Observable;

public interface AuthResolver {

    Observable<Boolean> isLoggedIn();

}
