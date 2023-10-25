package com.example.ventasrovianda.login.view;

import java.io.File;

public interface LoginViewPresenter {
    void goToHome(String nameUser,String uid);
    void showErrors(String msg);

    void setEmailInputError(String msg);
    void setPasswordInputError(String msg);
    String getEmailInputText();
    String getPasswordText();
    void setStatusLogin(Boolean isLoading);
    void disableButtonLogin(Boolean disable);
    void setStatusConnectionServer(Boolean statusConnectionServer);
}
