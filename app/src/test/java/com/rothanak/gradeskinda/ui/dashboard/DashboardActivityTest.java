package com.rothanak.gradeskinda.ui.dashboard;

import android.app.Application;
import android.content.Intent;

import com.rothanak.gradeskinda.AppComponent;
import com.rothanak.gradeskinda.BuildConfig;
import com.rothanak.gradeskinda.DaggerAppComponent;
import com.rothanak.gradeskinda.TestGradesApplication;
import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.data.auth.MockAuthModule;
import com.rothanak.gradeskinda.ui.login.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import rx.Observable;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DashboardActivityTest {

    private ActivityController<DashboardActivity> controller;
    private AuthFacade authFacade;

    @Before
    public void setUp() {
        // Inject an auth resolver to be later mocked in the tests
        final AppComponent component = DaggerAppComponent.builder()
                .authModule(new MockAuthModule())
                .build();

        Application application = RuntimeEnvironment.application;
        ((TestGradesApplication) application).component(component);

        authFacade = component.authFacade();
    }

    @Test
    public void openDashboard_WithoutCachedAuthToken_ShouldRedirectToLogin() {
        // Set the user login verification to always fail
        when(authFacade.isLoggedIn()).thenReturn(Observable.just(false));
        controller = Robolectric.buildActivity(DashboardActivity.class);

        // Launch the dashboard activity, which checks for auth internally
        DashboardActivity activity = controller.create().get();

        // Verify the user is sent to the LoginActivity and the Dashboard
        // activity finishes without showing any dashboard content
        ShadowActivity shadow = shadowOf(activity);
        Intent expectedIntent = new Intent(activity, LoginActivity.class);
        assertThat(shadow.getNextStartedActivity()).isEqualTo(expectedIntent);
        assertThat(shadow.getContentView()).isNull();
        assertThat(activity).isFinishing();
    }

    @Test
    public void openDashboard_WithCachedAuthToken_ShouldShowView() {
        // Set the user login verification to always pass
        when(authFacade.isLoggedIn()).thenReturn(Observable.just(true));
        controller = Robolectric.buildActivity(DashboardActivity.class);

        // Launch the dashboard activity, which checks for auth internally
        DashboardActivity activity = controller.create().get();

        // Verify the user isn't redirected and is shown the dashboard
        // content in the same Dashboard activity
        ShadowActivity shadow = shadowOf(activity);
        assertThat(shadow.getNextStartedActivity()).isNull();
        assertThat(shadow.getContentView()).isVisible();
        assertThat(activity).isNotFinishing();
    }

}