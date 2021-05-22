package com.example.ventasrovianda.clients.presenter;

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
import com.example.ventasrovianda.Utils.GsonRequest;
import com.example.ventasrovianda.Utils.Models.AddressClient;
import com.example.ventasrovianda.Utils.Models.ClientCount;
import com.example.ventasrovianda.Utils.Models.ClientModel;
import com.example.ventasrovianda.Utils.Models.ClientModelRequest;
import com.example.ventasrovianda.Utils.Models.ClientResponseDTO;
import com.example.ventasrovianda.Utils.Models.DaysVisited;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;
import com.example.ventasrovianda.clients.view.RegisterClientView;
import com.example.ventasrovianda.clients.view.RegisterClientViewContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterClientPresenter implements RegisterClientPresenterContract{
    Context context;
    RegisterClientViewContract view;

    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private String url ="https://us-central1-sistema-rovianda.cloudfunctions.net/app";//"https://us-central1-sistema-rovianda.cloudfunctions.net/app";
    private RequestQueue requestQueue;

    FirebaseAuth firebaseAuth;
    public RegisterClientPresenter(Context context,RegisterClientView view){
        this.context = context;
        this.view = view;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getCurrentCountClient(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientCount> clientPost = new GsonRequest<ClientCount>
                (url + "/rovianda/customer/customer-count", ClientCount.class, headers,
                        new Response.Listener<ClientCount>() {
                            @Override
                            public void onResponse(ClientCount response) {
                                view.setKeyClientText(String.valueOf(response.getCount()+1));
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }, Request.Method.GET, null
                );
        clientPost.setRetryPolicy(new RetryPolicy() {
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
        })  ;
        requestQueue.add(clientPost);
    }

    @Override
    public void registClient(ClientModel clientModel, DaysVisited daysVisited) {

        if(validClientModel(clientModel)==true) {

            ClientModelRequest clientModelRequest = generateRequest(clientModel,daysVisited);
            System.out.println("Modelo: "+parser.toJson(clientModelRequest));
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            GsonRequest<ClientResponseDTO> clientPost = new GsonRequest<ClientResponseDTO>
                    (url + "/rovianda/seller/customer/create", ClientResponseDTO.class, headers,
                            new Response.Listener<ClientResponseDTO>() {
                                @Override
                                public void onResponse(ClientResponseDTO response) {
                                    if(response.getClientId()==Integer.parseInt(clientModel.getClave().trim())){

                                        view.registroCompleto(null);
                                    }else{
                                        view.registroCompleto(String.valueOf(response.getClientId()));
                                    }

                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Mensaje de error: "+error.getMessage());
                            view.registroFallido();
                        }
                    }, Request.Method.POST, parser.toJson(clientModelRequest)
                    );
            clientPost.setRetryPolicy(new RetryPolicy() {
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
            })  ;
            requestQueue.add(clientPost);
        }
    }

    ClientModelRequest generateRequest(ClientModel clientModel,DaysVisited daysVisited){
        ClientModelRequest  clientModelRequest = new ClientModelRequest();
        AddressClient addressClient = new AddressClient();
        addressClient.setCp(Integer.parseInt(clientModel.getCp()));
        addressClient.setExtNumber(Integer.parseInt(clientModel.getNoExt()));
        addressClient.setIntersectionOne(clientModel.getEntreCalle());
        addressClient.setIntersectionTwo(clientModel.getyCalle());
        if(!clientModel.getNoInt().isEmpty()) {
            addressClient.setIntNumber(Integer.parseInt(clientModel.getNoInt()));
        }
        addressClient.setLocation(clientModel.getLocalidad());
        addressClient.setMunicipality(clientModel.getMunicipio());
        addressClient.setPopulation(clientModel.getPoblacion());
        addressClient.setReference(clientModel.getReferencia());
        addressClient.setState(clientModel.getEstado());
        addressClient.setStreet(clientModel.getCalle());
        addressClient.setSuburb(clientModel.getColonia());

        clientModelRequest.setAddressClient(addressClient);

        clientModelRequest.setKeyClient(Integer.parseInt(clientModel.getClave()));
        clientModelRequest.setName(clientModel.getNombre());
        clientModelRequest.setPhone(clientModel.getTelefono());
        clientModelRequest.setRfc(clientModel.getRfc());
        clientModelRequest.setSaleUid(this.firebaseAuth.getCurrentUser().getUid());
        clientModelRequest.setDaysVisited(daysVisited);
        clientModelRequest.setTypeClient(clientModel.getTypeClient());
        return clientModelRequest;
    }

    boolean validClientModel(ClientModel clientModel){
        boolean valid =true;
        if(clientModel.getNombre().isEmpty()){
            valid=false;
            view.setNombreError("El nombre no puede ir vacio");
        }
        if(clientModel.getClave().isEmpty()){
            valid=false;
            view.setClaveError("La clave no puede ir vacia");
        }
        /*if(clientModel.getTelefono().isEmpty()){
            valid=false;
            view.setTelefonoError("El telefono no puede ir vacio");
        }*/
        /*if(clientModel.getRfc().isEmpty()){
            valid=false;
            view.setRfcError("El rfc no puede ir vacio");
        }*/
        if(clientModel.getCalle().isEmpty()){
            valid=false;
            view.setCalleError("La calle no puede ir vacia");
        }
        if(clientModel.getNoExt().isEmpty()){
            valid=false;
            view.setNoExtError("El No. exterior no puede ir vacio");
        }
        /*if(clientModel.getNoInt().isEmpty()){
            valid=false;
        }*/
        if(clientModel.getEntreCalle().isEmpty()){
            valid=false;
            view.setExtreCalleError("El campo 'entre calle' no puede ir vacio");
        }
        if(clientModel.getyCalle().isEmpty()){
            valid=false;
            view.setYCalleError("El campo 'y calle' no puede ir vacio");
        }
        /*if(clientModel.getPoblacion().isEmpty()){
            valid=false;
            view.setPoblacionError("La poblacion no puede ir vacia");
        }*/
        if(clientModel.getColonia().isEmpty()){
            valid=false;
            view.setColoniaError("La colonia no puede ir vacia");
        }
        /*if(clientModel.getReferencia().isEmpty()){
            valid=false;
            view.setReferenciaError("La referencia no puede ir vacio");
        }*/
        /*if(clientModel.getLocalidad().isEmpty()){
            valid=false;
            view.setLocalicadError("La localidad no puede ir vacio");
        }*/
        if(clientModel.getMunicipio().isEmpty()){
            valid=false;
            view.setMunicipioError("El municipio no puede ir vacio");
        }
        if(clientModel.getCp().isEmpty()){
            valid=false;
            view.setCpError("El código postal no puede ir vacio");
        }
        if(clientModel.getEstado().isEmpty()){
            valid=false;
            view.setEstadoError("El estado no puede ir vacio");
        }
        if(valid==false){
            view.registroFallido();
        }
        return valid;
    };
}
