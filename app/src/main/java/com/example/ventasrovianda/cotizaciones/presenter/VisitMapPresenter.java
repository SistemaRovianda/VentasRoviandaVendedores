package com.example.ventasrovianda.cotizaciones.presenter;

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
import com.example.ventasrovianda.Utils.Constants;
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.ClientVisitDTO;
import com.example.ventasrovianda.cotizaciones.models.ClientVisitListItem;
import com.example.ventasrovianda.cotizaciones.view.VisitsMapView;
import com.example.ventasrovianda.cotizaciones.view.VisitsMapViewContract;
import com.example.ventasrovianda.cotizaciones.view.VisitsView;
import com.example.ventasrovianda.cotizaciones.view.VisitsViewContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VisitMapPresenter implements VisitMapPresenterContract{

    Context context;
    VisitsMapViewContract view;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url = Constants.URL;
    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;
    public VisitMapPresenter(Context context, VisitsMapView view){
        this.context=context;
        this.view=view;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

   /* @Override
    public void getClientsToVisit() {
        Map<String,String> headers = new HashMap<>();
        String uid = this.firebaseAuth.getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        GsonRequest<ClientVisitListItem[]> productsRoviandaGet = new GsonRequest<ClientVisitListItem[]>
                (url+"/rovianda/getcurrentvisits/clients-location?sellerUid="+uid+"&date="+dateParsed,ClientVisitListItem[].class,headers,
                        new Response.Listener<ClientVisitListItem[]>(){
                            @Override
                            public void onResponse(ClientVisitListItem[] response) {
                                //view.setClientsToVisit(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        if(error.networkResponse!=null && error.networkResponse.statusCode==409){
                            view.genericMessage("Error en visita","Ya registraste una visita a este cliente ó hay una visita en curso.");
                        }
                        //view.setClientVisits(null);
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
                return 1;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }*/

}
