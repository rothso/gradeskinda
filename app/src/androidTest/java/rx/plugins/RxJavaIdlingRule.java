package rx.plugins;

import android.support.test.espresso.Espresso;

import com.rothanak.gradeskinda.testutil.RxJavaIdlingHook;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Configures Espresso to wait for ongoing RxJava operations.
 */
public class RxJavaIdlingRule extends RxJavaPlugins implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                RxJavaPlugins.getInstance().reset();
                RxJavaPlugins.getInstance().registerSchedulersHook(RxJavaIdlingHook.get());
                Espresso.registerIdlingResources(RxJavaIdlingHook.get());

                base.evaluate();

                Espresso.unregisterIdlingResources(RxJavaIdlingHook.get());
            }
        };
    }

}
