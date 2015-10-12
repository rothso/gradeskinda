package com.rothanak.gradeskinda.cucumber;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rothanak.gradeskinda.R;
import com.rothanak.gradeskinda.ui.login.LoginActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginSteps {

    @Rule public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    private String username;
    private String password;

    public LoginSteps() {
        rule.launchActivity(new Intent(Intent.ACTION_MAIN));
    }

    @Given("^(.*) has an account$")
    public void givenUserHasAccount(String name) {
        username = name;
        password = "correct horse battery staple";
    }

    @When("^he logs in$")
    public void whenLogIn() {
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.submit_login)).perform(click());
    }

    @Then("^he should see his grades$")
    public void thenShouldSeeGrades() {
        onView(withText(R.string.hello_world)).check(matches(isDisplayed()));
    }

}
