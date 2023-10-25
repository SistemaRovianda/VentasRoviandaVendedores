package com.example.ventasrovianda.home.presenter;

import android.content.Context;
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
import com.android.volley.toolbox.StringRequest;
import com.example.ventasrovianda.Utils.Constants;
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.AddressCoordenatesRequest;
import com.example.ventasrovianda.Utils.Models.AddressCoordenatesResponse;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.CounterTime;
import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.EatTimeRequest;
import com.example.ventasrovianda.Utils.Models.ErrorResponse;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ModeOfflineNewVersion;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleSuccess;
import com.example.ventasrovianda.Utils.Models.SincronizationNewVersionRequest;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.Models.SincronizeSingleSaleSuccess;
import com.example.ventasrovianda.home.view.HomeView;
import com.example.ventasrovianda.home.view.HomeViewContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

public class HomePresenter implements HomePresenterContract{

    Context context;
    HomeViewContract view;
    FirebaseAuth firebaseAuth;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;

    private String url = Constants.URL;
    private RequestQueue requestQueue;
    public HomePresenter(Context context, HomeView view){
        this.context=context;
        this.view=view;
        this.firebaseAuth = FirebaseAuth.getInstance();

        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }

    @Override
    public void getAddressByCoordenates(Double latitude,Double longitude) {
        AddressCoordenatesRequest addressCoordenatesRequest = new AddressCoordenatesRequest();
        addressCoordenatesRequest.setLatitude(latitude);
        addressCoordenatesRequest.setLongitude(longitude);
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<AddressCoordenatesResponse> addressCoordenates = new GsonRequest<AddressCoordenatesResponse>
                (url+"/rovianda/geocodingaddress", AddressCoordenatesResponse.class,headers,
                        new Response.Listener<AddressCoordenatesResponse>(){
                            @Override
                            public void onResponse(AddressCoordenatesResponse response) {
                                view.setAddressForConfirm(response,latitude,longitude);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                                view.onDialogNegativeClick();
                    }
                }   , Request.Method.POST,this.parser.toJson(addressCoordenatesRequest)
                );
        requestQueue.add(addressCoordenates).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.onDialogNegativeClick();
            }
        });
    }

    @Override
    public void doLogout() {
        if(this.firebaseAuth!=null) {
            this.firebaseAuth.signOut();
        }
        view.goToLogin();
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

    @Override
    public void sincronizeSales(List<ModeOfflineSM> ModeOfflineSMS, List<DebPayedRequest> debtsPayedRequest, List<DevolutionRequestServer> devolutionRequestServers,List<String> foliosWithoutPayment,String sellerId) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        SincronizationNewVersionRequest sincronizationNewVersionRequest = new SincronizationNewVersionRequest();
        sincronizationNewVersionRequest.setSales(ModeOfflineSMS);
        sincronizationNewVersionRequest.setDebts(debtsPayedRequest);
        sincronizationNewVersionRequest.setDevolutions(devolutionRequestServers);
        sincronizationNewVersionRequest.setDebtsOlded(foliosWithoutPayment);
        GsonRequest<SincronizationResponse> presentationsgGet = new GsonRequest<SincronizationResponse>
                (url+"/rovianda/sincronize-single/v2/sale?sellerId="+sellerId, SincronizationResponse.class,headers,
                        new Response.Listener<SincronizationResponse>(){
                            @Override
                            public void onResponse(SincronizationResponse response) {
                                view.hiddeNotificationSincronizastion();
                                view.completeSincronzation(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            view.showNotificationSincronization("Error al sincronizar venta ");
                    }
                }   , Request.Method.POST,parser.toJson(sincronizationNewVersionRequest)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new DefaultRetryPolicy(180000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }






    void getTicket(int ticketId){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/rovianda/sale-ticket/"+ticketId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        view.saleSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.saleError("No se pudo obtener el ticket");
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
    public void sendEndDayRecord(String date,String uid) {
                    Map<String,String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    GsonRequest<String> tokenSended = new GsonRequest<String>
                            (url+"/rovianda/endday/record?sellerId="+uid, String.class,headers,
                                    new Response.Listener<String>(){
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println("EndDay registrado");
                                        }

                                    },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }   , Request.Method.POST,"{\"endDay\":\""+date+"\""+"}"
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
