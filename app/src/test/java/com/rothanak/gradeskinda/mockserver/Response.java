package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public abstract class Response<T> {

    /**
     * todo docs
     */
    protected abstract void execute(T request, WireMockRule svProvider, WireMockRule idProvider);

}
