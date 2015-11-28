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
import com.rothanak.gradeskinda.ui.dashboard.DashboardActivity;

import org.junit.Before;
import org.junit.Test;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.shadows.ShadowToast.showedToast;
import static org.robolectric.shadows.ShadowToast.shownToastCount;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LoginActivityTest {

    @Bind(R.id.username) EditText usernameField;
    @Bind(R.id.password) EditText passwordField;
    @Bind(R.id.submit_login) Button loginButton;

    @Before
    public void setUp() {
        // Inject a mockable InteractorModule so that LoginInteractor can be mocked
        Application application = RuntimeEnvironment.application;
        AppComponent component = DaggerAppComponent.builder().interactorModule(new MockInteractorModule()).build();
        ((TestGradesApplication) application).component(component);

        // Preconfigure successful logins so the network doesn't have to be hit
        LoginInteractor mockInteractor = component.loginInteractor();
        when(mockInteractor.login(any(Credentials.class))).thenReturn(Observable.just(false));
        when(mockInteractor.login(new Credentials("Username", "Password"))).thenReturn(Observable.just(true));
    }

    @Test
    public void login_WithGoodCredentials_ShouldShowDashboard() {
        LoginActivity activity = Robolectric.setupActivity(LoginActivity.class);
        ButterKnife.bind(this, activity);

        usernameField.setText("Username");
        passwordField.setText("Password");
        loginButton.performClick();

        Intent expectedIntent = new Intent(activity, DashboardActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity(), is(equalTo(expectedIntent)));
        assertThat(shownToastCount(), is(0));
    }

    @Test
    public void login_WithBadCredentials_ShouldShowError() {
        LoginActivity activity = Robolectric.setupActivity(LoginActivity.class);
        ButterKnife.bind(this, activity);

        usernameField.setText("Username");
        passwordField.setText("NotPassword");
        loginButton.performClick();

        assertThat(shadowOf(activity).getNextStartedActivity(), is(nullValue()));
        assertThat(showedToast("The username or password is invalid."), is(true));
    }

}