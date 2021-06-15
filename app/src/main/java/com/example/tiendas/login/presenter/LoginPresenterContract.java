package com.example.tiendas.login.presenter;

public interface LoginPresenterContract {
    void doLogin(final String email,final String password);
    void checkLogin();

    String checkLoginStr();
}
