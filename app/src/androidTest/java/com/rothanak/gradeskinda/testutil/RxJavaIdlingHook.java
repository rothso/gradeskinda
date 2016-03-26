package com.rothanak.gradeskinda.testutil;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

import rx.functions.Action0;
import rx.plugins.RxJavaSchedulersHook;
import timber.log.Timber;

/**
 * Allows Espresso to recognize scheduled RxJava operations as idling resources.
 * See discussion: https://github.com/stablekernel/RxEspresso/issues/4#issuecomment-162880275
 *
 * @author Thomas Keller https://github.com/tommyd3mdi
 */
public class RxJavaIdlingHook extends RxJavaSchedulersHook implements IdlingResource {

    private static final String TAG = RxJavaIdlingHook.class.getSimpleName();

    private static RxJavaIdlingHook INSTANCE;

    private final AtomicInteger scheduledActions = new AtomicInteger(0);
    private ResourceCallback resourceCallback;

    private RxJavaIdlingHook() {
    }

    public static RxJavaIdlingHook get() {
        if (INSTANCE == null) {
            INSTANCE = new RxJavaIdlingHook();
            Espresso.registerIdlingResources(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public Action0 onSchedule(final Action0 action) {
        // This is a dirty hack: Since our implementation does not know from where it was scheduled, it also does not
        // know whether or not our scheduled action will return immediately or block / recursively schedule itself again.
        //
        // The only way to kind-of getting this information is by looking at the current stack trace and check whether
        // we have been scheduledPeriodically; in this case we skip schedule counting and just return the action undecorated.
        if (scheduledPeriodically()) {
            return action;
        }

        int current = scheduledActions.incrementAndGet();
        Timber.tag(TAG).d("Scheduling action %s (%d)", action, current);
        return () -> {
            try {
                action.call();
            } finally {
                int remaining = scheduledActions.decrementAndGet();
                Timber.tag(TAG).d("Action %s executed (%d)", action, remaining);
                if (remaining == 0) {
                    resourceCallback.onTransitionToIdle();
                }
            }
        };
    }

    private boolean scheduledPeriodically() {
        for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
            if (el.getMethodName().equals("schedulePeriodically")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public boolean isIdleNow() {
        return scheduledActions.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}
