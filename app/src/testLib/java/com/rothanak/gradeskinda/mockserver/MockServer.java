package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.WireMockServer;

public class MockServer {

    protected final WireMockServer serviceServer;
    protected final WireMockServer identityServer;

    protected MockServer(WireMockServer serviceServer, WireMockServer identityServer) {
        this.serviceServer = serviceServer;
        this.identityServer = identityServer;
    }

    /**
     * todo docs
     * todo maybe require <T extends Request>?
     */
    public <T> Invoker<T> given(T requestPayload) {
        return new Invoker<>(requestPayload, serviceServer, identityServer);
    }

    public WireMockServer getServiceServer() {
        return serviceServer;
    }

}
