package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;

import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Url;
import rx.Observable;

public class RemoteLoginService implements LoginService {

    private final OkHttpClient client;
    private final CookieManager cookieManager;
    private final LoginApi loginApi;

    public RemoteLoginService(OkHttpClient client, CookieManager cookieManager) {
        this.client = client;
        this.cookieManager = cookieManager;
        this.loginApi = new Retrofit.Builder()
                .baseUrl("https://duval.focusschoolsoftware.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client).build()
                .create(LoginApi.class);
    }

    @Override
    public Observable<AuthToken> login(Credentials credentials) {
        String user = credentials.getUsername();
        String pass = credentials.getPassword();

        // Required so that the initial HEAD request doesn't redirect and timeout
        client.setFollowRedirects(false);

        return loginApi.initialize()
                .doOnNext(voidResponse -> client.setFollowRedirects(true))
                .map(this::constructLoginUrl)
                .flatMap(loginUrl -> loginApi.login(loginUrl, "DCPS\\" + user, pass))
                .map(this::parseForResponseKey)
                .filter(key -> key != null && !key.isEmpty())
                .flatMap(loginApi::finalize)
                .flatMap(response ->
                        // Filter the cookies and store the session details in an AuthToken
                        Observable.from(cookieManager.getCookieStore().getCookies())
                                .filter(cookie -> cookie.getName().matches("PHPSESSID|session_timeout"))
                                .map(HttpCookie::toString)
                                .reduce((acc, cookie) -> acc + ";" + cookie)
                                .map(AuthToken::new)
                );
    }

    private String constructLoginUrl(Response<Void> response) {
        String base = response.headers().get("Location");
        String arg = "&RedirectToIdentityProvider=urn%3Afederation%3ADCPS";
        return base + arg;
    }

    private String parseForResponseKey(Response<ResponseBody> response) {
        try {
            String body = response.body().string();
            Document document = Jsoup.parse(body);
            Elements inputs = document.getElementsByAttributeValue("name", "SAMLResponse");
            return inputs.isEmpty() ? null : inputs.first().attr("value");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private interface LoginApi {

        @GET("focus/") Observable<Response<Void>> initialize();

        @FormUrlEncoded @POST Observable<Response<ResponseBody>> login(
                @Url String loginUrl,
                @Field("UserName") String username,
                @Field("Password") String password
        );

        @FormUrlEncoded
        @POST("focus/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp")
        Observable<Response<ResponseBody>> finalize(@Field("SAMLResponse") String responseKey);

    }
}
