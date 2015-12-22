package com.rothanak.gradeskinda.domain.model;

public class CredentialsBuilder {

    public static final String DEFAULT_USER = "JohnDoe";
    public static final String DEFAULT_PASS = "correct horse battery staple";

    private String username = DEFAULT_USER;
    private String password = DEFAULT_PASS;

    private CredentialsBuilder() {
    }

    public static CredentialsBuilder defaultCredentials() {
        return new CredentialsBuilder();
    }

    public static CredentialsBuilder otherCredentials() {
        return CredentialsBuilder.defaultCredentials()
                .withUsername("JaneDoe")
                .withPassword("wrong horse battery staple");
    }

    public CredentialsBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public CredentialsBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public Credentials build() {
        return new Credentials(username, password);
    }

}
