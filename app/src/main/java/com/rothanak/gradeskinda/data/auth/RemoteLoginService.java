package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;

import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Url;
import rx.Observable;

class RemoteLoginService implements LoginService {

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

    /**
     * Attempt to authenticate the user on the server. TODO docs
     *
     * @return A Session object containing the PHPSESSID and session timeout http cookies,
     * respectively, or a null object if the login failed. TODO: simplify Session
     */
    @Override
    public Observable<Session> login(Credentials credentials) {
        String user = credentials.getUsername();
        String pass = credentials.getPassword();

        // Required so that the initial HEAD request doesn't redirect and timeout
        client.setFollowRedirects(false);

        return loginApi.initialize()
                .doOnNext(voidResponse -> client.setFollowRedirects(true))
                .map(this::constructLoginUrl)
                .flatMap(loginUrl -> loginApi.login(loginUrl, "DCPS\\" + user, pass))
                .filter(this::isLoginSuccessful)
                .map(this::parseForResponseKey)
                .flatMap(loginApi::finalize)
                .flatMap(response ->
                        // Filter the cookies and store them in a Session
                        Observable.from(cookieManager.getCookieStore().getCookies())
                                .filter(cookie -> cookie.getName().matches("PHPSESSID|session_timeout"))
                                .toList()
                                .map(Session::new)
                )
                .defaultIfEmpty(null);
    }

    /*
     * Get the URL to which the login credentials should be posted.
     *
     * Following redirects and parsing HTML forms is slow. Instead, we can just deduce the POST
     * target url from the redirect header and then append our federation as a query parameter.
     */
    private String constructLoginUrl(Response<Void> response) {
        String base = response.headers().get("Location");
        String arg = "&RedirectToIdentityProvider=urn%3Afederation%3ADCPS";
        return base + arg;
    }

    /*
     * Determine the success of the login on the server. A non-successful login will redirect the
     * http client back to the login form without setting a session cookie.
     */
    private boolean isLoginSuccessful(Response<ResponseBody> response) {
        String cookie = response.headers().get("Set-Cookie");
        return cookie != null && cookie.startsWith("SAMLSession=");
    }

    /*
     * Get the response key to be forwarded to the service provider.
     *
     * The identity provider responds with a javascript form redirect instead of a 302 code upon
     * successful logins, so we end up extracting the key to forward the request ourselves.
     */
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
