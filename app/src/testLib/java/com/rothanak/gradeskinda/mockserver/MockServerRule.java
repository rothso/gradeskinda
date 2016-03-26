package com.rothanak.gradeskinda.mockserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class MockServerRule extends MockServer implements MethodRule, TestRule {

    // Port dedicated to running WireMock
    public static final int DEFAULT_PORT = 8080;
    public static final String ROOT_DIR = "src/testLib/assets/mockserver";

    public MockServerRule() {
        super(
                new WireMockServer(wireMockConfig().withRootDirectory(ROOT_DIR).port(DEFAULT_PORT)),
                new WireMockServer(wireMockConfig().withRootDirectory(ROOT_DIR).port(DEFAULT_PORT + 1))
        );
    }

    public MockServerRule(WireMockConfiguration svServerConfig, WireMockConfiguration idServerConfig) {
        super(new WireMockServer(svServerConfig), new WireMockServer(idServerConfig));
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return apply(base, null, null);
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                serviceServer.start();
                identityServer.start();
                try {
                    base.evaluate();
                } finally {
                    serviceServer.stop();
                    identityServer.stop();
                }
            }

        };
    }
}
