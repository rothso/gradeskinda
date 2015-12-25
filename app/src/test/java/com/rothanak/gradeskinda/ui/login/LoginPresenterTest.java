package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.model.CredentialsBuilder;
import com.rothanak.gradeskinda.ui.login.LoginPresenter.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class LoginPresenterTest {

    @Mock private LoginInteractor interactor;
    @Mock private View view;
    private LoginPresenter presenter;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new LoginPresenter(interactor);
        presenter.attachView(view);
    }

    public class VerifyCredentials {

        public class WhenCredentialsGood {

            @Test public void shouldTriggerShowDashboard() {
                // Arrange a successful login
                Credentials goodCredentials = CredentialsBuilder.defaultCredentials().build();
                String username = goodCredentials.getUsername();
                String password = goodCredentials.getPassword();
                given(interactor.login(goodCredentials)).willReturn(Observable.just(true));

                // Log in with good credentials
                presenter.verifyCredentials(username, password);

                // Verify we can proceed
                verify(view).gotoDashboard();
                verify(view, never()).showBadCredentialsError();
            }

        }

        public class WhenCredentialsBad {

            @Test public void shouldTriggerError() {
                // Arrange a doomed login
                Credentials badCredentials = CredentialsBuilder.defaultCredentials().build();
                String username = badCredentials.getUsername();
                String password = badCredentials.getPassword();
                given(interactor.login(badCredentials)).willReturn(Observable.just(false));

                // Log in with bad credentials
                presenter.verifyCredentials(username, password);

                // Verify the user cannot proceed and sees an error
                verify(view, never()).gotoDashboard();
                verify(view).showBadCredentialsError();
            }

        }
    }
}