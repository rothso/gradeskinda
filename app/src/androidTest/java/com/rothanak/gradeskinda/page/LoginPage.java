package com.rothanak.gradeskinda.page;

import com.rothanak.gradeskinda.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class LoginPage implements Page {

    @Override
    public int viewId() {
        return R.id.login;
    }

    public static DashboardPage submitLogin(String username, String password) {
        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.submit_login)).perform(click());
        return new DashboardPage();
    }

}
