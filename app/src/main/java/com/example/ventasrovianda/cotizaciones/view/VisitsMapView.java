package com.example.ventasrovianda.cotizaciones.view;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.AppDatabase;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.Utils.bd.entities.ClientVisit;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.cotizaciones.adapters.ClientVisitListItemAdapter;
import com.example.ventasrovianda.cotizaciones.models.ClientVisitListItem;
import com.example.ventasrovianda.cotizaciones.presenter.VisitMapPresenter;
import com.example.ventasrovianda.cotizaciones.presenter.VisitMapPresenterContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisitsMapView extends Fragment implements VisitsMapViewContract, View.OnClickListener, OnMapReadyCallback {


    List<ClientVisitListItem> listClients;
    GoogleMap googleMapMain;
    VisitMapPresenterContract presenter;
    NavController navController;
    ClientDTO clientInVisit;
    String userName;
    Button visitMapGoBack,actionButton;
    BluetoothDeviceSerializable bluetoothDeviceSerializable;
    SupportMapFragment supportMapFragment;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    ViewModelStore  viewModelStore;
    ListView listClientsVisits;
    Integer currentIndexSelected=null;
    boolean currentIndexIsVisited=false;
    boolean availableForVisitRecord=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.listClients = new ArrayList<>();
        View view = inflater.inflate(R.layout.clientv2_visits_view_map, null);
        presenter = new VisitMapPresenter(getContext(), this);
        navController = NavHostFragment.findNavController(this);
        this.clientInVisit = VisitsViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = VisitsViewArgs.fromBundle(getArguments()).getUserName();
        this.bluetoothDeviceSerializable = VisitsViewArgs.fromBundle(getArguments()).getPrinterDevice();
        listClientsVisits = view.findViewById(R.id.listClientsVisits);
        visitMapGoBack = view.findViewById(R.id.visitMapGoBack);
        visitMapGoBack.setOnClickListener(this);
        actionButton=view.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(this);
        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
    }

    @Override
    public void genericMessage(String title, String msg) {

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //reloadVisits();
                    }
                }).setCancelable(false).create();
        dialog.show();
    }


    public void searchClients() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Client> clients = new ArrayList<>();
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Calendar calendar = Calendar.getInstance();
                int day =calendar.get(Calendar.DAY_OF_WEEK);
                switch (day){
                    case 2:
                        clients = conexion.clientDao().getClientsMonday(viewModelStore.getStore().getSellerId());
                        break;
                    case 3:
                        clients = conexion.clientDao().getClientsTuesday(viewModelStore.getStore().getSellerId());
                        break;
                    case 4:
                        clients=conexion.clientDao().getClientsWednesday(viewModelStore.getStore().getSellerId());
                        break;
                    case 5:
                        clients=conexion.clientDao().getClientsThursday(viewModelStore.getStore().getSellerId());
                        break;
                    case 6:
                        clients = conexion.clientDao().getClientsFriday(viewModelStore.getStore().getSellerId());
                        break;
                    case 7:
                        clients = conexion.clientDao().getClientsSaturday(viewModelStore.getStore().getSellerId());
                        break;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateFormat = dateFormat.format(calendar.getTime());
                List<ClientVisitListItem> clientsOfDay= new ArrayList<>();
                for(Client client : clients){
                    List<Sale> sales = new ArrayList<>();
                    ClientVisitListItem clientVisitListItem = new ClientVisitListItem();
                    clientVisitListItem.setClientName(client.name);
                    clientVisitListItem.setLatitude(client.latitude);
                    clientVisitListItem.setLongitude(client.longitude);
                    clientVisitListItem.setMustVisited(true);
                    if(client.clientRovId!=null && client.clientRovId!=0){
                        sales = conexion.saleDao().getAllSalesByDateAndClientId(currentDateFormat+"T00:00:00.000Z",currentDateFormat+"T23:59:59.000Z",client.clientRovId);
                        clientVisitListItem.setClientId(client.clientRovId);
                        clientVisitListItem.setKeyClient(client.clientKey);
                        clientVisitListItem.setKeyClientTemp(false);
                    }else{
                        sales = conexion.saleDao().getAllSalesByDateAndKeyClientTemp(currentDateFormat+"T00:00:00.000Z",currentDateFormat+"T23:59:59.000Z",client.clientKeyTemp);
                        clientVisitListItem.setClientId(client.clientMobileId);
                        clientVisitListItem.setKeyClient(client.clientKeyTemp);
                        clientVisitListItem.setKeyClientTemp(true);
                    }
                    if(sales.size()>0){
                        clientVisitListItem.setVisited(true);
                        Float amount =0f;
                        for(Sale sale : sales){
                            amount += sale.amount;
                        }
                        clientVisitListItem.setAmount(amount);
                        clientVisitListItem.setAvailableForRecordOfVisit(false);
                    }else{
                        clientVisitListItem.setVisited(false);
                        ClientVisit clientVisit =null;
                        if(client.clientRovId!=null && client.clientRovId!=0) {
                            clientVisit =  conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientRovId, currentDateFormat);
                        }else{
                            clientVisit =  conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientMobileId, currentDateFormat);
                        }
                        if(clientVisit!=null){
                            clientVisitListItem.setAvailableForRecordOfVisit(false);
                        }else{
                            clientVisitListItem.setAvailableForRecordOfVisit(true);
                        }
                    }
                    clientsOfDay.add(clientVisitListItem);
                }
                /*List<Sale> saleOfDay = conexion.saleDao().getAllSalesByDateBySeller(currentDateFormat+"T00:00:00.000Z",currentDateFormat+"T23:59:59.000Z",viewModelStore.getStore().getSellerId());
                List<Sale> salesWithCustomerWithoutVisitAtDay = new ArrayList<>();
                for(Sale sale : saleOfDay){
                    if(sale.isTempKeyClient){

                    }else{

                    }
                }*/
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LatLng firstLocation = new LatLng(0,0);
                        boolean firstLocationAdded =false;
                        for(ClientVisitListItem client : clientsOfDay){
                            if (client.getLatitude() != null && client.getLongitude() != null && client.getLatitude()!=0.0
                                    && client.getLongitude()!=0.0) {
                                if(!firstLocationAdded){
                                    firstLocation = new LatLng(client.getLatitude(),client.getLongitude());
                                    googleMapMain.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation,10));
                                    firstLocationAdded=true;
                                }
                                System.out.println("Se agrega un flag");
                                MarkerOptions markerOptions = new MarkerOptions();
                                LatLng latLng = new LatLng(client.getLatitude(), client.getLongitude());
                                markerOptions.position(latLng);
                                markerOptions.title(client.getClientName());
                                if(client.isVisited()) {
                                        markerOptions.icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                }else{
                                    markerOptions.icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    if(!client.isAvailableForRecordOfVisit()){
                                        markerOptions.icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                    }
                                }

                                googleMapMain.addMarker(markerOptions);
                            }

                        }
                        if(!firstLocationAdded){
                            googleMapMain.clear();
                            LatLng orizaba = new LatLng(18.849463,-97.098212);
                            googleMapMain.animateCamera(CameraUpdateFactory.newLatLngZoom(orizaba,15));
                        }
                        fillClientsList(clientsOfDay);
                    }
                });
            }});

    }

    private void fillClientsList(List<ClientVisitListItem> clients){
        listClients=clients;
        System.out.println("Clientes: "+clients.size());
        ClientVisitListItemAdapter adapter = new ClientVisitListItemAdapter(getContext(),clients,this);
        listClientsVisits.setAdapter(adapter);
    }
    @Override
    public void goBack() {
        navController.navigate(VisitsMapViewDirections.actionVisitsMapViewToVisitsView(this.userName).setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.visitMapGoBack:
                goBack();
                break;
            case R.id.actionButton:
                openModalActions();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapMain = googleMap;
        searchClients();
    }

    @Override
    public void setSelection(int index,boolean isVisited,boolean availableForVisitRecord) {
        if(listClients.size()>0){
            currentIndexSelected=index;
            currentIndexIsVisited=isVisited;
            this.availableForVisitRecord=availableForVisitRecord;
            for(ClientVisitListItem item : listClients){
                item.setSelected(false);
            }
            ClientVisitListItem itemSelected = listClients.get(index);
            itemSelected.setSelected(true);
            if(itemSelected.getLatitude()!=null && itemSelected.getLongitude()!=null && itemSelected.getLatitude()!=0.0
                    && itemSelected.getLongitude()!=0.0) {
                LatLng latLng = new LatLng(itemSelected.getLatitude(),itemSelected.getLongitude());
                googleMapMain.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
            fillClientsList(listClients);
        }
    }
    AlertDialog dialogActions=null;
    private void openModalActions(){
        if(listClients.size()>0 && currentIndexSelected!=null && !currentIndexIsVisited && availableForVisitRecord){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(R.layout.clientv2_visit_creation_modal, null);
            Button buttonVisitWithoutSale = view.findViewById(R.id.buttonVisitWithoutSale);
            Button buttonNotVisit = view.findViewById(R.id.buttonNotVisit);
            builder.setView(view);
            dialogActions = builder.create();
            dialogActions.show();
            buttonVisitWithoutSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClientVisitListItem client = listClients.get(currentIndexSelected);

                    registVisitWithoutSale(client);
                }
            });
            buttonNotVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClientVisitListItem client = listClients.get(currentIndexSelected);
                    modalCreationVisitWithObservations(client);
                }
            });
        }
    }

    AlertDialog dialogSinc=null;

    private void modalCreationVisit(String msg){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_load_message,null);
        TextView messageLoad = view.findViewById(R.id.message_load);
        messageLoad.setText(msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        this.dialogSinc=builder.create();
        this.dialogSinc.show();
    }
    private void closeCreationVisit(){
        if(dialogSinc!=null && dialogSinc.isShowing()){
            dialogSinc.dismiss();
        }
        if(dialogActions!=null && dialogActions.isShowing()){
            dialogActions.dismiss();
        }
    }

    AlertDialog dialogWithObservations=null;
    private void modalCreationVisitWithObservations(ClientVisitListItem client){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.clientv2_visit_with_observations,null);
        TextInputLayout observationsContainer = view.findViewById(R.id.observationsContainer);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button acceptButton = view.findViewById(R.id.acceptButton);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        this.dialogWithObservations=builder.create();
        this.dialogWithObservations.show();
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = observationsContainer.getEditText().getText().toString();
                if(!message.isEmpty()) {
                    registVisitWithObservations(client, message);
                }else{
                    observationsContainer.getEditText().setError("No puede ser vacio");
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeModalWithObservations();
                closeCreationVisit();
            }
        });
    }
    private void closeModalWithObservations(){
        if(dialogWithObservations!=null && dialogWithObservations.isShowing()){
            dialogWithObservations.dismiss();
        }
    }
    private void registVisitWithoutSale(ClientVisitListItem client){
        modalCreationVisit("Registrando visita sin venta");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar =Calendar.getInstance();
        String currentDateFormat = dateFormat.format(calendar.getTime());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ClientVisit clientVisit = new ClientVisit();
                clientVisit.clientId=client.getClientId();
                if(client.isKeyClientTemp()) {
                    clientVisit.isClientIdTemp=true;
                }else{
                    clientVisit.isClientIdTemp=false;
                }
                clientVisit.visited=true;
                clientVisit.amount=0f;
                clientVisit.date=currentDateFormat;
                clientVisit.observations="Sin venta";
                clientVisit.sincronized=false;
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                conexion.clientVisitDao().insertClientVisit(clientVisit);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        postCreationVisit();
                    }
                });
            }
        });
    }

    private void registVisitWithObservations(ClientVisitListItem client,String msg){
        modalCreationVisit("Registrando visita con observaciones");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar =Calendar.getInstance();
        String currentDateFormat = dateFormat.format(calendar.getTime());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ClientVisit clientVisit = new ClientVisit();
                clientVisit.clientId=client.getClientId();
                if(client.isKeyClientTemp()) {
                    clientVisit.isClientIdTemp=true;
                }else{
                    clientVisit.isClientIdTemp=false;
                }
                clientVisit.visited=true;
                clientVisit.amount=0f;
                clientVisit.date=currentDateFormat;
                clientVisit.observations=msg;
                clientVisit.sincronized=false;
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                conexion.clientVisitDao().insertClientVisit(clientVisit);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        closeModalWithObservations();
                        postCreationVisit();
                    }
                });
            }
        });
    }

    private void postCreationVisit(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    closeCreationVisit();
                    searchClients();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
