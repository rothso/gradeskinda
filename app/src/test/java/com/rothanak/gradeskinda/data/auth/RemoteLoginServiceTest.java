package com.rothanak.gradeskinda.data.auth;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
import com.squareup.okhttp.HttpUrl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpCookie;
import java.util.List;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class RemoteLoginServiceTest {

    /*
     * Focus uses the SAML protocol for authentication, requiring a chain of requests to be sent to
     * two servers, the service provider and the identity provider (Duval), both of which will need
     * to be stubbed to return canned responses. One port stubs SvP requests, the other stubs IdP.
     */
    public static int SVP_PORT = 8080;  // duval.focusschoolsoftware.com
    public static int IDP_PORT = 8081;  // fs.duvalschools.org
    @Rule public WireMockRule serviceProvider = new WireMockRule(SVP_PORT);
    @Rule public WireMockRule identityProvider = new WireMockRule(IDP_PORT);

    private HttpUrl testServiceEndpoint = HttpUrl.parse("http://localhost:" + SVP_PORT);
    private HttpUrl testIdentityEndpoint = HttpUrl.parse("http://localhost:" + IDP_PORT);

    private RemoteLoginService loginService;

    @Before public void setUp() {
        loginService = (RemoteLoginService) DaggerAuthComponent.builder()
                .authModule(new AuthModule().setEndpoint(testServiceEndpoint))
                .build().loginService();
    }

    /* TODO replace hard-coded magic numbers -> test builder constants */
    public class Login {

        public class WhenCredentialsSucceed {

            @Before public void setUp() {
                // TODO clean up
                serviceProvider.givenThat(get(urlEqualTo("/focus/"))
                        .willReturn(aResponse()
                                .withStatus(302)
                                .withHeader("Set-Cookie", "PHPSESSID=583i92voktl9c8lii88fp67l26; path=/focus")
                                .withHeader("Set-Cookie", "SimpleSAMLSessionID=376c770c7803113b6066fc95b78e9b17; path=/; httponly")
                                // TODO use endpoint variable instead of hard-coding
                                .withHeader("Location", "http://localhost:8081/adfs/ls/?SAMLRequest=rVLLbtswEPwVgXeJkhzFAmEbcGIENZC")
                                .withBodyFile("login.init.index.html")));

                identityProvider.givenThat(post(urlPathEqualTo("/adfs/ls/"))
                        .withQueryParam("SAMLRequest", equalTo("rVLLbtswEPwVgXeJkhzFAmEbcGIENZC"))
                        .withRequestBody(equalTo("UserName=DCPS%5CJohnDoe&Password=correct%20horse%20battery%20staple"))
                        .willReturn(aResponse()
                                .withHeader("Set-Cookie", "SAMLSession=aHR0cHMlM2ElMmYlMmZkdXZhbC5mb2N1c3NjaG9vbHNvZnR3YXJlLm")
                                .withBodyFile("login.success.samlresponse.html")));

                serviceProvider.givenThat(post(urlPathMatching("/focus/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp"))
                        .withRequestBody(containing("SAMLResponse=PHNhbWxwOlJlc3BvbnNlIElEPSJfNWU5ODY0YmQtNmM4NS00YjkxLWI1NmYtMjdhMTY"))
                        .willReturn(aResponse()
                                .withStatus(302)
                                .withHeader("Location", "/focus/Modules.php?modname=misc/Portal.php")
                                .withBodyFile("blank.html")));

                serviceProvider.givenThat(get(urlPathEqualTo("/focus/Modules.php"))
                        .withQueryParam("modname", equalTo("misc/Portal.php"))
                        .willReturn(aResponse()
                                .withHeader("Set-Cookie", "session_timeout=1451268200")
                                .withBodyFile("login.success.done.html")));
            }

            @Test public void shouldReturnSession() {
                Credentials credentials = CredentialsBuilder.defaultCredentials()
                        .withUsername("JohnDoe")
                        .withPassword("correct horse battery staple")
                        .build();

                Session session = loginService.login(credentials).toBlocking().first();
                List<HttpCookie> cookies = session.getCookies();

                // TODO domain language assertions
                assertThat(cookies.get(0).getValue()).isEqualTo("583i92voktl9c8lii88fp67l26");
                assertThat(cookies.get(1).getValue()).isEqualTo("1451268200");
            }

        }

        public class WhenCredentialsFail {

            @Before public void setUp() {
                // TODO above
                serviceProvider.givenThat(get(urlEqualTo("/focus/"))
                        .willReturn(aResponse()
                                .withStatus(302)
                                .withHeader("Set-Cookie", "PHPSESSID=583i92voktl9c8lii88fp67l26; path=/focus")
                                .withHeader("Set-Cookie", "SimpleSAMLSessionID=376c770c7803113b6066fc95b78e9b17; path=/; httponly")
                                .withHeader("Location", "http://localhost:8081/adfs/ls/?SAMLRequest=rVLLbtswEPwVgXeJkhzFAmEbcGIENZC")
                                .withBodyFile("login.init.index.html")));

                identityProvider.givenThat(post(urlPathEqualTo("/adfs/ls/"))
                        .withQueryParam("SAMLRequest", equalTo("rVLLbtswEPwVgXeJkhzFAmEbcGIENZC"))
                        .withRequestBody(equalTo("UserName=DCPS%5CJohnDoe&Password=wrong%20horse%20battery%20staple"))
                        .willReturn(aResponse()
                                .withBodyFile("login.failed.response.html")));
            }

            @Test public void shouldReturnNull() {
                Credentials credentials = CredentialsBuilder.defaultCredentials()
                        .withUsername("JohnDoe")
                        .withPassword("wrong horse battery staple")
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