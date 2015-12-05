package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthenticatorTest {

    @Mock private LoginService loginService;
    @Mock private AuthRepository repository;
    private DefaultAuthenticator authenticator;

    @Before
    public void setUp() {
        authenticator = new DefaultAuthenticator(loginService, repository);
    }

    @Test
    public void loginSuccessful_ReturnsTrue() {
        when(loginService.login(any(Credentials.class))).thenReturn(Observable.just(any(AuthToken.class)));

        Credentials credentials = new Credentials("Username", "Password");
        boolean success = authenticator.login(credentials).toBlocking().first();

        assertThat(success).isTrue();
    }

    @Test
    public void loginSuccessful_PersistsAuthToken() {
        AuthToken token = new AuthToken("Token");
        when(loginService.login(any(Credentials.class))).thenReturn(Observable.just(token));

        Credentials credentials = new Credentials("Username", "Password");
        authenticator.login(credentials).toBlocking().first();

        verify(repository).store(token);
    }

    @Test
    public void loginFailed_ReturnsFalse() {
        when(loginService.login(any(Credentials.class))).thenReturn(Observable.empty());

        Credentials credentials = new Credentials("Username", "Password");
        boolean success = authenticator.login(credentials).toBlocking().first();

        assertThat(success).isFalse();
    }

    @Test
    public void loginFailed_WontPersistAnything() {
        when(loginService.login(any(Credentials.class))).thenReturn(Observable.empty());

        Credentials credentials = new Credentials("Username", "Password");
        authenticator.login(credentials).toBlocking().first();

        verify(repository, never()).store(any());
    }

}
