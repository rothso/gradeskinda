package com.rothanak.gradeskinda.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.rothanak.gradeskinda.R;
import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.ui.login.LoginActivity;

import javax.inject.Inject;

import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity {

    @Inject AuthFacade authFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO dagger injection
        // TODO mvp pattern

        boolean loggedIn = authFacade.isLoggedIn().toBlocking().first();

        if (!loggedIn) {
            Timber.d("No cached auth token, redirecting to login.");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Timber.d("Found auth token, showing view.");
            setContentView(R.layout.activity_dashboard);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

}
