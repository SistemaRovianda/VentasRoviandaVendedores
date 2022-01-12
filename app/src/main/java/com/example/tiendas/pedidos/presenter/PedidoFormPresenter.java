package com.example.tiendas.pedidos.presenter;

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
import com.example.tiendas.Utils.Models.OrderDTO;
import com.example.tiendas.Utils.Models.ProductRoviandaToSale;
import com.example.tiendas.pedidos.view.PedidoForm;
import com.example.tiendas.pedidos.view.PedidoFormContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PedidoFormPresenter implements PedidoFormPresenterContract{

    Context context;
    PedidoFormContract view;
    FirebaseAuth firebaseAuth;
    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url ="https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
    private RequestQueue requestQueue;

    public PedidoFormPresenter(Context context, PedidoForm view){
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
    public void findProduct(String code,String quantity) {

            Map<String, String> headers = new HashMap<>();
            String userId = firebaseAuth.getCurrentUser().getUid();
            GsonRequest<ProductRoviandaToSale> productsRoviandaGet = new GsonRequest<ProductRoviandaToSale>
                    (url + "/rovianda/inve-product/" + code, ProductRoviandaToSale.class, headers,
                            new Response.Listener<ProductRoviandaToSale>() {
                                @Override
                                public void onResponse(ProductRoviandaToSale response) {
                                    view.addProductToPedido(response);
                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            view.setProductCodeError("No existe el producto");
                        }
                    }, Request.Method.GET, null
                    );
            requestQueue.add(productsRoviandaGet).setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 30000;
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
    public void registerOrder(OrderDTO orderDTO) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String userId = firebaseAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateParsed = dateFormat.format(calendar.getTime());
        orderDTO.setDate(dateParsed);
        GsonRequest<String> saleRequets = new GsonRequest<String>
                (url+"/rovianda/seller/order/"+userId,String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String  response) {
                                view.registerSuccess();
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.registerError();
                        view.genericMessage("Error","Revise su conexión a internet");
                    }
                }   , Request.Method.POST,parser.toJson(orderDTO)
                );

        saleRequets.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 180000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        })  ;
        requestQueue.add(saleRequets);
    }
}
