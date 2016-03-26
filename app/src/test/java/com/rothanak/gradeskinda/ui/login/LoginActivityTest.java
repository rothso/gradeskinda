package com.rothanak.gradeskinda.ui.login;

import android.app.Application;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import com.rothanak.gradeskinda.AppComponent;
import com.rothanak.gradeskinda.BuildConfig;
import com.rothanak.gradeskinda.DaggerAppComponent;
import com.rothanak.gradeskinda.R;
import com.rothanak.gradeskinda.TestGradesApplication;
import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;
import com.rothanak.gradeskinda.domain.interactor.MockInteractorModule;
import com.rothanak.gradeskinda.domain.model.Credentials;
import com.rothanak.gradeskinda.testbuilder.CredentialsBuilder;
import com.rothanak.gradeskinda.ui.dashboard.DashboardActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowToast.showedToast;
import static org.robolectric.shadows.ShadowToast.shownToastCount;

/*
 * RobolectricGradleTestRunner and HierarchyContextRunner don't play nice together: both are only
 * available as Runners and evaluate the tests after setting things up, so fall back to JUnit's
 * experimental (albeit much-more-boilerplatey) Enclosed runner for now.
 */
@RunWith(Enclosed.class)
public class LoginActivityTest {

    protected LoginActivity activity;
    protected LoginInteractor interactor;
    @Bind(R.id.username) EditText usernameField;
    @Bind(R.id.password) EditText passwordField;
    @Bind(R.id.submit_login) Button loginButton;

    @Before public void setUp() {
        // Inject a mockable InteractorModule so that LoginInteractor can be mocked
        Application application = RuntimeEnvironment.application;
        AppComponent component = DaggerAppComponent.builder()
                .interactorModule(new MockInteractorModule())
                .build();
        ((TestGradesApplication) application).component(component);

        // For arranging successful logins, now the network doesn't have to be hit
        interactor = component.loginInteractor();

        // Set up the activity and bind view components to fields
        activity = Robolectric.setupActivity(LoginActivity.class);
        ButterKnife.bind(this, activity);
    }

    /*
     * The test methods are cold-evaluated only once, but shown twice in the GUI view because the
     * @RunWith annotation leads IntelliJ to treat this inner class as any other test class.
     */
    @RunWith(Enclosed.class)
    public static class SubmitLogin {

        @RunWith(RobolectricGradleTestRunner.class)
        @Config(constants = BuildConfig.class, sdk = 21)
        public static class WhenCredentialsGood extends LoginActivityTest {

            @Test public void shouldShowDashboard() {
                Credentials goodCredentials = CredentialsBuilder.defaultCredentials().build();
                String username = goodCredentials.getUsername();
                String password = goodCredentials.getPassword();
                given(interactor.login(goodCredentials)).willReturn(Observable.just(true));

                usernameField.setText(username);
                passwordField.setText(password);
                loginButton.performClick();

                Intent expectedIntent = new Intent(activity, DashboardActivity.class);
                assertThat(shadowOf(activity).getNextStartedActivity(), is(equalTo(expectedIntent)));
                assertThat(shownToastCount(), is(0));
            }

        }

        @RunWith(RobolectricGradleTestRunner.class)
        @Config(constants = BuildConfig.class, sdk = 21)
        public static class WhenCredentialsBad extends LoginActivityTest {

            @Test public void shouldShowError() {
                Credentials badCredentials = CredentialsBuilder.defaultCredentials().build();
                String username = badCredentials.getUsername();
                String password = badCredentials.getPassword();
                given(interactor.login(badCredentials)).willReturn(Observable.just(false));

                usernameField.setText(username);
                passwordField.setText(password);
                loginButton.performClick();

                assertThat(shadowOf(activity).getNextStartedActivity(), is(nullValue()));
                assertThat(showedToast("The username or password is invalid."), is(true));
            }

        }
    }
}