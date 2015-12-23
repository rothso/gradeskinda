package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;

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
    @Mock private SessionRepository repository;
    private DefaultAuthenticator authenticator;

    @Before
    public void setUp() {
        authenticator = new DefaultAuthenticator(loginService, repository);
    }

    @Test
    public void loginSuccessful_ReturnsTrue() {
        Session session = SessionBuilder.defaultSession().build();
        Credentials goodCredentials = CredentialsBuilder.defaultCredentials().build();
        when(loginService.login(goodCredentials)).thenReturn(Observable.just(session));

        boolean success = authenticator.login(goodCredentials).toBlocking().first();

        assertThat(success).isTrue();
    }

    @Test
    public void loginSuccessful_PersistsAuthToken() {
        Session session = SessionBuilder.defaultSession().build();
        Credentials goodCredentials = CredentialsBuilder.defaultCredentials().build();
        when(loginService.login(goodCredentials)).thenReturn(Observable.just(session));

        authenticator.login(goodCredentials).toBlocking().first();

        verify(repository).store(session);
    }

    @Test
    public void loginFailed_ReturnsFalse() {
        Credentials badCredentials = CredentialsBuilder.defaultCredentials().build();
        when(loginService.login(badCredentials)).thenReturn(Observable.empty());

        boolean success = authenticator.login(badCredentials).toBlocking().first();

        assertThat(success).isFalse();
    }

    @Test
    public void loginFailed_WontPersistAnything() {
        Credentials badCredentials = CredentialsBuilder.defaultCredentials().build();
        when(loginService.login(badCredentials)).thenReturn(Observable.empty());

        authenticator.login(badCredentials).toBlocking().first();

        verify(repository, never()).store(any());
    }

}
