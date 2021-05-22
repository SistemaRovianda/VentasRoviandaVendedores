package com.example.ventasrovianda.login.presenter;

public interface LoginPresenterContract {
    void doLogin(final String email,final String password);
    void checkLogin();

    String checkLoginStr();
}
