package com.rothanak.gradeskinda.ui.dashboard;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.rothanak.gradeskinda.BuildConfig;
import com.rothanak.gradeskinda.ui.login.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static org.assertj.android.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DashboardActivityTest {

    private Application application = RuntimeEnvironment.application;
    private ActivityController<DashboardActivity> controller;

    @Test
    public void openDashboard_WithoutCachedAuthToken_RedirectsToLogin() {
        getPrefsEditor(application).putString("auth_token", null).apply();
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
        getPrefsEditor(application).putString("auth_token", "sometoken").apply();
        controller = Robolectric.buildActivity(DashboardActivity.class);

        DashboardActivity activity = controller.create().get();

        ShadowActivity shadow = shadowOf(activity);
        assertThat(shadow.getNextStartedActivity()).isNull();
        assertThat(shadow.getContentView()).isVisible();
        assertThat(activity).isNotFinishing();
    }

    private SharedPreferences.Editor getPrefsEditor(Application application) {
        return application.getSharedPreferences("gradeskinda", 0).edit();
    }

}