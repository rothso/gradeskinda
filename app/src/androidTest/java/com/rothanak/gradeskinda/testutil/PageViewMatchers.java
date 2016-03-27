package com.rothanak.gradeskinda.testutil;

import android.view.View;

import com.rothanak.gradeskinda.page.Page;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

public final class PageViewMatchers {

    public static Matcher<View> ofPage(Page page) {
        return withId(page.viewId());
    }

}
