package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.data.auth.AuthFacade;

public class LoginPresenter {

    private final AuthFacade authenticator;
    private View view; // TODO WeakReference

    public LoginPresenter(AuthFacade authenticator) {
        this.authenticator = authenticator;
    }

    public void verifyCredentials(String user, String pass) {
        authenticator.login(user, pass).subscribe(
                isSuccess -> {
                    if (isSuccess) {
                        view.gotoDashboard();
                    } else {
                        view.showBadCredentialsError();
                    }
                }
        );
    }

    public void attachView(View view) {
        this.view = view;
    }

    public interface View {

        void gotoDashboard();

        void showBadCredentialsError();

    }

}
