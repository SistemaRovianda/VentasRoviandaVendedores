package com.example.ventasrovianda.login.presenter;

import com.example.ventasrovianda.Utils.Models.Token;

public interface LoginPresenterContract {
    void doLogin(final String email,final String password);
    void checkLogin();

    String checkLoginStr();
    //void setToken(String uid);
    //void sendToken(String uid, Token token);
}
