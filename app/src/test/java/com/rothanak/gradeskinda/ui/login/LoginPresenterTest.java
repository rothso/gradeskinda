package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.testbuilder.CredentialsBuilder;
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
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(HierarchicalContextRunner.class)
public class LoginPresenterTest {

    @Mock private LoginInteractor interactor;
    @Mock private View view;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
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

            private final Credentials badCredentials = CredentialsBuilder.defaultCredentials().build();
            private final String username = badCredentials.getUsername();
            private final String password = badCredentials.getPassword();

            @Before
            public void setUp() {
                // Arrange a doomed login
                given(interactor.login(badCredentials)).willReturn(Observable.just(false));
            }

            @Test
            public void shouldNotTriggerShowDashboard() {
                presenter.verifyCredentials(username, password);
                verify(view, never()).gotoDashboard();
            }

            @Test
            public void shouldTriggerBadCredentialsError() {
                presenter.verifyCredentials(username, password);
                verify(view).showBadCredentialsError();
            }

        }

        public class WhenCredentialsNull {

            private final String username = null;
            private final String password = null;

            @Test
            public void shouldNotTriggerShowDashboard() {
                presenter.verifyCredentials(username, password);
                verify(view, never()).gotoDashboard();
            }

            @Test
            public void shouldNotLogIn() {
                presenter.verifyCredentials(username, password);
                verifyZeroInteractions(interactor);
            }

        }

    }
}