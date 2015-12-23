package com.rothanak.gradeskinda.data.auth;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionBuilder {

    public static final HttpCookie COOKIE1 = new HttpCookie("Cookie1", "Value");
    public static final HttpCookie COOKIE2 = new HttpCookie("Cookie2", "Value");
    public static final List<HttpCookie> DEFAULT_COOKIES = new ArrayList<>(Arrays.asList(COOKIE1, COOKIE2));

    private List<HttpCookie> cookies = DEFAULT_COOKIES;

    private SessionBuilder() {
    }

    public static SessionBuilder defaultSession() {
        return new SessionBuilder();
    }

    public static SessionBuilder emptySession() {
        return defaultSession()
                .withNoCookies();
    }

    public SessionBuilder withNoCookies() {
        cookies = null;
        return this;
    }

    public SessionBuilder withCookie(HttpCookie cookie) {
        if (cookies == null) {
            cookies = new ArrayList<>();
        }
        cookies.add(cookie);
        return this;
    }

    public SessionBuilder withCookies(List<HttpCookie> cookieList) {
        if (cookies == null) {
            cookies = new ArrayList<>();
        }
        cookies.addAll(cookieList);
        return this;
    }

    public Session build() {
        return new Session(cookies);
    }

}
