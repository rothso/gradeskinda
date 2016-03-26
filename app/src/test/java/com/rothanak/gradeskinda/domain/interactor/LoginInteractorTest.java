package com.rothanak.gradeskinda.domain.interactor;

import com.rothanak.gradeskinda.domain.interactor.scheduler.TestAddSchedulesTransformer;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.domain.service.Authenticator;
import com.rothanak.gradeskinda.testbuilder.CredentialsBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class LoginInteractorTest {

    @Mock private Authenticator authenticator;
    private LoginInteractor interactor;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new LoginInteractor(authenticator, TestAddSchedulesTransformer.get());
    }

    public class Login {

        public class WhenCredentialsGiven {

            @Test public void shouldPassThroughToAuthenticator() {
                Credentials credentials = CredentialsBuilder.defaultCredentials().build();
                given(authenticator.login(credentials)).willReturn(Observable.just(true));

                Observable<Boolean> login = interactor.login(credentials);
                boolean loginSuccess = login.toBlocking().first();

                assertThat(loginSuccess).isTrue();
                verify(authenticator).login(credentials);
            }

        }
    }
}