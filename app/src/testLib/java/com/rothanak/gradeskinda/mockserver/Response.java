package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.WireMockServer;

public abstract class Response<T> {

    /**
     * todo docs
     */
    protected abstract void execute(T request, WireMockServer svProvider, WireMockServer idProvider);

}
