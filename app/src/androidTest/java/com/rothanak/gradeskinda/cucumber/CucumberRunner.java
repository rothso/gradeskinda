package com.rothanak.gradeskinda.cucumber;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.runner.MonitoringInstrumentation;

import cucumber.api.android.CucumberInstrumentationCore;

public class CucumberRunner extends MonitoringInstrumentation {

    private final CucumberInstrumentationCore instrumentation = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        instrumentation.create(bundle);
        start();
    }

    @Override
    public void onStart() {
        waitForIdleSync();
        instrumentation.start();
    }

    //@Override
    //public Application newApplication(@NonNull ClassLoader cl, String className, Context context)
    //        throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    //    // Return a custom GradesApplication that routes all network requests to LOCALHOST
    //    // so that server responses can be mocked and interactions can be verified.
    //    return super.newApplication(cl, CucumberApplication.class.getName(), context);
    //}

}
