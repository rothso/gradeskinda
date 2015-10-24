package com.rothanak.gradeskinda.interactor;

import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.ui.login.scheduler.TestAddSchedulesTransformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Single;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginInteractorTest {

    @Mock private AuthFacade authenticator;
    private LoginInteractor interactor;

    @Before
    public void setUp() {
        when(authenticator.login(anyString(), anyString())).thenReturn(Single.just(true));
        interactor = new LoginInteractor(authenticator, TestAddSchedulesTransformer.get());
    }

    @Test
    public void login_PassesThroughToAuthenticator() {
        String username = "Username";
        String password = "Password";

        Single<Boolean> login = interactor.login(username, password);

        assertThat(login.toObservable().toBlocking().first()).isTrue();
        verify(authenticator).login(username, password);
    }

}