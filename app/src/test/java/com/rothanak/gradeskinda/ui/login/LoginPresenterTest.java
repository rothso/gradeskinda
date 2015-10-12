package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.ui.login.LoginPresenter.View;

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

    private LoginPresenter presenter;
    @Mock private AuthFacade authenticator;
    @Mock private View view;

    @Before
    public void setUp() {
        // Set predefined responses for "good" and "bad" logins
        when(authenticator.login(GOOD_USER, GOOD_PASS)).thenReturn(Single.just(true));
        when(authenticator.login(BAD_USER, BAD_PASS)).thenReturn(Single.just(false));

        presenter = new LoginPresenter(authenticator);
    }

    @Test
    public void verifyCredentials_WithGoodCredentials_ShouldShowDashboard() {
        // Allow the presenter to control the view
        presenter.attachView(view);

        // Mimic a user submitting good credentials
        presenter.verifyCredentials(GOOD_USER, GOOD_PASS);

        // Verify the user is allowed to proceed
        verify(view).gotoDashboard();
        verify(view, never()).showBadCredentialsError();
    }

    @Test
    public void verifyCredentials_WithBadCredentials_ShouldShowError() {
        // Allow the presenter to control the view
        presenter.attachView(view);

        // Mimic a user submitting bad credentials
        presenter.verifyCredentials(BAD_USER, BAD_PASS);

        // Verify the user cannot proceed and sees an error
        verify(view, never()).gotoDashboard();
        verify(view).showBadCredentialsError();
    }

}