package com.rothanak.gradeskinda.data.auth;

// TODO: replace with SessionCookies
public class AuthToken {

    private final String value;

    public AuthToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
