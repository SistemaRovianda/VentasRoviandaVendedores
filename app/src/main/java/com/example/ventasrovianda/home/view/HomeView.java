package com.example.ventasrovianda.home.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.MainActivity;
import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.DatePickerFragment;
import com.example.ventasrovianda.Utils.Models.AddressCoordenatesResponse;
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
import com.example.ventasrovianda.Utils.bd.AppDatabase;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.Utils.bd.entities.ClientVisit;
import com.example.ventasrovianda.Utils.bd.entities.Debt;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;
import com.example.ventasrovianda.Utils.bd.entities.EndingDay;
import com.example.ventasrovianda.Utils.bd.entities.Product;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.Utils.bd.entities.UserDataInitial;
import com.example.ventasrovianda.home.adapters.AdapterListProductSale;
import com.example.ventasrovianda.home.fragments.MapDialogFragment;
import com.example.ventasrovianda.home.presenter.HomePresenter;
import com.example.ventasrovianda.home.presenter.HomePresenterContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;


public class HomeView extends Fragment implements View.OnClickListener, HomeViewContract {
    BottomNavigationView homeButton;
    MaterialButton buscarClienteButton, agregarProductoButton, cobrarButton;
    TextInputLayout clientInput, keyProductInput, weightProduct;
    NavController navController;
    CircularProgressIndicator circularProgressIndicator;
    TextView clientSaeKey, clientName;
    Button endDayButton, logoutButton;
    ImageView printerButton;
    HomePresenterContract presenter;
    String printerName;
    BluetoothAdapter bluetoothAdapter;
    ClientDTO currentClient = null;
    ClientDTO selectedClientTovisit = null;
    LinearLayout listCarSale;
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
    Boolean sincronizateSession = false;
    Client clientSelected = null;
    UserDataInitial userData = null;
    String dateSelected = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home, container, false);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        navController = NavHostFragment.findNavController(this);
        this.userName = HomeViewArgs.fromBundle(getArguments()).getUserName();
        this.userNameTextView = v.findViewById(R.id.userName);
        userNameTextView.setText("Usuario: " + this.userName);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));
        this.presenter = new HomePresenter(getContext(), this);
        this.printerButton = v.findViewById(R.id.printerButton);
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
        this.printerUtil = new PrinterUtil(getContext());
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
        checkConnection();
        checkIfPrinterConfigured();
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("FECHA_DE_HOY: " + ldt);
        String dateParsed = formmat1.format(ldt);
        dateSelected = dateParsed;
    }


    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    void checkSalesUnSincronized() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Sale> sales = conexion.saleDao().getAllSalesUnsincronized();
                List<ModeOfflineSM> modeOfflineSMS = new ArrayList<>();
                List<DevolutionRequestServer> devolutionRequestServers = new ArrayList<>();
                for (Sale sale : sales) {
                    if(!sale.isTempKeyClient){
                        System.out.println("Sale without sincronization: " + sale.folio);
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
                        for (SubSale subSale : subSales) {
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
                    List<DevolutionRequest> devolutionsRequests = conexion.devolutionRequestDao().getAllUnsincronized();

                    for (DevolutionRequest devolutionRequest : devolutionsRequests) {
                        DevolutionRequestServer devolutionRequestServer = new DevolutionRequestServer();
                        devolutionRequestServer.setCreateAt(devolutionRequest.createAt);
                        devolutionRequestServer.setDevolutionId(devolutionRequest.devolutionRequestId);
                        devolutionRequestServer.setFolio(devolutionRequest.folio);
                        devolutionRequestServer.setObservations(devolutionRequest.description);
                        devolutionRequestServer.setTypeDevolution(devolutionRequest.typeDevolution);
                        List<DevolutionSubSaleRequestServer> devolutionSubSaleRequestServersModified = new ArrayList<>();
                        List<DevolutionSubSaleRequestServer> devolutionSubSaleRequestServersOriginal = new ArrayList<>();
                        List<DevolutionSubSale> devolutionSubSales = conexion.devolutionSubSaleDao().findByDevolutionRequestId(devolutionRequest.devolutionRequestId);
                        for (DevolutionSubSale devolutionSubSale : devolutionSubSales) {
                            DevolutionSubSaleRequestServer devolutionSubSaleRequestServer = new DevolutionSubSaleRequestServer();
                            devolutionSubSaleRequestServer.setAmount(devolutionSubSale.price);
                            devolutionSubSaleRequestServer.setAppSubSaleId(devolutionSubSale.subSaleId);
                            devolutionSubSaleRequestServer.setCreateAt(devolutionRequest.createAt);
                            devolutionSubSaleRequestServer.setPresentationId(devolutionSubSale.presentationId);
                            devolutionSubSaleRequestServer.setProductId(devolutionSubSale.productId);
                            devolutionSubSaleRequestServer.setQuantity(devolutionSubSale.quantity);
                            devolutionSubSaleRequestServersModified.add(devolutionSubSaleRequestServer);
                        }
                        List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(devolutionRequest.folio);
                        for (SubSale subSale : subSales) {
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
                }
                List<Debt> debts = conexion.debtDao().getAllDebsWithoutSincronization();
                List<DebPayedRequest> debtsPayed = new ArrayList<>();
                for (Debt debt : debts) {
                    if (debt.sincronized == false && debt.deleted == false) {
                        DebPayedRequest debPayedRequest = new DebPayedRequest();
                        Sale sale = conexion.saleDao().getByFolio(debt.folio);
                        if(!sale.isTempKeyClient) {
                            debPayedRequest.setAmountPayed(sale.amount);
                            debPayedRequest.setDatePayed(debt.createAt);
                            debPayedRequest.setFolio(sale.folio);
                            debPayedRequest.setPayedType(debt.payedType);
                            debtsPayed.add(debPayedRequest);
                        }
                    }
                }
                List<Sale> debtsWithoutPayment = conexion.saleDao().getAllWithoutPayment();
                List<String> foliosWithoutPayment = new ArrayList<String>();
                for (Sale sale : debtsWithoutPayment) {
                    foliosWithoutPayment.add(sale.folio);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (modeOfflineSMS.size() > 0 || debtsPayed.size() > 0 || devolutionRequestServers.size() > 0 || foliosWithoutPayment.size() > 0) {
                            if (hasInternet) {
                                showNotificationSincronization("HOME Sincronizando...");
                                presenter.sincronizeSales(modeOfflineSMS, debtsPayed, devolutionRequestServers, foliosWithoutPayment, viewModelStore.getStore().getSellerId());
                            }
                        } else {
                            showNotificationSincronization("Nada por sincronizar...");
                        }
                    }

                });
            }
        });
    }

    Boolean isConnected = true;
    ConnectivityManager.NetworkCallback networkCallback = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    void checkConnection() {
        if (this.networkCallback != null) {
            this.unregisteredNetworkCallback();
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        this.networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isConnected = true;
                hasInternet = false;
                presenter.checkCommunicationToServer();
            }

            @Override
            public void onLost(@NonNull Network network) {
                isConnected = false;
                setStatusConnectionServer(false);
            }
        };
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    Boolean hasInternet = true;

    @Override
    public void setStatusConnectionServer(Boolean statusConnectionServer) {
        hasInternet = statusConnectionServer;
        if (hasInternet) {
            checkSalesUnSincronized();
        } else if (isConnected) {
            showNotificationSincronization("Conectado pero sin Internet, (Intermitencia de señal/No hay datos moviles).");
        } else if (!hasInternet && !isConnected) {
            showNotificationSincronization("No hay acceso a internet, (Sin señal/Sin Wifi)");
        }
    }


    @Override
    public void completeSincronzation(SincronizationResponse sincronizationResponse) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                for (int i = 0; i < sincronizationResponse.getSalesSincronized().size(); i++) {
                    System.out.println("Sincronized: " + sincronizationResponse.getSalesSincronized().get(i).getFolio());
                    conexion.saleDao().updateSaleId(sincronizationResponse.getSalesSincronized().get(i).getSaleId(), sincronizationResponse.getSalesSincronized().get(i).getFolio());
                }
                for (String folio : sincronizationResponse.getDebtsSicronized()) {
                    Debt debt = conexion.debtDao().getDebtByFolio(folio);
                    debt.sincronized = true;
                    conexion.debtDao().updateDebtSincronization(debt);
                }
                for (String folio : sincronizationResponse.getDevolutionsSincronized()) {
                    DevolutionRequest devolutionRequest = conexion.devolutionRequestDao().findDevolutionRequestByFolio(folio);
                    devolutionRequest.sincronized = 1;
                    conexion.devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }

                for (String folio : sincronizationResponse.getDevolutionsAccepted()) {
                    DevolutionRequest devolutionRequest = conexion.devolutionRequestDao().findDevolutionRequestByFolio(folio);
                    devolutionRequest.status = "ACCEPTED";
                    conexion.devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }
                for (String folio : sincronizationResponse.getDevolutionsRejected()) {
                    DevolutionRequest devolutionRequest = conexion.devolutionRequestDao().findDevolutionRequestByFolio(folio);
                    devolutionRequest.status = "DECLINED";
                    conexion.devolutionRequestDao().updateDevolutionRequest(devolutionRequest);
                }
                for (String folio : sincronizationResponse.getDebtsOldedPayed()) {
                    Sale saleDebt = conexion.saleDao().getByFolio(folio);
                    saleDebt.status = false;
                    conexion.saleDao().updateSale(saleDebt);
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void unregisteredNetworkCallback() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);
        networkCallback = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void goToCotizaciones() {
        unregisteredNetworkCallback();
        navController.navigate(HomeViewDirections.actionHomeViewToCotizacionesView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void goToClient() {
        unregisteredNetworkCallback();
        navController.navigate(HomeViewDirections.actionHomeViewToClientView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void goToPedidos() {
        unregisteredNetworkCallback();
        navController.navigate(HomeViewDirections.actionHomeViewToPedidoView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void goToSalesHistory() {
        unregisteredNetworkCallback();
        navController.navigate(HomeViewDirections.actionHomeViewToSalesView(this.userName).setUserName(this.userName).setClientInVisit(this.selectedClientTovisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void goToLogin() {
        unregisteredNetworkCallback();
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
                                AppDatabase conexion = AppDatabase.getInstance(getContext());
                                conexion.userDataInitialDao().updateAllLogedInFalse();
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
                        logoutPicked = false;
                    }
                }).setCancelable(false).create();
        dialog.show();
    }


    private Boolean logoutPicked = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Logout_button:
                if (!logoutPicked) {
                    this.logoutPicked = true;
                    logout();
                }
                break;
            case R.id.printerButton:
                if (this.sincronizateSession) {
                    if (this.printerConnected == true) {
                        this.printerConnected = false;
                        if (this.printerUtil != null) {
                            this.printerUtil.desconect();
                            this.printer = null;
                        }
                        this.printerNoConnected();
                    } else if (!isLoading) {
                        System.out.println("TOUCHED: " + isLoading);
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
                        try {
                            Integer code = Integer.parseInt(this.clientInput.getEditText().getText().toString());
                            findClient(code);
                        }catch (NumberFormatException e){
                            System.out.println("Codigo de cliente incorrecto");
                        }
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
                if (this.sincronizateSession) {
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
                if (this.sincronizateSession) {

                    if (isLoading == false) {

                        showDatePicker();

                    }
                }
                break;

        }

    }
    void checkAllVisitsForCurrentDay(String currentDate){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Calendar calendar = Calendar.getInstance();
                int day =calendar.get(Calendar.DAY_OF_WEEK);
                List<Client> clients = new ArrayList<>();
                List<Client> withouPendingVisitRecord = new ArrayList<>();
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
                for(Client client : clients){
                    ClientVisit clientVisit=null;
                    if(client.clientRovId!=null && client.clientRovId!=0) {
                        clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientRovId,currentDate);
                    }else{
                        clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientMobileId,currentDate);
                    }
                    if(clientVisit==null){
                        withouPendingVisitRecord.add(client);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //findEndDay(currentDate, "CLOSE");
                        if(withouPendingVisitRecord.size()>0){
                            showModalMessage("Visitas pendientes","Falta registrar visitas a clientes");
                        }else{
                            findEndDay(currentDate, "CLOSE");
                        }
                    }
                });
            }
        });
    }

    AlertDialog modalSuccess = null;
    public void showModalMessage(String title,String msg){
        this.isLoading=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view= layoutInflater.inflate(R.layout.clientv2_modal_coordenates, null);
        ProgressBar progressBarModalCoordenates = view.findViewById(R.id.coodenatesModalProgress);
        TextView coordenatesModalTitle = view.findViewById(R.id.coodenatesModalTitle);
        TextView coordenatesModalMsg = view.findViewById(R.id.coordenatesModalMsg);
        builder.setCancelable(false);
        builder.setView(view);
        this.modalSuccess= builder.create();
        modalSuccess.show();
        Button acceptButtonModalCoordeantes= view.findViewById(R.id.acceptCoordenatesButton);
        acceptButtonModalCoordeantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalSuccess.dismiss();
            }
        });
        coordenatesModalTitle.setText(title);
        coordenatesModalMsg.setText(msg);
        progressBarModalCoordenates.setVisibility(View.GONE);
    }

    void showDatePicker() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = String.valueOf(month + 1);
                String day = String.valueOf(dayOfMonth);
                if ((month + 1) < 10) monthStr = "0" + monthStr;
                if (dayOfMonth < 10) day = "0" + day;
                dateSelected = year + "-" + monthStr + "-" + day;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateFormat = dateFormat.format(calendar.getTime());
                if (currentDateFormat.equals(dateSelected)) {
                    checkAllVisitsForCurrentDay(currentDateFormat);
                } else {
                    getEndDayTicketOffline(dateSelected);
                }
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    void findEndDay(String endDay, String type) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                EndingDay endingDay = conexion.endingDayDao().getEndingDayByDate(endDay);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (type.equals("CLOSE")) {
                            if (endingDay != null) {
                                getEndDayTicketOffline(dateSelected);
                            } else {
                                showAlertEndDay(endDay);
                            }
                        } else if (type.equals("SALE")) {
                            if (endingDay == null) {
                                if(clientSelected.latitude==null && clientSelected.longitude==null) {
                                    showAddressToAssign();
                                }else{
                                    showOptionsPayed();
                                }
                            } else {
                                genericMessage("Alerta", "Ya no se pueden hacer ventas en la fecha actual");
                            }
                        }
                    }
                });
            }
        });
    }

    void showAddressToAssign(){
        Location location = getLastKnownLocation();
        if(location!=null){
            presenter.getAddressByCoordenates(location.getLatitude(),location.getLongitude());
        }else{
            onDialogNegativeClick();
        }
    }

    void showModalMap(){
        DialogFragment mapDialogFragment = new MapDialogFragment(this);
        mapDialogFragment.setCancelable(false);
        mapDialogFragment.show(getActivity().getSupportFragmentManager(), "MapDialogFragment");
    }


    void showAlertEndDay(String currentDate) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cierre de día")
                .setMessage("¿Está seguro que desea cerrar la venta del dia " + currentDate + ", (No podrá realizar otra venta en esta fecha) ?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveEndDayRecord(currentDate);
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    void saveEndDayRecord(String endDay) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                EndingDay endingDay = new EndingDay();
                endingDay.date = endDay;
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                conexion.endingDayDao().saveEndingDay(endingDay);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        presenter.sendEndDayRecord(dateSelected, viewModelStore.getStore().getSellerId());
                        getEndDayTicketOffline(dateSelected);
                    }
                });
            }
        });
    }


    void findProduct(String productKey) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Product product = conexion.productDao().getProductByProduct(productKey, viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (product == null) {
                            findByProductEnd("%" + productKey);
                        } else {
                            addProductToSaleCar(product);
                        }
                    }
                });
            }
        });
    }

    void findByProductEnd(String productKey) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Product product = conexion.productDao().getProductByProduct(productKey, viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (product == null) {
                            keyProductInput.getEditText().setError("No existe el producto indicado");
                            isLoading = false;
                            circularProgressIndicator.setVisibility(View.GONE);
                        } else {
                            addProductToSaleCar(product);
                        }
                    }
                });
            }
        });
    }

    void findClient(Integer clientKey) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Client client = conexion.clientDao().getClientByKey(clientKey, viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (client != null) {
                            clientSelected = client;
                            circularProgressIndicator.setVisibility(View.GONE);
                            isLoading = false;
                            clientInput.getEditText().setError(null);
                            clientSaeKey.setText("Cliente: " + client.clientKey);
                            clientName.setText(client.name);
                            clientSaeKey.setTextColor(Color.parseColor("#236EF2"));
                            clientName.setTextColor(Color.parseColor("#236EF2"));
                            carSale = new ArrayList<>();
                            fillList();
                        } else {
                            clientInput.getEditText().setError("Cliente no existe");
                        }
                    }
                });

            }
        });
    }

    void checkIfSincronizate() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitials = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (userDataInitials == null) {
                            genericMessage("Alerta Sistema", "Requiere de su primera sincronización");
                            sincronizateSession = false;
                        } else {
                            userData = userDataInitials;
                            sincronizateSession = true;
                        }
                    }
                });
            }
        });
    }

    int selectIndexPrinter;

    PrinterUtil printerUtil = null;

    @Override
    public void setErrorClientInput(String msg) {
        this.clientInput.getEditText().setError(msg);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findPrinter(Set<BluetoothDevice> devices) {
        this.circularProgressIndicator.setVisibility(View.VISIBLE);
        isLoading = true;
        List<BluetoothDevice> bluetoothDevicesMapped = devices.stream().collect(Collectors.toList());
        String[] bluetoothDevices = new String[devices.size()];
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < devices.size(); i++) {
                bluetoothDevices[i] = bluetoothDevicesMapped.get(i).getName();
            }
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
                                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                                    conexion.userDataInitialDao().updatePrinterAddress(viewModelStore.getStore().getSellerId(),printer.getAddress());
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
                this.isLoading=true;
                if(!this.bluetoothAdapter.isEnabled()){
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth,0);
                }else {
                    printerUtil = new PrinterUtil(getContext());
                    final Set<BluetoothDevice> deviceList = printerUtil.findDevices();
                    if (deviceList.size() > 0) {
                        findPrinter(deviceList);
                    }
                }

    }
    void checkIfPrinterConfigured(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
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
        //ProductRoviandaToSale[] productRoviandaToSales = new ProductRoviandaToSale[carSale.size()];
        amount = Float.parseFloat("0");
        listCarSale.removeAllViews();
        for (int i = 0; i < carSale.size(); i++) {
            //productRoviandaToSales[i] = carSale.get(i);
            amount += carSale.get(i).getPrice() * carSale.get(i).getWeight();

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view= layoutInflater.inflate(R.layout.item_list_product_sale, null);

            TextView code = (TextView) view.findViewById(R.id.productCode);
            TextView name = (TextView) view.findViewById(R.id.productName);
            TextView weight = (TextView) view.findViewById(R.id.productWeight);
            code.setText(carSale.get(i).getKeySae());
            name.setText(carSale.get(i).getNameProduct()+"\n"+carSale.get(i).getPresentationType());
            name.setTextSize(14);
            weight.setText(String.valueOf(carSale.get(i).getWeight()));
            final int index=i;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    carSale.remove(index);
                    fillList();
                    return true;
                }
            });
            listCarSale.addView(view);
        }

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

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateFormat = dateFormat.format(calendar.getTime());
        findEndDay(currentDateFormat,"SALE");

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        this.selectionPay=0;
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
                    }).setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            paying = false;
                            selectionPay=0;
                        }
                    }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Selected: " + selectionPay);
                            doSaleModalConfirmation();
                        }
                    }).setCancelable(false).create();
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
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<Sale> salesOfday = conexion.saleDao().getAllSalesByDate(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
                for(Sale sale : salesOfday){
                    totalTickets++;
                    if(!(sale.statusStr.equals("CANCELED") && sale.cancelAutorized!=null && sale.cancelAutorized.equals("true"))) {


                        List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(sale.folio);
                        DevolutionRequest devolutionRequest = conexion.devolutionRequestDao().findDevolutionRequestByFolioRegister(sale.folio);
                        Float amountOfSale=Float.parseFloat("0");
                        if(devolutionRequest!=null && devolutionRequest.status.equals("ACCEPTED")){

                            for(SubSale subSale : subSales){
                                DevolutionSubSale devolutionSubSale = conexion.devolutionSubSaleDao().findDevolutionSubSaleBySubSaleId(subSale.subSaleId);
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
                        clientsStr += "\n" + sale.folio + " " + sale.clientName +(sale.isTempKeyClient==true?" Temp:":" ")+sale.keyClient+ "\n $" + amountOfSale + " " + ((sale.typeSale.equals("CREDITO")) ? "C" : (sale.typeSale.equals("TRANSFERENCIA")?"TRANSFER":"")) + " ";
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

                List<Debt> debts = conexion.debtDao().getAllSalesDebtsBetweenDates(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
                for(Debt debt : debts){
                    if(!debt.deleted) {
                        Sale sale = conexion.saleDao().getByFolio(debt.folio);
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

            builder.setView(view);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!textInputLayout.getEditText().getText().toString().isEmpty()) {
                        System.out.println("acepto el cobró");
                        cobrarButton.setEnabled(false);
                        circularProgressIndicator.setVisibility(View.VISIBLE);
                        saveSqlSale(amount);
                    }else{
                        paying=false;
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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateFormat = dateFormat.format(calendar.getTime());
        executor.execute(new  Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
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

                    if(clientSelected.clientRovId==null || clientSelected.clientRovId==0) {
                        sale.keyClient = clientSelected.clientKeyTemp;
                        sale.isTempKeyClient=true;
                    }else {
                        sale.keyClient = clientSelected.clientKey;
                        sale.isTempKeyClient = false;
                    }
                    sale.payed=amount;
                    sale.sellerId=viewModelStore.getStore().getSellerId();
                    sale.sincronized=false;
                    sale.statusStr="ACTIVE";
                    if(clientSelected.clientRovId!=null && clientSelected.clientRovId!=0) {
                        sale.clientId = clientSelected.clientRovId;
                    }else{
                        sale.clientId=clientSelected.clientMobileId;
                    }
                    sale.modified=false;
                    sale.folio=userDataInitial.nomenclature+(folioCount+1);
                    List<SubSale> subSales = new ArrayList<>();
                    for(ProductRoviandaToSale product : carSale){
                        Product product1=conexion.productDao().getProductByKey(product.getKeySae());
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
                    conexion.saleDao().insertAll(sale);
                    conexion.userDataInitialDao().updateFolioCount(folioCount+1, viewModelStore.getStore().getSellerId());
                    for (SubSale subSale1 : subSales) {
                        conexion.subSalesDao().insertAllSubSales(subSale1);
                    }
                ClientVisit clientVisit=null;

                    if(clientSelected.clientRovId!=null && clientSelected.clientRovId!=0) {
                        clientVisit=conexion.clientVisitDao().getClientVisitByIdAndDate(clientSelected.clientRovId, currentDateFormat);
                    }else{
                        clientVisit=conexion.clientVisitDao().getClientVisitByIdAndDate(clientSelected.clientMobileId,currentDateFormat);
                    }
                    if(clientVisit==null){
                        clientVisit = new ClientVisit();
                        clientVisit.isClientIdTemp=!(clientSelected.clientRovId!=null && clientSelected.clientRovId!=0);
                        clientVisit.clientId=(clientSelected.clientRovId!=null && clientSelected.clientRovId!=0)?clientSelected.clientRovId:clientSelected.clientMobileId;
                        clientVisit.visited=true;
                        clientVisit.observations="";
                        clientVisit.sincronized=false;
                        clientVisit.amount=sale.amount;
                        clientVisit.date=currentDateFormat;
                        conexion.clientVisitDao().insertClientVisit(clientVisit);
                    }else{
                        clientVisit.amount+=sale.amount;
                        clientVisit.sincronized=false;
                        clientVisit.visited=true;
                        clientVisit.observations="";
                        conexion.clientVisitDao().updateClientVisit(clientVisit);
                    }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        doTicketSaleOffline(sale,subSales,productsMap);
                        System.out.println("SaleSavedInSQL");
                        presenter.checkCommunicationToServer();
                        checkSalesUnSincronized();
                    }
                });
            }
        });
    }

    void saveCurrentLocationToClient(Double latitude,Double longitude) {

            clientSelected.latitude = latitude;
            clientSelected.longitude = longitude;
            clientSelected.sincronized = false;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                    conexion.clientDao().updateClient(clientSelected);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Ubicacion de cliente asignada");
                        }
                    });
                }
            });

    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                l = locationManager.getLastKnownLocation(provider);
            }else{
                l=null;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    void doTicketSaleOffline(Sale sale,List<SubSale> subSales,Map<String,Product> productsMap){
        Calendar cal = Calendar.getInstance();
        Instant instant = cal.toInstant();
        try{
            instant = Instant.parse(sale.date);
            cal.setTime(java.sql.Date.from(instant));
            cal.add(Calendar.HOUR_OF_DAY,5);
        }catch(RuntimeException pe){
            System.out.println("Error al parsear fecha de impresion");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        String dateParsed = dateFormat.format(cal.getTime());
        String ticket = "ROVIANDA SAPI DE CV\nAV.1 #5 Esquina Calle 1\nCongregación Donato Guerra\nParque Industrial Valle de Orizaba\nC.P 94780\nRFC 8607056P8\nTEL 272 72 46077, 72 4 5690\n";
        ticket+="Pago en una Sola Exhibición\nLugar de Expedición: Ruta\nNota No. "+sale.folio+"\nFecha: "+dateParsed+"\n\n";
        ticket+="Vendedor:"+viewModelStore.getStore().getUsername()+"\n\nCliente: "+clientSelected.name+"\n"+(sale.isTempKeyClient==true?"Clave Temp:":"Clave:")+clientSelected.clientKey+"\n";
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

        ticket+=ticket;

        saleSuccess(ticket);
    }

    Float extractIva(Float amount){
        return (amount/116)*16;
    }

    Float extractIeps(Float amount,Float percent){
        return (amount/(100+percent))*percent;
    }


    public BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    int intentsToClose = 0;
    @Override
    public void saleSuccess(String ticket) {
        intentsToClose=0;
        paying=false;
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

    @Override
    public void onDialogPositiveClick(Double latitude,Double longitude) {
        presenter.getAddressByCoordenates(latitude,longitude);
    }

    @Override
    public void onDialogNegativeClick() {
        showOptionsPayed();
        showModalMessage("Infomación","Favor de asignar direccion al cliente mas tarde");
    }

    @Override
    public void setAddressForConfirm(AddressCoordenatesResponse addressForConfirm,Double latitude,Double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view =layoutInflater.inflate(R.layout.modal_confirm_direcction,null);
        TextInputEditText modalAddressDetails  = view.findViewById(R.id.modalAddressDetails);
        Button confirmButton = view.findViewById(R.id.confirmButton);
        Button changeButton = view.findViewById(R.id.changeButton);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                paying=false;
                onDialogNegativeClick();
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        modalAddressDetails.setText("Calle: "+addressForConfirm.getStreet()+"\nColonia: "+addressForConfirm.getSuburb()+"\nMunicipio: "+addressForConfirm.getMunicipality()+"\nCp. "+addressForConfirm.getCp());
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentLocationToClient(latitude,longitude);
                dialog.dismiss();
                showOptionsPayed();
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showModalMap();
            }
        });

    }



}
