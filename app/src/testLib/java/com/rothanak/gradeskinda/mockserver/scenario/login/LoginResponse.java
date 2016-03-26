package com.rothanak.gradeskinda.mockserver.scenario.login;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.rothanak.gradeskinda.mockserver.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

public abstract class LoginResponse<T extends LoginResponse<T>> extends Response<LoginScenario> {

    public static final String DEFAULT_SESSION_ID = "583i92voktl9c8lii88fp67l26";
    public static final String DEFAULT_SESSION_TIMEOUT = "1451268200";

    /**
     * Only the existence of the keys which correspond to these values matter in our login process.
     * The values themselves are arbitrary tokens used by the two servers to share state during
     * the transaction, and are negligible provided they stay constant throughout the exchange.
     */
    protected static final String SAML_REQUEST, SAML_RESPONSE, SAML_SESSION, MSIS_AUTH;

    static {
        SAML_REQUEST = "rVLLbtswEPwVgXeJkhzFAmEbcGIENZCebHjqwZnlxsM";
        SAML_RESPONSE = "PHNhbWxwOlJlc3BvbnNlIElEPSJfNWU5ODY0YmQtNm";
        SAML_SESSION = "aHR0cHMlM2ElMmYlMmZkdXZhbC5mb2N1c3NjaG9vHZn";
        MSIS_AUTH = "AAEAAACDpXFYb0b2ugR9bCaD2QD0oeNets84BF+QSeuaYT";
    }

    protected String phpSessionId = DEFAULT_SESSION_ID;
    protected String sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    protected String loginPayload;

    /**
     * WireMock response delay, can be configured to invoke SocketTimeoutExceptions.
     */
    protected int fixedDelayMillis;

    public static LoginSuccessfulResponse successfulResponse() {
        return new LoginSuccessfulResponse();
    }

    public static LoginFailedResponse failureResponse() {
        return new LoginFailedResponse();
    }

    /**
     * Delay the time it takes for the first request to send back its response.
     *
     * @param milliseconds length of delay, default 0.
     */
    @SuppressWarnings("unchecked")
    public T withDelay(int milliseconds) {
        this.fixedDelayMillis = milliseconds < 0 ? 0 : milliseconds;
        return (T) this;
    }

    @Override
    protected void execute(LoginScenario request, WireMockServer svProvider, WireMockServer idProvider) {
        // Location of the identity server to which request #1 is redirected
        final String idServer = "http://localhost:" + idProvider.port();

        // All login processes begin with a request to /focus/ on the service host, which redirects
        // the http client to a unique SSO url on the identity server.
        svProvider.givenThat(get(urlEqualTo("/focus/"))
                .willReturn(aResponse().withStatus(302)
                        .withFixedDelay(fixedDelayMillis)
                        .withHeader("Set-Cookie", "PHPSESSID=" + phpSessionId + "; path=/focus")
                        .withHeader("Location", idServer + "/adfs/ls/?SAMLRequest=" + SAML_REQUEST)
                        .withBodyFile("login_init_index.html")));

        // Login payload to be submitted to the identity server in a followup request
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            username = username != null ? URLEncoder.encode("DCPS\\" + username, "UTF-8") : ".*";
            password = password != null ? URLEncoder.encode(password, "UTF-8") : ".*";
            loginPayload = String.format("UserName=%s&Password=%s", username, password);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static class LoginSuccessfulResponse extends LoginResponse<LoginSuccessfulResponse> {

        private LoginSuccessfulResponse() {
        }

        public LoginSuccessfulResponse withPhpSessionId(String phpSessionId) {
            this.phpSessionId = phpSessionId;
            return this;
        }

        public LoginSuccessfulResponse withSessionTimeout(String sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
            return this;
        }

        @Override
        protected void execute(LoginScenario request, WireMockServer svProvider, WireMockServer idProvider) {
            super.execute(request, svProvider, idProvider);

            // adfs/ls/?SAMLRequest=*
            idProvider.givenThat(post(urlPathEqualTo("/adfs/ls/"))
                    .withQueryParam("SAMLRequest", equalTo(SAML_REQUEST))
                    .withRequestBody(matching(loginPayload))
                    .willReturn(aResponse()
                            .withHeader("Set-Cookie", "MSISAuth=" + MSIS_AUTH)
                            .withHeader("Set-Cookie", "SamlSession=" + SAML_SESSION)
                            .withBodyFile("login_success_samlresponse.html")));

            // focus/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp
            svProvider.givenThat(post(urlPathMatching("/focus/simplesaml/*"))
                    .withRequestBody(containing("SAMLResponse=" + SAML_RESPONSE))
                    .willReturn(aResponse().withStatus(302)
                            .withHeader("Location", "/focus/Modules.php?modname=misc/Portal.php")
                            .withBodyFile("blank.html")));

            // focus/Modules.php?modname=misc/Portal.php
            svProvider.givenThat(get(urlPathEqualTo("/focus/Modules.php"))
                    .withQueryParam("modname", equalTo("misc/Portal.php"))
                    .willReturn(aResponse()
                            .withHeader("Set-Cookie", "session_timeout=" + sessionTimeout)
                            .withBodyFile("login_success_done.html")));
        }

    }

    public static class LoginFailedResponse extends LoginResponse<LoginFailedResponse> {

        private LoginFailedResponse() {
        }

        @Override
        protected void execute(LoginScenario request, WireMockServer svProvider, WireMockServer idProvider) {
            super.execute(request, svProvider, idProvider);

            // adfs/ls/?SAMLRequest=*
            idProvider.givenThat(post(urlPathEqualTo("/adfs/ls/"))
                    .withQueryParam("SAMLRequest", equalTo(SAML_REQUEST))
                    .withRequestBody(equalTo(loginPayload))
                    .willReturn(aResponse().withBodyFile("login_failed_response.html")));
        }

    }

}
