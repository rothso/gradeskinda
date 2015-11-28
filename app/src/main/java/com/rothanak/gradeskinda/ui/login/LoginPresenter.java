package com.rothanak.gradeskinda.ui.login;

import com.rothanak.gradeskinda.domain.interactor.LoginInteractor;
import com.rothanak.gradeskinda.domain.model.Credentials;

import javax.inject.Inject;

public class LoginPresenter {

    private final LoginInteractor interactor;
    private View view; // TODO WeakReference

    @Inject
    public LoginPresenter(LoginInteractor interactor) {
        this.interactor = interactor;
    }

    public void verifyCredentials(String user, String pass) {
        Credentials credentials = new Credentials(user, pass);
        interactor.login(credentials).subscribe(
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
