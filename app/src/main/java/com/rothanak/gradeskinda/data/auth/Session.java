package com.rothanak.gradeskinda.data.auth;

import java.net.HttpCookie;
import java.util.List;

class Session {

    private List<HttpCookie> cookies;

    public Session(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public List<HttpCookie> getCookies() {
        return cookies;
    }

}
