package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.mockserver.MockServerRule;
import com.rothanak.gradeskinda.testbuilder.CredentialsBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.observers.TestSubscriber;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginResponse.failureResponse;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginResponse.successfulResponse;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginScenario.anyLogin;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginScenario.loginWith;
import static com.squareup.okhttp.logging.HttpLoggingInterceptor.Level;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class LoginServiceTest {

    /*
     * Focus uses the SAML protocol for authentication, requiring a chain of requests to be sent to
     * two servers: the service provider and the identity provider (Duval), both of which we are
     * stubbing to return canned responses.
     */
    @Rule public MockServerRule mockServer = new MockServerRule();

    private LoginService loginService;
    private OkHttpClient okHttpClient;

    @Before public void setUp() {
        // Create an stock OkHttpClient
        RxJavaCallAdapterFactory rxCallAdapter = RxJavaCallAdapterFactory.create();
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient();
        client.setCookieHandler(cookieManager);
        client.interceptors().add(new HttpLoggingInterceptor().setLevel(Level.BODY));
        okHttpClient = client;

        // Create a LoginService from the DI module
        HttpUrl localEndPoint = HttpUrl.parse("http://localhost:" + MockServerRule.DEFAULT_PORT);
        AuthModule authModule = new LocalAuthModule(() -> localEndPoint);
        AuthModule.LoginApi loginApi = authModule.loginApi(client, rxCallAdapter);
        loginService = authModule.loginService(loginApi, client, cookieManager);
    }

    public class Login {

        public class WhenCredentialsSucceed {

            public static final String SUCCESSFUL_USERNAME = "JohnDoe";
            public static final String SUCCESSFUL_PASSWORD = "correct";
            public static final String PHPSESSID = "583i92voktl9c8lii88fp67l26";
            public static final String SESSION_TIMEOUT = "1451268200";

            @Before public void setUp() {
                mockServer
                        .given(loginWith(SUCCESSFUL_USERNAME, SUCCESSFUL_PASSWORD))
                        .willServe(successfulResponse()
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
                mockServer
                        .given(loginWith(BAD_USERNAME, BAD_PASSWORD))
                        .willServe(failureResponse());
            }

            @Test
            public void shouldReturnEmpty() {
                Credentials credentials = CredentialsBuilder.defaultCredentials()
                        .withUsername(BAD_USERNAME)
                        .withPassword(BAD_PASSWORD)
                        .build();

                Observable<Session> sessionObservable = loginService.login(credentials);

                // TODO replace with assertj-rx?
                assertThat(sessionObservable.toList().toBlocking().first()).isEmpty();
            }

        }

        public class WhenSocketTimeout {

            public static final int READ_TIMEOUT = 1000; // milliseconds

            @Before
            public void setUp() {
                okHttpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
                mockServer
                        .given(anyLogin())
                        .willServe(successfulResponse()
                                .withDelay(READ_TIMEOUT + 1));
            }

            @Test
            public void shouldRetryUpToThreeTimes() {
                Credentials credentials = CredentialsBuilder.defaultCredentials().build();
                TestSubscriber<Session> subscriber = TestSubscriber.create();

                loginService.login(credentials).subscribe(subscriber);

                mockServer.getServiceServer().verify(3, getRequestedFor(urlEqualTo("/focus/")));
                subscriber.assertError(SocketTimeoutException.class);
            }

        }

        public class WhenRemoteNetworkDown {

            @Test @Ignore
            public void shouldThrowRemoteNetworkDownException() {}

        }

        public class WhenLocalNetworkDown {

            @Test @Ignore
            public void ShouldThrowLocalNetworkDownException() {}

        }

    }
}