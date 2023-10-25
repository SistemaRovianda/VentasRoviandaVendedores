package com.example.ventasrovianda.sales.presenter;

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
import com.example.ventasrovianda.Utils.Constants;
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.CancelRequestSincronizationResponse;
import com.example.ventasrovianda.Utils.Models.ErrorResponse;
import com.example.ventasrovianda.Utils.Models.ModeOfflineNewVersion;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.OrderDTO;
import com.example.ventasrovianda.Utils.Models.PayDebtsModel;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleResponseDTO;
import com.example.ventasrovianda.Utils.Models.SaleSuccess;
import com.example.ventasrovianda.Utils.Models.SincronizationNewVersionRequest;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.Models.TotalSoldedDTO;
import com.example.ventasrovianda.sales.view.SaleViewContract;
import com.example.ventasrovianda.sales.view.SalesView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
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
    private String url = Constants.URL;
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
        if(this.firebaseAuth!=null){
            this.firebaseAuth.signOut();
        }
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
        requestQueue.add(ping).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void sendCancelationRequest(ModeOfflineSM cancelation) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        GsonRequest<CancelRequestSincronizationResponse> cancelRequest = new GsonRequest<CancelRequestSincronizationResponse>
                (url+"/rovianda/request-cancelations?sellerId="+cancelation.getSellerId(), CancelRequestSincronizationResponse.class,headers,
                        new Response.Listener<CancelRequestSincronizationResponse>(){
                            @Override
                            public void onResponse(CancelRequestSincronizationResponse response) {
                                view.markSaleSincronized(cancelation.getFolio());
                                view.modalInfo("Se envió la cancelación al Sistema Rovianda, espere a su aprobación, la sincronización se hará en automatico dentro de unos minutos.");
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                                System.out.println("Error en request: "+error.getMessage());
                                view.modalInfo("No fue posible enviar la cancelación debido a intermitencia de Red, la solicitud se enviará cuando la red sea mas estable o cuando haya buena señal.");
                    }
                }   , Request.Method.POST,parser.toJson(cancelation)
                );
        requestQueue.add(cancelRequest).setRetryPolicy(new RetryPolicy() {
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
    public void verifyCancelation(String folio) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        GsonRequest<CancelRequestSincronizationResponse> cancelRequest = new GsonRequest<CancelRequestSincronizationResponse>
                (url+"/rovianda/check-cancelations?folio="+folio, CancelRequestSincronizationResponse.class,headers,
                        new Response.Listener<CancelRequestSincronizationResponse>(){
                            @Override
                            public void onResponse(CancelRequestSincronizationResponse response) {
                                view.closeModalVerifyCancelationRequest();
                                switch(response.getRequestStatus()){
                                    case "ACCEPTED":
                                        view.updateStatusCancelation(folio,"CANCELED");
                                        view.modalInfo("Se consultó la solicitud y ha sido aprobada.");
                                        break;
                                    case "PENDING":
                                        view.modalInfo("Se consultó la solicitud y sigue pendiente de aprobación..");
                                        break;
                                    case "DECLINED":
                                        view.updateStatusCancelation(folio,"ACTIVE");
                                        view.modalInfo("Se consultó la solicitud y ha sido rechazada.");
                                        break;
                                }

                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error en request: "+error.getMessage());
                        view.closeModalVerifyCancelationRequest();
                        view.modalInfo("No fue posible verificar la cancelación debido a intermitencia de Red, la solicitud se verificará cuando la red sea mas estable o cuando haya buena señal.");
                    }
                }   , Request.Method.GET,null
                );
        requestQueue.add(cancelRequest).setRetryPolicy(new RetryPolicy() {
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
