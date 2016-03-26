package com.rothanak.gradeskinda.data.auth;

import retrofit.BaseUrl;

/**
 * Instrumentation-friendly implementation of AuthModule that allows all requests to be sent to
 * the device's localhost.
 */
public class LocalAuthModule extends AuthModule {

    public LocalAuthModule(BaseUrl remoteEndpoint) {
        this.remoteEndpoint = remoteEndpoint;
    }

}
