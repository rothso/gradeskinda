package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.data.auth.AuthFacade;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Single;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    private static final String GOOD_USER = "1";
    private static final String GOOD_PASS = "1";
    private static final String BAD_USER = "2";
    private static final String BAD_PASS = "2";
    @Mock private AuthFacade authenticator;
    @Mock private LoginPresenter.View view;

    @Before
    public void setUp() {
        when(authenticator.login(GOOD_USER, GOOD_PASS)).thenReturn(Single.just(true));
        when(authenticator.login(BAD_USER, BAD_PASS)).thenReturn(Single.just(false));
    }

    @Test
    public void verifyCredentials_WithGoodCredentials_ShouldShowDashboard() {
        LoginPresenter presenter = new LoginPresenter(authenticator);
        presenter.attachView(view);

        presenter.verifyCredentials(GOOD_USER, GOOD_PASS);

        verify(view).gotoDashboard();
        verify(view, never()).showBadCredentialsError();
    }

    @Test
    public void verifyCredentials_WithBadCredentials_ShouldShowError() {
        LoginPresenter presenter = new LoginPresenter(authenticator);
        presenter.attachView(view);

        presenter.verifyCredentials(BAD_USER, BAD_PASS);

        verify(view, never()).gotoDashboard();
        verify(view).showBadCredentialsError();
    }

}