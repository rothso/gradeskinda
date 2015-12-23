package com.rothanak.gradeskinda.data.auth;

import rx.Observable;

public interface SessionRepository {

    Observable<Void> store(Session session);

}
