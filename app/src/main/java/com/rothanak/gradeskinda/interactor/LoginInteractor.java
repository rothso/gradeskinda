package com.rothanak.gradeskinda.interactor;

import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.data.entity.Credentials;
import com.rothanak.gradeskinda.interactor.scheduler.AddSchedulesTransformer;

import javax.inject.Inject;

import rx.Observable;

/**
 * Allows the presenter to submit a login request without knowing anything
 * about persistence or the login process.
 */
public class LoginInteractor {

    private final AuthFacade authenticator;
    private final AddSchedulesTransformer scheduler;

    @Inject
    public LoginInteractor(AuthFacade authenticator, AddSchedulesTransformer scheduler) {
        this.authenticator = authenticator;
        this.scheduler = scheduler;
    }

    /**
     * Queue a login request for a user. If the request succeeds, the login
     * will be internally persisted. If the request reaches the server but
     * fails, it will be assumed that the credentials are wrong. In the event
     * the request is short-circuited due to a network error or similar, an
     * Observable error should have propagated for the calling code to handle.
     *
     * @param credentials the username and password
     * @return true if success, false if bad credentials, throwable otherwise
     */
    @SuppressWarnings("unchecked")
    public Observable<Boolean> login(Credentials credentials) {
        return authenticator.login(credentials).compose(scheduler);
    }

}
