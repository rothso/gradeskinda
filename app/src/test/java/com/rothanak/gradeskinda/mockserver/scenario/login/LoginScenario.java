package com.rothanak.gradeskinda.mockserver.scenario.login;

public class LoginScenario {

    private final String username;
    private final String password;

    private LoginScenario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static LoginScenario loginWith(String username, String password) {
        return new LoginScenario(username, password);
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

}
