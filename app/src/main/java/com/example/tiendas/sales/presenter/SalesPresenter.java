package com.example.tiendas.sales.presenter;

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
import com.example.tiendas.Utils.GsonRequest;
import com.example.tiendas.Utils.Models.PayDebtsModel;
import com.example.tiendas.Utils.Models.SaleResponseDTO;
import com.example.tiendas.Utils.Models.TotalSoldedDTO;
import com.example.tiendas.sales.view.SaleViewContract;
import com.example.tiendas.sales.view.SalesView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SalesPresenter implements SalePresenterContract {

    Context context;
    SaleViewContract view;
    FirebaseAuth firebaseAuth;

    //JsonRequest
    private Cache cache;
    private Network network;
    private String url ="https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
    private RequestQueue requestQueue;
    private Gson parser;
    public SalesPresenter(Context context,SalesView view){
        this.view = view;
        this.context = context;

        cache = new DiskBasedCache(this.context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        this.parser = new Gson();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getAllSaleOfDay() {
        Map<String,String> headers = new HashMap<>();
        String userId = firebaseAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        GsonRequest<SaleResponseDTO[]> productsRoviandaGet = new GsonRequest<SaleResponseDTO[]>
                (url+"/rovianda/sales-history/"+userId+"?date="+dateParsed,SaleResponseDTO[].class,headers,
                        new Response.Listener<SaleResponseDTO[]>(){
                            @Override
                            public void onResponse(SaleResponseDTO[] response) {
                                view.setSalesOfDay(response);
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
    public void getAllSaleDebtsOfDay() {
        Map<String,String> headers = new HashMap<>();
        String userId = firebaseAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        GsonRequest<SaleResponseDTO[]> productsRoviandaGet = new GsonRequest<SaleResponseDTO[]>
                (url+"/rovianda/seller-debts/"+userId,SaleResponseDTO[].class,headers,
                        new Response.Listener<SaleResponseDTO[]>(){
                            @Override
                            public void onResponse(SaleResponseDTO[] response) {
                                view.setSalesDebtsOfDay(response);
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
    public void cancelSale(Long saleId) {
        view.setLoading(true);
        Map<String,String> headers = new HashMap<>();
        GsonRequest<String> productsRoviandaGet = new GsonRequest<String>
                (url+"/rovianda/cancel-sale/"+saleId,String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                getAllSaleOfDay();
                                view.setLoading(false);
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("No se pudo obtener las ordenes");
                        view.setLoading(false);
                    }
                }   , Request.Method.PUT,null
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
    public void cancelSaleOffline(String folio) {
        view.cancelSale(folio);
    }

    @Override
    public void getTicket(Long ticketId){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/rovianda/sale-ticket/"+ticketId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        view.reprintTicket(response);
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
    public void getResguardedTicket(){
        String userId = this.firebaseAuth.getCurrentUser().getUid();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/rovianda/seller-resguarded/"+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        view.printTiket(response);
                        view.reprintTicket(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.genericMessage("No se pudo obtener el resguardo","Verifique la conexión a internet.");
            }
        });
        requestQueue.add(stringRequest).setRetryPolicy(new RetryPolicy() {
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
    public void verifyConnectionPrinterToGetTicket(Long ticketId) {
        view.checkPrinterConnection(ticketId);
    }

    @Override
    public void verifyConnectionPrinterToGetTicketOffline(String folio) {
        view.checkPrinterConnectionOffline(folio);
    }

    @Override
    public void logout() {
        this.firebaseAuth.signOut();
        view.goToLogin();
    }

    @Override
    public void doPayDebt(Long saleId, PayDebtsModel payDebtsModel) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        GsonRequest<String> saleRequets = new GsonRequest<String>
                (url+"/rovianda/seller-debts/"+saleId,String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String  response) {
                                getTicket(saleId);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode!=500) {

                                view.genericMessage("Error de red","Intente más tarde");
                        }

                    }
                }   , Request.Method.POST,parser.toJson(payDebtsModel)
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

    @Override
    public void checkPayDeb(SaleResponseDTO sale) {
            view.checkPaydeb(sale);
    }

    @Override
    public void reprintPaydeb(SaleResponseDTO sale) {
        view.printTicketSale(sale.getFolio());
    }

    @Override
    public void checkAccumulated() {
        Map<String,String> headers = new HashMap<>();
        String userId = firebaseAuth.getCurrentUser().getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        GsonRequest<TotalSoldedDTO> getTotalAcumulated = new GsonRequest<TotalSoldedDTO>
                (url+"/rovianda/get-status/sales/"+userId+"?date="+dateParsed, TotalSoldedDTO.class,headers,
                        new Response.Listener<TotalSoldedDTO>(){
                            @Override
                            public void onResponse(TotalSoldedDTO response) {
                                view.setAcumulated(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("No se pudo obtener las ordenes");
                    }
                }   , Request.Method.GET,null
                );
        requestQueue.add(getTotalAcumulated).setRetryPolicy(new RetryPolicy() {
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
