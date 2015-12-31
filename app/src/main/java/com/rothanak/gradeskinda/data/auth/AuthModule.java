package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.service.Authenticator;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import java.net.CookieManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.BaseUrl;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Url;
import rx.Observable;

@Module
public class AuthModule {

    private BaseUrl remoteEndpoint = () -> HttpUrl.parse("https://duval.focusschoolsoftware.com");

    public AuthModule setEndpoint(HttpUrl remoteEndpoint) {
        this.remoteEndpoint = () -> remoteEndpoint;
        return this;
    }

    @Provides @Singleton
    LoginService loginService(LoginApi loginApi, OkHttpClient client, CookieManager cookieManager) {
        return new LoginService(loginApi, client, cookieManager);
    }

    @Provides @Singleton SessionRepository sessionRepository() {
        return session -> {
            throw new UnsupportedOperationException();
        };
    }

    @Provides @Singleton
    Authenticator authenticator(LoginService loginService, SessionRepository sessionRepository) {
        return new AuthenticatorImpl(loginService, sessionRepository);
    }

    @Provides @Singleton
    LoginApi loginApi(OkHttpClient client, RxJavaCallAdapterFactory rxCallAdapter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(remoteEndpoint)
                .addCallAdapterFactory(rxCallAdapter)
                .client(client)
                .build();

        return retrofit.create(LoginApi.class);
    }

    interface LoginApi {

        @GET("focus/") Observable<Response<Void>> initialize();

        @POST @FormUrlEncoded Observable<Response<ResponseBody>> login(
                @Url String loginUrl,
                @Field("UserName") String username,
                @Field("Password") String password
        );

        @POST("focus/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp") @FormUrlEncoded
        Observable<Response<ResponseBody>> finalize(@Field("SAMLResponse") String responseKey);

    }

}
