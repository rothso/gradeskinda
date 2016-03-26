package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.rothanak.gradeskinda.testutil.AssetSource;

import static com.github.tomakehurst.wiremock.core.Options.DEFAULT_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class InstrumentationMockServer {

    // Subfolder inside /assets containing the server's HTML files
    public static final String FILES_DIR = "mockserver";

    private InstrumentationMockServer() {
    }

    /**
     * Get a {@link MockServerRule} instance that pulls server files from Android's assets folder.
     */
    public static MockServerRule getRule() {
        AssetSource assetSource = new AssetSource(FILES_DIR);
        WireMockConfiguration svConfig = wireMockConfig().fileSource(assetSource).port(DEFAULT_PORT);
        WireMockConfiguration idConfig = wireMockConfig().fileSource(assetSource).port(DEFAULT_PORT + 1);
        return new MockServerRule(svConfig, idConfig);
    }

}
