package com.example.ventasrovianda.home.presenter;

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
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.CounterTime;
import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.EatTimeRequest;
import com.example.ventasrovianda.Utils.Models.ErrorResponse;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class HomePresenter implements HomePresenterContract{

    Context context;
    HomeViewContract view;
    FirebaseAuth firebaseAuth;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url ="https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
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
    public void doLogout() {
        if(this.firebaseAuth!=null) {
            this.firebaseAuth.signOut();
        }
        view.goToLogin();
    }

    /*@Override
    public void UploadChanges(ModeOfflineSincronize modeOfflineSincronize) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String sellerId = firebaseAuth.getUid();
        GsonRequest<String> presentationsgGet = new GsonRequest<String>
                (url+"/rovianda/sincronize/"+sellerId, String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {

                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }   , Request.Method.POST,parser.toJson(modeOfflineSincronize)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }*/

    @Override
    public void sincronizeSales(List<ModeOfflineSM> ModeOfflineSMS, List<DebPayedRequest> debtsPayedRequest, List<DevolutionRequestServer> devolutionRequestServers,String sellerId) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        SincronizationNewVersionRequest sincronizationNewVersionRequest = new SincronizationNewVersionRequest();
        sincronizationNewVersionRequest.setSales(ModeOfflineSMS);
        sincronizationNewVersionRequest.setDebts(debtsPayedRequest);
        sincronizationNewVersionRequest.setDevolutions(devolutionRequestServers);
        GsonRequest<SincronizationResponse> presentationsgGet = new GsonRequest<SincronizationResponse>
                (url+"/rovianda/sincronize-single/sale?sellerId="+sellerId, SincronizationResponse.class,headers,
                        new Response.Listener<SincronizationResponse>(){
                            @Override
                            public void onResponse(SincronizationResponse response) {
                                view.hiddeNotificationSincronizastion();
                                view.completeSincronzation(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            view.showNotificationSincronization("Error al sincronizasr venta ");
                    }
                }   , Request.Method.POST,parser.toJson(sincronizationNewVersionRequest)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
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
    public void getStockOnline() {
        Map<String,String> headers = new HashMap<>();
        String sellerId = firebaseAuth.getUid();
        GsonRequest<ModeOfflineModel> getModeOffline = new GsonRequest<ModeOfflineModel>
                (url+"/rovianda/getstock/"+sellerId, ModeOfflineModel.class,headers,
                        new Response.Listener<ModeOfflineModel>(){
                            @Override
                            public void onResponse(ModeOfflineModel response) {
                                if(response!=null) {
                                    System.out.println("Se obtuvo respuesta");
                                    //view.setModeOffline(response);
                                }else{
                                    System.out.println("Error al obtener respuesta");
                                }
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Se obtuvo respuesta: "+error.getMessage());

                    }
                }   , Request.Method.GET,null
                );
        requestQueue.add(getModeOffline).setRetryPolicy(new RetryPolicy() {
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
        });
    }

/*
    @Override
    public void findUser(Integer userId) {
        Map<String,String> headers = new HashMap<>();
        GsonRequest<ClientDTO> productsRoviandaGet = new GsonRequest<ClientDTO>
                (url+"/rovianda/customer/client-key/"+userId,ClientDTO.class,headers,
                        new Response.Listener<ClientDTO>(){
                            @Override
                            public void onResponse(ClientDTO response) {
                                if(response!=null) {
                                    view.setClient(response);
                                }else{
                                    view.setErrorClientInput("Cliente no existe");
                                }
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setErrorClientInput("Cliente no existe");
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

    }*/



    /*@Override
    public void findProduct(String code) {
        Map<String,String> headers = new HashMap<>();
        String userId = firebaseAuth.getCurrentUser().getUid();
        GsonRequest<ProductRoviandaToSale> productsRoviandaGet = new GsonRequest<ProductRoviandaToSale>
                (url+"/rovianda/sales-product/"+userId+"/"+code,ProductRoviandaToSale.class,headers,
                        new Response.Listener<ProductRoviandaToSale>(){
                            @Override
                            public void onResponse(ProductRoviandaToSale response) {

                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setErrorProductkeyInput("No existe el producto");
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
    }*/

    @Override
    public void doSale(SaleDTO saleDTO) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String userId = firebaseAuth.getCurrentUser().getUid();
        saleDTO.setSellerId(userId);
        GsonRequest<SaleSuccess> saleRequets = new GsonRequest<SaleSuccess>
                (url+"/rovianda/sale",SaleSuccess.class,headers,
                        new Response.Listener<SaleSuccess>(){
                            @Override
                            public void onResponse(SaleSuccess  response) {
                                getTicket(response.getSaleId());
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode!=500) {
                            try {
                                String responseStr = new String(error.networkResponse.data, "UTF-8");
                                ErrorResponse errorResponse = parser.fromJson(responseStr, ErrorResponse.class);
                                view.saleError(errorResponse.getMsg());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }   , Request.Method.POST,parser.toJson(saleDTO)
                );

        saleRequets.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
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
    public void getEndDayTicket() {
        String userId = this.firebaseAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        System.out.println("Finded by: "+dateParsed);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/rovianda/day-ended/"+userId+"?date="+dateParsed,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        view.saleSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                System.out.println(error.networkResponse.statusCode);
                view.saleError("No se pudo obtener el ticket");
            }
        });
        requestQueue.add(stringRequest).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });;
    }


    /*int currentHours=0;
    int currentMinutes=0;
    int currentSeconds=0;

    @Override
    public void getCounterTimer(int intent) {
        Map<String,String> headers = new HashMap<>();
        String userId = firebaseAuth.getCurrentUser().getUid();
        GsonRequest<CounterTime> counteTimerRequest = new GsonRequest<CounterTime>
                (url+"/rovianda/seller/eat/time/"+userId,CounterTime.class,headers,
                        new Response.Listener<CounterTime>(){
                            @Override
                            public void onResponse(CounterTime response) {
                                if(intent==1) {
                                    currentMinutes=response.getMinutes();
                                    currentHours=response.getHours();
                                    currentSeconds=response.getSeconds();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                sleep(1000);
                                                getCounterTimer(intent+1);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                                }else if(intent==2) {
                                    if(response.getSeconds()==currentSeconds && response.getMinutes()==currentMinutes && response.getHours()==currentHours) {
                                        view.genericMessage("Alerta","Ya haz utilizado tu tiempo de comida");
                                        view.isCountingTime(3);
                                    }else{
                                        view.setCounterTimer(response);
                                        view.isCountingTime(2);
                                    }
                                }else if(intent==3){
                                    currentMinutes=response.getMinutes();
                                    currentHours=response.getHours();
                                    currentSeconds=response.getSeconds();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                sleep(1000);
                                                getCounterTimer(intent+1);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }else if(intent==4){
                                    if(response.getSeconds()==currentSeconds && response.getMinutes()==currentMinutes && response.getHours()==currentHours) {
                                        view.isCountingTime(3);
                                    }else{
                                        view.isCountingTime(2);
                                    }
                                }
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (intent == 1) {
                            view.confirmEatTime();

                        }
                        if(intent==3){
                            view.isCountingTime(1);
                        }
                    }
                }   , Request.Method.GET,null
                );
        requestQueue.add(counteTimerRequest).setRetryPolicy(new RetryPolicy() {
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

            }
        });
    }

    @Override
    public void startEatTime() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String userId = firebaseAuth.getCurrentUser().getUid();
        EatTimeRequest eatTimeRequest= new EatTimeRequest();
        eatTimeRequest.setSellerUid(userId);
        GsonRequest<String> saleRequets = new GsonRequest<String>
                (url+"/rovianda/seller/eat/time",String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String  response) {
                                CounterTime counterTime = new CounterTime();
                                counterTime.setHours(0);
                                counterTime.setMinutes(0);
                                counterTime.setSeconds(1);
                                view.setCounterTimer(counterTime);
                                view.isCountingTime(2);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        //view.genericMessage("Error tiempo comida","No se pudo hacer la venta");
                        view.isCountingTime(1);
                    }
                }   , Request.Method.POST,parser.toJson(eatTimeRequest)
                );

        saleRequets.setRetryPolicy(new RetryPolicy() {
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

            }
        })  ;
        requestQueue.add(saleRequets);

    }

    @Override
    public void endEatTime() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String userId = firebaseAuth.getCurrentUser().getUid();

        GsonRequest<String> saleRequets = new GsonRequest<String>
                (url+"/rovianda/seller/eat/"+userId,String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String  response) {
                                view.genericMessage("Completado","Ya haz utilizado tu tiempo de comida.");
                                view.isCountingTime(3);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        view.genericMessage("Error tiempo comida","No se pudo terminar el tiempo.");
                        view.isCountingTime(3);
                    }
                }   , Request.Method.PATCH,""
                );

        saleRequets.setRetryPolicy(new RetryPolicy() {
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

            }
        });
        requestQueue.add(saleRequets);
    }*/
}
