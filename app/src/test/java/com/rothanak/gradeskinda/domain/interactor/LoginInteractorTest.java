package com.rothanak.gradeskinda.domain.interactor;

import com.rothanak.gradeskinda.domain.interactor.scheduler.TestAddSchedulesTransformer;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
import com.rothanak.gradeskinda.domain.service.Authenticator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginInteractorTest {

    @Mock private Authenticator authenticator;
    private LoginInteractor interactor;

    @Before
    public void setUp() {
        when(authenticator.login(any(Credentials.class))).thenReturn(Observable.just(true));
        interactor = new LoginInteractor(authenticator, TestAddSchedulesTransformer.get());
    }

    @Test
    public void login_PassesThroughToAuthenticator() {
        Credentials credentials = CredentialsBuilder.defaultCredentials().build();

        Observable<Boolean> login = interactor.login(credentials);

        assertThat(login.toBlocking().first()).isTrue();
        verify(authenticator).login(credentials);
    }

}