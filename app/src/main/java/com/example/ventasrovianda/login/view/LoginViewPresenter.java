package com.example.ventasrovianda.login.view;

import java.io.File;

public interface LoginViewPresenter {
    void goToHome(String nameUser);
    void showErrors(String msg);

    void setEmailInputError(String msg);
    void setPasswordInputError(String msg);
    void checkSincronizedDataExist();
    String readFileFromPath(File file);
}
