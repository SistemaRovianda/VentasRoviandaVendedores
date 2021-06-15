package com.example.tiendas.login.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.example.tiendas.Utils.GsonRequest;
import com.example.tiendas.Utils.Models.UserDetails;
import com.example.tiendas.login.view.LoginView;
import com.example.tiendas.login.view.LoginViewPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LoginPresenter implements LoginPresenterContract{
    Context context;
    LoginViewPresenter view;
    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url ="https://us-central1-sistema-rovianda.cloudfunctions.net/app";;//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;
    public LoginPresenter(Context context, LoginView loginView){
        this.context=context;
        this.view=loginView;
        this.firebaseAuth = FirebaseAuth.getInstance();
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
    }

    @Override
    public void doLogin(final String email,final String password) {
        if(email.isEmpty()){
            view.setEmailInputError("Email no puede estar vacio");
            return;
        }
        if(password.isEmpty()){
            view.setPasswordInputError("Password no puede estar vacio");
            return;
        }
        System.out.println(email.toLowerCase().trim());
        System.out.println(password.toLowerCase().trim());
        this.firebaseAuth.signInWithEmailAndPassword(email.toLowerCase().trim(),password.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete() && task.isSuccessful()){
                    getDetailsOfUser(task.getResult().getUser().getUid());

                }else{
                    view.showErrors("Error con las credenciales");
                }
            }
        });
    }

    void getDetailsOfUser(String uid){
        Map<String,String> headers = new HashMap<>();
        GsonRequest<UserDetails> presentationsgGet = new GsonRequest<UserDetails>
                (url+"/rovianda/user/"+uid, UserDetails.class,headers,
                        new Response.Listener<UserDetails>(){
                            @Override
                            public void onResponse(UserDetails response) {
                                if(response.getRol().equals("SALESUSER") || response.getRol().equals("SUCURSAL")
                                || response.getRol().equals("SUCURSAL_PLUS") || response.getRol().equals("SUPERMARKET")){
                                    view.goToHome(response.getName());
                                }else{
                                    view.showErrors("Usuario no autorizado");
                                }
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
}
