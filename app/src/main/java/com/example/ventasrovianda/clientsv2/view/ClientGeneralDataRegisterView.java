package com.example.ventasrovianda.clientsv2.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.MainActivity;
import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.AddressCoordenatesResponse;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.AppDatabase;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.clients.view.ClientViewArgs;
import com.example.ventasrovianda.clientsv2.models.ClientV2Request;
import com.example.ventasrovianda.clientsv2.models.ClientV2Response;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateResponse;
import com.example.ventasrovianda.clientsv2.presenter.ClientGeneralDataRegisterPresenter;
import com.example.ventasrovianda.clientsv2.presenter.ClientGeneralDataRegisterPresenterContract;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientGeneralDataRegisterView extends Fragment implements ClientGeneralDataRegisterViewContract, View.OnClickListener {

    private LocationManager locManager;
    private Location loc;

    private TextInputLayout customerNameField, customerStreetField, customerMunicipalityField, customerSuburbField, customerNoExtField, customerCpField;
    private ClientGeneralDataRegisterPresenterContract presenter;
    private Button nextButton, cancelButton,goToMap;
    private NavController navController;
    private String username;
    private Double currentLatitude = null;
    private Double currentLongitude = null;
    private Integer currentClientId=0;
    private Integer currentClientRovId=0;
    private CheckBox checkBoxModay, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    ViewModelStore viewModelStore;
    private TextView latitudText, longitudText;
    private boolean isLoading = false;
    private String action = "";
    private boolean coordsEdited=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clientv2_generaldata, null);
        this.customerNameField = view.findViewById(R.id.customerNameField);
        this.customerStreetField = view.findViewById(R.id.customerStreetField);
        this.customerMunicipalityField = view.findViewById(R.id.customerMunicipalityField);
        this.customerSuburbField = view.findViewById(R.id.customerSuburbField);
        this.customerNoExtField = view.findViewById(R.id.customerNoExtField);
        this.customerCpField = view.findViewById(R.id.customerCpField);
        this.nextButton = view.findViewById(R.id.nextButton);
        this.nextButton.setOnClickListener(this);
        this.cancelButton = view.findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(this);
        this.checkBoxModay = view.findViewById(R.id.checkBoxModay);
        this.checkBoxTuesday = view.findViewById(R.id.checkBoxTuesday);
        this.checkBoxWednesday = view.findViewById(R.id.checkBoxWednesday);
        this.checkBoxThursday = view.findViewById(R.id.checkBoxThursday);
        this.checkBoxFriday = view.findViewById(R.id.checkBoxFriday);
        this.checkBoxSaturday = view.findViewById(R.id.checkBoxSaturday);
        this.username = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getUsername();
        this.action = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getAction();
        String latitude = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getLatitude();
        String longitude = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getLongitude();

        this.goToMap = view.findViewById(R.id.goToMap);
        this.goToMap.setOnClickListener(this);
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        this.presenter = new ClientGeneralDataRegisterPresenter(getContext(), this);
        if(latitude!=null && longitude!=null && latitude!="null" && longitude!="null"){
            System.out.println("Latitude: "+latitude);
            System.out.println("Longitude: "+longitude);
            currentLatitude=Double.parseDouble(latitude);
            currentLongitude=Double.parseDouble(longitude);
            coordsEdited=true;
            if(!this.action.equals("CREATE")) {
                getCoordenates();
            }
        }
        if (this.action.equals("CREATE")) {
            getCoordenates();
        } else {
            currentClientId=ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getClientMobileId();
            currentClientRovId = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getClientRovId();
            fillData();
        }
        this.navController = NavHostFragment.findNavController(this);
        this.latitudText = view.findViewById(R.id.latitudText);
        this.longitudText = view.findViewById(R.id.longitudText);
        this.latitudText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getCoordenates();
                return true;
            }

            ;
        });
        return view;
    }

    private void fillData() {
        if(currentClientRovId!=null && currentClientRovId!=0) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    Client client= conexion.clientDao().getClientBydId(currentClientRovId);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            customerNameField.getEditText().setText(client.name);
                            customerStreetField.getEditText().setText(client.street);
                            customerMunicipalityField.getEditText().setText(client.municipality);
                            customerSuburbField.getEditText().setText(client.suburb);
                            if (client.noExterior != null) {
                                customerNoExtField.getEditText().setText(client.noExterior);
                            }
                            customerCpField.getEditText().setText(client.cp);
                            checkBoxModay.setChecked(client.monday);
                            checkBoxTuesday.setChecked(client.tuesday);
                            checkBoxWednesday.setChecked(client.wednesday);
                            checkBoxThursday.setChecked(client.thursday);
                            checkBoxFriday.setChecked(client.friday);
                            checkBoxSaturday.setChecked(client.saturday);
                            if (client.latitude != null) {
                                latitudText.setText("Latitud: " + client.latitude);
                                if(!coordsEdited) {
                                    currentLatitude = client.latitude;
                                    latitudText.setText("Latitud: " + currentLatitude);
                                }
                            }
                            if (client.longitude != null) {
                                longitudText.setText("Longitud: " + client.longitude);
                                if(!coordsEdited) {
                                    currentLongitude = client.longitude;
                                    longitudText.setText("Latitud: " + currentLongitude);
                                }
                            }
                        }
                    });
                }
            });
        }else{
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    Client client=conexion.clientDao().getClientByClientIdMobile(currentClientId);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            customerNameField.getEditText().setText(client.name);
                            customerStreetField.getEditText().setText(client.street);
                            customerMunicipalityField.getEditText().setText(client.municipality);
                            customerSuburbField.getEditText().setText(client.suburb);
                            if (client.noExterior != null) {
                                customerNoExtField.getEditText().setText(client.noExterior);
                            }
                            customerCpField.getEditText().setText(client.cp);
                            checkBoxModay.setChecked(client.monday);
                            checkBoxTuesday.setChecked(client.tuesday);
                            checkBoxWednesday.setChecked(client.wednesday);
                            checkBoxThursday.setChecked(client.thursday);
                            checkBoxFriday.setChecked(client.friday);
                            checkBoxSaturday.setChecked(client.saturday);
                            if (client.latitude != null) {
                                latitudText.setText("Latitud: " + client.latitude);
                                if(!coordsEdited) {
                                    currentLatitude = client.latitude;
                                    latitudText.setText("Latitud: " + currentLatitude);
                                }
                            }
                            if (client.longitude != null) {
                                longitudText.setText("Longitud: " + client.longitude);
                                if(!coordsEdited) {
                                    currentLongitude = client.longitude;
                                    longitudText.setText("Latitud: " + currentLongitude);
                                }
                            }
                        }
                    });
                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                if (!isLoading) {
                    goBack();
                }
                break;
            case R.id.nextButton:
                if (!isLoading) {
                    if (this.action.equals("CREATE")) {
                        saveNewClient();
                    } else {
                        editClientByType();
                    }
                }
                break;
            case R.id.goToMap:
                if(!isLoading){
                    goToMap();
                }
                break;
        }

    }

    void editClientByType() {
        boolean isValid = validFields();
        if (!isValid) {
            if (this.checkBoxModay.isChecked() || this.checkBoxTuesday.isChecked() || this.checkBoxWednesday.isChecked() || this.checkBoxThursday.isChecked() || this.checkBoxFriday.isChecked() || this.checkBoxSaturday.isChecked()) {
                showModalCoordenatesSuccessError("Faltan dias de visita", "Favor de seleccionar dias de visita");
            } else {
                showModalCoordenatesSuccessError("Faltan campos", "Llenar los campos obligatorios");
            }
        } else {
            showModalRegisteringUpdating("Actualizando","Enviando registro a servidor");
            this.isLoading = true;
            Integer clientMobileId = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getClientMobileId();
            Integer clientRovId = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getClientRovId();
            System.out.println("ClientMobileId: "+clientMobileId);
            System.out.println("ClientRovId: "+clientRovId);
            if(clientRovId!=null && clientRovId!=0){
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase conexion = AppDatabase.getInstance(getContext());
                        Client client=conexion.clientDao().getClientBydId(clientRovId);
                        client.name = customerNameField.getEditText().getText().toString();
                        client.street = customerStreetField.getEditText().getText().toString();
                        client.cp = customerCpField.getEditText().getText().toString();
                        client.municipality = customerMunicipalityField.getEditText().getText().toString();
                        client.suburb = customerSuburbField.getEditText().getText().toString();
                        client.latitude = currentLatitude;
                        client.longitude = currentLongitude;
                        client.monday = checkBoxModay.isChecked();
                        client.tuesday = checkBoxTuesday.isChecked();
                        client.wednesday = checkBoxWednesday.isChecked();
                        client.thursday = checkBoxThursday.isChecked();
                        client.friday = checkBoxFriday.isChecked();
                        client.saturday = checkBoxSaturday.isChecked();
                        client.sincronized = false;
                        client.noExterior = customerNoExtField.getEditText().getText().toString();
                        conexion.clientDao().updateClient(client);

                        ClientV2Request clientV2Request= new ClientV2Request();
                        clientV2Request.setClientCp(client.cp);
                        clientV2Request.setClientName(client.name);
                        clientV2Request.setClientMobileId(client.clientMobileId);
                        clientV2Request.setClientType(client.type);
                        clientV2Request.setClientStreet(client.street);
                        clientV2Request.setClientSuburb(client.suburb);
                        clientV2Request.setClientMunicipality(client.municipality);
                        clientV2Request.setClientExtNumber(client.noExterior);
                        clientV2Request.setClientSellerUid(viewModelStore.getStore().getSellerId());
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

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.tryRegisterClient(clientV2Request);
                            }
                        });
                    }
                });
            }else{
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase conexion = AppDatabase.getInstance(getContext());
                        Client client =conexion.clientDao().getClientByClientIdMobile(clientMobileId);
                        client.name = customerNameField.getEditText().getText().toString();
                        client.street = customerStreetField.getEditText().getText().toString();
                        client.cp = customerCpField.getEditText().getText().toString();
                        client.municipality = customerMunicipalityField.getEditText().getText().toString();
                        client.suburb = customerSuburbField.getEditText().getText().toString();
                        client.latitude = currentLatitude;
                        client.longitude = currentLongitude;
                        client.monday = checkBoxModay.isChecked();
                        client.tuesday = checkBoxTuesday.isChecked();
                        client.wednesday = checkBoxWednesday.isChecked();
                        client.thursday = checkBoxThursday.isChecked();
                        client.friday = checkBoxFriday.isChecked();
                        client.saturday = checkBoxSaturday.isChecked();
                        client.sincronized = false;
                        client.noExterior = customerNoExtField.getEditText().getText().toString();
                        conexion.clientDao().updateClient(client);

                        /*List<ClientV2UpdateRequest> clientV2UpdateRequestList = new ArrayList<>();
                        ClientV2UpdateRequest clientV2UpdateRequest = new ClientV2UpdateRequest();
                        clientV2UpdateRequest.setClientId(clientRovId);
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
                        */
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //presenter.updateCustomerV2(clientV2UpdateRequestList);
                            }
                        });
                    }
                });
            }

        }
    }

    void getCoordenates() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            loc =getLastKnownLocation();
            this.showModalCoordenates();
            this.isLoading = true;
            if(coordsEdited){
                loc.setLatitude(currentLatitude);
                loc.setLongitude(currentLongitude);
            }
            this.presenter.getAddressByCoordenates(loc.getLatitude(), loc.getLongitude());
            return;
        }
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                l = locationManager.getLastKnownLocation(provider);
            }

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    ProgressBar progressBarModalCoordenates = null;
    TextView coordenatesModalTitle = null, coordenatesModalMsg = null;
    Button acceptButtonModalCoordeantes = null;
    AlertDialog modalLocation = null;

    void showModalCoordenates() {
        this.isLoading = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.clientv2_modal_coordenates, null);
        progressBarModalCoordenates = view.findViewById(R.id.coodenatesModalProgress);
        coordenatesModalTitle = view.findViewById(R.id.coodenatesModalTitle);
        coordenatesModalMsg = view.findViewById(R.id.coordenatesModalMsg);
        builder.setCancelable(false);
        builder.setView(view);
        this.modalLocation = builder.create();
        modalLocation.show();
        this.acceptButtonModalCoordeantes = view.findViewById(R.id.acceptCoordenatesButton);
        this.acceptButtonModalCoordeantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalLocation.dismiss();
            }
        });

    }

    AlertDialog modalRegisteringUpdating=null;
    void showModalRegisteringUpdating(String title,String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.clientv2_modal_coordenates, null);
        progressBarModalCoordenates = view.findViewById(R.id.coodenatesModalProgress);
        coordenatesModalTitle = view.findViewById(R.id.coodenatesModalTitle);
        coordenatesModalTitle.setText(title);
        coordenatesModalMsg = view.findViewById(R.id.coordenatesModalMsg);
        coordenatesModalMsg.setText(msg);
        builder.setCancelable(false);
        builder.setView(view);
        this.modalRegisteringUpdating = builder.create();
        modalRegisteringUpdating.show();
        this.acceptButtonModalCoordeantes = view.findViewById(R.id.acceptCoordenatesButton);
        this.acceptButtonModalCoordeantes.setVisibility(View.GONE);
    }

    @Override
    public void closeModalRegisteringUpdating(){
        if(modalRegisteringUpdating!=null && modalRegisteringUpdating.isShowing()){
            modalRegisteringUpdating.dismiss();
        }
    }

    @Override
    public void showModalCoordenatesSuccessError(String title, String msg) {
        this.isLoading = false;
        if (modalLocation != null && modalLocation.isShowing()) {
            modalLocation.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.clientv2_modal_coordenates, null);
        progressBarModalCoordenates = view.findViewById(R.id.coodenatesModalProgress);
        coordenatesModalTitle = view.findViewById(R.id.coodenatesModalTitle);
        coordenatesModalMsg = view.findViewById(R.id.coordenatesModalMsg);
        builder.setCancelable(false);
        builder.setView(view);
        this.modalLocation = builder.create();
        modalLocation.show();
        this.acceptButtonModalCoordeantes = view.findViewById(R.id.acceptCoordenatesButton);
        this.acceptButtonModalCoordeantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalLocation.dismiss();
            }
        });
        coordenatesModalTitle.setText(title);
        coordenatesModalMsg.setText(msg);
        progressBarModalCoordenates.setVisibility(View.GONE);

    }

    AlertDialog modalSuccess = null;
    @Override
    public void showModalSuccess(String title,String msg){
        this.isLoading=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view= layoutInflater.inflate(R.layout.clientv2_modal_coordenates, null);
        progressBarModalCoordenates = view.findViewById(R.id.coodenatesModalProgress);
        coordenatesModalTitle = view.findViewById(R.id.coodenatesModalTitle);
        coordenatesModalMsg = view.findViewById(R.id.coordenatesModalMsg);
        builder.setCancelable(false);
        builder.setView(view);
        this.modalSuccess= builder.create();
        modalSuccess.show();
        this.acceptButtonModalCoordeantes = view.findViewById(R.id.acceptCoordenatesButton);
        this.acceptButtonModalCoordeantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalSuccess.dismiss();
                goBack();
            }
        });
        coordenatesModalTitle.setText(title);
        coordenatesModalMsg.setText(msg);
        progressBarModalCoordenates.setVisibility(View.GONE);

    }

    @Override
    public void cannotTakeCoordenatesInfo() {
        showModalCoordenatesSuccessError("Localización fallo","No se pudo obtener la información de ubicación.");
    }

    @Override
    public void setAddressByCoordenates(AddressCoordenatesResponse addressCoordenatesResponse,Double latitude,Double longitude) {
        System.out.println("Street: "+addressCoordenatesResponse.getStreet());
        System.out.println("Street: "+addressCoordenatesResponse.getMunicipality());
        System.out.println("Street: "+addressCoordenatesResponse.getSuburb());
        System.out.println("Street: "+addressCoordenatesResponse.getCp());
        this.customerStreetField.getEditText().setText(addressCoordenatesResponse.getStreet());
        this.customerMunicipalityField.getEditText().setText(addressCoordenatesResponse.getMunicipality());
        this.customerSuburbField.getEditText().setText(addressCoordenatesResponse.getSuburb());
        this.customerCpField.getEditText().setText(addressCoordenatesResponse.getCp());
        this.currentLatitude=latitude;
        this.currentLongitude=longitude;
        String msg = "Latitud: "+latitude+" Longitud: "+longitude;
        this.latitudText.setText("Latitud: "+latitude);
        this.longitudText.setText("Longitud: "+longitude);
        showModalCoordenatesSuccessError("Localizado",msg);
    }

    private void saveNewClient(){
        boolean isValid = validFields();
        if(!isValid){
           if(this.checkBoxModay.isChecked() || this.checkBoxTuesday.isChecked() || this.checkBoxWednesday.isChecked() || this.checkBoxThursday.isChecked() || this.checkBoxFriday.isChecked() || this.checkBoxSaturday.isChecked()){
               showModalCoordenatesSuccessError("Faltan dias de visita","Favor de seleccionar dias de visita");
           } else{
               showModalCoordenatesSuccessError("Faltan campos","Llenar los campos obligatorios");
           }
        }else{
            this.isLoading=true;
            showModalRegisteringUpdating("Registrando","Enviando registro a servidor");
            // is valid
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion=AppDatabase.getInstance(getContext());
                    Client lastClient = conexion.clientDao().getLastClient();
                    int lastKey = 1;
                    if(lastClient!=null){
                        lastKey=lastClient.clientMobileId+1;
                    }
                    Client client = new Client();
                    client.name=customerNameField.getEditText().getText().toString();
                    client.clientKey=lastKey;
                    client.clientKeyTemp=lastKey;
                    client.clientMobileId=lastKey;
                    client.clientRovId=null;
                    client.street=customerStreetField.getEditText().getText().toString();
                    client.type="CONTADO";
                    client.cp=customerCpField.getEditText().getText().toString();
                    client.municipality=customerMunicipalityField.getEditText().getText().toString();
                    client.suburb=customerSuburbField.getEditText().getText().toString();
                    client.latitude=currentLatitude;
                    client.longitude=currentLongitude;
                    client.monday=checkBoxModay.isChecked();
                    client.tuesday=checkBoxTuesday.isChecked();
                    client.wednesday=checkBoxWednesday.isChecked();
                    client.thursday=checkBoxThursday.isChecked();
                    client.friday=checkBoxFriday.isChecked();
                    client.saturday=checkBoxSaturday.isChecked();
                    client.sincronized=false;
                    client.registeredInMobile=true;
                    client.noExterior=customerNoExtField.getEditText().getText().toString();
                    client.uid=viewModelStore.getStore().getSellerId();
                    conexion.clientDao().insertClient(client);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ClientV2Request clientV2Request = new ClientV2Request();
                            clientV2Request.setClientCp(client.cp);
                            clientV2Request.setClientName(client.name);
                            clientV2Request.setClientMobileId(client.clientMobileId);
                            clientV2Request.setClientType(client.type);
                            clientV2Request.setClientStreet(client.street);
                            clientV2Request.setClientSuburb(client.suburb);
                            clientV2Request.setClientMunicipality(client.municipality);
                            clientV2Request.setClientExtNumber(client.noExterior);
                            clientV2Request.setClientSellerUid(viewModelStore.getStore().getSellerId());
                            if(client.latitude!=null){
                                clientV2Request.setLatitude(client.latitude);
                            }
                            if(client.longitude!=null){
                                clientV2Request.setLongitude(client.longitude);
                            }
                            clientV2Request.setMonday(client.monday);
                            clientV2Request.setTuesday(client.tuesday);
                            clientV2Request.setWednesday(client.wednesday);
                            clientV2Request.setThursday(client.thursday);
                            clientV2Request.setFriday(client.friday);
                            clientV2Request.setSaturday(client.saturday);
                            presenter.tryRegisterClient(clientV2Request);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void updateClientRegisteredInServer(ClientV2Response clientV2Response) {
        this.isLoading=true;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion=AppDatabase.getInstance(getContext());
                Client client = conexion.clientDao().getClientByClientIdMobile(clientV2Response.getClientMobileId());
                if(client!=null) {
                    client.sincronized = true;
                    client.clientRovId = clientV2Response.getClientId();
                    client.clientKey=clientV2Response.getClientId();
                    conexion.clientDao().updateClient(client);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading=false;
                    }
                });
            }
        });
    }

    @Override
    public void updateClientInServer(List<ClientV2UpdateResponse> clientV2UpdateResponse) {
        this.isLoading=true;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion=AppDatabase.getInstance(getContext());
                for(ClientV2UpdateResponse clientV2UpdateResponse1 : clientV2UpdateResponse) {
                    Client client = conexion.clientDao().getClientByKeyClient(clientV2UpdateResponse1.getClientId());
                    if (client != null) {
                        client.sincronized = true;
                        conexion.clientDao().updateClient(client);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading=false;
                    }
                });
            }
        });
    }

    public boolean validFields(){
        if(!this.customerNameField.getEditText().getText().toString().isEmpty()
        && !this.customerStreetField.getEditText().getText().toString().isEmpty()
        && !this.customerMunicipalityField.getEditText().getText().toString().isEmpty()
        && !this.customerSuburbField.getEditText().getText().toString().isEmpty()
        && !this.customerCpField.getEditText().getText().toString().isEmpty()
        && (this.checkBoxModay.isChecked() || this.checkBoxTuesday.isChecked() || this.checkBoxWednesday.isChecked() || this.checkBoxThursday.isChecked() || this.checkBoxFriday.isChecked() || this.checkBoxSaturday.isChecked()) ){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void goBack() {
        this.navController.navigate(ClientGeneralDataRegisterViewDirections.actionClientGeneralDataRegisterViewToClientView(this.username));
    }

    @Override
    public void goToMap() {
        this.navController.navigate(ClientGeneralDataRegisterViewDirections.actionClientGeneralDataRegisterViewToClientRegisterMapView(String.valueOf(currentLatitude),String.valueOf(currentLongitude),username,action).setClientId(currentClientId).setClientRovId(currentClientRovId));
    }

    @Override
    public void failConnectionService() {
        if(this.action.equals("EDIT")){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    Integer clientMobileId = ClientGeneralDataRegisterViewArgs.fromBundle(getArguments()).getClientMobileId();
                    Client client = conexion.clientDao().getClientByClientIdMobile(clientMobileId);
                    client.sincronized=false;
                    conexion.clientDao().updateClient(client);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            });
        }
    }
}
