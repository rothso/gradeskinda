package com.rothanak.gradeskinda.ui.dashboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.rothanak.gradeskinda.R;
import com.rothanak.gradeskinda.data.auth.AuthFacade;

import javax.inject.Inject;

public class DashboardActivity extends AppCompatActivity {

    @Inject AuthFacade authFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

}
