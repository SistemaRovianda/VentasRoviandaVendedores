package com.example.ventasrovianda.home.view;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.DatePickerFragment;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientOfflineMode;
import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.DevolutionSubSaleRequestServer;
import com.example.ventasrovianda.Utils.Models.InventoryOfflineMode;
import com.example.ventasrovianda.Utils.Models.ModeOfflineDebts;
import com.example.ventasrovianda.Utils.Models.ModeOfflineS;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSMP;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;
import com.example.ventasrovianda.Utils.Models.ProductSaleDTO;
import com.example.ventasrovianda.Utils.Models.ProductsOfflineMode;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleOfflineMode;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.Models.SincronizeSingleSaleSuccess;
import com.example.ventasrovianda.Utils.NumberDecimalFilter;
import com.example.ventasrovianda.Utils.PrinterUtil;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.Utils.bd.entities.Debt;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;
import com.example.ventasrovianda.Utils.bd.entities.Product;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.Utils.bd.entities.UserDataInitial;
import com.example.ventasrovianda.home.adapters.AdapterListProductSale;
import com.example.ventasrovianda.home.presenter.HomePresenter;
import com.example.ventasrovianda.home.presenter.HomePresenterContract;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;


public class HomeView extends Fragment implements View.OnClickListener,HomeViewContract {
    private static final long START_TIME_IN_MILLIS = 1800000;
    BottomNavigationView homeButton;
    MaterialButton buscarClienteButton, agregarProductoButton, cobrarButton, stopEatTimeButton;

    TextInputLayout clientInput, keyProductInput, weightProduct;
    NavController navController;
    CircularProgressIndicator circularProgressIndicator;

    TextView logoutButton, clientSaeKey, clientName, endDayButton, eatTimeButton;
    ImageView printerButton;
    HomePresenterContract presenter;
    String printerName;

    BluetoothAdapter bluetoothAdapter;

    ClientDTO currentClient = null;
    ClientOfflineMode currentClientOffline = null;
    ClientDTO selectedClientTovisit = null;
    ListView listCarSale;
    private Float amount;
    TextView amountTextView;

    boolean printerConnected;
    BluetoothDevice printer;
    boolean isLoading = false;

    BluetoothDeviceSerializable bluetoothDeviceSerializable = null;

    String userName;
    TextView userNameTextView;

    ViewModelStore viewModelStore;
    Gson parser;

    Boolean offlineActive = false;
    Boolean sincronizateSession=false;

    Client clientSelected=null;
    UserDataInitial userData=null;
    String dateSelected="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home, container, false);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        navController = NavHostFragment.findNavController(this);
        //selectedClientTovisit = HomeViewArgs.fromBundle(getArguments()).getClientInVisit();
        //bluetoothDeviceSerializable = HomeViewArgs.fromBundle(getArguments()).getPrinterDevice();
        this.userName = HomeViewArgs.fromBundle(getArguments()).getUserName();
        this.userNameTextView = v.findViewById(R.id.userName);
        userNameTextView.setText("Usuario: " + this.userName);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));
        this.presenter = new HomePresenter(getContext(), this);
        this.printerButton = v.findViewById(R.id.printerButton);
        //this.printerButton.setEnabled(false);
        //this.printerButton.setVisibility(View.INVISIBLE);

        this.printerConnected = false;
        this.agregarProductoButton = v.findViewById(R.id.AgregarProductoButton);

        this.amount = Float.parseFloat("0");
        logoutButton = v.findViewById(R.id.Logout_button);
        logoutButton.setOnClickListener(this);

        this.circularProgressIndicator = v.findViewById(R.id.loginLoadingSpinner);
        homeButton = v.findViewById(R.id.bottom_navigation_home);
        homeButton.setSelectedItemId(R.id.home_section);
        this.buscarClienteButton = v.findViewById(R.id.buscarClienteButton);

        this.clientInput = v.findViewById(R.id.cliente_input);
        this.clientSaeKey = v.findViewById(R.id.cliente_key_sae);
        this.clientName = v.findViewById(R.id.cliente_name);
        this.keyProductInput = v.findViewById(R.id.codigo_producto);
        this.weightProduct = v.findViewById(R.id.peso_input);
        this.weightProduct.getEditText().setFilters(new InputFilter[]{new NumberDecimalFilter()});
        this.carSale = new ArrayList<>();
        this.listCarSale = v.findViewById(R.id.lista_carrito);
        this.amountTextView = v.findViewById(R.id.total);
        this.amountTextView.setText("Total :" + amount.toString());
        this.cobrarButton = v.findViewById(R.id.cobrarButton);

        this.endDayButton = v.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.cobrarButton.setOnClickListener(this);
        this.buscarClienteButton.setOnClickListener(this);
        this.agregarProductoButton.setOnClickListener(this);
        this.printerButton.setOnClickListener(this);
        /*this.eatTimeButton = v.findViewById(R.id.eat_time_button);
        this.eatTimeButton.setVisibility(View.INVISIBLE);*/
        /*if (selectedClientTovisit != null) {
            currentClient = selectedClientTovisit;
            this.clientInput.getEditText().setEnabled(false);
            this.clientInput.getEditText().setText(String.valueOf(currentClient.getKeyClient()));
            presenter.findUser(currentClient.getKeyClient());
            buscarClienteButton.setEnabled(false);
        }*/
        this.printerUtil = new PrinterUtil(getContext());
        /*if(bluetoothDeviceSerializable!=null){
            if(this.bluetoothDeviceSerializable.isPrinterConnected()==true){
                this.printerConnected=true;
                this.printer = this.bluetoothDeviceSerializable.getBluetoothDevice();
                this.printerConnected();
                if(printerUtil==null){

                }
            }
        }*/
        this.parser = new Gson();

        homeButton.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_section:
                        System.out.println("No cambia de seccion");
                        break;
                    case R.id.visitas_section:
                        goToAnotherSection(2);

                        break;
                    case R.id.cliente_section:
                        goToAnotherSection(3);

                        break;
                    case R.id.pedidos_section:
                        goToAnotherSection(4);
                        break;
                    case R.id.ventas_section:
                        goToSalesHistory();
                        break;
                }
                return false;
            }
        });

        return v;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        checkIfSincronizate();
        checkSalesUnSincronized();
        checkIfPrinterConfigured();
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("FECHA_DE_HOY: "+ldt);
        String dateParsed = formmat1.format(ldt);
        dateSelected=dateParsed;
    }



    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    void checkSalesUnSincronized(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Sale> sales = viewModelStore.getAppDatabase().saleDao().getAllSalesUnsincronized();
                List<ModeOfflineSM> modeOfflineSMS = new ArrayList<>();
                for(Sale sale : sales) {
                    System.out.println("Sale without sincronization: "+sale.folio);
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
                        modeOfflineSMP.setAppSubSaleId(subSale.subSaleId);
                        modeOfflineSMPS.add(modeOfflineSMP);
                    }
                    modeOfflineSM.setProducts(modeOfflineSMPS);
                    modeOfflineSMS.add(modeOfflineSM);
                }
                List<DevolutionRequest> devolutionsRequests = viewModelStore.getAppDatabase().devolutionRequestDao().getAllUnsincronized();
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
                        if(modeOfflineSMS.size()>0 || debtsPayed.size()>0 || devolutionRequestServers.size()>0) {
                            showNotificationSincronization("Sincronizando...");
                            presenter.sincronizeSales(modeOfflineSMS,debtsPayed,devolutionRequestServers,viewModelStore.getStore().getSellerId());
                        }else{
                            showNotificationSincronization("Nada por sincronizar...");
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
                    System.out.println("Sincronized: "+sincronizationResponse.getSalesSincronized().get(i).getFolio());
                    viewModelStore.getAppDatabase().saleDao().updateSaleId(sincronizationResponse.getSalesSincronized().get(i).getSaleId(),sincronizationResponse.getSalesSincronized().get(i).getFolio());
                }
                for(String folio  : sincronizationResponse.getDebtsSicronized()){
                    Debt debt = viewModelStore.getAppDatabase().debtDao().getDebtByFolio(folio);
                    debt.sincronized=true;
                    viewModelStore.getAppDatabase().debtDao().updateDebtSincronization(debt);
                }
                for(String folio : sincronizationResponse.getDevolutionsSincronized()){
                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolio(folio);
                    devolutionRequest.sincronized=1;
                    viewModelStore.getAppDatabase().devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }

                for(String folio : sincronizationResponse.getDevolutionsAccepted()){
                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolio(folio);
                    devolutionRequest.status="ACCEPTED";
                    viewModelStore.getAppDatabase().devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }
                for(String folio : sincronizationResponse.getDevolutionsRejected()){
                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolio(folio);
                    devolutionRequest.status="DECLINED";
                    viewModelStore.getAppDatabase().devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sincronized Complete");
                    }
                });
            }
        });
    }

   /* @Override
    public void markSincronizedSale(String folio, Integer saleId,List<ModeOfflineSM> ModeOfflineSMS, Integer index) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                viewModelStore.getAppDatabase().saleDao().updateStatusStr(saleId,folio);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        presenter.sincronizeSingleSale(ModeOfflineSMS,index);
                    }
                });
            }
        });
    }*/

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

    /*@RequiresApi(api = Build.VERSION_CODES.N)
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



    void goToCotizaciones() {
        navController.navigate(HomeViewDirections.actionHomeViewToCotizacionesView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void goToClient() {
        navController.navigate(HomeViewDirections.actionHomeViewToClientView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void goToPedidos() {
        navController.navigate(HomeViewDirections.actionHomeViewToPedidoView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void goToSalesHistory() {
        navController.navigate(HomeViewDirections.actionHomeViewToSalesView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @Override
    public void goToLogin() {
        if (this.printerUtil != null) {
            this.printerUtil.desconect();
        }
        navController.navigate(HomeViewDirections.actionHomeViewToLoginView());
    }

    boolean paying = false;

    void logout() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                viewModelStore.getAppDatabase().userDataInitialDao().updateAllLogedInFalse();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        presenter.doLogout();
                                    }
                                });
                            }
                        });

                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    /*@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void setModeOffline(ModeOfflineModel modeOffline) {
        System.out.println("Resguado offline");
        String data = parser.toJson(modeOffline);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());

        try {

            File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "offline-"+dateParsed+".rovi");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(),"Se escribio un archivo",Toast.LENGTH_SHORT).show();
        viewModelStore.saveStore(modeOffline);
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Logout_button:
                logout();
                break;
            case R.id.printerButton:
                if (this.sincronizateSession) {
                    if (this.printerConnected == true) {
                        this.printerConnected = false;
                        if(this.printerUtil!=null) {
                            this.printerUtil.desconect();
                            this.printer = null;
                        }
                        this.printerNoConnected();
                    } else if (isLoading == false) {
                        activatePrinter();
                        isLoading = true;
                    }
                }
                break;
            case R.id.buscarClienteButton:
                if (this.sincronizateSession) {
                    if (this.clientInput.getEditText().getText().toString().trim().isEmpty()) {
                        this.setErrorClientInput("Campo obligatorio");
                    } else {
                        findClient(Integer.parseInt(this.clientInput.getEditText().getText().toString()));
                    }
                }
                break;
            case R.id.AgregarProductoButton:
                if (this.sincronizateSession) {
                    if (clientSelected != null) {
                        if (isLoading == false) {
                            if (!this.weightProduct.getEditText().getText().toString().trim().isEmpty()) {
                                isLoading = true;
                                this.circularProgressIndicator.setVisibility(View.VISIBLE);
                                findProduct(this.keyProductInput.getEditText().getText().toString());
                            } else {
                                this.setErrorProductWeightInput("Por favor indica un peso o número de piezas");
                            }
                        }
                    } else {
                        this.setErrorClientInput("Por favor selecciona un cliente");
                    }
                }
                break;
            case R.id.cobrarButton:
                if(this.sincronizateSession) {
                    System.out.println("Is paying: " + paying);
                    if (paying == false) {
                        if (carSale.size() > 0) {
                            paying = true;
                            System.out.println("Paying true: " + paying);
                            payProducts();
                        } else {
                            Toast.makeText(getContext(), "No haz agregado productos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.end_day_button:
                if(this.sincronizateSession) {

                        if (isLoading == false) {

                            showDatePicker();

                        }
                }
                break;

        }

    }

    void showDatePicker(){
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = String.valueOf(month+1);
                String day = String.valueOf(dayOfMonth);
                if((month+1)<10) monthStr="0"+monthStr;
                if(dayOfMonth<10) day="0"+day;
                dateSelected = year+"-"+monthStr+"-"+day;
                System.out.println("Fecha seleccionada: "+dateSelected);
                getEndDayTicketOffline(dateSelected);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
    }

    void findProduct(String productKey){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Product product = viewModelStore.getAppDatabase().productDao().getProductByProduct(productKey,viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                       if(product==null){
                           findByProductEnd("%"+productKey);
                       }else{
                           addProductToSaleCar(product);
                       }
                    }
                });
            }
        });
    }

    void findByProductEnd(String productKey){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Product product = viewModelStore.getAppDatabase().productDao().getProductByProduct(productKey,viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(product==null){
                            keyProductInput.getEditText().setError("No existe el producto indicado");
                            isLoading=false;
                            circularProgressIndicator.setVisibility(View.GONE);
                        }else{
                            addProductToSaleCar(product);
                        }
                    }
                });
            }
        });
    }

    void findClient(Integer clientKey){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Client client = viewModelStore.getAppDatabase().clientDao().getClientByKey(clientKey,viewModelStore.getStore().getSellerId());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(client!=null){
                            clientSelected=client;
                            circularProgressIndicator.setVisibility(View.GONE);
                            isLoading = false;
                            clientInput.getEditText().setError(null);
                            clientSaeKey.setText("Cliente: " + client.clientKey);
                            clientName.setText(client.name);
                            clientSaeKey.setTextColor(Color.parseColor("#236EF2"));
                            clientName.setTextColor(Color.parseColor("#236EF2"));
                            carSale = new ArrayList<>();
                        }else{
                            clientInput.getEditText().setError("Cliente no existe");
                        }
                    }
                });

            }
        });
    }

    void checkIfSincronizate(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                UserDataInitial userDataInitials = viewModelStore.getAppDatabase().userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(userDataInitials==null){
                            genericMessage("Alerta Sistema","Requiere de su primera sincronización");
                            sincronizateSession=false;
                        }else{
                            userData=userDataInitials;
                            sincronizateSession=true;
                        }
                    }
                });
            }
        });
    }

    /*void findProductOffline(String productKeySae) {
        ProductRoviandaToSale productRovianda = null;
        for (InventoryOfflineMode productInve : viewModelStore.getStore().getInventory()) {
            if (productInve.getCodeSae().equals(productKeySae) ) {
                ProductRoviandaToSale productRoviandaToSale = new ProductRoviandaToSale();
                productRoviandaToSale.setNameProduct(productInve.getProductName());
                productRoviandaToSale.setKeySae(productInve.getCodeSae());
                productRoviandaToSale.setWeight(Float.parseFloat(viewModelStore.getStore().getLimitOfSales().toString()));

                if(productInve.getUniMed().equals("PZ")) {
                    productRoviandaToSale.setIsPz(true);
                    productRoviandaToSale.setWeight(Float.parseFloat("100"));
                    productRoviandaToSale.setQuantity(Float.parseFloat(viewModelStore.getStore().getLimitOfSales().toString()));
                }else{
                    productRoviandaToSale.setIsPz(false);
                    productRoviandaToSale.setWeight(productInve.getWeight());
                    productRoviandaToSale.setQuantity(Float.parseFloat(viewModelStore.getStore().getLimitOfSales().toString()));
                }
                productRoviandaToSale.setPrice(Float.parseFloat(productInve.getPrice().toString()));
                productRoviandaToSale.setPresentationType(productInve.getPresentation());
                productRoviandaToSale.setPresentationId(productInve.getPresentationId());
                productRoviandaToSale.setProductId(productInve.getProductId());
                productRoviandaToSale.setNameProduct(productInve.getProductName());
                productRoviandaToSale.setPresentationType(productInve.getPresentation());
                productRoviandaToSale.setWeightOriginal(productInve.getWeightOriginal());
                System.out.println("Peso original: "+productInve.getWeightOriginal());
                System.out.println("Tipo: "+productInve.getUniMed());
                productRovianda = productRoviandaToSale;
            }
        }
        if(productRovianda==null) {
            for (InventoryOfflineMode productInve : viewModelStore.getStore().getInventory()) {
                if (productInve.getCodeSae().endsWith(productKeySae)) {
                    ProductRoviandaToSale productRoviandaToSale = new ProductRoviandaToSale();
                    productRoviandaToSale.setNameProduct(productInve.getProductName());
                    productRoviandaToSale.setKeySae(productInve.getCodeSae());
                    productRoviandaToSale.setWeight(Float.parseFloat("300"));

                    if (productInve.getUniMed().equals("PZ")) {
                        productRoviandaToSale.setIsPz(true);
                        productRoviandaToSale.setWeight(Float.parseFloat("300"));
                        productRoviandaToSale.setQuantity(Float.parseFloat(viewModelStore.getStore().getLimitOfSales().toString()));
                    } else {
                        productRoviandaToSale.setIsPz(false);
                        productRoviandaToSale.setWeight(productInve.getWeight());
                        productRoviandaToSale.setQuantity(Float.parseFloat(viewModelStore.getStore().getLimitOfSales().toString()));
                    }
                    productRoviandaToSale.setPrice(Float.parseFloat(productInve.getPrice().toString()));
                    productRoviandaToSale.setPresentationType(productInve.getPresentation());
                    productRoviandaToSale.setPresentationId(productInve.getPresentationId());
                    productRoviandaToSale.setProductId(productInve.getProductId());
                    productRoviandaToSale.setNameProduct(productInve.getProductName());
                    productRoviandaToSale.setPresentationType(productInve.getPresentation());
                    productRoviandaToSale.setWeightOriginal(productInve.getWeightOriginal());
                    System.out.println("Peso original: " + productInve.getWeightOriginal());
                    System.out.println("Tipo: " + productInve.getUniMed());
                    productRovianda = productRoviandaToSale;
                }
            }
        }
        if (productRovianda != null) {
            //addProductToSaleCar(productRovianda);
        } else {
            Toast.makeText(getContext(), "No existe el producto en inventario", Toast.LENGTH_SHORT).show();
            isLoading = false;
            circularProgressIndicator.setVisibility(View.GONE);
        }
    }*/

/*
    void getEndDayTicket() {

        if (this.printerConnected == true) {
            presenter.getEndDayTicket();
        } else {
            this.genericMessage("Error de impresora", "Revisa la conexión");
            this.circularProgressIndicator.setVisibility(View.GONE);
            isLoading = false;
        }
    }
*/

    int selectIndexPrinter;

    PrinterUtil printerUtil = null;

    @Override
    public void setErrorClientInput(String msg) {
        this.clientInput.getEditText().setError(msg);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findPrinter(Set<BluetoothDevice> devices) {
        this.circularProgressIndicator.setVisibility(View.VISIBLE);
        isLoading=true;
        List<BluetoothDevice> bluetoothDevicesMapped = devices.stream().collect(Collectors.toList());
        String[] bluetoothDevices = new String[devices.size()];
        for(int i=0;i<devices.size();i++){
            bluetoothDevices[i]=bluetoothDevicesMapped.get(i).getName();
        }

        int checkedItem = 1;

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Selecciona la impresora bluetooth")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Se cancelo");
                        dialog.cancel();
                        showErrorConnectingPrinter();
                        circularProgressIndicator.setVisibility(View.GONE);
                        isLoading=false;
                    }
                }).setPositiveButton("Enlazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Index: "+which);
                        if(selectIndexPrinter!=-1) {
                            which=selectIndexPrinter;
                            System.out.println("Estableciendo conexión");
                            printerName = bluetoothDevices[which];
                            printer = bluetoothDevicesMapped.get(which);

                                    printerConnected = printerUtil.connectWithPrinter(printer);
                                    circularProgressIndicator.setVisibility(View.GONE);
                                    isLoading=false;
                                    if(printerConnected==true) {

                                        connectionPrinterSuccess();
                                        printerConnected();
                                    }else {
                                        showErrorConnectingPrinter();
                                        printerNoConnected();

                                    }
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    viewModelStore.getAppDatabase().userDataInitialDao().updatePrinterAddress(viewModelStore.getStore().getSellerId(),printer.getAddress());
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            System.out.println("Printer saved");
                                        }
                                    });
                                }
                            });

                        }
                    }
                }).setSingleChoiceItems(bluetoothDevices, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Se selecciono uno: "+which);
                        selectIndexPrinter=which;
                    }
                }).setCancelable(false).create();

        dialog.show();;
    }

    @Override
    public void showErrorConnectingPrinter() {
        isLoading = false;
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Error de enlace")
                .setMessage("No se pudo enlazar a la impresora ").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        printerNoConnected();
                    }
                }).create();
        dialog.show();
    }

    void printerNoConnected() {
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#BDB5B5")));
    }

    void printerConnected() {
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#39ED20")));
    }

    @Override
    public void connectionPrinterSuccess() {
        isLoading = false;
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Enlace de impresora")
                .setMessage("Enlace exitoso con impresora : " + printerName).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        printerConnected();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        printerConnected();
                    }
                }).create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void activatePrinter(){
        if(!this.bluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth,0);
        }else{
            printerUtil = new PrinterUtil(getContext());
            final Set<BluetoothDevice> deviceList = printerUtil.findDevices();
            if(deviceList.size()>0) {
                findPrinter(deviceList);
            }
        }
    }

    void checkIfPrinterConfigured(){
        executor.execute(new Runnable() {
            @Override
            public void run() {

                UserDataInitial userDataInitial = viewModelStore.getAppDatabase().userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());


                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        if(userDataInitial!=null && userDataInitial.printerMacAddress!=null){
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                            for(BluetoothDevice bluetoothDevice :pairedDevices){
                                if(bluetoothDevice.getAddress().equals(userDataInitial.printerMacAddress)){
                                    printer=bluetoothDevice;
                                    printerConnected=true;
                                    printerConnected();
                                }
                            }
                            if(!printerConnected){
                                printerNoConnected();
                            }
                        }
                    }
                });
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == -1) {//activado

                if (this.printerUtil == null) {
                    this.printerUtil = new PrinterUtil(getContext());
                }
                Set<BluetoothDevice> deviceList = this.printerUtil.findDevices();
                if(deviceList.size()>0){
                    findPrinter(deviceList);
                }else{
                    genericMessage("Error al buscar dispositivos","No tienes dispositivos emparejados");
                }
            } else if (resultCode == 0) {//desactivado
                System.out.println("Request permission");
                System.out.println("Permission: " + resultCode);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            System.out.println("Request permission");
            for (String item : permissions) {
                System.out.println("Permission: " + item);
            }
            for (int item : grantResults) {
                System.out.println("Permission: " + item);
            }
        }
    }

    @Override
    public void genericMessage(String title, String msg) {
        this.circularProgressIndicator.setVisibility(View.GONE);
        isLoading = false;
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).create();
        dialog.show();
    }

    /*@Override
    public void setClient(ClientDTO client) {
        this.circularProgressIndicator.setVisibility(View.GONE);
        isLoading = false;
        this.clientInput.getEditText().setError(null);
        this.currentClient = client;
        this.clientSaeKey.setText("Cliente: " + this.currentClient.getKeyClient());
        this.clientName.setText(this.currentClient.getName());
        this.clientSaeKey.setTextColor(Color.parseColor("#236EF2"));
        this.clientName.setTextColor(Color.parseColor("#236EF2"));
        this.carSale = new ArrayList<>();
        fillList();
    }*/

    /*public void setClientOffclient(Client client) {
        this.circularProgressIndicator.setVisibility(View.GONE);
        isLoading = false;
        this.clientInput.getEditText().setError(null);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setTypeClient(client.type);
        clientDTO.setKeyClient(client.clientKey);
        clientDTO.setName(client.name);
        clientDTO.setId(client.clientId);
        this.currentClient = clientDTO;
        this.clientSaeKey.setText("Cliente: " + client.clientKey);
        this.clientName.setText(client.name);
        this.clientSaeKey.setTextColor(Color.parseColor("#236EF2"));
        this.clientName.setTextColor(Color.parseColor("#236EF2"));
        this.carSale = new ArrayList<>();
        fillList();
    }*/

    List<ProductRoviandaToSale> carSale;


    public void addProductToSaleCar(Product productRoviandaToSale) {
        isLoading = false;
        this.circularProgressIndicator.setVisibility(View.GONE);
        Float countRequested;
        System.out.println("Es pieza: " + productRoviandaToSale.productKey + " " + productRoviandaToSale.uniMed);
        if (productRoviandaToSale.uniMed.toLowerCase().equals("pz")) {
            countRequested = Float.parseFloat(String.valueOf(Math.round(Float.parseFloat(this.weightProduct.getEditText().getText().toString()))));
            if (countRequested == 0) {
                genericMessage("El producto se vende por piezas", "Introduce un número entero.");
                return;
            }
        } else {
            countRequested = Float.parseFloat(this.weightProduct.getEditText().getText().toString());
        }
        if (countRequested > 0) {
            Float totalResguarded = Float.parseFloat("0");
            for (ProductRoviandaToSale product : carSale) {
                if (product.getKeySae().equals(productRoviandaToSale.productKey)) {
                    totalResguarded += product.getWeight();
                }
            }
            Float totalResguardedTemp = totalResguarded + countRequested;
            if (productRoviandaToSale.quantity >= totalResguardedTemp) {

                int index = -1;
                for (int i = 0; i < carSale.size(); i++) {
                    ProductRoviandaToSale item = carSale.get(i);
                    if (item.getPresentationId() == productRoviandaToSale.presentationId) {
                        index = i;
                        item.setWeight(
                                item.getWeight() + countRequested
                        );
                        item.setQuantity(item.getQuantity()+countRequested);
                    }
                }
                if (index == -1) {
                    ProductRoviandaToSale productRoviandaToSale1 = new ProductRoviandaToSale();
                    productRoviandaToSale1.setIsPz(productRoviandaToSale.uniMed.toLowerCase().equals("pz"));
                    productRoviandaToSale1.setKeySae(productRoviandaToSale.productKey);
                    productRoviandaToSale1.setNameProduct(productRoviandaToSale.name);
                    productRoviandaToSale1.setPresentationType(productRoviandaToSale.presentationName);
                    productRoviandaToSale1.setPrice(productRoviandaToSale.price);
                    productRoviandaToSale1.setPresentationId(productRoviandaToSale.presentationId);
                    productRoviandaToSale1.setProductId(productRoviandaToSale.productId);
                    productRoviandaToSale1.setWeightOriginal(productRoviandaToSale.weightOriginal);
                    productRoviandaToSale1.setWeight(countRequested);
                    productRoviandaToSale1.setQuantity(countRequested);
                    carSale.add(productRoviandaToSale1);
                }
                this.keyProductInput.getEditText().setText(null);
                this.weightProduct.getEditText().setText(null);
                fillList();
            } else {
                genericMessage("Error en stock", "Solo tienes: " + (productRoviandaToSale.quantity - totalResguarded) + " para vender");
            }
        }
    }

    void fillList() {
        this.circularProgressIndicator.setVisibility(View.GONE);
        isLoading = false;
        ProductRoviandaToSale[] productRoviandaToSales = new ProductRoviandaToSale[carSale.size()];
        amount = Float.parseFloat("0");
        for (int i = 0; i < carSale.size(); i++) {
            productRoviandaToSales[i] = carSale.get(i);
            amount += carSale.get(i).getPrice() * carSale.get(i).getWeight();
        }
        AdapterListProductSale adapterListProductSale = new AdapterListProductSale(getContext(), productRoviandaToSales);
        listCarSale.setAdapter(adapterListProductSale);
        listCarSale.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Position:" + position);
                carSale.remove(position);
                fillList();
                return true;
            }
        });
        this.amountTextView.setText("Total :" + amount.toString());
    }

    @Override
    public void setErrorProductkeyInput(String msg) {
        isLoading = false;
        circularProgressIndicator.setVisibility(View.GONE);
        this.keyProductInput.getEditText().setError(msg);
    }

    @Override
    public void setErrorProductWeightInput(String msg) {
        this.weightProduct.getEditText().setError(msg);
    }

    void payProducts() {
       /*if(printerConnected==false){
           paying=false;
           System.out.println("Paying false: "+paying);
           genericMessage("Error con impresora","Asegurate de tener la impresora conectada.");
        }else{*/
        //connectPrinter();
        showOptionsPayed();
        //}
    }

    /*void connectPrinter(){
        if(printer!=null && isLoading==false) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    printerUtil.connectWithPrinter(printer,getContext());
                    isLoading=true;
                }
            }).start();
        }
    }*/

    void goToAnotherSection(int option) {

        if (currentClient != null || carSale.size() > 0) {
            AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("¿Seguro que desea salir de la sección?")
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navigate(option);
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    }).create();
            dialog.show();
        } else {
            navigate(option);
        }
    }

    void navigate(int section) {
        if (section == 2) {
            goToCotizaciones();
        } else if (section == 3 && sincronizateSession) {
            goToClient();
        } else if (section == 4 && sincronizateSession) {
            goToPedidos();
        }
    }

    int selectionPay = 0;
    String[] contadoOptions = {"EFECTIVO", "TRANSFERENCIA", "CHEQUE"};
    String[] creditoOptions = {"CREDITO", "EFECTIVO"};

    void showOptionsPayed() {
        String[] selectMode = null;

        if (clientSelected != null) {
            if (clientSelected.type.equals("CONTADO")) {
                selectMode = contadoOptions;
            } else {
                selectMode = creditoOptions;
            }

            AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Seleccione el tipo de pago")
                    .setSingleChoiceItems(selectMode, selectionPay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Selected: " + which);
                            selectionPay = which;
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            System.out.println("acepto el cobró");
                            paying = false;
                        }
                    }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Selected: " + selectionPay);
                            doSaleModalConfirmation();
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            paying = false;
                        }
                    }).create();
            dialog.show();
        }
    }

    void getEndDayTicketOffline(String date) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Double weightG = Double.parseDouble("0");
                int totalTickets=0;
                int totalCanceled=0;
                int totalDeclinedCanceled=0;
                int totalPendingCanceled=0;
                int totalDevolutionsPending=0;
                int totalDevolutionsAccepted=0;
                int totalDevolutionsDeclined=0;
                int totalDevolutions=0;

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                    String dateParsed = dateFormat.format(calendar.getTime());
                String ticket = "\nReporte de cierre\nVendedor: "+viewModelStore.getStore().getUsername()+"\nFecha: "+dateParsed+"\n------------------------\n";
                ticket+="ART.   DESC    CANT    PRECIO  IMPORTE\n";
                Map<String,String> skus = new HashMap<>();
                Map<String,Float> pricesBySku = new HashMap<>();
                Map<String,Float> weightTotal = new HashMap<>();
                Map<String,Float> piecesTotal = new HashMap<>();
                Map<String,Float> amountTotal = new HashMap<>();
                Float efectivo=Float.parseFloat("0");
                Float credito=Float.parseFloat("0");
                Float transferencia=Float.parseFloat("0");
                Float cheque=Float.parseFloat("0");
                Float creditCob = Float.parseFloat("0");
                Float iva = Float.parseFloat("0");
                String clientsStr="";
                String devolutionProducts="PRODUCTO DEVUELTO: \n";
                List<Sale> salesOfday = viewModelStore.getAppDatabase().saleDao().getAllSalesByDate(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
                for(Sale sale : salesOfday){
                    totalTickets++;
                    if(!(sale.statusStr.equals("CANCELED") && sale.cancelAutorized!=null && sale.cancelAutorized.equals("true"))) {


                        List<SubSale> subSales = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(sale.folio);
                        DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(sale.folio);
                        Float amountOfSale=Float.parseFloat("0");
                        if(devolutionRequest!=null && devolutionRequest.status.equals("ACCEPTED")){

                            for(SubSale subSale : subSales){
                                DevolutionSubSale devolutionSubSale = viewModelStore.getAppDatabase().devolutionSubSaleDao().findDevolutionSubSaleBySubSaleId(subSale.subSaleId);
                                devolutionProducts+=subSale.productName+" "+subSale.productPresentationType +"\n"+ String.format("%.02f",(subSale.quantity-devolutionSubSale.quantity)) +" "+subSale.uniMed+"\n";
                                subSale.price = (subSale.price/ subSale.quantity)*devolutionSubSale.quantity;
                                subSale.quantity=devolutionSubSale.quantity;
                                amountOfSale+=subSale.price;
                            }
                        }else{
                            amountOfSale=sale.amount;
                        }
                        for (SubSale subSale : subSales) {
                            String productName = skus.get(subSale.productKey);
                            if (productName == null) {
                                skus.put(subSale.productKey,subSale.productName + " " + subSale.productPresentationType);
                            }
                            Float weight = weightTotal.get(subSale.productKey);
                            if (weight == null) {
                                Float weightByProduct = (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                                weightTotal.put(subSale.productKey, weightByProduct);
                                weightG += weightByProduct;
                            } else {
                                weight += (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                                weightTotal.put(subSale.productKey, weight);
                                weightG += (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                            }

                            Float amountByProduct = pricesBySku.get(subSale.productKey);
                            if (amountByProduct == null) {
                                amount = subSale.price / subSale.quantity;
                                pricesBySku.put(subSale.productKey, amount);
                            }

                            Float amountSubSale = amountTotal.get(subSale.productKey);
                            if (amountSubSale == null) {
                                Float amount = subSale.price;
                                amountTotal.put(subSale.productKey, amount);
                            }else{
                                amountSubSale+=subSale.price;
                                amountTotal.put(subSale.productKey, amountSubSale);
                            }
                            if(subSale.uniMed.toLowerCase().equals("pz")){
                                Float piecesOfProduct = piecesTotal.get(subSale.productKey);
                                if(piecesOfProduct==null){
                                    piecesTotal.put(subSale.productKey,subSale.quantity);
                                }else{
                                    piecesTotal.put(subSale.productKey,(piecesOfProduct+subSale.quantity));
                                }
                            }
                        }
                        if((sale.statusStr.equals("CANCELED") && sale.cancelAutorized!=null && sale.cancelAutorized.equals("")) || ( sale.statusStr.equals("CANCELED") && sale.cancelAutorized==null) ){
                            totalPendingCanceled++;
                        }else if(sale.statusStr.equals("ACTIVE") && sale.cancelAutorized!=null && sale.cancelAutorized.equals("false")){
                            totalDeclinedCanceled++;
                        }
                        clientsStr += "\n" + sale.folio + " " + sale.clientName +" "+sale.keyClient+ "\n $" + amountOfSale + " " + ((sale.typeSale.equals("CREDITO")) ? "C" : (sale.typeSale.equals("TRANSFERENCIA")?"TRANSFER":"")) + " ";
                        if(sale.statusStr.equals("CANCELED")){
                            if(sale.cancelAutorized!=null && sale.cancelAutorized.equals("true")){
                                clientsStr+="CANCELADO";
                            }else{
                                clientsStr+="CANCEL. PEND.";
                            }
                        }
                        if(devolutionRequest!=null){
                            totalDevolutions++;
                            clientsStr+="DEV.";
                            String status = devolutionRequest.status;
                            if(status.equals("PENDING")){
                                clientsStr+="PEN.";
                                totalDevolutionsPending++;
                            }else if(status.equals("ACCEPTED")){
                                clientsStr+="ACEPT.";
                                totalDevolutionsAccepted++;
                            }else if(status.equals("DECLINED")){
                                clientsStr+="RECH.";
                                totalDevolutionsDeclined++;
                            }
                        }
                        if (sale.typeSale.equals("CREDITO")) {
                            credito += amountOfSale;
                        } else if (sale.typeSale.equals("TRANSFERENCIA")) {
                            transferencia += amountOfSale;
                        } else if (sale.typeSale.equals("EFECTIVO")) {
                            efectivo += amountOfSale;
                        } else if (sale.typeSale.equals("CHEQUE")) {
                            cheque += amountOfSale;
                        }

                    }else{

                            totalCanceled++;
                            clientsStr += "\n" + sale.folio + " " + sale.clientName +" "+sale.keyClient+ "\n $" + sale.amount + " " + ((sale.typeSale.equals("CREDITO")) ? "C" : (sale.typeSale.equals("TRANSFERENCIA")?"TRANSFER":"")) + " " + (sale.statusStr.equals("CANCELED") ? "CANCELADO" : "");
                    }

                }

                List<Debt> debts = viewModelStore.getAppDatabase().debtDao().getAllSalesDebtsBetweenDates(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
                for(Debt debt : debts){
                    if(!debt.deleted) {
                        Sale sale = viewModelStore.getAppDatabase().saleDao().getByFolio(debt.folio);
                        creditCob += sale.amount;
                    }
                }
                List<String> allSkus = new ArrayList<>();
                for(String sku : skus.keySet()){
                    allSkus.add(sku);
                }
                Collections.sort(allSkus, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                for(String sku : allSkus){
                    String productName = skus.get(sku);
                    Float weight = weightTotal.get(sku);
                    Float price = pricesBySku.get(sku);
                    Float amount = amountTotal.get(sku);
                    Float totalPieces = piecesTotal.get(sku);
                    if(sku.length()>5){
                        sku=sku.substring(sku.length()-5,sku.length());
                    }
                    if(totalPieces==null) {
                        ticket += "\n" + sku + " " + productName + "\n" + String.format("%.02f", weight) + " $" + price + " $" + String.format("%.02f", amount);
                    }else{
                        ticket += "\n" + sku + " " + productName + "\n"+Math.round(totalPieces)+ " pz " + String.format("%.02f", weight) + " $" + price + " $" + String.format("%.02f", amount);
                    }
                }
                ticket+="\n-----------------------------------------\nDOC NOMBRE  CLIENTE IMPORTE TIPOVENTA\n-----------------------------------------\n";
                ticket+=clientsStr+"\n\n";
                ticket+="Total de notas: "+totalTickets+"\n";
                ticket+="Total de canceladas: "+totalCanceled+"\n";
                ticket+="Total de devoluciones: "+totalDevolutionsAccepted+"\n";
                ticket+=devolutionProducts+"\n\n";
                ticket+="VENTAS POR CONCEPTO\nEFECTIVO: $"+String.format("%.02f",efectivo)+"\nCREDITO: $"+String.format("%.02f",credito)+"\nTRANSFERENCIA: $"+String.format("%.02f",transferencia)+"\nCHEQUE: $"+String.format("%.02f",cheque)+"\n";
                ticket+="TOTAL KILOS: "+String.format("%.02f",weightG)+"\nVENTA TOTAL:$ "+String.format("%.02f",efectivo+transferencia+cheque+credito)+"\n";
                ticket+="Recup. Cobranza\n$ "+String.format("%.02f",creditCob)+"\n";
                ticket+="\n\n\n\n";
                String ticketCreated = ticket;
                ///adeudos pendientes
                System.out.println("Ticket: "+ticket);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = false;
                        circularProgressIndicator.setVisibility(View.INVISIBLE);
                        printTiket(ticketCreated);
                    }
                });
            }
        });




    }



    void doSaleModalConfirmation(){

            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Cobrar");

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view= layoutInflater.inflate(R.layout.pay_modal, null);
            TextView amountView = view.findViewById(R.id.totalApagarInput);
            amountView.setText("Total a cobrar: "+amount);
            TextInputLayout textInputLayout = view.findViewById(R.id.pagoCon);
            textInputLayout.getEditText().setEnabled(false);
            textInputLayout.getEditText().setText(String.valueOf(amount));
            /*if(currentClient.getTypeClient().equals("CREDITO") && selectionPay==0){
                textInputLayout.getEditText().setEnabled(false);
                textInputLayout.getEditText().setText(amount.toString());
            }else {
                textInputLayout.getEditText().setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

                textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        String amountPayed = s.toString();
                        if (!amountPayed.trim().isEmpty()) {
                            try {
                                if (Float.parseFloat(amountPayed) < amount) {
                                    textInputLayout.getEditText().setBackgroundColor(Color.RED);
                                } else {
                                    textInputLayout.getEditText().setBackgroundColor(Color.WHITE);
                                }
                            }catch (NumberFormatException e){
                                textInputLayout.getEditText().setBackgroundColor(Color.RED);
                            }
                        } else {
                                textInputLayout.getEditText().setBackgroundColor(Color.RED);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String amountPayed = s.toString();
                        if (!amountPayed.trim().isEmpty()) {
                            try {
                                if (Float.parseFloat(amountPayed) < amount) {
                                    textInputLayout.getEditText().setBackgroundColor(Color.RED);
                                } else {
                                    textInputLayout.getEditText().setBackgroundColor(Color.WHITE);
                                }
                            }catch (NumberFormatException e){
                                textInputLayout.getEditText().setBackgroundColor(Color.RED);
                            }
                        } else {
                            textInputLayout.getEditText().setBackgroundColor(Color.WHITE);

                        }
                    }
                });
            }*/
            builder.setView(view);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!textInputLayout.getEditText().getText().toString().isEmpty()) {
                        System.out.println("acepto el cobró");
                        //SaleDTO saleDTO = null;
                        //saleDTO = generateRequestSale(textInputLayout.getEditText().getText().toString(), 0);
                        cobrarButton.setEnabled(false);
                        circularProgressIndicator.setVisibility(View.VISIBLE);
                        /*if(!offlineActive) {
                            presenter.doSale(saleDTO);
                        }else{
                            List<SaleDTO> salesMaked= viewModelStore.getStore().getSalesMaked();
                            if(salesMaked==null){
                                salesMaked = new ArrayList<SaleDTO>();
                            }
                            Integer folioCount = viewModelStore.getStore().getFolioCount()+1;
                            saleDTO.setFolio(viewModelStore.getStore().getFolioNomenclature()+folioCount);
                            TimeZone tz = TimeZone.getTimeZone("UTC");
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                            df.setTimeZone(tz);
                            String nowAsISO = df.format(new Date());
                            saleDTO.setClientName(currentClient.getName());
                            System.out.println("ES CREDITO: "+ saleDTO.getTypeSale());
                            if(saleDTO.getTypeSale().equals("Crédito") ){
                                saleDTO.setTypeSale("CREDITO");
                                SaleOfflineMode saleOfflineMode = new SaleOfflineMode();
                                saleOfflineMode.setAmount(saleDTO.getAmount());
                                saleOfflineMode.setCredit(saleDTO.getAmount());
                                saleOfflineMode.setFolio(saleDTO.getFolio());
                                saleOfflineMode.setKeyClient(Long.parseLong(String.valueOf(saleDTO.getKeyClient())));
                                saleOfflineMode.setPayed(saleDTO.getAmount());
                                saleOfflineMode.setSellerId(viewModelStore.getStore().getSellerId());
                                saleOfflineMode.setTypeSale(saleDTO.getTypeSale());
                                saleOfflineMode.setClientName(currentClient.getName());
                                saleOfflineMode.setProducts(
                                        saleDTO.getProducts().stream().map(product->{
                                            ProductsOfflineMode productsOfflineMode= new ProductsOfflineMode();
                                            productsOfflineMode.setQuantity(product.getQuantity());
                                            productsOfflineMode.setPrice(product.getPrice());
                                            productsOfflineMode.setProductKey(product.getProductKey());
                                            productsOfflineMode.setType(product.getTypeUnid());
                                            //productsOfflineMode.setWeightStandar(product.getClass());
                                            productsOfflineMode.setDate(nowAsISO);
                                            return  productsOfflineMode;
                                        }).collect(Collectors.toList())
                                );
                                saleOfflineMode.setStatus(true);
                                saleDTO.setStatus(true);
                                saleOfflineMode.setStatusStr("ACTIVE");
                                List<SaleOfflineMode> saleOfflineModes = viewModelStore.getStore().getDebts();
                                if(saleOfflineModes==null){
                                    saleOfflineModes= new ArrayList<>();
                                }
                                saleOfflineModes.add(saleOfflineMode);
                                viewModelStore.getStore().setDebts(saleOfflineModes);

                            }else{
                                saleDTO.setStatus(false);
                            }
                            saleDTO.setDate(nowAsISO);
                            saleDTO.setStatusStr("ACTIVE");
                            saleDTO.setClientId(currentClient.getId());
                            viewModelStore.getStore().setFolioCount(folioCount);
                            salesMaked.add(saleDTO);
                            */
                        saveSqlSale(amount);
                        //viewModelStore.getStore().setSalesMaked(salesMaked);
                        //doTicketSaleOffline(saleDTO);
                        //setModeOffline(viewModelStore.getStore());
                        //}

                    }else{
                        genericMessage("Venta no realizada","No se asignó un monto de pago ó se canceló el cobro");
                    }
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    paying = false;
                    genericMessage("Venta no realizada","No se asignó un monto de pago ó se canceló el cobro");
                }
            });
            builder.show();
    }

    void saveSqlSale(Float amount){

        executor.execute(new  Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                UserDataInitial userDataInitial = viewModelStore.getAppDatabase().userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                Map<String,Product> productsMap = new HashMap<>();
                    Integer folioCount = userDataInitial.count;
                    Sale sale = new Sale();
                    sale.amount=amount;
                    sale.clientName=clientSelected.name;
                    if(clientSelected.type.equals("CONTADO")) {
                        sale.typeSale=contadoOptions[selectionPay];
                        sale.status=false;
                    }else if(clientSelected.type.equals("CREDITO")){
                        if(creditoOptions[selectionPay].equals("CREDITO")) {
                            sale.typeSale = creditoOptions[selectionPay];
                            sale.credit = amount;
                            sale.status = true;
                        }else{
                            sale.typeSale = creditoOptions[selectionPay];
                            sale.status = false;
                        }
                    }
                    ZonedDateTime zdt = ZonedDateTime.now();
                    zdt=zdt.minusHours(5);
                    String nowAsISO= zdt.format(DateTimeFormatter.ISO_INSTANT);
                    sale.date=nowAsISO;
                    sale.keyClient=clientSelected.clientKey;
                    sale.payed=amount;
                    sale.sellerId=viewModelStore.getStore().getSellerId();
                    sale.sincronized=false;
                    sale.statusStr="ACTIVE";
                    sale.clientId=clientSelected.clientId;
                    sale.modified=false;
                    sale.folio=userDataInitial.nomenclature+(folioCount+1);
                    List<SubSale> subSales = new ArrayList<>();
                    for(ProductRoviandaToSale product : carSale){
                        Product product1=viewModelStore.getAppDatabase().productDao().getProductByKey(product.getKeySae());
                        productsMap.put(product.getKeySae(),product1);
                        SubSale subSale = new SubSale();
                        subSale.folio=userDataInitial.nomenclature+(folioCount+1);
                        subSale.price=product.getPrice()*product.getQuantity();
                        subSale.productKey=product.getKeySae();
                        subSale.productName=product.getNameProduct();
                        subSale.productPresentationType=product.getPresentationType();
                        subSale.quantity=product.getQuantity();
                        subSale.weightStandar=product.getWeightOriginal();
                        subSale.presentationId=product.getPresentationId();
                        subSale.productId=product.getProductId();
                        subSale.uniMed=product.isIsPz()?"PZ":"KG";
                        subSales.add(subSale);
                    }
                    viewModelStore.getAppDatabase().saleDao().insertAll(sale);
                    viewModelStore.getAppDatabase().userDataInitialDao().updateFolioCount(folioCount+1, viewModelStore.getStore().getSellerId());
                    for (SubSale subSale1 : subSales) {
                        viewModelStore.getAppDatabase().subSalesDao().insertAllSubSales(subSale1);
                    }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        doTicketSaleOffline(sale,subSales,productsMap);
                        System.out.println("SaleSavedInSQL");
                        checkSalesUnSincronized();
                    }
                });
            }
        });
    }




    /*@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setModeOffline(ModeOfflineModel modeOffline) {
        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);
        viewModelStore.saveStore(modeOffline);
        String data = parser.toJson(modeOffline);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());

        try {

            File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "offline-"+dateParsed+".rovi");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(),"Se almaceno offline",Toast.LENGTH_SHORT).show();

    }*/
    void doTicketSaleOffline(Sale sale,List<SubSale> subSales,Map<String,Product> productsMap){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        String dateParsed = dateFormat.format(calendar.getTime());
        String ticket = "ROVIANDA SAPI DE CV\nAV.1 #5 Esquina Calle 1\nCongregación Donato Guerra\nParque Industrial Valle de Orizaba\nC.P 94780\nRFC 8607056P8\nTEL 272 72 46077, 72 4 5690\n";
        ticket+="Pago en una Sola Exhibición\nLugar de Expedición: Ruta\nNota No. "+sale.folio+"\nFecha: "+dateParsed+"\n\n";
        ticket+="Vendedor:"+viewModelStore.getStore().getUsername()+"\n\nCliente: "+clientSelected.name+"\nClave: "+clientSelected.clientKey+"\n";
        ticket+="Tipo de venta: "+ sale.typeSale +"\n--------------------------------\nDESCR   PRECIO   CANT  IMPU.   IMPORTE \n--------------------------------\n";
        Float total = Float.parseFloat("0");
        Float totalImp = Float.parseFloat("0");
        for(SubSale product : subSales){
            Product product1 = productsMap.get(product.productKey);
            Float singleIva = Float.parseFloat("0");
            Float singleIeps = Float.parseFloat("0");
            Float amount = (product.price/product.quantity);

            switch (product1.esqKey){
                case 1:
                    singleIva=this.extractIva(amount);
                    break;
                case 4:
                    singleIva=this.extractIva(amount);
                    singleIeps=this.extractIeps((amount-this.extractIva(amount)),Float.parseFloat("8"));
                    break;
                case 5:
                    singleIva=this.extractIva(amount);
                    singleIeps=this.extractIeps((amount-this.extractIva(amount)),Float.parseFloat("25"));
                    break;
                case 6:
                    singleIva=this.extractIva(amount);
                    singleIeps=this.extractIeps((amount-this.extractIva(amount)),Float.parseFloat("50"));
                    break;
            }
            Float singlePrice=amount-(singleIva+singleIeps);
            totalImp+=((singleIva+singleIeps)*product.quantity);
            if(product.uniMed.equals("PZ")) {
                ticket += product.productName + " " + product.productPresentationType + "\n" + String.format("%.02f",singlePrice) +" "+ Math.round(product.quantity) + "pz " +String.format("%.02f",(singleIeps+singleIva)*product.quantity)  +" "+String.format("%.02f",product.price) +"\n";
            }else{
                ticket += product.productName + " " + product.productPresentationType + "\n"+ Math.round(singlePrice) +" " + product.quantity + "kg "+ String.format("%.02f",(singleIeps+singleIva)*product.quantity) +" "+String.format("%.02f",product.price)+ "\n";
            }
            total+=product.price;
        }
        ticket+="--------------------------------\n";
        ticket+="SUB TOTAL: $"+String.format("%.02f",total-totalImp)+"\n";
        ticket+="IMPUESTO:  $"+String.format("%.02f",totalImp)+"\n";
        ticket+="TOTAL: $ "+String.format("%.02f",total)+"\n\n\n";
        //ticket+="ticket+=`Piezas:  \n\n*** GRACIAS POR SU COMPRA ***\n";
        if(sale.typeSale.equals("CREDITO")){
           ticket+="Esta venta se incluye en la\nventa global del dia, por el\npresente reconozco deber\ny me obligo a pagar en esta\nciudad y cualquier otra que\nse me de pago a la orden de\nROVIANDA S.A.P.I. de C.V. la\ncantidad que se estipula como\ntotal en el presente documento.\n-------------------\n      Firma\n\n";
            ticket+=(sale.status)?"\nSE ADEUDA\n\n\n\n\n":"\nPAGADO\n\n\n\n\n";
        }

        saleSuccess(ticket);
    }

    Float extractIva(Float amount){
        return (amount/116)*16;
    }

    Float extractIeps(Float amount,Float percent){
        return (amount/(100+percent))*percent;
    }
    /*SaleDTO generateRequestSale(String amountPayed,int days){
        SaleDTO sale = new SaleDTO();
        sale.setAmount(amount);
        sale.setPayed(Float.parseFloat(amountPayed));
        sale.setKeyClient(currentClient.getKeyClient());
        System.out.println("Tipo de cliente: "+currentClient.getTypeClient());
        if(currentClient.getTypeClient().equals("CONTADO")) {
            sale.setTypeSale(contadoOptions[selectionPay]);
        }else if(currentClient.getTypeClient().equals("CREDITO")){
            sale.setTypeSale(creditoOptions[selectionPay]);
            sale.setDays(0);
            sale.setCredit(Float.parseFloat(amountPayed));
        }
        List<ProductSaleDTO> productsSold  = new ArrayList<>();
        sale.setStatusStr("ACTIVE");
        for(ProductRoviandaToSale productSold : carSale){
            ProductSaleDTO productSaleDTO = new ProductSaleDTO();
            productSaleDTO.setProductKey(productSold.getKeySae());
            productSaleDTO.setPrice(productSold.getPrice());
            productSaleDTO.setQuantity(productSold.getWeight());
            productSaleDTO.setProductName(productSold.getNameProduct());
            productSaleDTO.setProductPresentation(productSold.getPresentationType());
            productSaleDTO.setTypeUnid(productSold.getPz()?"PZ":"KG");
            productSaleDTO.setWeightOriginal(productSold.getWeightOriginal());
            productSaleDTO.setProductId(productSold.getProductId());
            productSaleDTO.setPresentationId(productSold.getPresentationId());
            productsSold.add(productSaleDTO);
        }
        sale.setProducts(productsSold);

        return sale;
    }*/

    public BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    int intentsToClose = 0;
    @Override
    public void saleSuccess(String ticket) {
        intentsToClose=0;
        circularProgressIndicator.setVisibility(View.GONE);
        isLoading=false;
        clientSelected=null;
        clientName.setText("");
        clientSaeKey.setText("");
        cobrarButton.setEnabled(true);
        this.carSale= new ArrayList<>();
        this.fillList();
        this.keyProductInput.getEditText().setText(null);
        this.weightProduct.getEditText().setText(null);
        this.clientInput.getEditText().setText(null);
        printTiket(ticket);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("Imprimir ticket")
                .setNeutralButton("Terminar",null)
                .setPositiveButton("Reimprimir",null)
                .setCancelable(false).create();
        dialog.show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setTextColor(Color.parseColor("#000000"));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //connectPrinter();
                printTiket(ticket);
            }
        });

        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setTextColor(Color.parseColor("#000000"));
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               /* if(intentsToClose>=1){
                    intentsToClose=0;

                }else{
                    if(intentsToClose==0) {
                        Toast.makeText(getContext(), "Vuelve a presionar el botón para confirmar.", Toast.LENGTH_LONG).show();
                    }
                    intentsToClose+=1;
                }
                */
            }
        });
    }

    void printTiket(String ticket){
        Toast.makeText(getContext(),"Imprimiendo",Toast.LENGTH_LONG).show();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {

                    printerUtil.connectWithPrinter(printer);

                    sleep(3000);
                    printerUtil.IntentPrint(ticket);

                }catch (InterruptedException e){
                    System.out.println("Exception: "+e.getMessage());
                }
            }
        }.start();
    }



    @Override
    public void saleError(String msg) {
        cobrarButton.setEnabled(true);
        circularProgressIndicator.setVisibility(View.GONE);
        isLoading=false;
        this.genericMessage("Error",msg);
    }


}
