package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class MockLoginServer {

    private final WireMockRule serviceProvider;
    private final WireMockRule identityProvider;

    private MockLoginServer(WireMockRule serviceServer, WireMockRule idServer) {
        this.serviceProvider = serviceServer;
        this.identityProvider = idServer;
    }

    /**
     * todo find a more elegant way to inject the servers?
     */
    public static MockLoginServer with(WireMockRule serviceServer, WireMockRule idServer) {
        return new MockLoginServer(serviceServer, idServer);
    }

    /**
     * todo docs
     * todo maybe require <T extends Request>?
     */
    public <T> Invoker<T> given(T requestPayload) {
        // TODO find alternative to passing around WireMockRUle?
        return new Invoker<>(requestPayload, serviceProvider, identityProvider);
    }

}
