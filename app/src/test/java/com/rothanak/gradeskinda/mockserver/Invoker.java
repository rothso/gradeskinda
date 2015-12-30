package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class Invoker<T> {

    private final T request;
    private WireMockRule serviceProvider;
    private WireMockRule identityProvider;

    Invoker(T request, WireMockRule serviceProvider, WireMockRule identityProvider) {
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
