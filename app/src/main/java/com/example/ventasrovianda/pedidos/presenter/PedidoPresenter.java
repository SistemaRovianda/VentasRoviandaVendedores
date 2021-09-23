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
import com.android.volley.toolbox.StringRequest;
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.OrderDTO;
import com.example.ventasrovianda.Utils.Models.OrderDetails;
import com.example.ventasrovianda.Utils.Models.OrderDetailsToEdit;
import com.example.ventasrovianda.Utils.Models.OrderPresentationDetails;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;
import com.example.ventasrovianda.pedidos.view.PedidoView;
import com.example.ventasrovianda.pedidos.view.PedidoViewContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoPresenter implements PedidoPresenterContract{

    Context context;
    PedidoViewContract view;
    FirebaseAuth firebaseAuth;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url ="https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
    private RequestQueue requestQueue;

    public PedidoPresenter(Context context, PedidoView view){
        this.context = context;
        this.view = view;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getOrders(String uid) {
        Map<String,String> headers = new HashMap<>();

        GsonRequest<OrderDTO[]> productsRoviandaGet = new GsonRequest<OrderDTO[]>
                (url+"/rovianda/seller/orders/"+uid,OrderDTO[].class,headers,
                        new Response.Listener<OrderDTO[]>(){
                            @Override
                            public void onResponse(OrderDTO[] response) {
                                view.setOrders(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("No se pudo obtener las ordenes");
                    }
                }   , Request.Method.GET,null
                );
        requestQueue.add(productsRoviandaGet).setRetryPolicy(new RetryPolicy() {
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
    public void getOrderDetailsToEditToPrint(Long orderId) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/rovianda/order-print/"+orderId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Ticket: "+response);
                        view.printTicketOrder(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.genericMessage("No se pudo obtener el ticket","Verifique la conexión a internet.");
            }
        });
        requestQueue.add(stringRequest).setRetryPolicy(new RetryPolicy() {
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
    public void getDetailsPresentation(int orderId, int productId) {

        Map<String,String> headers = new HashMap<>();
        GsonRequest<OrderPresentationDetails[]> productsRoviandaGet = new GsonRequest<OrderPresentationDetails[]>
                (url+"/rovianda/seller/order/"+orderId+"/product/"+productId,OrderPresentationDetails[].class,headers,
                        new Response.Listener<OrderPresentationDetails[]>(){
                            @Override
                            public void onResponse(OrderPresentationDetails[] response) {
                                view.showPresetationDetails(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.genericMessage("Error","No se pudieron obtener detalles de las presentationes");
                    }
                }   , Request.Method.GET,null
                );
        requestQueue.add(productsRoviandaGet).setRetryPolicy(new RetryPolicy() {
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
        if(this.firebaseAuth!=null) {
            this.firebaseAuth.signOut();
        }
        view.goToLogin();
    }
}
