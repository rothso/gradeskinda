package com.rothanak.gradeskinda.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rothanak.gradeskinda.R;
import com.rothanak.gradeskinda.data.auth.AuthFacade;
import com.rothanak.gradeskinda.ui.dashboard.DashboardActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Single;

public class LoginActivity extends AppCompatActivity implements LoginPresenter.View {

    @Bind(R.id.username) EditText usernameField;
    @Bind(R.id.password) EditText passwordField;
    @Bind(R.id.submit_login) Button loginButton;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // TODO: 9/26/15 refactor
        AuthFacade authenticator = new AuthFacade() {
            @Override public Observable<Boolean> isLoggedIn() {
                return null;
            }

            @Override public Single<Boolean> login(String username, String password) {
                return Single.just(username.equals("Username") && password.equals("Password"));
            }
        };
        presenter = new LoginPresenter(authenticator);
        presenter.attachView(this);

        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            presenter.verifyCredentials(username, password);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override public void gotoDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override public void showBadCredentialsError() {
        Toast.makeText(this, "The username or password is invalid.", Toast.LENGTH_SHORT).show();
    }
}
