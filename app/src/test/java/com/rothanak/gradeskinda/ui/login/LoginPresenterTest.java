package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
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

    @Mock private LoginInteractor interactor;
    @Mock private View view;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
        presenter = new LoginPresenter(interactor);
        presenter.attachView(view);
    }

    @Test
    public void verifyCredentials_WithGoodCredentials_ShouldShowDashboard() {
        // Arrange a successful login
        Credentials goodCredentials = CredentialsBuilder.defaultCredentials().build();
        String username = goodCredentials.getUsername();
        String password = goodCredentials.getPassword();
        when(interactor.login(goodCredentials)).thenReturn(Observable.just(true));

        // Log in with good credentials
        presenter.verifyCredentials(username, password);

        // Verify we can proceed
        verify(view).gotoDashboard();
        verify(view, never()).showBadCredentialsError();
    }

    @Test
    public void verifyCredentials_WithBadCredentials_ShouldShowError() {
        // Arrange a doomed login
        Credentials badCredentials = CredentialsBuilder.defaultCredentials().build();
        String username = badCredentials.getUsername();
        String password = badCredentials.getPassword();
        when(interactor.login(badCredentials)).thenReturn(Observable.just(false));

        // Log in with bad credentials
        presenter.verifyCredentials(username, password);

        // Verify the user cannot proceed and sees an error
        verify(view, never()).gotoDashboard();
        verify(view).showBadCredentialsError();
    }

}