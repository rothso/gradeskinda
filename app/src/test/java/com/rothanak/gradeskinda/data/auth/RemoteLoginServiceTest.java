package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.junit.Ignore;
import org.junit.Test;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import rx.Observable;

public class RemoteLoginServiceTest {

    @Ignore @Test
    public void login_WithGoodCredentials_ReturnsSession() {
        // TODO replace manual test DI with Dagger
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
        RemoteLoginService loginService = new RemoteLoginService(client, cookieManager);

        Credentials credentials = CredentialsBuilder.defaultCredentials().build();
        Session session = loginService.login(credentials).toBlocking().first();

        // todo
    }

}