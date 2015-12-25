package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import rx.Observable;

@Ignore
@RunWith(HierarchicalContextRunner.class)
public class RemoteLoginServiceTest {

    private RemoteLoginService loginService;

    @Before public void setUp() {
        // TODO replace this mess with a Dagger module setup
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient()
                .setCookieHandler(cookieManager);
        List<Interceptor> interceptors = client.interceptors();
        interceptors.add(logging);
        interceptors.add(chain -> {
            Request request = chain.request();
            Request.Builder requestBuilder = request.newBuilder();
            Observable.from(cookieManager.getCookieStore().getCookies()).subscribe(cookie ->
                    requestBuilder.addHeader("Cookie", cookie.getName() + "=" + cookie.getValue())
            );
            return chain.proceed(requestBuilder.build());
        });
        loginService = new RemoteLoginService(client, cookieManager);
    }

    public class Login {

        public class WhenCredentialsSucceed {

            @Test public void ShouldReturnSession() {
                Credentials credentials = CredentialsBuilder.defaultCredentials().build();
                // TODO network stubbing with Betamax

                Session session = loginService.login(credentials).toBlocking().first();

                // TODO assertions
            }

        }

        public class WhenCredentialsFail {

            @Test public void ShouldReturnNull() {
            }

        }

        public class WhenRemoteNetworkDown {

            @Test public void ShouldThrowRemoteNetworkDownException() {
            }

        }

        public class WhenLocalNetworkDown {

            @Test public void ShouldThrowLocalNetworkDownException() {
            }

        }

    }
}