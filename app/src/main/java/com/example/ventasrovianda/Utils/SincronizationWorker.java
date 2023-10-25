package com.example.ventasrovianda.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.DevolutionSubSaleRequestServer;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSMP;
import com.example.ventasrovianda.Utils.Models.SincronizationNewVersionRequest;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.bd.AppDatabase;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.Utils.bd.entities.ClientVisit;
import com.example.ventasrovianda.Utils.bd.entities.Debt;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.clientsv2.models.ClientV2Request;
import com.example.ventasrovianda.clientsv2.models.ClientV2Response;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateResponse;
import com.example.ventasrovianda.clientsv2.models.ClientV2VisitRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2VisitResponse;
import com.example.ventasrovianda.cotizaciones.models.SaleCreditPayedResponse;
import com.example.ventasrovianda.cotizaciones.presenter.VisitsPresenterContract;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SincronizationWorker extends Worker {
    Context context;
    private Cache cache;
    private Network network;
    private Gson parser;
    private GsonRequest serviceConsumer;
    private RequestQueue requestQueue;
    NotificationManagerCompat notificationManager;
    private static final String url = Constants.URL;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private String dateSincronization=null;
    public SincronizationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotificationSynchronization("Sincronización automática");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateSincronization = dateFormat.format(calendar.getTime());
        firstStep();
        return Result.success();
    }

    void firstStep(){

        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                List<Client> clientsUnsincronized =conexion.clientDao().getAllClientsUnsicronized();
                List<ClientV2Request> requestRegister = new ArrayList<>();
                for(Client client : clientsUnsincronized) {
                    if (client.registeredInMobile != null && client.registeredInMobile == true && client.clientRovId==null) {
                        ClientV2Request clientV2Request = new ClientV2Request();
                        clientV2Request.setClientCp(client.cp);
                        clientV2Request.setClientName(client.name);
                        clientV2Request.setClientMobileId(client.clientMobileId);
                        clientV2Request.setClientType(client.type);
                        clientV2Request.setClientStreet(client.street);
                        clientV2Request.setClientSuburb(client.suburb);
                        clientV2Request.setClientMunicipality(client.municipality);
                        clientV2Request.setClientExtNumber(client.noExterior);
                        clientV2Request.setClientSellerUid(client.uid);
                        if (client.latitude != null) {
                            clientV2Request.setLatitude(client.latitude);
                        }
                        if (client.longitude != null) {
                            clientV2Request.setLongitude(client.longitude);
                        }
                        clientV2Request.setMonday(client.monday);
                        clientV2Request.setTuesday(client.tuesday);
                        clientV2Request.setWednesday(client.wednesday);
                        clientV2Request.setThursday(client.thursday);
                        clientV2Request.setFriday(client.friday);
                        clientV2Request.setSaturday(client.saturday);
                        requestRegister.add(clientV2Request);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(requestRegister.size()>0) {
                            tryRegisterClients(requestRegister);
                        }else{
                            setClientsRegisters(new ArrayList<>());
                        }
                    }
                });
            }
        });
    }

    public void tryRegisterClients(List<ClientV2Request> clientV2Request) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2Response[]> addressCoordenates = new GsonRequest<ClientV2Response[]>
                (url+"/rovianda/customers/v2/register-arr", ClientV2Response[].class,headers,
                        new Response.Listener<ClientV2Response[]>(){
                            @Override
                            public void onResponse(ClientV2Response[] response) {
                                setClientsRegisters(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showNotificationSynchronization("Sincronizacion automatica - Error al sincronizar los clientes nuevos (paso 1)");
                        checkSalesUnSincronized();
                    }
                }   , Request.Method.POST,this.parser.toJson(clientV2Request)
                );
        requestQueue.add(addressCoordenates).setRetryPolicy(new RetryPolicy() {
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
    /** updating clients registered to database*/
    public void setClientsRegisters(List<ClientV2Response> clientsRegistered) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                for(ClientV2Response clientReg : clientsRegistered){
                    Client client = conexion.clientDao().getClientByClientIdMobile(clientReg.getClientMobileId());
                    client.clientRovId=clientReg.getClientId();
                    client.sincronized=true;
                    conexion.clientDao().updateClient(client);
                    ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(clientReg.getClientMobileId(),dateSincronization);
                    clientVisit.clientId=clientReg.getClientId();
                    conexion.clientVisitDao().updateClientVisit(clientVisit);
                    List<Sale> salesTemp = conexion.saleDao().getAllSalesByDateAndClientId(dateSincronization+"T00:00:00.000Z",dateSincronization+"T23:59:59.000Z",clientReg.getClientMobileId());
                    for(Sale sale : salesTemp){
                        sale.isTempKeyClient=false;
                        sale.keyClient=client.clientRovId;
                        sale.clientId=client.clientRovId;
                        conexion.saleDao().updateSale(sale);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        secondStep();
                    }
                });
            }
        });
    }

    /** updating clients unsincronized */
    void secondStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                List<Client> clientsUnsincronized =conexion.clientDao().getAllClientsUnsicronized();
                List<ClientV2UpdateRequest> clientV2UpdateRequestList = new ArrayList<>();
                for(Client client : clientsUnsincronized) {
                    if (client.sincronized==false && client.clientRovId!=null) {
                        ClientV2UpdateRequest clientV2UpdateRequest = new ClientV2UpdateRequest();
                        clientV2UpdateRequest.setClientId(client.clientRovId);
                        clientV2UpdateRequest.setClientKey(client.clientKey);
                        clientV2UpdateRequest.setClientCp(client.cp);
                        clientV2UpdateRequest.setClientName(client.name);
                        clientV2UpdateRequest.setClientStreet(client.street);
                        clientV2UpdateRequest.setClientSuburb(client.suburb);
                        clientV2UpdateRequest.setClientMunicipality(client.municipality);
                        if (client.noExterior != null) {
                            clientV2UpdateRequest.setClientExtNumber(client.noExterior);
                        }
                        clientV2UpdateRequest.setMonday(client.monday);
                        clientV2UpdateRequest.setTuesday(client.tuesday);
                        clientV2UpdateRequest.setWednesday(client.wednesday);
                        clientV2UpdateRequest.setThursday(client.thursday);
                        clientV2UpdateRequest.setFriday(client.friday);
                        clientV2UpdateRequest.setSaturday(client.saturday);
                        if (client.latitude != null) {
                            clientV2UpdateRequest.setLatitude(client.latitude);
                        }
                        if (client.longitude != null) {
                            clientV2UpdateRequest.setLongitude(client.longitude);
                        }
                        clientV2UpdateRequestList.add(clientV2UpdateRequest);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(clientV2UpdateRequestList.size()>0) {
                            updateCustomerV2(clientV2UpdateRequestList);
                        }else{
                            setClientsUpdated(new ArrayList<>());
                        }
                    }
                });
            }
        });
    }


    public void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2UpdateResponse[]> addressCoordenates = new GsonRequest<ClientV2UpdateResponse[]>
                (url+"/rovianda/customers/v2/update", ClientV2UpdateResponse[].class,headers,
                        new Response.Listener<ClientV2UpdateResponse[]>(){
                            @Override
                            public void onResponse(ClientV2UpdateResponse[] response) {
                                setClientsUpdated(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        showNotificationSynchronization("Sincronizacion automatica - Error al sincronizar los clientes actualizados (pase 2)");
                        checkSalesUnSincronized();
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

    /** Updating clients updated to database*/
    public void setClientsUpdated(List<ClientV2UpdateResponse> clientsUpdated) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                for(ClientV2UpdateResponse clientReg : clientsUpdated){
                    Client client = conexion.clientDao().getClientBydId(clientReg.getClientId());
                    if(client!=null) {
                        client.sincronized = true;
                        conexion.clientDao().updateClient(client);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        thirdStep();
                    }
                });
            }
        });
    }

    /** Registering clients visits to server */
    void thirdStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                List<ClientVisit> visits = conexion.clientVisitDao().getClientVisitByDateUnsincronized(dateSincronization);
                List<ClientV2VisitRequest> requests = new ArrayList<>();
                for(ClientVisit clientVisit : visits){
                    ClientV2VisitRequest request = new ClientV2VisitRequest();
                    request.setVisited(clientVisit.visited);
                    request.setDate(clientVisit.date);
                    request.setAmount(clientVisit.amount);
                    request.setObservations(clientVisit.observations);
                    request.setClientId(clientVisit.clientId);
                    requests.add(request);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(requests.size()>0) {
                            registerVisitsV2(requests);
                        }else{
                            setClientVisitedRegistered(new ArrayList<>());
                        }
                    }
                });
            }
        });
    }
    public void registerVisitsV2(List<ClientV2VisitRequest> clientV2VisitRequests) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<ClientV2VisitResponse[]> addressCoordenates = new GsonRequest<ClientV2VisitResponse[]>
                (url+"/rovianda/customer/visit", ClientV2VisitResponse[].class,headers,
                        new Response.Listener<ClientV2VisitResponse[]>(){
                            @Override
                            public void onResponse(ClientV2VisitResponse[] response) {
                                setClientVisitedRegistered(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showNotificationSynchronization("Sincronizacion Automatica - Error al sincronizar las visitas de clientes (paso 3)");
                        checkSalesUnSincronized();
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

            }
        });
    }
    /** Updating clients visits in database */
    public void setClientVisitedRegistered(List<ClientV2VisitResponse> clientV2Visit) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                for(ClientV2VisitResponse response : clientV2Visit){
                    ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(response.getClientId(),response.getDate());
                    clientVisit.sincronized=true;
                    conexion.clientVisitDao().updateClientVisit(clientVisit);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkSalesUnSincronized();
                    }
                });
            }
        });
    }

    private void showNotificationSynchronization(String msg){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "rovisapi")

                .setSmallIcon(R.drawable.ic_logorov)
                .setContentTitle("Sistema Rovianda")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    void checkSalesUnSincronized(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getApplicationContext());
                List<Sale> sales=new ArrayList<>();
                sales = conexion.saleDao().getAllSalesUnsincronized();

                List<ModeOfflineSM> modeOfflineSMS = new ArrayList<>();
                for(Sale sale : sales) {
                    System.out.println("Venta sin sincronizacion: "+sale.folio);
                    System.out.println("status: "+sale.statusStr);
                    System.out.println("modificado: "+sale.modified);
                    System.out.println("sincronizado: "+sale.sincronized);
                    ModeOfflineSM modeOfflineSM = new ModeOfflineSM();
                    modeOfflineSM.setAmount(sale.amount);
                    modeOfflineSM.setClientId(sale.clientId);
                    modeOfflineSM.setCredit(sale.credit);
                    modeOfflineSM.setDate(sale.date);
                    modeOfflineSM.setFolio(sale.folio);
                    modeOfflineSM.setPayedWith(sale.payed);
                    modeOfflineSM.setSellerId(sale.sellerId);
                    modeOfflineSM.setStatus(sale.status);
                    modeOfflineSM.setStatusStr(sale.statusStr);
                    modeOfflineSM.setTypeSale(sale.typeSale);
                    List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(sale.folio);
                    List<ModeOfflineSMP> modeOfflineSMPS = new ArrayList<>();
                    for(SubSale subSale : subSales){
                        ModeOfflineSMP modeOfflineSMP = new ModeOfflineSMP();
                        modeOfflineSMP.setPresentationId(subSale.presentationId);
                        modeOfflineSMP.setProductId(subSale.productId);
                        modeOfflineSMP.setQuantity(subSale.quantity);
                        modeOfflineSMP.setAmount(subSale.price);
                        modeOfflineSMPS.add(modeOfflineSMP);
                    }
                    modeOfflineSM.setProducts(modeOfflineSMPS);
                    modeOfflineSMS.add(modeOfflineSM);
                }
                List<DevolutionRequest> devolutionsRequests;
                devolutionsRequests = conexion.devolutionRequestDao().getAllUnsincronized();

                List<DevolutionRequestServer> devolutionRequestServers=new ArrayList<>();
                for(DevolutionRequest devolutionRequest :devolutionsRequests){
                    DevolutionRequestServer  devolutionRequestServer = new DevolutionRequestServer();
                    devolutionRequestServer.setCreateAt(devolutionRequest.createAt);
                    devolutionRequestServer.setDevolutionId(devolutionRequest.devolutionRequestId);
                    devolutionRequestServer.setFolio(devolutionRequest.folio);
                    devolutionRequestServer.setObservations(devolutionRequest.description);
                    devolutionRequestServer.setTypeDevolution(devolutionRequest.typeDevolution);
                    List<DevolutionSubSaleRequestServer> devolutionSubSaleRequestServersModified = new ArrayList<>();
                    List<DevolutionSubSaleRequestServer> devolutionSubSaleRequestServersOriginal = new ArrayList<>();
                    List<DevolutionSubSale> devolutionSubSales = conexion.devolutionSubSaleDao().findByDevolutionRequestId(devolutionRequest.devolutionRequestId);
                    for(DevolutionSubSale devolutionSubSale : devolutionSubSales){
                        DevolutionSubSaleRequestServer devolutionSubSaleRequestServer = new DevolutionSubSaleRequestServer();
                        devolutionSubSaleRequestServer.setAmount(devolutionSubSale.price);
                        devolutionSubSaleRequestServer.setAppSubSaleId(devolutionSubSale.subSaleId);
                        devolutionSubSaleRequestServer.setCreateAt(devolutionRequest.createAt);
                        devolutionSubSaleRequestServer.setPresentationId(devolutionSubSale.presentationId);
                        devolutionSubSaleRequestServer.setAppSubSaleId(devolutionSubSale.subSaleId);
                        devolutionSubSaleRequestServer.setProductId(devolutionSubSale.productId);
                        devolutionSubSaleRequestServer.setQuantity(devolutionSubSale.quantity);
                        devolutionSubSaleRequestServersModified.add(devolutionSubSaleRequestServer);
                    }
                    List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(devolutionRequest.folio);
                    for(SubSale subSale : subSales){
                        DevolutionSubSaleRequestServer devolutionSubSaleRequestServer = new DevolutionSubSaleRequestServer();
                        devolutionSubSaleRequestServer.setAmount(subSale.price);
                        devolutionSubSaleRequestServer.setAppSubSaleId(subSale.subSaleId);
                        devolutionSubSaleRequestServer.setCreateAt(devolutionRequest.createAt);
                        devolutionSubSaleRequestServer.setPresentationId(subSale.presentationId);
                        devolutionSubSaleRequestServer.setProductId(subSale.productId);
                        devolutionSubSaleRequestServer.setAppSubSaleId(subSale.subSaleId);
                        devolutionSubSaleRequestServer.setQuantity(subSale.quantity);
                        devolutionSubSaleRequestServersOriginal.add(devolutionSubSaleRequestServer);
                    }
                    devolutionRequestServer.setProductsNew(devolutionSubSaleRequestServersModified);
                    devolutionRequestServer.setProductsOld(devolutionSubSaleRequestServersOriginal);
                    devolutionRequestServers.add(devolutionRequestServer);
                }
                List<Debt> debts = conexion.debtDao().getAllDebsWithoutSincronization();
                List<DebPayedRequest> debtsPayed = new ArrayList<>();
                for(Debt debt : debts){
                    if(debt.sincronized==false && debt.deleted==false){
                        DebPayedRequest debPayedRequest = new DebPayedRequest();
                        Sale sale = conexion.saleDao().getByFolio(debt.folio);
                        debPayedRequest.setAmountPayed(sale.amount);
                        debPayedRequest.setDatePayed(debt.createAt);
                        debPayedRequest.setFolio(sale.folio);
                        debPayedRequest.setPayedType(debt.payedType);
                        debtsPayed.add(debPayedRequest);
                    }
                }
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        if(modeOfflineSMS.size()>0 || debtsPayed.size()>0) {
                            sincronizeSales(modeOfflineSMS,debtsPayed,devolutionRequestServers);
                        }else{
                            notificationManager.cancel(1);
                        }
                    }
                });
            }
        });
    }

    public void sincronizeSales(List<ModeOfflineSM> ModeOfflineSMS, List<DebPayedRequest> debtsPayedRequest, List<DevolutionRequestServer> devolutionRequestServers) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        SincronizationNewVersionRequest sincronizationNewVersionRequest = new SincronizationNewVersionRequest();
        sincronizationNewVersionRequest.setSales(ModeOfflineSMS);
        sincronizationNewVersionRequest.setDebts(debtsPayedRequest);
        sincronizationNewVersionRequest.setDevolutions(devolutionRequestServers);
        GsonRequest<SincronizationResponse> presentationsgGet = new GsonRequest<SincronizationResponse>
                (url+"/rovianda/sincronize-single/v2/sale", SincronizationResponse.class,headers,
                        new Response.Listener<SincronizationResponse>(){
                            @Override
                            public void onResponse(SincronizationResponse response) {
                                completeSincronzation(response);
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }   , Request.Method.POST,parser.toJson(sincronizationNewVersionRequest)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new DefaultRetryPolicy(180000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    public void completeSincronzation(SincronizationResponse sincronizationResponse) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getApplicationContext());
                for(int i=0;i<sincronizationResponse.getSalesSincronized().size();i++){
                    conexion.saleDao().updateSaleId(sincronizationResponse.getSalesSincronized().get(i).getSaleId(),sincronizationResponse.getSalesSincronized().get(i).getFolio());
                }
                for(String folio  : sincronizationResponse.getDebtsSicronized()){
                    Debt debt = conexion.debtDao().getDebtByFolio(folio);
                    debt.sincronized=true;
                    conexion.debtDao().updateDebtSincronization(debt);
                }
                for(String folio : sincronizationResponse.getDevolutionsSincronized()){
                    DevolutionRequest devolutionRequest = conexion.devolutionRequestDao().findDevolutionRequestByFolioRegister(folio);
                    if(devolutionRequest!=null) {
                        devolutionRequest.sincronized = 1;
                        conexion.devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                    }
                }

                for(String folio : sincronizationResponse.getDevolutionsAccepted()){
                    DevolutionRequest devolutionRequest = conexion.devolutionRequestDao().findDevolutionRequestByFolioRegister(folio);
                    devolutionRequest.status="ACCEPTED";
                    conexion.devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }
                for(String folio : sincronizationResponse.getDevolutionsRejected()){
                    DevolutionRequest devolutionRequest = conexion.devolutionRequestDao().findDevolutionRequestByFolioRegister(folio);
                    devolutionRequest.status="DECLINED";
                    conexion.devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkAllSaleCreditsPayed();

                    }
                });
            }
        });
    }

    void checkAllSaleCreditsPayed(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                List<Sale> sales = conexion.saleDao().getAllDebts();
                List<String> folios = new ArrayList<>();
                for(Sale sale : sales){
                    folios.add(sale.folio);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkSalesCredit(folios);
                    }
                });
            }
        });
    }

    public void setAllSalesCreditPaymentStatus(List<SaleCreditPayedResponse> payments){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(context);
                for(SaleCreditPayedResponse item : payments){
                    Sale sale = conexion.saleDao().getByFolio(item.getFolio());
                    if(item.isPayed() && sale.status==true){
                        sale.status=false;
                        conexion.saleDao().updateSale(sale);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notificationManager.cancel(1);
                    }
                });
            }
        });
    }

    public void checkSalesCredit(List<String> folios) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        GsonRequest<SaleCreditPayedResponse[]> presentationsgGet = new GsonRequest<SaleCreditPayedResponse[]>
                (url+"/rovianda/salescredit/check", SaleCreditPayedResponse[].class,headers,
                        new Response.Listener<SaleCreditPayedResponse[]>(){
                            @Override
                            public void onResponse(SaleCreditPayedResponse[] response) {
                                setAllSalesCreditPaymentStatus(Arrays.asList(response));
                            }
                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showNotificationSynchronization("Sincronizacion Automatica - Error al consultar pagos de notas de credito (paso 5)");
                    }
                }   , Request.Method.POST,parser.toJson(folios)
                );
        requestQueue.add(presentationsgGet).setRetryPolicy(new DefaultRetryPolicy(180000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
