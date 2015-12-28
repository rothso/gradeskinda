package com.rothanak.gradeskinda.data.auth;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.CookieManager;
import java.net.CookiePolicy;
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
     * to be stubbed to return canned responses.
     */
    @Rule public WireMockRule serviceProvider = new WireMockRule(8080);  // duval.focusschoolsoftware.com
    @Rule public WireMockRule identityProvider = new WireMockRule(8081); // fs.duvalschools.org

    private RemoteLoginService loginService;

    @Before public void setUp() {
        // TODO replace this mess with a Dagger module setup
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient()
                .setCookieHandler(cookieManager);

        // Rewrite requests to use the stubbing proxies
        client.interceptors().add(chain -> {
            Request request = chain.request();

            HttpUrl url = request.httpUrl();
            String host = url.host();
            if (host.equals("duval.focusschoolsoftware.com")) {
                url = url.newBuilder().scheme("http").host("localhost").port(8080).build();
            } else if (host.equals("fs.duvalschools.org")) {
                url = url.newBuilder().scheme("http").host("localhost").port(8081).build();
            }
            request = request.newBuilder().url(url).build();

            return chain.proceed(request);
        });

        client.networkInterceptors().add(logging);
        loginService = new RemoteLoginService(client, cookieManager);
    }

    public class Login {

        public class WhenCredentialsSucceed {

            @Before public void setUp() {
                serviceProvider.givenThat(get(urlEqualTo("/focus/"))
                        .willReturn(aResponse()
                                .withStatus(302)
                                .withHeader("Set-Cookie", "PHPSESSID=583i92voktl9c8lii88fp67l26; path=/focus")
                                .withHeader("Set-Cookie", "SimpleSAMLSessionID=376c770c7803113b6066fc95b78e9b17; path=/; httponly")
                                .withHeader("Location", "http://localhost:8081/adfs/ls/?SAMLRequest=rVLLbtswEPwVgXeJkhzFAmEbcGIENZC")
                                .withBodyFile("login.success.index.html")));

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

            @Test public void ShouldReturnSession() {
                Credentials credentials = CredentialsBuilder.defaultCredentials()
                        .withUsername("JohnDoe")
                        .withPassword("correct horse battery staple")
                        .build();

                Session session = loginService.login(credentials).toBlocking().first();
                List<HttpCookie> cookies = session.getCookies();

                assertThat(cookies.get(0).getValue()).isEqualTo("583i92voktl9c8lii88fp67l26");
                assertThat(cookies.get(1).getValue()).isEqualTo("1451268200");
            }

        }

        public class WhenCredentialsFail {

            @Test @Ignore
            public void ShouldReturnNull() {
            }

        }

        public class WhenRemoteNetworkDown {

            @Test @Ignore
            public void ShouldThrowRemoteNetworkDownException() {
            }

        }

        public class WhenLocalNetworkDown {

            @Test @Ignore
            public void ShouldThrowLocalNetworkDownException() {
            }

        }

    }
}