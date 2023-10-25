package com.example.ventasrovianda.cotizaciones.presenter;

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
import com.example.ventasrovianda.Utils.Constants;
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientVisitDTO;
import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ModeOfflineNewVersion;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.ProductPresentation;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;
import com.example.ventasrovianda.Utils.Models.SincronizationNewVersionRequest;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.Models.SincronizeSingleSaleSuccess;
import com.example.ventasrovianda.clientsv2.models.ClientV2Request;
import com.example.ventasrovianda.clientsv2.models.ClientV2Response;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateResponse;
import com.example.ventasrovianda.clientsv2.models.ClientV2VisitRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2VisitResponse;
import com.example.ventasrovianda.cotizaciones.models.SaleCreditPayedResponse;
import com.example.ventasrovianda.cotizaciones.view.VisitsView;
import com.example.ventasrovianda.cotizaciones.view.VisitsViewContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitsPresenter implements VisitsPresenterContract {

    Context context;
    VisitsViewContract view;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url = Constants.URL;
    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;
    public VisitsPresenter(Context context, VisitsView view){
        this.context=context;
        this.view=view;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void getClientsVisits(String uid) {
        Map<String,String> headers = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
            GsonRequest<ClientVisitDTO[]> productsRoviandaGet = new GsonRequest<ClientVisitDTO[]>
                    (url+"/rovianda/seller/customer/schedule?sellerUid="+uid+"&date="+dateParsed,ClientVisitDTO[].class,headers,
                     new Response.Listener<ClientVisitDTO[]>(){
                         @Override
                         public void onResponse(ClientVisitDTO[] response) {
                             //view.setClientVisits(response);
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
    }



    @Override
    public void getPresentations(ProductRovianda productRovianda) {
        Map<String,String> headers = new HashMap<>();
        GsonRequest<ProductPresentation[]> presentationsgGet = new GsonRequest<ProductPresentation[]>
                (url+"/rovianda/products-rovianda/catalog/"+productRovianda.getId(), ProductPresentation[].class,headers,
                        new Response.Listener<ProductPresentation[]>(){
                            @Override
                            public void onResponse(ProductPresentation[] response) {
                                List<ProductPresentation> presentations = new ArrayList<>();
                                for(int i=0;i<response.length;i++){

                                    presentations.add(response[i]);
                                }
                                view.showPresentationProduct(presentations,productRovianda.getName());
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.showPresentationProduct(new ArrayList<>(),productRovianda.getName());
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
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void UploadChanges(ModeOfflineSincronize modeOfflineSincronize,String uid) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        GsonRequest<String> presentationsgGet = new GsonRequest<String>
                (url+"/rovianda/sincronize/"+uid, String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                view.setUploadingStatus(false);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.sincronizeError();
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
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void startVisit(int clientId, ClientDTO clientVisited) {
        Map<String,String> headers = new HashMap<>();
        GsonRequest<String> presentationsgGet = new GsonRequest<String>
                (url+"/rovianda/seller/customer/schedule/"+clientId, String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                view.setClientVisited(clientVisited);
                                view.reloadVisits();
                                view.goToHome();
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.reloadVisits();
                    }
                }   , Request.Method.POST,null
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
    public void endVisit(int clientId) {
        Map<String,String> headers = new HashMap<>();
        GsonRequest<String> presentationsgGet = new GsonRequest<String>
                (url+"/rovianda/seller/customer/schedule/"+clientId, String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {

                                view.setClientVisited(null);
                                view.reloadVisits();
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.reloadVisits();
                    }
                }   , Request.Method.PUT,null
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
    public void setClientVisit(ClientDTO clientVisit) {
        view.setClientVisited(clientVisit);
    }

    @Override
    public void logout() {
        if(this.firebaseAuth!=null) {
            this.firebaseAuth.signOut();
        }
        view.goToLogin();
    }

    @Override
    public void updateStatusSincronizedClient(List<Integer> clients) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<String> sincronizeClients = new GsonRequest<String>
                (url+"/rovianda/status-sincronized/client", String.class,headers,
                        new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {

                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.modalMessageOperation(error.getMessage());
                    }
                }   , Request.Method.POST,parser.toJson(clients)
                );
        requestQueue.add(sincronizeClients).setRetryPolicy(new RetryPolicy() {
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
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void checkSalesCredit(List<String> folios) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        GsonRequest<SaleCreditPayedResponse[]> presentationsgGet = new GsonRequest<SaleCreditPayedResponse[]>
                (url+"/rovianda/salescredit/check", SaleCreditPayedResponse[].class,headers,
                        new Response.Listener<SaleCreditPayedResponse[]>(){
                            @Override
                            public void onResponse(SaleCreditPayedResponse[] response) {
                                view.setUploadingStatus(false);
                                view.setAllSalesCreditPaymentStatus(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation("Error al consultar pagos de notas de credito (paso 5)");
                    }
                }   , Request.Method.POST,parser.toJson(folios)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void sincronizeSales(List<ModeOfflineSM> ModeOfflineSMS, List<DebPayedRequest> debtsPayedRequest, List<DevolutionRequestServer> devolutionRequestServers,String sellerId) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        SincronizationNewVersionRequest sincronizationNewVersionRequest = new SincronizationNewVersionRequest();
        sincronizationNewVersionRequest.setSales(ModeOfflineSMS);
        sincronizationNewVersionRequest.setDebts(debtsPayedRequest);
        sincronizationNewVersionRequest.setDevolutions(devolutionRequestServers);
        GsonRequest<SincronizationResponse> presentationsgGet = new GsonRequest<SincronizationResponse>
                (url+"/rovianda/sincronize-single/v2/sale?sellerId="+sellerId, SincronizationResponse.class,headers,
                        new Response.Listener<SincronizationResponse>(){
                            @Override
                            public void onResponse(SincronizationResponse response) {
                                view.setUploadingStatus(false);
                                view.completeSincronzation(response);
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation(error.getMessage());
                    }
                }   , Request.Method.POST,parser.toJson(sincronizationNewVersionRequest)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(
                new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 60000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 0;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {
                        view.modalSincronizationEnd();
                        view.modalMessageOperation(error.getMessage());
                    }
                }
        );
    }

    @Override
    public void getDataInitial(String sellerUid,String date) {
        System.out.println("Getting data: "+sellerUid);
        Map<String,String> headers = new HashMap<>();
        GsonRequest<ModeOfflineNewVersion> getOfflineModeData = new GsonRequest<ModeOfflineNewVersion>(
                url + "/rovianda/sincronization/" + sellerUid+"?date="+date, ModeOfflineNewVersion.class, headers, new Response.Listener<ModeOfflineNewVersion>() {
            @Override
            public void onResponse(ModeOfflineNewVersion response) {
                view.setModeOffline(response);
                view.modalMessageOperation("Registros obtenidos");
                view.setClientVisits("","",false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error: "+error.getMessage());
            }
        },Request.Method.GET,null
        );
        requestQueue.add(getOfflineModeData).setRetryPolicy(new RetryPolicy() {
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
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void tryRegisterClients(List<ClientV2Request> clientV2Request) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2Response[]> addressCoordenates = new GsonRequest<ClientV2Response[]>
                (url+"/rovianda/customers/v2/register-arr", ClientV2Response[].class,headers,
                        new Response.Listener<ClientV2Response[]>(){
                            @Override
                            public void onResponse(ClientV2Response[] response) {
                                view.setClientsRegisters(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation(error.getMessage());
                        view.checkSalesUnSincronized();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2Request)
                );
        requestQueue.add(addressCoordenates).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2UpdateResponse[]> addressCoordenates = new GsonRequest<ClientV2UpdateResponse[]>
                (url+"/rovianda/customers/v2/update", ClientV2UpdateResponse[].class,headers,
                        new Response.Listener<ClientV2UpdateResponse[]>(){
                            @Override
                            public void onResponse(ClientV2UpdateResponse[] response) {
                                view.setClientsUpdated(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation("Error al sincronizar los clientes actualizados (paso 2)");
                        view.checkSalesUnSincronized();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2UpdateRequestList)
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
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }

    @Override
    public void registerVisitsV2(List<ClientV2VisitRequest> clientV2VisitRequests) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2VisitResponse[]> addressCoordenates = new GsonRequest<ClientV2VisitResponse[]>
                (url+"/rovianda/customer/visit", ClientV2VisitResponse[].class,headers,
                        new Response.Listener<ClientV2VisitResponse[]>(){
                            @Override
                            public void onResponse(ClientV2VisitResponse[] response) {
                                view.setClientVisitedRegistered(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setUploadingStatus(false);
                        view.modalSincronizationEnd();
                        view.modalMessageOperation("Error al sincronizar las visitas de clientes (paso 3)");
                        view.checkSalesUnSincronized();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2VisitRequests)
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
                view.modalSincronizationEnd();
                view.modalMessageOperation(error.getMessage());
            }
        });
    }


}
