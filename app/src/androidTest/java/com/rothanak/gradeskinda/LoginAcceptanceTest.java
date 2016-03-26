package com.rothanak.gradeskinda;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rothanak.gradeskinda.data.auth.LocalAuthModule;
import com.rothanak.gradeskinda.mockserver.InstrumentationMockServer;
import com.rothanak.gradeskinda.mockserver.MockServerRule;
import com.rothanak.gradeskinda.testutil.RxJavaIdlingHook;
import com.rothanak.gradeskinda.ui.login.LoginActivity;
import com.squareup.okhttp.HttpUrl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.plugins.RxJavaPlugins;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginResponse.successfulResponse;
import static com.rothanak.gradeskinda.mockserver.scenario.login.LoginScenario.loginWith;

@RunWith(AndroidJUnit4.class)
public class LoginAcceptanceTest {

    @Rule public MockServerRule mockServer = InstrumentationMockServer.getRule();

    @Rule public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<LoginActivity>(LoginActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            RxJavaPlugins.getInstance().registerSchedulersHook(RxJavaIdlingHook.get());

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

    public static final String GOOD_USERNAME = "Username";
    public static final String GOOD_PASSWORD = "Password";

    @BeforeClass
    public static void setUp() {
        Espresso.registerIdlingResources(RxJavaIdlingHook.get());
    }

    @Test
    public void login_WithGoodCredentials_ShouldShowDashboard() {
        mockServer
                .given(loginWith(GOOD_USERNAME, GOOD_PASSWORD))
                .willServe(successfulResponse());

        // todo bundle into page object -> LoginPage
        onView(withId(R.id.username)).perform(typeText(GOOD_USERNAME));
        onView(withId(R.id.password)).perform(typeText(GOOD_PASSWORD));
        onView(withId(R.id.submit_login)).perform(click());

        // todo refine
        onView(withId(R.id.dashboard)).check(matches(isDisplayed()));
    }

    @AfterClass
    public static void tearDown() {
        Espresso.unregisterIdlingResources(RxJavaIdlingHook.get());
    }

}
