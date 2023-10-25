package com.example.ventasrovianda.clientsv2.presenter;

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

import com.example.ventasrovianda.Utils.Models.AddressCoordenatesRequest;
import com.example.ventasrovianda.Utils.Models.AddressCoordenatesResponse;
import com.example.ventasrovianda.clients.view.ClientView;
import com.example.ventasrovianda.clientsv2.models.ClientV2Request;
import com.example.ventasrovianda.clientsv2.models.ClientV2Response;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateResponse;
import com.example.ventasrovianda.clientsv2.view.ClientGeneralDataRegisterView;
import com.example.ventasrovianda.clientsv2.view.ClientGeneralDataRegisterViewContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientGeneralDataRegisterPresenter implements ClientGeneralDataRegisterPresenterContract{
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url = Constants.URL;
    private RequestQueue requestQueue;
    private ClientGeneralDataRegisterViewContract view;
    private Context context;
    public ClientGeneralDataRegisterPresenter(Context context, ClientGeneralDataRegisterViewContract view){
        this.context = context;
        this.view = view;
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
                                view.setAddressByCoordenates(response,latitude,longitude);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.cannotTakeCoordenatesInfo();
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

            }
        });
    }

    @Override
    public void tryRegisterClient(ClientV2Request clientV2Request) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2Response> addressCoordenates = new GsonRequest<ClientV2Response>
                (url+"/rovianda/customers/v2/register", ClientV2Response.class,headers,
                        new Response.Listener<ClientV2Response>(){
                            @Override
                            public void onResponse(ClientV2Response response) {
                                view.closeModalRegisteringUpdating();
                                view.updateClientRegisteredInServer(response);
                                view.showModalSuccess("Registro en red","Registro en red exitoso");
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.failConnectionService();
                        view.closeModalRegisteringUpdating();
                        view.showModalSuccess("Registro completado con detalles","Se registro y guardo el cliente en telefono, pero no se envio a servidor.");
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2Request)
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
                                view.closeModalRegisteringUpdating();
                                view.updateClientInServer(Arrays.asList(response));
                                view.showModalSuccess("Registro en red","Registro en red exitoso");
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.closeModalRegisteringUpdating();
                        view.failConnectionService();
                        view.showModalSuccess("Registro completado con detalles","Se registro y guardo el cliente en telefono, pero no se envio a servidor.");
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

            }
        });
    }
}
