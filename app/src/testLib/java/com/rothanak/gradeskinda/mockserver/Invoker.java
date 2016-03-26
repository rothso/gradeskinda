package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.WireMockServer;

public class Invoker<T> {

    private final T request;
    private WireMockServer serviceProvider;
    private WireMockServer identityProvider;

    Invoker(T request, WireMockServer serviceProvider, WireMockServer identityProvider) {
        this.serviceProvider = serviceProvider;
        this.identityProvider = identityProvider;
        this.request = request;
    }

    /**
     * todo docs
     */
    public void willServe(Response<T> response) {
        response.execute(request, serviceProvider, identityProvider);
    }
}
