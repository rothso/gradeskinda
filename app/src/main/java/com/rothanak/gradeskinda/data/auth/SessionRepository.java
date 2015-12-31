package com.rothanak.gradeskinda.data.auth;

import rx.Observable;

interface SessionRepository {

    Observable<Void> store(Session session);

}
