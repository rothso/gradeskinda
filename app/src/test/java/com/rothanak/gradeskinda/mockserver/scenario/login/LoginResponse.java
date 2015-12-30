package com.rothanak.gradeskinda.mockserver.scenario.login;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rothanak.gradeskinda.mockserver.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

public abstract class LoginResponse extends Response<LoginScenario> {

    public static final String DEFAULT_SESSION_ID = "583i92voktl9c8lii88fp67l26";
    public static final String DEFAULT_SESSION_TIMEOUT = "1451268200";

    // Only the existence of the keys which correspond to these values matter in our login process.
    // The values themselves are arbitrary tokens used by the two servers to share state during
    // the transaction, and are negligible provided they stay constant throughout the exchange.
    protected static final String SAML_REQUEST = "rVLLbtswEPwVgXeJkhzFAmEbcGIENZCebHjqwZnlxsM";
    protected static final String SAML_RESPONSE = "PHNhbWxwOlJlc3BvbnNlIElEPSJfNWU5ODY0YmQtNm";
    protected static final String SAML_SESSION = "aHR0cHMlM2ElMmYlMmZkdXZhbC5mb2N1c3NjaG9vHZn";

    protected String phpSessionId = DEFAULT_SESSION_ID;
    protected String sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    protected String loginPayload;

    public static LoginSuccessfulResponse successful() {
        return new LoginSuccessfulResponse();
    }

    public static LoginFailedResponse failure() {
        return new LoginFailedResponse();
    }

    @Override
    protected void execute(LoginScenario request, WireMockRule svProvider, WireMockRule idProvider) {
        // Location of the identity server to which request #1 is redirected
        final String idServer = "http://localhost:" + idProvider.port();

        // All login processes begin with a request to /focus/ on the service host, which redirects
        // the http client to a unique SSO url on the identity server.
        svProvider.givenThat(get(urlEqualTo("/focus/"))
                .willReturn(aResponse().withStatus(302)
                        .withHeader("Set-Cookie", "PHPSESSID=" + phpSessionId + "; path=/focus")
                        .withHeader("Location", idServer + "/adfs/ls/?SAMLRequest=" + SAML_REQUEST)
                        .withBodyFile("login.init.index.html")));

        // Login payload to be submitted to the identity server in a followup request
        try {
            String username = URLEncoder.encode("DCPS\\" + request.getUsername(), "UTF-8");
            String password = URLEncoder.encode(request.getPassword(), "UTF-8");
            loginPayload = String.format("UserName=%s&Password=%s", username, password);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static class LoginSuccessfulResponse extends LoginResponse {

        public LoginSuccessfulResponse withPhpSessionId(String phpSessionId) {
            this.phpSessionId = phpSessionId;
            return this;
        }

        public LoginSuccessfulResponse withSessionTimeout(String sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
            return this;
        }

        @Override
        protected void execute(LoginScenario request, WireMockRule svProvider, WireMockRule idProvider) {
            super.execute(request, svProvider, idProvider);

            // adfs/ls/?SAMLRequest=*
            idProvider.givenThat(post(urlPathEqualTo("/adfs/ls/"))
                    .withQueryParam("SAMLRequest", equalTo(SAML_REQUEST))
                    .withRequestBody(equalTo(loginPayload))
                    .willReturn(aResponse()
                            .withHeader("Set-Cookie", "SAMLSession=" + SAML_SESSION)
                            .withBodyFile("login.success.samlresponse.html")));

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
                            .withBodyFile("login.success.done.html")));
        }

    }

    public static class LoginFailedResponse extends LoginResponse {

        @Override
        protected void execute(LoginScenario request, WireMockRule svProvider, WireMockRule idProvider) {
            super.execute(request, svProvider, idProvider);

            // adfs/ls/?SAMLRequest=*
            idProvider.givenThat(post(urlPathEqualTo("/adfs/ls/"))
                    .withQueryParam("SAMLRequest", equalTo(SAML_REQUEST))
                    .withRequestBody(equalTo(loginPayload))
                    .willReturn(aResponse().withBodyFile("login.failed.response.html")));
        }
    }

}
