package com.example.ventasrovianda.login.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.example.ventasrovianda.Utils.Constants;
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.ModeOfflineNewVersion;
import com.example.ventasrovianda.Utils.Models.ProductPresentation;
import com.example.ventasrovianda.Utils.Models.Token;
import com.example.ventasrovianda.Utils.Models.UserDetails;
import com.example.ventasrovianda.login.view.LoginView;
import com.example.ventasrovianda.login.view.LoginViewPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPresenter implements LoginPresenterContract{
    Context context;
    LoginViewPresenter view;
    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url = Constants.URL;
    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;
    private FirebaseMessaging fMsg;
    public LoginPresenter(Context context, LoginView loginView){
        this.context=context;
        this.view=loginView;
        this.firebaseAuth = FirebaseAuth.getInstance();
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        this.fMsg = FirebaseMessaging.getInstance();

        parser = new Gson();
    }

    @Override
    public void doLogin() {
        Boolean mustDoLogin = validateInputs();
        if(mustDoLogin) {
            view.setStatusLogin(true);
            String emailStr = view.getEmailInputText();
            String passwordStr = view.getPasswordText();

                this.firebaseAuth.signInWithEmailAndPassword(emailStr.toLowerCase().trim(), passwordStr.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            getDetailsOfUser(task.getResult().getUser().getUid());
                        } else {
                            view.showErrors("Error con las credenciales");
                            view.setStatusLogin(false);
                        }
                    }
                });

        }else{
            view.setStatusLogin(false);
        }
    }

    void getDetailsOfUser(String uid){
        Map<String,String> headers = new HashMap<>();
        GsonRequest<UserDetails> presentationsgGet = new GsonRequest<UserDetails>
                (url+"/rovianda/user/"+uid, UserDetails.class,headers,
                        new Response.Listener<UserDetails>(){
                            @Override
                            public void onResponse(UserDetails response) {
                                view.setStatusLogin(false);
                                if(response.getRol().equals("SALESUSER")){
                                    sendToken(uid);
                                    view.disableButtonLogin(true);
                                    view.goToHome(response.getName(),uid);
                                }else{
                                    view.showErrors("Usuario no autorizado");
                                }

                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setStatusLogin(false);
                        view.showErrors("El usuario no existe en el sistema");
                    }
                }   , Request.Method.GET,null
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    @Override
    public void checkLogin() {
        FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            getDetailsOfUser(firebaseUser.getUid());
        }
    }

    @Override
    public String checkLoginStr() {
        return (this.firebaseAuth.getCurrentUser()!=null)?this.firebaseAuth.getUid():null;
    }

    @Override
    public void sendToken(String uid) {
        this.fMsg.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    Map<String,String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    GsonRequest<String> tokenSended = new GsonRequest<String>
                            (url+"/rovianda/set-token/"+uid, String.class,headers,
                                    new Response.Listener<String>(){
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println("Token registrado");
                                        }

                                    },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }   , Request.Method.PUT,"{\"token\":\""+task.getResult()+"\""+"}"
                            );
                    requestQueue.add(tokenSended).setRetryPolicy(new RetryPolicy() {
                        @Override
                        public int getCurrentTimeout() {
                            return 15000;
                        }

                        @Override
                        public int getCurrentRetryCount() {
                            return 0;
                        }

                        @Override
                        public void retry(VolleyError error) throws VolleyError {

                        }
                    });
                }
            }
        });
    }

    @Override
    public Boolean validateInputs() {
        String email = view.getEmailInputText();
        String password = view.getPasswordText();
        if(email.isEmpty()) {view.setEmailInputError("Campo Obligatorio"); return false;}
        if(password.isEmpty()) {view.setPasswordInputError("Campo Obligatorio"); return false;}
        return true;
    }

    @Override
    public void checkCommunicationToServer() {

        Map<String,String> headers = new HashMap<>();
        GsonRequest<ModeOfflineNewVersion> ping = new GsonRequest<ModeOfflineNewVersion>(
                url + "/rovianda/ping", ModeOfflineNewVersion.class, headers, new Response.Listener<ModeOfflineNewVersion>() {
            @Override
            public void onResponse(ModeOfflineNewVersion response) {

                view.setStatusConnectionServer(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.setStatusConnectionServer(false);
            }
        },Request.Method.GET,null
        );
        requestQueue.add(ping).setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
