package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.ui.login.LoginPresenter.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    public static final String GOOD_USER = "1";
    public static final String GOOD_PASS = "1";
    public static final String BAD_USER = "2";
    public static final String BAD_PASS = "2";

    @Mock private LoginInteractor interactor;
    @Mock private View view;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
        Credentials goodCredentials = new Credentials(GOOD_USER, GOOD_PASS);
        Credentials badCredentials = new Credentials(BAD_USER, BAD_PASS);

        // Set predefined responses for "good" and "bad" logins
        when(interactor.login(goodCredentials)).thenReturn(Observable.just(true));
        when(interactor.login(badCredentials)).thenReturn(Observable.just(false));

        presenter = new LoginPresenter(interactor);
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