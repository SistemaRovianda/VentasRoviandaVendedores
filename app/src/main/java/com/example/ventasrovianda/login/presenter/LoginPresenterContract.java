package com.example.ventasrovianda.login.presenter;

import com.example.ventasrovianda.Utils.Models.Token;

public interface LoginPresenterContract {
    void doLogin();
    void checkLogin();

    String checkLoginStr();
    //void setToken(String uid);
    //void sendToken(String uid, Token token);
    Boolean validateInputs();

    void sendToken(String token);
    void checkCommunicationToServer();
}
