package com.rothanak.gradeskinda.data.auth;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
import com.rothanak.gradeskinda.mockserver.scenario.login.LoginResponse;
import com.rothanak.gradeskinda.mockserver.scenario.login.LoginScenario;
import com.squareup.okhttp.HttpUrl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpCookie;
import java.util.List;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

import static com.rothanak.gradeskinda.mockserver.MockLoginServer.with;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class RemoteLoginServiceTest {

    /*
     * Focus uses the SAML protocol for authentication, requiring a chain of requests to be sent to
     * two servers, the service provider and the identity provider (Duval), both of which will need
     * to be stubbed to return canned responses. One port stubs SvP requests, the other stubs IdP.
     */
    public static final int SVP_PORT = 8080;  // duval.focusschoolsoftware.com
    public static final int IDP_PORT = 8081;  // fs.duvalschools.org
    @Rule public WireMockRule serviceProvider = new WireMockRule(SVP_PORT);
    @Rule public WireMockRule identityProvider = new WireMockRule(IDP_PORT);

    private RemoteLoginService loginService;

    @Before public void setUp() {
        HttpUrl testServiceEndpoint = HttpUrl.parse("http://localhost:" + SVP_PORT);
        loginService = (RemoteLoginService) DaggerAuthComponent.builder()
                .authModule(new AuthModule().setEndpoint(testServiceEndpoint))
                .build().loginService();
    }

    public class Login {

        public class WhenCredentialsSucceed {

            public static final String SUCCESSFUL_USERNAME = "JohnDoe";
            public static final String SUCCESSFUL_PASSWORD = "correct";
            public static final String PHPSESSID = "583i92voktl9c8lii88fp67l26";
            public static final String SESSION_TIMEOUT = "1451268200";

            @Before public void setUp() {
                with(serviceProvider, identityProvider)
                        .given(LoginScenario.loginWith(SUCCESSFUL_USERNAME, SUCCESSFUL_PASSWORD))
                        .willServe(LoginResponse.successful()
                                .withPhpSessionId(PHPSESSID)
                                .withSessionTimeout(SESSION_TIMEOUT));
            }

            @Test public void shouldReturnSession() {
                Credentials credentials = CredentialsBuilder.defaultCredentials()
                        .withUsername(SUCCESSFUL_USERNAME)
                        .withPassword(SUCCESSFUL_PASSWORD)
                        .build();

                Session session = loginService.login(credentials).toBlocking().first();
                List<HttpCookie> cookies = session.getCookies();

                // TODO domain language assertions
                assertThat(cookies.get(0).getValue()).isEqualTo(PHPSESSID);
                assertThat(cookies.get(1).getValue()).isEqualTo(SESSION_TIMEOUT);
            }

        }

        public class WhenCredentialsFail {

            public static final String BAD_USERNAME = "JohnDoe";
            public static final String BAD_PASSWORD = "wrong";

            @Before public void setUp() {
                with(serviceProvider, identityProvider)
                        .given(LoginScenario.loginWith(BAD_USERNAME, BAD_PASSWORD))
                        .willServe(LoginResponse.failure());
            }

            @Test public void shouldReturnNull() {
                Credentials credentials = CredentialsBuilder.defaultCredentials()
                        .withUsername(BAD_USERNAME)
                        .withPassword(BAD_PASSWORD)
                        .build();

                Session session = loginService.login(credentials).toBlocking().first();

                assertThat(session).isNull();
            }

        }

        public class WhenSocketTimeout {

            @Test @Ignore
            public void shouldRetryThreeTimes() {
            }

        }

        public class WhenRemoteNetworkDown {

            @Test @Ignore
            public void shouldThrowRemoteNetworkDownException() {
            }

        }

        public class WhenLocalNetworkDown {

            @Test @Ignore
            public void ShouldThrowLocalNetworkDownException() {
            }

        }

    }
}