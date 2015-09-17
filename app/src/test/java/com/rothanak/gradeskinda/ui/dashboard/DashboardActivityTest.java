package com.rothanak.gradeskinda.ui.dashboard;

import android.app.Application;
import android.content.Intent;

import com.rothanak.gradeskinda.AppComponent;
import com.rothanak.gradeskinda.BuildConfig;
import com.rothanak.gradeskinda.DaggerAppComponent;
import com.rothanak.gradeskinda.TestGradesApplication;
import com.rothanak.gradeskinda.data.auth.AuthResolver;
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

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DashboardActivityTest {

    private Application application = RuntimeEnvironment.application;
    private ActivityController<DashboardActivity> controller;

    private AuthResolver authResolver;

    @Before
    public void setUp() {
        final AppComponent component = DaggerAppComponent.builder()
                .authModule(new MockAuthModule())
                .build();

        ((TestGradesApplication) application).component(component);
        authResolver = component.authResolver();
    }

    @Test
    public void openDashboard_WithoutCachedAuthToken_RedirectsToLogin() {
        when(authResolver.getAuthToken()).thenReturn(null);
        controller = Robolectric.buildActivity(DashboardActivity.class);

        DashboardActivity activity = controller.create().get();

        ShadowActivity shadow = shadowOf(activity);
        Intent expectedIntent = new Intent(activity, LoginActivity.class);
        assertThat(shadow.getNextStartedActivity()).isEqualTo(expectedIntent);
        assertThat(shadow.getContentView()).isNull();
        assertThat(activity).isFinishing();
    }

    @Test
    public void openDashboard_WithCachedAuthToken_ShowsView() {
        when(authResolver.getAuthToken()).thenReturn("DummyToken");
        controller = Robolectric.buildActivity(DashboardActivity.class);

        DashboardActivity activity = controller.create().get();

        ShadowActivity shadow = shadowOf(activity);
        assertThat(shadow.getNextStartedActivity()).isNull();
        assertThat(shadow.getContentView()).isVisible();
        assertThat(activity).isNotFinishing();
    }

}