package com.rothanak.gradeskinda.data.auth;

import rx.Observable;

public interface AuthRepository {

    Observable<Void> store(AuthToken token);

}
