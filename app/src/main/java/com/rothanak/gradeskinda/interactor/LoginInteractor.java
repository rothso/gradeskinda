package com.rothanak.gradeskinda.interactor;

import rx.Single;

/**
 * Allows the presenter to submit a login request without knowing anything
 * about persistence or the login process.
 */
public interface LoginInteractor {

    /**
     * Queue a login request for a user. If the request succeeds, the login
     * will be internally persisted. If the request reaches the server but
     * fails, it will be assumed that the credentials are wrong. In the event
     * the request is short-circuited due to a network error or similar, an
     * Observable error should have propagated for the calling code to handle.
     *
     * @param user the username to submit
     * @param pass the password to submit
     * @return true if success, false if bad credentials, throwable otherwise
     */
    Single<Boolean> login(String user, String pass);

}
