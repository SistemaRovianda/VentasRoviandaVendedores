package com.example.tiendas.clients.presenter;

import android.content.Context;

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
import com.example.tiendas.Utils.Models.ClientDTO;
import com.example.tiendas.clients.view.ClientView;
import com.example.tiendas.clients.view.ClientViewContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPresenter implements ClientPresenterContract {

    Context context;
    ClientViewContract view;
    FirebaseAuth firebaseAuth;
    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url ="https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
    private RequestQueue requestQueue;
    public ClientPresenter(Context context, ClientView view){
        this.context = context;
        this.view = view;
        this.firebaseAuth = FirebaseAuth.getInstance();

        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }


    @Override
    public void getClients() {
        Map<String,String> headers = new HashMap<>();
        GsonRequest<ClientDTO[]> presentationsgGet = new GsonRequest<ClientDTO[]>
                (url+"/rovianda/seller-clients/"+this.firebaseAuth.getCurrentUser().getUid(), ClientDTO[].class,headers,
                        new Response.Listener<ClientDTO[]>(){
                            @Override
                            public void onResponse(ClientDTO[] response) {
                               List<ClientDTO> clients = new ArrayList<>();
                               for(int i=0;i<response.length;i++){
                                   clients.add(response[i]);
                               }
                               view.setClients(clients);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setClients(new ArrayList<>());
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
    public void logout() {
        this.firebaseAuth.signOut();
        view.goToLogin();
    }
}
