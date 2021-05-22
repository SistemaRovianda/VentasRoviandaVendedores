package com.example.ventasrovianda.pedidos.presenter;

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
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.OrderDetailsToEdit;
import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;
import com.example.ventasrovianda.Utils.Models.UpdateOrderRequest;
import com.example.ventasrovianda.pedidos.view.PedidoDetails;
import com.example.ventasrovianda.pedidos.view.PedidoDetailsContract;
import com.example.ventasrovianda.pedidos.view.PedidoForm;
import com.example.ventasrovianda.pedidos.view.PedidoFormContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoDetailsPresenter implements PedidoDetailsPresentarContract {

    Context context;
    PedidoDetailsContract view;
    FirebaseAuth firebaseAuth;
    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url ="https://fe6954c7da75.ngrok.io/sistema-rovianda/us-central1/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
    private RequestQueue requestQueue;
    public PedidoDetailsPresenter(Context context, PedidoDetails view){
        this.view = view;
        this.context=context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }

    @Override
    public void getOrderDetailsToEdit(Long orderId) {

        Map<String, String> headers = new HashMap<>();

        GsonRequest<OrderDetailsToEdit[]> getOrderDetails = new GsonRequest<OrderDetailsToEdit[]>
                (url + "/rovianda/order-details/" + orderId, OrderDetailsToEdit[].class, headers,
                        new Response.Listener<OrderDetailsToEdit[]>() {
                            @Override
                            public void onResponse(OrderDetailsToEdit[] response) {
                                view.setDetails(response);
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        System.out.println("ERROR: al cargar la orden");
                    }
                }, Request.Method.GET, null
                );
        requestQueue.add(getOrderDetails).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

    }

    @Override
    public void udpateOrderDetails(List<UpdateOrderRequest> request,Long orderId) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        GsonRequest<String> orderUpdateRequest = new GsonRequest<String>
                (url + "/rovianda/order-update/" + orderId, String.class, headers,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                view.goBack();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR: al cargar la orden");
                    }
                }, Request.Method.PUT, parser.toJson(request)
                );
        requestQueue.add(orderUpdateRequest).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
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
