package com.rothanak.gradeskinda;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rothanak.gradeskinda.data.auth.LocalAuthModule;
import com.rothanak.gradeskinda.mockserver.InstrumentationMockServer;
import com.rothanak.gradeskinda.mockserver.MockServerRule;
import com.rothanak.gradeskinda.page.DashboardPage;
import com.rothanak.gradeskinda.page.LoginPage;
import com.rothanak.gradeskinda.ui.login.LoginActivity;
import com.squareup.okhttp.HttpUrl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.plugins.RxJavaIdlingRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginResponse.failureResponse;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginResponse.successfulResponse;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginScenario.loginWith;
import static com.rothanak.gradeskinda.testutil.PageViewMatchers.ofPage;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class LoginAcceptanceTest {

    @Rule public MockServerRule mockServer = InstrumentationMockServer.getRule();
    @Rule public RxJavaIdlingRule rxJavaIdlingRule = new RxJavaIdlingRule();

    @Rule public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<LoginActivity>(LoginActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            // Configure the app to send requests to the mock server
            Context applicationContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
            HttpUrl endPoint = HttpUrl.parse("http://localhost:" + MockServerRule.DEFAULT_PORT);
            LocalAuthModule authModule = new LocalAuthModule(() -> endPoint);
            AppComponent component = DaggerAppComponent.builder()
                    .authModule(authModule)
                    .build();
            ((GradesApplication) applicationContext).component(component);
        }
    };

    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";

    @Test
    public void login_EnteringCredentialsGood_ShouldShowDashboard() {
        mockServer.given(loginWith(USERNAME, PASSWORD))
                .willServe(successfulResponse());

        DashboardPage dashboard = LoginPage.submitLogin(USERNAME, PASSWORD);

        onView(ofPage(dashboard)).check(matches(isDisplayed()));
    }

    @Test
    public void login_EnteringCredentialsBad_ShouldError() {
        mockServer.given(loginWith(USERNAME, PASSWORD))
                .willServe(failureResponse());

        DashboardPage dashboard = LoginPage.submitLogin(USERNAME, PASSWORD);

        onView(ofPage(dashboard)).check(doesNotExist());
        onView(withText(R.string.login_error_bad_credentials))
                .inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

}
