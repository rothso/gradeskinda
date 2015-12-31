package com.rothanak.gradeskinda.data.auth;

import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class AuthenticatorImplTest {

    @Mock private LoginService loginService;
    @Mock private SessionRepository repository;
    private AuthenticatorImpl authenticator;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
        authenticator = new AuthenticatorImpl(loginService, repository);
    }

    public class Login {

        public class WhenCredentialsGood {

            private Session session = SessionBuilder.defaultSession().build();
            private Credentials goodCredentials = CredentialsBuilder.defaultCredentials().build();

            @Before public void setUp() {
                given(loginService.login(goodCredentials)).willReturn(Observable.just(session));
            }

            @Test public void shouldReturnTrue() {
                boolean loginSuccess = authenticator.login(goodCredentials).toBlocking().first();
                assertThat(loginSuccess).isTrue();
            }

            @Test public void shouldPersistSessionDetails() {
                authenticator.login(goodCredentials).toBlocking().first();
                verify(repository).store(session);
            }

        }

        public class WhenCredentialsBad {

            private Credentials badCredentials = CredentialsBuilder.defaultCredentials().build();

            @Before public void setUp() {
                given(loginService.login(badCredentials)).willReturn(Observable.empty());
            }

            @Test public void shouldReturnFalse() {
                boolean loginSuccess = authenticator.login(badCredentials).toBlocking().first();
                assertThat(loginSuccess).isFalse();
            }

            @Test public void shouldPersistNothing() {
                authenticator.login(badCredentials).toBlocking().first();
                verify(repository, never()).store(any());
            }

        }
    }
}
