package com.example.ventasrovianda.cotizaciones.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.service.autofill.FillEventHistory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.DatePickerFragment;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientOfflineMode;
import com.example.ventasrovianda.Utils.Models.ClientToSaveEntity;
import com.example.ventasrovianda.Utils.Models.ClientVisitDTO;
import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DebtOfflineNewVersion;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.DevolutionSubSaleRequestServer;
import com.example.ventasrovianda.Utils.Models.ModeOfflineDebts;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ModeOfflineNewVersion;
import com.example.ventasrovianda.Utils.Models.ModeOfflineS;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSMP;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.ProductPresentation;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;
import com.example.ventasrovianda.Utils.Models.ProductToSaveEntity;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleOfflineMode;
import com.example.ventasrovianda.Utils.Models.SaleResponseDTO;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.Models.SincronizeSingleSaleSuccess;
import com.example.ventasrovianda.Utils.Models.SubSaleOfflineNewVersion;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.AppDatabase;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.Utils.bd.entities.ClientVisit;
import com.example.ventasrovianda.Utils.bd.entities.Debt;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionResponseInitData;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSalesResponseInitData;
import com.example.ventasrovianda.Utils.bd.entities.Product;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.Utils.bd.entities.UserDataInitial;
import com.example.ventasrovianda.Utils.enums.ClientVisitStatus;
import com.example.ventasrovianda.clientsv2.models.ClientV2Request;
import com.example.ventasrovianda.clientsv2.models.ClientV2Response;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2UpdateResponse;
import com.example.ventasrovianda.clientsv2.models.ClientV2VisitRequest;
import com.example.ventasrovianda.clientsv2.models.ClientV2VisitResponse;
import com.example.ventasrovianda.cotizaciones.models.SaleCreditPayedResponse;
import com.example.ventasrovianda.cotizaciones.presenter.VisitsPresenter;
import com.example.ventasrovianda.cotizaciones.presenter.VisitsPresenterContract;
import com.example.ventasrovianda.cotizaciones.adapters.AdapterListClientVisit;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class VisitsView extends Fragment implements View.OnClickListener, VisitsViewContract {


    ListView simpleList;
    NavController navController;
    BottomNavigationView homeButton;
    Button LogoutButton;
    TextView endDayButton,eatTimeButton;
    VisitsPresenterContract presenter;
    ImageView printerButton;
    boolean isLoading=false;
    CircularProgressIndicator circularProgressIndicator;
    ClientDTO clientInVisit=null;
    BluetoothDeviceSerializable bluetoothDeviceSerializable=null;
    String userName;
    TextView userNameTextView;
    MaterialButton buscarCliente;
    TextInputLayout inputSearch;
    String currentHint="";
    boolean filtered=false;
    ImageButton download,upload,resincronize;
    LinearLayout downloadLayout,uploadLayout,linearLayoutResincronize;
    ViewModelStore viewModelStore;
    Button goToVisitMap;
    Gson parser;
    String dateSincronization=null;
    Boolean actionActivated=false;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.visits_layout,null);

        this.clientInVisit = VisitsViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = VisitsViewArgs.fromBundle(getArguments()).getUserName();
        this.bluetoothDeviceSerializable = VisitsViewArgs.fromBundle(getArguments()).getPrinterDevice();
        this.userNameTextView = v.findViewById(R.id.userName);
        this.userNameTextView.setText("Usuario: "+this.userName);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));
        simpleList = (ListView) v.findViewById(R.id.listClientsVisits);
        this.navController = NavHostFragment.findNavController(this);
        this.LogoutButton = v.findViewById(R.id.Logout_button);
        this.LogoutButton.setOnClickListener(this);
        this.printerButton = v.findViewById(R.id.printerButton);
        this.printerButton.setVisibility(View.INVISIBLE);
        this.printerButton.setOnClickListener(this);
        this.circularProgressIndicator = v.findViewById(R.id.loginLoadingSpinner);
        this.download = v.findViewById(R.id.downloadChanges);
        this.upload = v.findViewById(R.id.uploadChanges);
        this.resincronize = v.findViewById(R.id.resincronize);
        this.downloadLayout= v.findViewById(R.id.linearLayoutDownload);
        this.downloadLayout.setVisibility(View.VISIBLE);
        this.uploadLayout = v.findViewById(R.id.linearLayoutUpload);
        this.uploadLayout.setVisibility(View.VISIBLE);
        this.goToVisitMap= v.findViewById(R.id.goToVisitMap);
        this.goToVisitMap.setOnClickListener(this);
        this.linearLayoutResincronize = v.findViewById(R.id.linearLayoutResincronize);
        this.linearLayoutResincronize.setVisibility(View.VISIBLE);
        this.download.setOnClickListener(this);
        this.upload.setOnClickListener(this);
        this.resincronize.setOnClickListener(this);
        this.presenter = new VisitsPresenter(getContext(),this);

        homeButton = v.findViewById(R.id.bottom_navigation_cotizaciones);
        homeButton.setSelectedItemId(R.id.visitas_section);
        this.endDayButton = v.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.endDayButton.setVisibility(View.GONE);


        this.buscarCliente = v.findViewById(R.id.buscarClienteButton);
        this.buscarCliente.setOnClickListener(this);
        this.inputSearch = v.findViewById(R.id.cliente_input_search);
        this.parser=new Gson();
        this.inputSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!currentHint.equals(s)){
                    buscarCliente.setText("Buscar");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!currentHint.equals(s)){
                    buscarCliente.setText("Buscar");
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    if (!currentHint.equals(s)) {
                        buscarCliente.setText("Buscar");
                    }
                }else{
                    currentHint="";
                    search();
                }
            }
        });
        homeButton.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_section:

                        goToHome();
                        break;
                    case R.id.visitas_section:
                        System.out.println("No cambia de seccion");
                        break;
                    case R.id.cliente_section:

                        goToClient();
                        break;
                    case R.id.pedidos_section:

                        goToPedidos();
                        break;
                    case R.id.ventas_section:

                        goToHistory();
                        break;

                }
                return false;
            }
        });

        return v;
    }

    

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setModeOffline(ModeOfflineNewVersion modeOffline) {
        setUploadingStatus(false);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());

                if (userDataInitial == null) {
                    UserDataInitial userDataInitial1 = new UserDataInitial();
                    userDataInitial1.name = modeOffline.getName();
                    userName=modeOffline.getName();
                    userDataInitial1.count = modeOffline.getCount();
                    userDataInitial1.email = modeOffline.getEmail();
                    userDataInitial1.lastSincronization = modeOffline.getLastSicronization();
                    userDataInitial1.logedIn = true;
                    userDataInitial1.nomenclature = modeOffline.getNomenclature();
                    userDataInitial1.uid = modeOffline.getUid();
                    userDataInitial1.password = modeOffline.getPassword();
                    conexion.userDataInitialDao().insertUserDataDetail(userDataInitial1);
                    System.out.println("Informacion de usuario guardada");
                    System.out.println("Usuario: "+userDataInitial1.name);
                }else{
                    UserDataInitial userDataInitial1 = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                    userDataInitial1.name = modeOffline.getName();
                    userName=modeOffline.getName();
                    userDataInitial1.count = modeOffline.getCount();
                    userDataInitial1.email = modeOffline.getEmail();
                    userDataInitial1.lastSincronization = modeOffline.getLastSicronization();
                    userDataInitial1.logedIn = true;
                    userDataInitial1.nomenclature = modeOffline.getNomenclature();
                    userDataInitial1.uid = modeOffline.getUid();
                    userDataInitial1.password = modeOffline.getPassword();
                    conexion.userDataInitialDao().updateUserData(userDataInitial1);
                    System.out.println("Actualizando vendedor");
                }
                for (ProductToSaveEntity productToSave : modeOffline.getProducts()) {
                    Product product = conexion.productDao().getProductByProduct(productToSave.getProductKey(), viewModelStore.getStore().getSellerId());
                    if (product == null) {
                        Product product1 = new Product();
                        product1.name = productToSave.getName();
                        product1.presentationId = productToSave.getPresentationId();
                        product1.presentationName = productToSave.getPresentationName();
                        product1.price = productToSave.getPrice();
                        product1.productId = productToSave.getProductId();
                        product1.productKey = productToSave.getProductKey();
                        product1.quantity = productToSave.getQuantity();
                        product1.uniMed = productToSave.getUniMed();
                        product1.weightOriginal = productToSave.getWeightOriginal();
                        product1.sellerId = viewModelStore.getStore().getSellerId();
                        product1.esqKey=productToSave.getEsqKey();
                        product1.esqDescription = productToSave.getEsqDescription();
                        conexion.productDao().insertProduct(product1);
                        System.out.println("Producto instalado: " + product1.name + " " + product1.presentationName);
                    } else {
                        product.name = productToSave.getName();
                        product.presentationId = productToSave.getPresentationId();
                        product.presentationName = productToSave.getPresentationName();
                        product.price = productToSave.getPrice();
                        product.productId = productToSave.getProductId();
                        product.quantity = productToSave.getQuantity();
                        product.uniMed = productToSave.getUniMed();
                        product.weightOriginal = productToSave.getWeightOriginal();
                        product.sellerId = viewModelStore.getStore().getSellerId();
                        product.esqKey = productToSave.getEsqKey();
                        product.esqDescription=productToSave.getEsqDescription();
                        conexion.productDao().updateProduct(product);
                        System.out.println("Producto actualizad: " + product.name + " " + product.presentationName);
                    }

                }

                List<Integer> clientsUpdated = new ArrayList<>();
                for (ClientToSaveEntity clientItem : modeOffline.getClients()) {
                    Client client = conexion.clientDao().getClientBydId(clientItem.getClientId());
                    if (client == null) {
                        Client clientEntity = new Client();
                        clientEntity.clientRovId = clientItem.getClientId();
                        clientEntity.clientKey = clientItem.getKeyClient();
                        clientEntity.name = clientItem.getName();
                        clientEntity.creditLimit = clientItem.getCreditLimit();
                        clientEntity.currentCreditUsed = clientItem.getCurrentCreditUsed();
                        clientEntity.uid = viewModelStore.getStore().getSellerId();
                        clientEntity.type = clientItem.getType();
                        clientEntity.cp = clientItem.getCp();
                        clientEntity.monday = clientItem.getMonday();
                        clientEntity.tuesday = clientItem.getTuesday();
                        clientEntity.wednesday = clientItem.getWednesday();
                        clientEntity.thursday = clientItem.getThursday();
                        clientEntity.friday = clientItem.getFriday();
                        clientEntity.saturday = clientItem.getSaturday();
                        clientEntity.sunday = clientItem.getSunday();
                        clientEntity.street=clientItem.getStreet();
                        clientEntity.municipality=clientItem.getMunicipality();
                        clientEntity.suburb=clientItem.getSuburb();
                        clientEntity.latitude=clientItem.getLatitude();
                        clientEntity.longitude=clientItem.getLongitude();
                        clientEntity.sincronized=true;
                        clientEntity.registeredInMobile=false;
                        if(clientItem.getExtNum()!=null) {
                            clientEntity.noExterior = clientItem.getExtNum().toString();
                        }
                        conexion.clientDao().insertClient(clientEntity);
                        System.out.println("Cliente instalado: " + clientEntity.name);
                        if (clientItem.getKeyClient() == 1175) {
                            System.out.println("Cliente para todos: "+clientItem.getName()+" - " + clientItem.getKeyClient());
                        }
                    } else {
                        if (clientItem.getModified()) {
                            client.type= clientItem.getType();
                            client.clientKey = clientItem.getKeyClient();
                            client.cp = clientItem.getCp();
                            client.creditLimit = clientItem.getCreditLimit();
                            client.currentCreditUsed = clientItem.getCurrentCreditUsed();
                            client.name = clientItem.getName();
                            client.monday = clientItem.getMonday();
                            client.tuesday = clientItem.getTuesday();
                            client.wednesday = clientItem.getWednesday();
                            client.thursday = clientItem.getThursday();
                            client.friday = clientItem.getFriday();
                            client.saturday = clientItem.getSaturday();
                            client.sunday = clientItem.getSunday();
                            client.street=clientItem.getStreet();
                            client.municipality=clientItem.getMunicipality();
                            client.suburb=clientItem.getSuburb();
                            client.latitude=clientItem.getLatitude();
                            client.longitude=clientItem.getLongitude();
                            client.sincronized=true;
                            if(clientItem.getExtNum()!=null) {
                                client.noExterior = clientItem.getExtNum().toString();
                            }
                            conexion.clientDao().updateClient(client);
                            clientsUpdated.add(clientItem.getClientId());
                            System.out.println("Cliente modificado : "+client.name);
                        }
                    }
                }
                List<DebtOfflineNewVersion> currentSalesOfDay = modeOffline.getSalesOfDay();
                if (currentSalesOfDay != null) {
                    for (DebtOfflineNewVersion saleOfDay : currentSalesOfDay) {
                        Sale saleEntity = conexion.saleDao().getByFolio(saleOfDay.getFolio());
                        if (saleEntity == null) {
                            System.out.println("Bajando nota: "+saleOfDay.getFolio());
                            Sale sale = new Sale();
                            sale.amount = saleOfDay.getAmount();
                            sale.statusStr = saleOfDay.getStatusStr();
                            sale.typeSale = saleOfDay.getTypeSale();
                            sale.keyClient = saleOfDay.getKeyClient();
                            sale.clientName = saleOfDay.getClientName();
                            sale.folio = saleOfDay.getFolio();
                            sale.saleId = saleOfDay.getSaleId();
                            sale.credit = saleOfDay.getCredit();
                            sale.sincronized = true;
                            sale.modified = false;
                            sale.sellerId = saleOfDay.getSellerId();
                            sale.payed = saleOfDay.getPayed();
                            sale.date = saleOfDay.getDate();
                            sale.clientId = saleOfDay.getClientId();
                            sale.status = saleOfDay.getStatus();
                            sale.cancelAutorized=saleOfDay.getCancelAutorized();
                            conexion.saleDao().insertAll(sale);
                            for (SubSaleOfflineNewVersion subSaleOffline : saleOfDay.getProducts()) {
                                SubSale subSale = new SubSale();
                                System.out.println("Creando subproducto: "+sale.folio+" - "+subSaleOffline.getProductId()+" "+subSaleOffline.getQuantity());
                                /*if(subSaleOffline.getSubSaleAppId()!=null) {
                                    subSale.subSaleId = subSaleOffline.getSubSaleAppId();
                                }*/
                                subSale.quantity = subSaleOffline.getQuantity();
                                subSale.uniMed = subSaleOffline.getUniMed();
                                subSale.weightStandar = subSaleOffline.getWeightStandar();
                                subSale.productPresentationType = subSaleOffline.getProductPresentationType();
                                subSale.productName = subSaleOffline.getProductName();
                                subSale.price = subSaleOffline.getPrice();
                                subSale.folio = saleOfDay.getFolio();
                                subSale.presentationId = subSaleOffline.getPresentationId();
                                subSale.productId = subSaleOffline.getProductId();
                                subSale.productKey = subSaleOffline.getProductKey();
                                subSale.subSaleServerId = subSaleOffline.getSubSaleServerId();
                                conexion.subSalesDao().insertAllSubSales(subSale);
                            }
                            String[] dateForVisit = saleOfDay.getDate().split("T");
                            if(dateForVisit.length>0){
                                ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(sale.clientId,dateForVisit[0]);
                                if(clientVisit==null){
                                    clientVisit = new ClientVisit();
                                    clientVisit.visited=true;
                                    clientVisit.clientId=saleOfDay.getClientId();
                                    clientVisit.date=dateForVisit[0];
                                    clientVisit.observations="";
                                    clientVisit.amount=saleOfDay.getAmount();
                                    clientVisit.sincronized=true;
                                    conexion.clientVisitDao().insertClientVisit(clientVisit);
                                }else{
                                    clientVisit.amount+=saleOfDay.getAmount();
                                    conexion.clientVisitDao().updateClientVisit(clientVisit);
                                }
                            }
                        } else {
                            String[] dates = saleOfDay.getDate().split("T");
                            ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(saleEntity.clientId,dates[0]);
                            if(saleEntity.statusStr.equals("ACTIVE") && saleOfDay.getStatus().equals("CANCELED")){
                                clientVisit.amount-=saleOfDay.getAmount();
                                conexion.clientVisitDao().updateClientVisit(clientVisit);
                            }else if(saleEntity.statusStr.equals("CANCELED") && saleOfDay.getStatusStr().equals("ACTIVE")){
                                clientVisit.amount+=saleOfDay.getAmount();
                                conexion.clientVisitDao().updateClientVisit(clientVisit);
                            }
                            System.out.println("Actualizando estatus");
                            saleEntity.amount = saleOfDay.getAmount();
                            saleEntity.statusStr = saleOfDay.getStatusStr();
                            saleEntity.status = saleOfDay.getStatus();
                            saleEntity.typeSale = saleOfDay.getTypeSale();
                            saleEntity.keyClient = saleOfDay.getKeyClient();
                            saleEntity.clientName = saleOfDay.getClientName();
                            saleEntity.folio = saleOfDay.getFolio();
                            saleEntity.saleId = saleOfDay.getSaleId();
                            saleEntity.credit = saleOfDay.getCredit();
                            saleEntity.sincronized = true;
                            saleEntity.modified = false;
                            saleEntity.sellerId = saleOfDay.getSellerId();
                            saleEntity.payed = saleOfDay.getPayed();
                            saleEntity.date = saleOfDay.getDate();
                            saleEntity.clientId = saleOfDay.getClientId();
                            saleEntity.cancelAutorized=saleOfDay.getCancelAutorized();
                            conexion.saleDao().updateSale(saleEntity);

                        }
                    }
                }

                List<DebtOfflineNewVersion> debts = modeOffline.getDebts();
                if (debts != null) {
                    System.out.println("Obteniendo adeudos");
                for (DebtOfflineNewVersion debt : debts) {
                    Sale sale = conexion.saleDao().getByFolio(debt.getFolio());
                    if (sale == null) {
                        System.out.println("Bajanda adeudo de nota: "+debt.getFolio());
                        Sale saleToSave = new Sale();
                        saleToSave.amount = debt.getAmount();
                        saleToSave.statusStr = debt.getStatusStr();
                        saleToSave.typeSale = debt.getTypeSale();
                        saleToSave.keyClient = debt.getKeyClient();
                        saleToSave.clientName = debt.getClientName();
                        saleToSave.folio = debt.getFolio();
                        saleToSave.saleId = debt.getSaleId();
                        saleToSave.credit = debt.getCredit();
                        saleToSave.sincronized = true;
                        saleToSave.modified = false;
                        saleToSave.sellerId = debt.getSellerId();
                        saleToSave.payed = debt.getPayed();
                        saleToSave.date = debt.getDate();
                        saleToSave.clientId = debt.getClientId();
                        saleToSave.status = debt.getStatus();
                        conexion.saleDao().insertAll(saleToSave);
                        for (SubSaleOfflineNewVersion subSaleOffline : debt.getProducts()) {
                            SubSale subSale = new SubSale();
                            if(subSaleOffline.getSubSaleAppId()!=null) {
                                subSale.subSaleId = subSaleOffline.getSubSaleAppId();
                            }
                            subSale.quantity = subSaleOffline.getQuantity();
                            subSale.uniMed = subSaleOffline.getUniMed();
                            subSale.weightStandar = subSaleOffline.getWeightStandar();
                            subSale.productPresentationType = subSaleOffline.getProductPresentationType();
                            subSale.productName = subSaleOffline.getProductName();
                            subSale.price = subSaleOffline.getPrice();
                            subSale.folio = debt.getFolio();
                            subSale.presentationId = subSaleOffline.getPresentationId();
                            subSale.productId = subSaleOffline.getProductId();
                            subSale.productKey = subSaleOffline.getProductKey();
                            subSale.subSaleServerId = subSaleOffline.getSubSaleServerId();
                            conexion.subSalesDao().insertAllSubSales(subSale);
                        }
                    }
                }
            }

                for(DevolutionResponseInitData devolutionRequest : modeOffline.getDevolutionsRequest()){
                    DevolutionRequest devolutionRequest1 = conexion.devolutionRequestDao().findDevolutionRequestByFolioRegister(devolutionRequest.getFolio());
                    if(devolutionRequest1==null){
                        System.out.println("Bajando adeudo: "+devolutionRequest.getFolio());
                        DevolutionRequest devolutionRequest2 = new DevolutionRequest();
                        devolutionRequest2.status=devolutionRequest.getStatus();
                        devolutionRequest2.description=devolutionRequest.getObservations();
                        devolutionRequest2.sincronized=1;
                        devolutionRequest2.devolutionRequestId=devolutionRequest.getDevolutionAppRequestId();
                        devolutionRequest2.folio=devolutionRequest.getFolio();
                        devolutionRequest2.typeDevolution=devolutionRequest.getTypeDevolution();
                        devolutionRequest2.createAt=devolutionRequest.getCreateAt();
                        conexion.devolutionRequestDao().insertAll(devolutionRequest2);
                    }else{
                        System.out.println("Actualizando estatus: "+devolutionRequest.getFolio());
                        devolutionRequest1.status=devolutionRequest.getStatus();
                        conexion.devolutionRequestDao().updateDevolutionRequest(devolutionRequest1);
                    }
                }
                for(DevolutionSubSalesResponseInitData devolutionSubSale : modeOffline.getDevolutionsSubSales()){
                    DevolutionSubSale devolutionSubSale1 = conexion.devolutionSubSaleDao().findDevolutionSubSaleBySubSaleId(devolutionSubSale.getSubSaleIdIdentifier());
                    if(devolutionSubSale1==null){
                        System.out.println("Bajando devolución: "+devolutionSubSale.getSaleId());
                        DevolutionSubSale devolutionSubSale2=new DevolutionSubSale();
                        SubSale subSale = conexion.subSalesDao().getSubSaleBySubSaleId(devolutionSubSale.getSubSaleIdIdentifier());
                        devolutionSubSale2.quantity=devolutionSubSale.getQuantity();
                        devolutionSubSale2.weightStandar=subSale.weightStandar;
                        devolutionSubSale2.uniMed=subSale.uniMed;
                        devolutionSubSale2.subSaleId=devolutionSubSale.getSubSaleIdIdentifier();
                        devolutionSubSale2.productName=subSale.productName;
                        devolutionSubSale2.productId=subSale.productId;
                        devolutionSubSale2.productKey=subSale.productKey;
                        devolutionSubSale2.price=devolutionSubSale.getAmount();
                        devolutionSubSale2.presentationId=devolutionSubSale.getPresentationId();
                        devolutionSubSale2.devolutionRequestId=devolutionSubSale.getDevolutionRequestId();
                        devolutionSubSale2.productPresentationType=subSale.productPresentationType;
                        conexion.devolutionSubSaleDao().insertAll(devolutionSubSale2);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(clientsUpdated.size()>0){
                            presenter.updateStatusSincronizedClient(clientsUpdated);
                        }
                        modalSincronizationEnd();
                    }
                });
            }
        });

    }

    String currentDay="";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.MONDAY:
                currentDay="monday";
                break;
            case Calendar.TUESDAY:
                currentDay="tuesday";
                break;
            case Calendar.WEDNESDAY:
                currentDay="wednesday";
                break;
            case Calendar.THURSDAY:
                currentDay="thursday";
                break;
            case Calendar.FRIDAY:
                currentDay="friday";
                break;
            case Calendar.SATURDAY:
                currentDay="saturday";
                break;
            case Calendar.SUNDAY:
                currentDay="sunday";
                break;
        }

        this.setClientVisits(currentDay,"",false);
        dateSincronization=null;
        actionActivated=false;
        checkAllUnsincronized();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    void search(){
            if(filtered==false) {
                filtered = true;
                this.buscarCliente.setText("Cancelar");

                this.currentHint = this.inputSearch.getEditText().getText().toString();
            }else{
                if(!currentHint.equals(this.inputSearch.getEditText().getText().toString())) {
                    filtered = true;

                    this.buscarCliente.setText("Cancelar");
                    this.currentHint = this.inputSearch.getEditText().getText().toString();
                }else {
                    filtered = false;

                    this.buscarCliente.setText("Buscar");
                }
            }
                setClientVisits(currentDay,this.currentHint,filtered);
    }

    @Override
    public void goToHome(){
        this.navController.navigate(VisitsViewDirections.actionCotizacionesViewToHomeView(this.userName).setUserName(this.userName).setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }
    void goToClient(){
        this.navController.navigate(VisitsViewDirections.actionCotizacionesViewToClientView(this.userName).setUserName(this.userName).setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }
    void goToPedidos(){
        this.navController.navigate(VisitsViewDirections.actionCotizacionesViewToPedidoView(this.userName).setUserName(this.userName).setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void goToHistory(){
        this.navController.navigate(VisitsViewDirections.actionVisitsViewToSalesView(this.userName).setUserName(this.userName).setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }
    void goToVisitMap(){
        this.navController.navigate(VisitsViewDirections.actionVisitsViewToVisitsMapView(this.userName).setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void logout(){
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesion")
                .setMessage("¿Está seguro que desea cerrar sesion?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.logout();
                    }
                }).setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                    }
                }).setCancelable(false).create();
        dialog.show();
    }
    @Override
    public void goToLogin(){
        this.navController.navigate(VisitsViewDirections.actionCotizacionesViewToLoginView());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Logout_button:
                logout();
                break;
            case R.id.buscarClienteButton:

                search();
                break;
            case R.id.downloadChanges:
                if(!isLoading) {
                    setUploadingStatus(true);
                    LocalDateTime ldt = LocalDateTime.now();
                    DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    System.out.println("FECHA_DE_HOY: " + ldt);
                    String dateParsed = formmat1.format(ldt);
                    presenter.getDataInitial(viewModelStore.getStore().getSellerId(), dateParsed);
                    modalSincronizationStart("Bajando cambios");
                }
                break;
            case R.id.uploadChanges:
                    if(!isLoading) {

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dateSincronization = dateFormat.format(calendar.getTime());
                        actionActivated = true;
                        //checkSalesUnSincronized();
                        firstStep();
                    }

                break;
            case R.id.resincronize:

                    resinconizeMethod();

                break;
            case R.id.goToVisitMap:
                goToVisitMap();
                break;
        }
    }
    AlertDialog dialogResincronize=null;
     void resinconizeMethod(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.resincronize_modal,null);
        builder.setView(view);
        ImageView reDownload = view.findViewById(R.id.reDownload);
        ImageView reUpload = view.findViewById(R.id.reUpload);
        reDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResincronize.dismiss();
                showDatePicker(1);
            }
        });
        reUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResincronize.dismiss();
                showDatePicker(2);
            }
        });
        Button cancelResincronization= view.findViewById(R.id.cancelResincronization);
        builder.setPositiveButton(null,null);
        builder.setNegativeButton(null,null);
        dialogResincronize=builder.create();
        cancelResincronization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResincronize.dismiss();
            }
        });

        dialogResincronize.show();
     }

    void showDatePicker(Integer action){
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = String.valueOf(month+1);
                String day = String.valueOf(dayOfMonth);
                if((month+1)<10) monthStr="0"+monthStr;
                if(dayOfMonth<10) day="0"+day;
                String dateSelected = year+"-"+monthStr+"-"+day;
                if(action==1){
                    setUploadingStatus(true);
                    modalSincronizationStart("Sincronizando");
                    presenter.getDataInitial(viewModelStore.getStore().getSellerId(),dateSelected);
                }else if(action==2){
                    dateSincronization=dateSelected;
                    actionActivated=true;
                    checkSalesUnSincronized();
                }
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sincronizeComplete() {
        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);
    }

    @Override
    public void sincronizeError() {
        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Occurrio un error al sincronizar, Verifica tu conexión",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reloadVisits() {
        simpleList.removeAllViewsInLayout();
        circularProgressIndicator.setVisibility(View.VISIBLE);
        this.presenter.getClientsVisits(viewModelStore.getStore().getSellerId());
        isLoading=true;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setClientVisits(String dayStr,String hint,Boolean filter) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    List<Client> clientsToVisit=new ArrayList<>();
                    if(dayStr.equals("monday")){
                        List<Client> clients = conexion.clientDao().getClientsMonday(viewModelStore.getStore().getSellerId());
                        for(Client client :clients){
                            if(filter){
                                if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)){
                                    clientsToVisit.add(client);
                                }
                            }else{
                                clientsToVisit.add(client);
                            }

                        }
                    }else if(dayStr.equals("tuesday")){
                        List<Client> clients = conexion.clientDao().getClientsTuesday(viewModelStore.getStore().getSellerId());
                        for(Client client :clients){
                            if(filter){
                                if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)){
                                    clientsToVisit.add(client);
                                }
                            }else{
                                clientsToVisit.add(client);
                            }

                        }
                    }else if(dayStr.equals("wednesday")){
                        List<Client> clients = conexion.clientDao().getClientsWednesday(viewModelStore.getStore().getSellerId());
                        for(Client client :clients){
                            if(filter){
                                if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)){
                                    clientsToVisit.add(client);
                                }
                            }else{
                                clientsToVisit.add(client);
                            }
                        }
                    }else if(dayStr.equals("thursday")) {
                        List<Client> clients = conexion.clientDao().getClientsThursday(viewModelStore.getStore().getSellerId());
                        for(Client client :clients){
                            if(filter){
                                if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)){
                                    clientsToVisit.add(client);
                                }
                            }else{
                                clientsToVisit.add(client);
                            }
                        }
                    }else if(dayStr.equals("friday")){
                        List<Client> clients = conexion.clientDao().getClientsFriday(viewModelStore.getStore().getSellerId());
                        for(Client client :clients){
                            if(filter){
                                if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)){
                                    clientsToVisit.add(client);
                                }
                            }else{
                                clientsToVisit.add(client);
                            }
                        }
                    }else if(dayStr.equals("saturday")){
                        List<Client> clients = conexion.clientDao().getClientsSaturday(viewModelStore.getStore().getSellerId());
                        for(Client client :clients){
                            if(filter){
                                if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)){
                                    clientsToVisit.add(client);
                                }
                            }else{
                                clientsToVisit.add(client);
                            }
                        }
                    }else{
                        List<Client> clients = conexion.clientDao().getClientsSunday(viewModelStore.getStore().getSellerId());
                        for(Client client :clients){
                            if(filter){
                                if(client.name.contains(hint) || String.valueOf(client.clientKey).contains(hint)){
                                    clientsToVisit.add(client);
                                }
                            }else{
                                clientsToVisit.add(client);
                            }
                        }
                    }

                    System.out.println("Total de clientes: "+dayStr+" "+clientsToVisit.size());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(clientsToVisit.size()>0) {
                                List<String> clients = clientsToVisit.stream().map(client -> client.clientKey + " " + client.name).collect(Collectors.toList());
                                String[] arr = new String[clientsToVisit.size()];
                                clients.toArray(arr);
                                ArrayAdapter<String> customAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arr) {
                                    @NonNull
                                    @Override
                                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                        tv.setTextColor(Color.WHITE);
                                        return view;
                                    }
                                };
                                simpleList.setAdapter(customAdapter);
                            }
                        }
                    });
                }
            });
    }

    @Override
    public void showPresentationProduct(List<ProductPresentation> presentations,String presentationName) {

        String[] presentationsToSet =new String[presentations.size()];

        System.out.println("Tamaño presentacion: "+presentations.size());
        for(int i=0;i<presentations.size();i++){
            ProductPresentation productPresentation = presentations.get(i);
            presentationsToSet[i]=productPresentation.getPresentationType()+" "+productPresentation.getPresentationPricePublic()+" se vende por: "+(productPresentation.isPz()==true?"Pieza":"Kilo");
            System.out.println("presentacion: "+presentationsToSet[i]);
        }
        System.out.println("Tamaño presentacion 2: "+presentationsToSet.length);
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Presentaciones de : "+presentationName)
                .setCancelable(false)
                .setItems(presentationsToSet, null)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).create();
        dialog.show();
        circularProgressIndicator.setVisibility(View.INVISIBLE);
        isLoading=false;
    }

    @Override
    public void genericMessage(String title,String msg){

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //reloadVisits();
                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    @Override
    public void setClientVisited(ClientDTO clientVisited) {
        this.clientInVisit=clientVisited;
    }

    AlertDialog dialogSinc=null;
    @Override
    public void modalSincronizationStart(String msg){
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



    @Override
    public void modalMessageOperation(String msg){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.modal_success_operation,null);
        TextView messageLoad = view.findViewById(R.id.message_load);
        Button btnAccept = view.findViewById(R.id.acceptButtonModal);
        messageLoad.setText(msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog modalInfo= builder.create();
        modalInfo.show();
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalInfo.dismiss();
            }
        });
    }


    public void modalSincronizationEnd(){
        if(this.dialogSinc!=null && this.dialogSinc.isShowing()){
            this.dialogSinc.dismiss();
        }
    }
    /** registering clients unsincronized */
    @Override
    public void firstStep(){
        modalSincronizationStart("Subiendo cambios");
        setUploadingStatus(true);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Client> clientsUnsincronized =conexion.clientDao().getAllClientsUnsicronized();
                List<ClientV2Request> requestRegister = new ArrayList<>();
                for(Client client : clientsUnsincronized) {
                    if (client.clientRovId==null || client.clientRovId==0) {
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
                        presenter.tryRegisterClients(requestRegister);
                    }
                });
            }
        });
    }
    /** updating clients registered to database*/
    @Override
    public void setClientsRegisters(List<ClientV2Response> clientsRegistered) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
    @Override
    public void secondStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Client> clientsUnsincronized =conexion.clientDao().getAllClientsUnsicronized();
                List<ClientV2UpdateRequest> clientV2UpdateRequestList = new ArrayList<>();
                for(Client client : clientsUnsincronized) {
                    System.out.println("ClientSincronized: "+client.sincronized);
                    System.out.println("ClientRovId: "+client.clientRovId);
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
                        presenter.updateCustomerV2(clientV2UpdateRequestList);
                    }
                });
            }
        });
    }
    /** Updating clients updated to database*/
    @Override
    public void setClientsUpdated(List<ClientV2UpdateResponse> clientsUpdated) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
    @Override
    public void thirdStep(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
                        presenter.registerVisitsV2(requests);
                    }
                });
            }
        });
    }
    /** Updating clients visits in database */
    @Override
    public void setClientVisitedRegistered(List<ClientV2VisitResponse> clientV2Visit) {
        System.out.println("Actualizando estatus de visitas");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                for(ClientV2VisitResponse response : clientV2Visit){
                    ClientVisit clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(response.getClientId(),response.getDate());
                    clientVisit.sincronized=true;
                    conexion.clientVisitDao().updateClientVisit(clientVisit);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Iniciando proceso de ventas");
                        checkSalesUnSincronized();
                    }
                });
            }
        });
    }
    @Override
    public void checkSalesUnSincronized(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Sale> sales=new ArrayList<>();
                if(dateSincronization==null) {
                    sales = conexion.saleDao().getAllSalesUnsincronizedBySeller(viewModelStore.getStore().getSellerId());
                }else{
                    String date1=dateSincronization+"T00:00:00.000Z";
                    String date2=dateSincronization+"T23:59:59.000Z";
                    sales = conexion.saleDao().getAllSalesUnsincronizedByDate(date1,date2);
                }
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
                if(dateSincronization==null) {
                    devolutionsRequests = conexion.devolutionRequestDao().getAllUnsincronized();
                }else{
                    devolutionsRequests = conexion.devolutionRequestDao().getAllBetweenDateRegisters(dateSincronization+"T00:00:00.000Z",dateSincronization+"T23:59:59.000Z");
                }
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
                        System.out.println("Sincronizando notas");
                        if(modeOfflineSMS.size()>0 || debtsPayed.size()>0) {
                            presenter.sincronizeSales(modeOfflineSMS,debtsPayed,devolutionRequestServers,viewModelStore.getStore().getSellerId());
                        }else{
                            setUploadingStatus(false);
                            modalSincronizationEnd();
                            modalMessageOperation("Sincronización exitosa");
                            checkAllUnsincronized();
                        }
                    }
                });
            }
        });
    }

    void checkAllSaleCreditsPayed(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Sale> sales = conexion.saleDao().getAllDebts();
                List<String> folios = new ArrayList<>();
                for(Sale sale : sales){
                    folios.add(sale.folio);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        presenter.checkSalesCredit(folios);
                    }
                });
            }
        });
    }

    @Override
    public void setAllSalesCreditPaymentStatus(List<SaleCreditPayedResponse> payments){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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
                        setWithoutChangesToUpload();
                        modalSincronizationEnd();
                        modalMessageOperation("Sincronización exitosa");
                    }
                });
            }
        });
    }

    void checkAllUnsincronized(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Client> clientsUnsincronized = conexion.clientDao().getAllClientsUnsicronized();
                List<ClientVisit> clientVisitsUnsincronized = conexion.clientVisitDao().getClientVisitUnsincronized();
                List<Sale> sales=conexion.saleDao().getAllSalesUnsincronizedBySeller(viewModelStore.getStore().getSellerId());
                List<DevolutionRequest> devolutionsRequests = conexion.devolutionRequestDao().getAllUnsincronized();
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
                        if(clientsUnsincronized.size()>0 || clientVisitsUnsincronized.size()>0 || sales.size()>0 || devolutionsRequests.size()>0 || debtsPayed.size()>0) {
                            setWithChangesToUpload();
                        }else{
                            //setWithChangesToUpload();
                            setWithoutChangesToUpload();
                        }
                    }
                });
            }
        });
    }

    private void setWithoutChangesToUpload(){
        download.setEnabled(true);
        download.setColorFilter(Color.parseColor("#2FBF34"));
        upload.setColorFilter(Color.GRAY);
        upload.setEnabled(false);
        showNotificationSincronization("Sistema de sincronización, Nada por sincronizar...");
    }

    private void setWithChangesToUpload(){
        download.setEnabled(false);
        download.setColorFilter(Color.GRAY);
        upload.setColorFilter(Color.parseColor("#2FBF34"));
        upload.setEnabled(true);
        showNotificationSincronization("Hay cambios por sincronizar....");
    }

    @Override
    public void setUploadingStatus(boolean flag){
        System.out.println("Flag: "+flag);

        if(flag){
            circularProgressIndicator.setVisibility(View.VISIBLE);
            isLoading=true;
            upload.setEnabled(false);
            download.setEnabled(false);
        }else{
            circularProgressIndicator.setVisibility(View.GONE);
            isLoading=false;
            upload.setEnabled(true);
            download.setEnabled(true);
        }
    }

    @Override
    public void completeSincronzation(SincronizationResponse sincronizationResponse) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
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


    @Override
    public void showNotificationSincronization(String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "rovisapi")
                .setSmallIcon(R.drawable.ic_logorov)
                .setContentTitle("Sistema Rovianda")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void hiddeNotificationSincronizastion() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.cancel(1);
    }


}
