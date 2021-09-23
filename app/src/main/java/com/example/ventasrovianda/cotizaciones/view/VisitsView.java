package com.example.ventasrovianda.cotizaciones.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ImageView;
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
import com.example.ventasrovianda.Utils.bd.entities.Client;
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
    TextView logoutButton,endDayButton,eatTimeButton;
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
    ClientOfflineMode[] clientsVisits=null;
    ClientOfflineMode[] clientsVisitsTemp=null;
    boolean filtered=false;

    ImageView download,upload;

    ViewModelStore viewModelStore;
    Gson parser;

    Boolean sincronized=false;
    ImageView resincronizeButton;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.visits_layout,null);

        this.clientInVisit = VisitsViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = VisitsViewArgs.fromBundle(getArguments()).getUserName();
        this.userNameTextView = v.findViewById(R.id.userName);
        this.userNameTextView.setText("Usuario: "+this.userName);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));
        simpleList = (ListView) v.findViewById(R.id.listClientsVisits);
        this.navController = NavHostFragment.findNavController(this);
        this.logoutButton = v.findViewById(R.id.Logout_button);
        this.logoutButton.setOnClickListener(this);
        this.printerButton = v.findViewById(R.id.printerButton);
        this.printerButton.setVisibility(View.INVISIBLE);
        this.printerButton.setOnClickListener(this);
        this.circularProgressIndicator = v.findViewById(R.id.loginLoadingSpinner);
        //circularProgressIndicator.setVisibility(View.VISIBLE);
        this.download = v.findViewById(R.id.downloadChanges);
        this.upload = v.findViewById(R.id.uploadChanges);
        this.download.setVisibility(View.VISIBLE);
        this.upload.setVisibility(View.VISIBLE);
        this.download.setOnClickListener(this);
        this.upload.setOnClickListener(this);
        this.presenter = new VisitsPresenter(getContext(),this);
        this.resincronizeButton=v.findViewById(R.id.resincronizeButton);
        this.resincronizeButton.setVisibility(View.VISIBLE);
        this.resincronizeButton.setOnClickListener(this);
        //this.presenter.getClientsVisits();
        //isLoading=true;
        homeButton = v.findViewById(R.id.bottom_navigation_cotizaciones);
        homeButton.setSelectedItemId(R.id.visitas_section);
        this.endDayButton = v.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.eatTimeButton = v.findViewById(R.id.eat_time_button);
        this.eatTimeButton.setOnClickListener(this);
        this.endDayButton.setVisibility(View.GONE);
        this.eatTimeButton.setVisibility(View.GONE);
        this.bluetoothDeviceSerializable = VisitsViewArgs.fromBundle(getArguments()).getPrinterDevice();

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

        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);
        executor.execute(new Runnable() {
            @Override
            public void run() {

                UserDataInitial userDataInitial = viewModelStore.getAppDatabase().userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                if (userDataInitial == null) {
                    UserDataInitial userDataInitial1 = new UserDataInitial();
                    userDataInitial1.name = modeOffline.getName();
                    userDataInitial1.count = modeOffline.getCount();
                    userDataInitial1.email = modeOffline.getEmail();
                    userDataInitial1.lastSincronization = modeOffline.getLastSicronization();
                    userDataInitial1.logedIn = true;
                    userDataInitial1.nomenclature = modeOffline.getNomenclature();
                    userDataInitial1.uid = modeOffline.getUid();
                    userDataInitial1.password = modeOffline.getPassword();
                    viewModelStore.getAppDatabase().userDataInitialDao().insertUserDataDetail(userDataInitial1);
                    System.out.println("Data init user installed");
                }
                for (ProductToSaveEntity productToSave : modeOffline.getProducts()) {
                    Product product = viewModelStore.getAppDatabase().productDao().getProductByProduct(productToSave.getProductKey(), viewModelStore.getStore().getSellerId());
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
                        viewModelStore.getAppDatabase().productDao().insertProduct(product1);
                        System.out.println("Product installed: " + product1.name + " " + product1.presentationName);
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
                        viewModelStore.getAppDatabase().productDao().updateProduct(product);
                    }

                }

                List<Integer> clientsUpdated = new ArrayList<>();
                for (ClientToSaveEntity clientItem : modeOffline.getClients()) {
                    Client client = viewModelStore.getAppDatabase().clientDao().getClientBydId(clientItem.getClientId());
                    if (client == null) {
                        Client clientEntity = new Client();
                        clientEntity.clientId = clientItem.getClientId();
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
                        viewModelStore.getAppDatabase().clientDao().insertClient(clientEntity);
                        System.out.println("Client installed: " + clientEntity.name);
                        if (clientItem.getKeyClient() == 1175) {
                            System.out.println("Client: " + clientItem.getKeyClient());
                        }
                    } else {
                        if (clientItem.getModified()) {
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
                            viewModelStore.getAppDatabase().clientDao().updateClient(client);
                            clientsUpdated.add(clientItem.getClientId());
                        }
                    }
                }
                List<DebtOfflineNewVersion> currentSalesOfDay = modeOffline.getSalesOfDay();
                if (currentSalesOfDay != null) {

                    for (DebtOfflineNewVersion saleOfDay : currentSalesOfDay) {
                        Sale saleEntity = viewModelStore.getAppDatabase().saleDao().getByFolio(saleOfDay.getFolio());
                        if (saleEntity == null) {
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
                            viewModelStore.getAppDatabase().saleDao().insertAll(sale);
                            for (SubSaleOfflineNewVersion subSaleOffline : saleOfDay.getProducts()) {
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
                                subSale.folio = saleOfDay.getFolio();
                                subSale.presentationId = subSaleOffline.getPresentationId();
                                subSale.productId = subSaleOffline.getProductId();
                                subSale.productKey = subSaleOffline.getProductKey();
                                subSale.subSaleServerId = subSaleOffline.getSubSaleServerId();
                                viewModelStore.getAppDatabase().subSalesDao().insertAllSubSales(subSale);
                            }
                        } else {
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
                            viewModelStore.getAppDatabase().saleDao().updateSale(saleEntity);
                        }
                    }
                }

                List<DebtOfflineNewVersion> debts = modeOffline.getDebts();
                if (debts != null) {

                for (DebtOfflineNewVersion debt : debts) {
                    Sale sale = viewModelStore.getAppDatabase().saleDao().getByFolio(debt.getFolio());
                    if (sale == null) {
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
                        viewModelStore.getAppDatabase().saleDao().insertAll(saleToSave);
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
                            viewModelStore.getAppDatabase().subSalesDao().insertAllSubSales(subSale);
                        }
                    }
                }
            }

                for(DevolutionResponseInitData devolutionRequest : modeOffline.getDevolutionsRequest()){
                    DevolutionRequest devolutionRequest1 = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(devolutionRequest.getFolio());
                    if(devolutionRequest1==null){
                        DevolutionRequest devolutionRequest2 = new DevolutionRequest();
                        devolutionRequest2.status=devolutionRequest.getStatus();
                        devolutionRequest2.description=devolutionRequest.getObservations();
                        devolutionRequest2.sincronized=1;
                        devolutionRequest2.devolutionRequestId=devolutionRequest.getDevolutionAppRequestId();
                        devolutionRequest2.folio=devolutionRequest.getFolio();
                        devolutionRequest2.typeDevolution=devolutionRequest.getTypeDevolution();
                        devolutionRequest2.createAt=devolutionRequest.getCreateAt();
                        viewModelStore.getAppDatabase().devolutionRequestDao().insertAll(devolutionRequest2);
                    }else{
                        devolutionRequest1.status=devolutionRequest.getStatus();
                        viewModelStore.getAppDatabase().devolutionRequestDao().updateDevolutionRequest(devolutionRequest1);
                    }
                }
                for(DevolutionSubSalesResponseInitData devolutionSubSale : modeOffline.getDevolutionsSubSales()){
                    DevolutionSubSale devolutionSubSale1 = viewModelStore.getAppDatabase().devolutionSubSaleDao().findDevolutionSubSaleBySubSaleId(devolutionSubSale.getSubSaleIdIdentifier());
                    if(devolutionSubSale1==null){
                        DevolutionSubSale devolutionSubSale2=new DevolutionSubSale();
                        SubSale subSale = viewModelStore.getAppDatabase().subSalesDao().getSubSaleBySubSaleId(devolutionSubSale.getSubSaleIdIdentifier());
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
                        viewModelStore.getAppDatabase().devolutionSubSaleDao().insertAll(devolutionSubSale2);
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


    /*@RequiresApi(api = Build.VERSION_CODES.N)

    public void setModeOfflineBackup(ModeOfflineModel modeOffline) {

        String data = parser.toJson(modeOffline);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());

        try {

            File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
            if (!root.exists()) {
                root.mkdirs();
            }
            Boolean already = true;
            File gpxfile=null;
            int i=0;
            while(already) {
                gpxfile = new File(root, "offline-" + dateParsed + "-backup"+i+".rovi");
                if(gpxfile.exists()){
                    i++;
                }else{
                    already=false;
                }
            }

            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
            this.sincronized=true;
            viewModelStore.saveStore(new ModeOfflineModel());
            download.setEnabled(true);
            download.setColorFilter(Color.parseColor("#2FBF34"));
            upload.setEnabled(false);
            upload.setColorFilter(Color.GRAY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    String currentDay="";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        /*if(checkOffline()){
            this.setModeOffline(viewModelStore.getStore());
            checkIfOffline();
            ClientOfflineMode[] array = new ClientOfflineMode[viewModelStore.getStore().getClientsToVisit().size()];
            viewModelStore.getStore().getClientsToVisit().toArray(array);
            clientsVisits=array;

        }*/
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
        checkSalesUnSincronized(false,null);
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
                        isLoading=true;
                        circularProgressIndicator.setVisibility(View.VISIBLE);
                        LocalDateTime ldt = LocalDateTime.now();
                        DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        System.out.println("FECHA_DE_HOY: "+ldt);
                        String dateParsed = formmat1.format(ldt);
                        presenter.getDataInitial(viewModelStore.getStore().getSellerId(),dateParsed);
                        modalSincronizationStart();
                    }

                break;
            case R.id.uploadChanges:
                if(!isLoading){
                    isLoading=true;
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    checkSalesUnSincronized(true,null);
                }
                break;
            case R.id.resincronizeButton:
                resinconizeMethod();
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
                    isLoading=true;
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    modalSincronizationStart();
                    presenter.getDataInitial(viewModelStore.getStore().getSellerId(),dateSelected);
                }else if(action==2){
                    isLoading=true;
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    checkSalesUnSincronized(true,dateSelected);
                }
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
    }
/*
    @RequiresApi(api = Build.VERSION_CODES.N)
    ModeOfflineSincronize generateModeOfflineRequest(){
        ModeOfflineSincronize modeOfflineSincronize= new ModeOfflineSincronize();

        if(viewModelStore.getStore().getSalesMaked()!=null) {
            modeOfflineSincronize.setSalesMaked(viewModelStore.getStore().getSalesMaked().stream().map(sale -> {
                ModeOfflineSM modeOfflineSM = new ModeOfflineSM();
                modeOfflineSM.setAmount(sale.getAmount());
                modeOfflineSM.setClientId(sale.getClientId());
                modeOfflineSM.setCredit(sale.getCredit());
                modeOfflineSM.setDate(sale.getDate());
                modeOfflineSM.setFolio(sale.getFolio());
                modeOfflineSM.setPayedWith(sale.getPayed());
                modeOfflineSM.setSellerId(sale.getSellerId());
                modeOfflineSM.setStatus(sale.getStatus());
                modeOfflineSM.setStatusStr(sale.getStatusStr());
                modeOfflineSM.setTypeSale(sale.getTypeSale());
                modeOfflineSM.setProducts(
                        sale.getProducts().stream().map(prod -> {
                            ModeOfflineSMP modeOfflineSMP = new ModeOfflineSMP();
                            modeOfflineSMP.setAmount(prod.getPrice() * prod.getQuantity());
                            modeOfflineSMP.setPresentationId(prod.getPresentationId());
                            modeOfflineSMP.setProductId(prod.getProductId());
                            modeOfflineSMP.setQuantity(prod.getQuantity());
                            return modeOfflineSMP;
                        }).collect(Collectors.toList())
                );
                return modeOfflineSM;
            }).collect(Collectors.toList()));
        }else{
            modeOfflineSincronize.setSalesMaked(new ArrayList<>());
        }
        if(viewModelStore.getStore().getSales()!=null) {
            modeOfflineSincronize.setSales(
                    viewModelStore.getStore().getSales().stream().map(sale->{
                        ModeOfflineS modeOfflineS = new ModeOfflineS();
                        modeOfflineS.setDate(sale.getDate());
                        modeOfflineS.setFolio(sale.getFolio());
                        modeOfflineS.setSaleId(sale.getSaleId());
                        modeOfflineS.setStatus(sale.getStatus());
                        modeOfflineS.setStatusStr(sale.getStatusStr());
                        return modeOfflineS;
                    }).collect(Collectors.toList())
            );
        }else{
            modeOfflineSincronize.setSales(new ArrayList<>());
        }

        if(viewModelStore.getStore().getDebts()!=null){
            modeOfflineSincronize.setDebts(
                    viewModelStore.getStore().getDebts().stream().map(deb->{
                        ModeOfflineDebts modeOfflineDebts = new ModeOfflineDebts();
                        modeOfflineDebts.setDate(deb.getDate());
                        modeOfflineDebts.setFolio(deb.getFolio());
                        modeOfflineDebts.setSaleId(deb.getSaleId());
                        modeOfflineDebts.setTypeSale(deb.getTypeSale());
                        modeOfflineDebts.setStatus(deb.getStatus());
                        return modeOfflineDebts;
                    }).collect(Collectors.toList())
            );
        }else{
            modeOfflineSincronize.setDebts(new ArrayList<>());
        }

        return modeOfflineSincronize;
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sincronizeComplete() {

        //setModeOfflineBackup(viewModelStore.getStore());
        /*Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
        if (!root.exists()) {
            root.mkdirs();
        }
        File gpxfile = new File(root, "offline-"+dateParsed+".rovi");
        gpxfile.delete();*/
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
                    List<Client> clientsToVisit=new ArrayList<>();
                    if(dayStr.equals("monday")){
                        List<Client> clients = viewModelStore.getAppDatabase().clientDao().getClientsMonday(viewModelStore.getStore().getSellerId());
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
                        List<Client> clients = viewModelStore.getAppDatabase().clientDao().getClientsTuesday(viewModelStore.getStore().getSellerId());
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
                        List<Client> clients = viewModelStore.getAppDatabase().clientDao().getClientsWednesday(viewModelStore.getStore().getSellerId());
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
                        List<Client> clients = viewModelStore.getAppDatabase().clientDao().getClientsThursday(viewModelStore.getStore().getSellerId());
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
                        List<Client> clients = viewModelStore.getAppDatabase().clientDao().getClientsFriday(viewModelStore.getStore().getSellerId());
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
                        List<Client> clients = viewModelStore.getAppDatabase().clientDao().getClientsSaturday(viewModelStore.getStore().getSellerId());
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
                        List<Client> clients = viewModelStore.getAppDatabase().clientDao().getClientsSunday(viewModelStore.getStore().getSellerId());
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
                        reloadVisits();
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
    public void modalSincronizationStart(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_load_message,null);
        TextView messageLoad = view.findViewById(R.id.message_load);
        messageLoad.setText("Sincronizando...");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        this.dialogSinc=builder.create();
        this.dialogSinc.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void modalSincronizationEnd(){
        if(this.dialogSinc!=null && this.dialogSinc.isShowing()){
            this.dialogSinc.dismiss();
        }
        setClientVisits(currentDay,"",false);
    }

    void checkSalesUnSincronized(Boolean action,String date){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Sale> sales=new ArrayList<>();
                if(date==null) {
                    sales = viewModelStore.getAppDatabase().saleDao().getAllSalesUnsincronized();
                }else{
                    String date1=date+"T00:00:00.000Z";
                    String date2=date+"T23:59:59.000Z";
                    sales = viewModelStore.getAppDatabase().saleDao().getAllSalesUnsincronizedByDate(date1,date2);
                }
                List<ModeOfflineSM> modeOfflineSMS = new ArrayList<>();
                for(Sale sale : sales) {
                    System.out.println("Sale without sincronization: "+sale.folio);
                    System.out.println("status: "+sale.statusStr);
                    System.out.println("modified: "+sale.modified);
                    System.out.println("sincronized: "+sale.sincronized);
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
                    List<SubSale> subSales = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(sale.folio);
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
                if(date==null) {
                    devolutionsRequests = viewModelStore.getAppDatabase().devolutionRequestDao().getAllUnsincronized();
                }else{
                    devolutionsRequests = viewModelStore.getAppDatabase().devolutionRequestDao().getAllBetweenDateRegisters(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
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
                    List<DevolutionSubSale> devolutionSubSales = viewModelStore.getAppDatabase().devolutionSubSaleDao().findByDevolutionRequestId(devolutionRequest.devolutionRequestId);
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
                    List<SubSale> subSales = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(devolutionRequest.folio);
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
                List<Debt> debts = viewModelStore.getAppDatabase().debtDao().getAllSalesWithoutSincronization();
                List<DebPayedRequest> debtsPayed = new ArrayList<>();
                for(Debt debt : debts){
                    if(debt.sincronized==false && debt.deleted==false){
                        DebPayedRequest debPayedRequest = new DebPayedRequest();
                        Sale sale = viewModelStore.getAppDatabase().saleDao().getByFolio(debt.folio);
                        debPayedRequest.setAmountPayed(sale.amount);
                        debPayedRequest.setDatePayed(debt.createAt);
                        debPayedRequest.setFolio(sale.folio);
                        debPayedRequest.setPayedType(debt.payedType);
                        debtsPayed.add(debPayedRequest);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(modeOfflineSMS.size()>0 || debtsPayed.size()>0) {
                            if(action) {
                                showNotificationSincronization("Sistema de sincronización,Sincronizando...");
                                presenter.sincronizeSales(modeOfflineSMS,debtsPayed,devolutionRequestServers,viewModelStore.getStore().getSellerId());
                            }else{
                                download.setEnabled(false);
                                download.setColorFilter(Color.GRAY);
                                upload.setEnabled(true);
                                upload.setColorFilter(Color.parseColor("#2FBF34"));
                                showNotificationSincronization("Hay ventas sin sincronizar...");
                            }
                        }else{
                            download.setEnabled(true);
                            download.setColorFilter(Color.parseColor("#2FBF34"));
                            upload.setColorFilter(Color.GRAY);
                            upload.setEnabled(false);
                            showNotificationSincronization("Sistema de sincronización,Nada por sincronizar...");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void completeSincronzation(SincronizationResponse sincronizationResponse) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                for(int i=0;i<sincronizationResponse.getSalesSincronized().size();i++){
                    viewModelStore.getAppDatabase().saleDao().updateSaleId(sincronizationResponse.getSalesSincronized().get(i).getSaleId(),sincronizationResponse.getSalesSincronized().get(i).getFolio());
                }
                for(String folio  : sincronizationResponse.getDebtsSicronized()){
                    Debt debt = viewModelStore.getAppDatabase().debtDao().getDebtByFolio(folio);
                    debt.sincronized=true;
                    viewModelStore.getAppDatabase().debtDao().updateDebtSincronization(debt);
                }
                for(String folio : sincronizationResponse.getDevolutionsSincronized()){
                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(folio);
                    if(devolutionRequest!=null) {
                        devolutionRequest.sincronized = 1;
                        viewModelStore.getAppDatabase().devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                    }
                }

                for(String folio : sincronizationResponse.getDevolutionsAccepted()){
                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(folio);
                    devolutionRequest.status="ACCEPTED";
                    viewModelStore.getAppDatabase().devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }
                for(String folio : sincronizationResponse.getDevolutionsRejected()){
                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(folio);
                    devolutionRequest.status="DECLINED";
                    viewModelStore.getAppDatabase().devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading=false;
                        circularProgressIndicator.setVisibility(View.GONE);
                        System.out.println("Sincronized Complete");
                        download.setEnabled(true);
                        download.setColorFilter(Color.parseColor("#2FBF34"));
                        upload.setColorFilter(Color.GRAY);
                        upload.setEnabled(false);
                        showNotificationSincronization("Sistema de sincronización,Nada por sincronizar...");
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
