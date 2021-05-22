package com.example.ventasrovianda.home.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientOfflineMode;
import com.example.ventasrovianda.Utils.Models.CounterTime;
import com.example.ventasrovianda.Utils.Models.InventoryOfflineMode;
import com.example.ventasrovianda.Utils.Models.ModeOfflineDebts;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ModeOfflineS;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSMP;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;
import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;
import com.example.ventasrovianda.Utils.Models.ProductSaleDTO;
import com.example.ventasrovianda.Utils.Models.ProductsOfflineMode;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleOfflineMode;
import com.example.ventasrovianda.Utils.Models.TotalSoldedDTO;
import com.example.ventasrovianda.Utils.NumberDecimalFilter;
import com.example.ventasrovianda.Utils.PrinterUtil;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.home.adapters.AdapterListProductSale;
import com.example.ventasrovianda.home.presenter.HomePresenter;
import com.example.ventasrovianda.home.presenter.HomePresenterContract;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
    AlertDialog loadModal = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home, container, false);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        navController = NavHostFragment.findNavController(this);
        selectedClientTovisit = HomeViewArgs.fromBundle(getArguments()).getClientInVisit();
        bluetoothDeviceSerializable = HomeViewArgs.fromBundle(getArguments()).getPrinterDevice();
        this.userName = HomeViewArgs.fromBundle(getArguments()).getUserName();
        this.userNameTextView = v.findViewById(R.id.userName);
        userNameTextView.setText("Usuario: " + this.userName);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));
        this.presenter = new HomePresenter(getContext(), this);
        this.printerButton = v.findViewById(R.id.printerButton);
        //this.printerButton.setEnabled(false);
        //this.printerButton.setVisibility(View.INVISIBLE);
        this.printerButton.setOnClickListener(this);
        this.printerConnected = false;
        this.agregarProductoButton = v.findViewById(R.id.AgregarProductoButton);
        this.agregarProductoButton.setOnClickListener(this);
        this.amount = Float.parseFloat("0");
        logoutButton = v.findViewById(R.id.Logout_button);
        logoutButton.setOnClickListener(this);
        this.circularProgressIndicator = v.findViewById(R.id.loginLoadingSpinner);
        homeButton = v.findViewById(R.id.bottom_navigation_home);
        homeButton.setSelectedItemId(R.id.home_section);
        this.buscarClienteButton = v.findViewById(R.id.buscarClienteButton);
        this.buscarClienteButton.setOnClickListener(this);
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
        this.cobrarButton.setOnClickListener(this);
        this.endDayButton = v.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.eatTimeButton = v.findViewById(R.id.eat_time_button);
        //this.eatTimeButton.setOnClickListener(this);
        this.eatTimeButton.setVisibility(View.INVISIBLE);

        //this.presenter.getCounterTimer(3);
        //this.presenter.getStockOnline();
        if (selectedClientTovisit != null) {
            currentClient = selectedClientTovisit;
            this.clientInput.getEditText().setEnabled(false);
            this.clientInput.getEditText().setText(String.valueOf(currentClient.getKeyClient()));
            presenter.findUser(currentClient.getKeyClient());
            buscarClienteButton.setEnabled(false);
        }
        this.printerUtil = new PrinterUtil(getContext());
        if(bluetoothDeviceSerializable!=null){
            if(this.bluetoothDeviceSerializable.isPrinterConnected()==true){
                this.printerConnected=true;
                this.printer = this.bluetoothDeviceSerializable.getBluetoothDevice();
                this.printerConnected();
                if(printerUtil==null){

                }
            }
        }
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        /*Boolean modified=false;
        if(viewModelStore.getStore()!=null){
            if(viewModelStore.getStore().getSalesMaked()!=null && viewModelStore.getStore().getSalesMaked().size()>0){
                modified=true;
            }
        }*/
        if (checkOffline()) {
            offlineActive = true;
            /*if(isNetworkAvailable() && modified) {
                this.showLoadModal();
                isLoading = true;
                circularProgressIndicator.setVisibility(View.VISIBLE);
                ModeOfflineSincronize modeOfflineSincronize = generateModeOfflineRequest();
                presenter.UploadChanges(modeOfflineSincronize);
            }*/
            Toast.makeText(getContext(), "Modo offline", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Modo online", Toast.LENGTH_SHORT).show();
            offlineActive = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sincronizeComplete() {
        setModeOfflineBackup(viewModelStore.getStore());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
        if (!root.exists()) {
            root.mkdirs();
        }
        File gpxfile = new File(root, "offline-"+dateParsed+".rovi");
        gpxfile.delete();
        presenter.getStockOnline();

    }



    @RequiresApi(api = Build.VERSION_CODES.N)

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

            viewModelStore.saveStore(new ModeOfflineModel());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void sincronizeError() {
        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Occurrio un error al sincronizar, Verifica tu conexión",Toast.LENGTH_SHORT).show();
        dismissLoadModal();
    }

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
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Boolean checkOffline(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
        File gpxfile = new File(root, "offline-"+dateParsed+".rovi");
        return gpxfile.exists();
    }

    @Override
    public void isCountingTime(int typeUsed) {
        if (typeUsed == 1) {
            this.eatTimeButton.setTextColor(Color.parseColor("#28BF2E"));
        } else if (typeUsed == 2) {
            this.eatTimeButton.setTextColor(Color.parseColor("#FFC107"));
        } else if (typeUsed == 3) {
            this.eatTimeButton.setTextColor(Color.GRAY);
        }
    }

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
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesion")
                .setMessage("¿Está seguro que desea cerrar sesion?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.doLogout();
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
                if(this.printerConnected==true){
                   this.printerConnected=false;
                   this.printerUtil.desconect();
                   this.printer=null;
                   this.bluetoothDeviceSerializable.setPrinterConnected(false);
                   this.printerNoConnected();
                }else if(isLoading==false) {
                    activatePrinter();
                    isLoading=true;
                }
                break;
            case R.id.buscarClienteButton:
                if (this.clientInput.getEditText().getText().toString().trim().isEmpty()) {
                    this.setErrorClientInput("Campo obligatorio");
                } else {
                    try {
                        if (!offlineActive) {
                            isLoading = true;
                            circularProgressIndicator.setVisibility(View.VISIBLE);
                            presenter.findUser(Integer.parseInt(this.clientInput.getEditText().getText().toString().trim()));
                        } else {
                            ClientOfflineMode client = null;
                            List<ClientOfflineMode> clients = viewModelStore.getStore().getClients();
                            for (ClientOfflineMode clientItem : clients) {
                                if (clientItem.getKeyClient().equals(this.clientInput.getEditText().getText().toString().trim())) {
                                    client = clientItem;
                                }
                            }
                            if (client == null) {
                                setErrorClientInput("No se encontro el cliente");
                            } else {
                                currentClientOffline = client;
                                setClientOffclient(currentClientOffline);
                            }


                        }
                    } catch (NumberFormatException e) {
                        this.setErrorClientInput("Código invalido");
                    }
                }
                break;
            case R.id.AgregarProductoButton:
                if (offlineActive == false) {
                    if (currentClient != null) {
                        if (isLoading == false) {
                            if (!this.weightProduct.getEditText().getText().toString().trim().isEmpty()) {
                                isLoading = true;
                                this.circularProgressIndicator.setVisibility(View.VISIBLE);
                                presenter.findProduct(this.keyProductInput.getEditText().getText().toString().trim());
                            } else {
                                this.setErrorProductWeightInput("Por favor indica un peso o numero de piezas");
                            }
                        }
                    } else {
                        this.setErrorClientInput("Por favor selecciona un cliente");
                    }
                } else {
                    if (currentClientOffline != null) {
                        if (!this.weightProduct.getEditText().getText().toString().trim().isEmpty()) {
                            isLoading = true;
                            this.circularProgressIndicator.setVisibility(View.VISIBLE);
                            //presenter.findProduct(this.keyProductInput.getEditText().getText().toString().trim());
                            findProductOffline(this.keyProductInput.getEditText().getText().toString().trim());
                        } else {
                            this.setErrorProductWeightInput("Por favor indica un peso o numero de piezas");
                        }
                    } else {
                        this.setErrorClientInput("Por favor selecciona un cliente");
                    }
                }
                break;
            case R.id.cobrarButton:
                System.out.println("Is paying: " + paying);
                if (paying == false) {
                    if(carSale.size()>0) {
                        paying = true;
                        System.out.println("Paying true: " + paying);
                        payProducts();
                    }else{
                        Toast.makeText(getContext(),"No haz agregado productos",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.end_day_button:
                if (!offlineActive) {
                    if (isLoading == false) {
                        isLoading = true;
                        this.circularProgressIndicator.setVisibility(View.VISIBLE);
                        this.getEndDayTicket();
                    }
                } else {
                    if (isLoading == false) {
                        isLoading = true;
                        this.circularProgressIndicator.setVisibility(View.VISIBLE);
                        this.getEndDayTicketOffline();
                    }
                }
                break;
            case R.id.eat_time_button:
                if (isLoading == false) {
                    isLoading = true;
                    presenter.getCounterTimer(1);
                    this.circularProgressIndicator.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.terminar_timer:
                if (isLoading == false) {
                    isLoading = true;
                    this.circularProgressIndicator.setVisibility(View.VISIBLE);
                    System.out.println("Terminado");
                    timerActive = false;
                    presenter.endEatTime();
                    if (dialogTimer.isShowing()) {
                        dialogTimer.dismiss();
                    }
                }
                break;
        }
    }

    void findProductOffline(String productKeySae) {
        ProductRoviandaToSale productRovianda = null;
        for (InventoryOfflineMode productInve : viewModelStore.getStore().getInventory()) {
            if (productInve.getCodeSae().equals(productKeySae) ) {
                ProductRoviandaToSale productRoviandaToSale = new ProductRoviandaToSale();
                productRoviandaToSale.setNameProduct(productInve.getProductName());
                productRoviandaToSale.setKeySae(productInve.getCodeSae());
                productRoviandaToSale.setWeight(Float.parseFloat("100"));

                if(productInve.getUniMed().equals("PZ")) {
                    productRoviandaToSale.setIsPz(true);
                    productRoviandaToSale.setWeight(Float.parseFloat("100"));
                    productRoviandaToSale.setQuantity(productInve.getPieces());
                }else{
                    productRoviandaToSale.setIsPz(false);
                    productRoviandaToSale.setWeight(productInve.getWeight());
                    productRoviandaToSale.setQuantity(Float.parseFloat("100"));
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
                        productRoviandaToSale.setQuantity(productInve.getPieces());
                    } else {
                        productRoviandaToSale.setIsPz(false);
                        productRoviandaToSale.setWeight(productInve.getWeight());
                        productRoviandaToSale.setQuantity(Float.parseFloat("300"));
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
            addProductToSaleCar(productRovianda);
        } else {
            Toast.makeText(getContext(), "No existe el producto en inventario", Toast.LENGTH_SHORT).show();
            isLoading = false;
            circularProgressIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void confirmEatTime() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Hora de comida")
                .setMessage("¿Seguro que desea habilitar la hora de comida?, solo dispone de 30 minutos.").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.startEatTime();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isLoading = false;
                        circularProgressIndicator.setVisibility(View.GONE);
                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    void getEndDayTicket() {

        if (this.printerConnected == true) {
            presenter.getEndDayTicket();
        } else {
            this.genericMessage("Error de impresora", "Revisa la conexión");
            this.circularProgressIndicator.setVisibility(View.GONE);
            isLoading = false;
        }
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
                }).setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
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
                                    }else{
                                        showErrorConnectingPrinter();
                                        printerNoConnected();

                                    }
                            if(bluetoothDeviceSerializable==null) {
                                bluetoothDeviceSerializable = new BluetoothDeviceSerializable();
                            }
                            bluetoothDeviceSerializable.setBluetoothDevice(printer);
                            bluetoothDeviceSerializable.setPrinterConnected(true);
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
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Error de conexión")
                .setMessage("No se pudo conectar a la impresora ").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Conexión con impresora")
                .setMessage("Conectado a la impresora : " + printerName).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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

    @Override
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
    }

    public void setClientOffclient(ClientOfflineMode client) {
        this.circularProgressIndicator.setVisibility(View.GONE);
        isLoading = false;
        this.clientInput.getEditText().setError(null);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setTypeClient(client.getType());
        clientDTO.setKeyClient(Integer.parseInt(client.getKeyClient()));
        clientDTO.setName(client.getClientName());
        clientDTO.setId(client.getClientId());
        this.currentClient = clientDTO;
        this.clientSaeKey.setText("Cliente: " + client.getKeyClient());
        this.clientName.setText(client.getClientName());
        this.clientSaeKey.setTextColor(Color.parseColor("#236EF2"));
        this.clientName.setTextColor(Color.parseColor("#236EF2"));
        this.carSale = new ArrayList<>();
        fillList();
    }

    List<ProductRoviandaToSale> carSale;

    @Override
    public void addProductToSaleCar(ProductRoviandaToSale productRoviandaToSale) {
        isLoading = false;
        this.circularProgressIndicator.setVisibility(View.GONE);
        Float countRequested;
        System.out.println("Es pieza: " + productRoviandaToSale.getKeySae() + " " + productRoviandaToSale.isIsPz());
        if (productRoviandaToSale.isIsPz()) {
            countRequested = Float.parseFloat(String.valueOf(Math.round(Float.parseFloat(this.weightProduct.getEditText().getText().toString()))));
            if (countRequested == 0) {
                genericMessage("El producto se vende por piezas", "Introduce un numero entero.");
                return;
            }
        } else {
            countRequested = Float.parseFloat(this.weightProduct.getEditText().getText().toString());
        }
        if (countRequested > 0) {
            Float totalResguarded = Float.parseFloat("0");
            for (ProductRoviandaToSale product : carSale) {
                if (product.getKeySae().equals(productRoviandaToSale.getKeySae())) {
                    totalResguarded += product.getWeight();
                }
            }
            Float totalResguardedTemp = totalResguarded + countRequested;
            if (productRoviandaToSale.getQuantity() >= totalResguardedTemp) {
                productRoviandaToSale.setWeight(countRequested);
                int index = -1;
                for (int i = 0; i < carSale.size(); i++) {
                    ProductRoviandaToSale item = carSale.get(i);
                    if (item.getPresentationId() == productRoviandaToSale.getPresentationId()) {
                        index = i;
                        item.setWeight(
                                item.getWeight() + productRoviandaToSale.getWeight()
                        );
                    }
                }
                if (index == -1) {
                    carSale.add(productRoviandaToSale);
                }
                this.keyProductInput.getEditText().setText(null);
                this.weightProduct.getEditText().setText(null);
                fillList();
            } else {
                genericMessage("Error en stock", "Solo tienes: " + (productRoviandaToSale.getQuantity() - totalResguarded) + " para vender");
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
        } else if (section == 3) {
            goToClient();
        } else if (section == 4) {
            goToPedidos();
        }
    }

    int selectionPay = 0;
    String[] contadoOptions = {"Efectivo", "Transferencia", "Cheque"};
    String[] creditoOptions = {"Crédito", "Efectivo"};

    void showOptionsPayed() {
        String[] selectMode = null;

        if (currentClient != null) {
            if (currentClient.getTypeClient().equals("CONTADO")) {
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

    void getEndDayTicketOffline() {

        Double weightG = Double.parseDouble("0");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        String clientsStr="";
        if(viewModelStore.getStore()!=null){
        if( viewModelStore.getStore().getSales()!=null) {
            for (SaleOfflineMode sale : viewModelStore.getStore().getSales()) {
                if(!sale.getStatusStr().equals("CANCELED")) {
                    for (ProductsOfflineMode productOffline : sale.getProducts()) {
                        String productName = skus.get(productOffline.getProductKey());
                        if (productName == null) {
                            skus.put(productOffline.getProductKey(), productOffline.getProductName() + " " + productOffline.getProductPresentationType());
                        }
                        Float weight = weightTotal.get(productOffline.getProductKey());
                        if (weight == null) {
                            Float weightByProduct = (productOffline.getType().equals("PZ") ? productOffline.getQuantity() * productOffline.getWeightStandar() : productOffline.getQuantity());
                            weightTotal.put(productOffline.getProductKey(), weightByProduct);
                            weightG += weightByProduct;
                        } else {
                            weight += (productOffline.getType().equals("PZ") ? productOffline.getQuantity() * productOffline.getWeightStandar() : productOffline.getQuantity());
                            weightTotal.put(productOffline.getProductKey(), weight);
                            weightG += (productOffline.getType().equals("PZ") ? productOffline.getQuantity() * productOffline.getWeightStandar() : productOffline.getQuantity());
                            ;
                        }

                        Float amountByProduct = pricesBySku.get(productOffline.getProductKey());
                        if (amountByProduct == null) {
                            Float amount = productOffline.getPrice();
                            pricesBySku.put(productOffline.getProductKey(), amount);
                        }

                        Float amountSubSale = amountTotal.get(productOffline.getProductKey());
                        if (amountSubSale == null) {
                            Float amount = productOffline.getPrice()*productOffline.getQuantity();
                            amountTotal.put(productOffline.getProductKey(), amount);
                        }else{
                            amountSubSale+=productOffline.getPrice()*productOffline.getQuantity();
                            amountTotal.put(productOffline.getProductKey(), amountSubSale);
                        }
                        if(productOffline.getType().equals("PZ")){
                            Float piecesOfProduct = piecesTotal.get(productOffline.getProductKey());
                            if(piecesOfProduct==null){
                                piecesTotal.put(productOffline.getProductKey(),productOffline.getQuantity());
                            }else{
                                piecesTotal.put(productOffline.getProductKey(),(piecesOfProduct+productOffline.getQuantity()));
                            }
                        }
                    }

                    clientsStr += "\n" + sale.getFolio() + " " + sale.getClientName() +" "+sale.getKeyClient()+ "\n $" + sale.getAmount() + " " + ((sale.getTypeSale().equals("Crédito") || sale.getTypeSale().equals("CREDITO")) ? "C" : "") + " " + (sale.getStatusStr().equals("CANCELED") ? "CANCELADO" : "");

                    if (sale.getTypeSale().equals("CREDITO") || sale.getTypeSale().equals("Crédito")) {
                        credito += sale.getAmount();
                    } else if (sale.getTypeSale().equals("Transferencia")) {
                        transferencia += sale.getAmount();
                    } else if (sale.getTypeSale().equals("Efectivo")) {
                        efectivo += sale.getAmount();
                    } else if (sale.getTypeSale().equals("Cheque")) {
                        cheque += sale.getAmount();
                    }
                }
            }
        }
        if(viewModelStore.getStore().getSalesMaked()!=null) {
            for (SaleDTO saleDTO : viewModelStore.getStore().getSalesMaked()) {
                if(!saleDTO.getStatusStr().equals("CANCELED")) {
                    for (ProductSaleDTO productSaleDTO : saleDTO.getProducts()) {
                        String productName = skus.get(productSaleDTO.getProductKey());
                        if (productName == null) {
                            skus.put(productSaleDTO.getProductKey(), productSaleDTO.getProductName() + " " + productSaleDTO.getProductPresentation());
                        }
                        Float weight = weightTotal.get(productSaleDTO.getProductKey());
                        if (weight == null) {
                            Float weightByProduct = (productSaleDTO.getTypeUnid().equals("PZ") ? productSaleDTO.getQuantity() * productSaleDTO.getWeightOriginal() : productSaleDTO.getQuantity());
                            weightTotal.put(productSaleDTO.getProductKey(), weightByProduct);
                            weightG += weightByProduct;
                        } else {
                            weight += (productSaleDTO.getTypeUnid().equals("PZ") ? productSaleDTO.getQuantity() * productSaleDTO.getWeightOriginal() : productSaleDTO.getQuantity());
                            weightTotal.put(productSaleDTO.getProductKey(), weight);
                            weightG += (productSaleDTO.getTypeUnid().equals("PZ") ? productSaleDTO.getQuantity() * productSaleDTO.getWeightOriginal() : productSaleDTO.getQuantity());
                            ;
                        }

                        Float amountByProduct = pricesBySku.get(productSaleDTO.getProductKey());
                        if (amountByProduct == null) {
                            Float amount = productSaleDTO.getPrice();
                            pricesBySku.put(productSaleDTO.getProductKey(), amount);
                        }

                        Float amountSubSale = amountTotal.get(productSaleDTO.getProductKey());
                        if (amountSubSale == null) {
                            Float amount = productSaleDTO.getPrice() * productSaleDTO.getQuantity();
                            amountTotal.put(productSaleDTO.getProductKey(), amount);
                        }else{
                            amountSubSale+= productSaleDTO.getPrice() * productSaleDTO.getQuantity();
                            amountTotal.put(productSaleDTO.getProductKey(), amountSubSale);
                        }

                        if(productSaleDTO.getTypeUnid().equals("PZ")){
                            Float piecesOfProduct = piecesTotal.get(productSaleDTO.getProductKey());
                            if(piecesOfProduct==null){
                                piecesTotal.put(productSaleDTO.getProductKey(),productSaleDTO.getQuantity());
                            }else{
                                piecesTotal.put(productSaleDTO.getProductKey(),(piecesOfProduct+productSaleDTO.getQuantity()));
                            }
                        }
                    }

                    clientsStr += "\n" + saleDTO.getFolio() + " " + saleDTO.getClientName() +" "+saleDTO.getKeyClient()+ "\n $" + saleDTO.getAmount() + " " + ((saleDTO.getTypeSale().equals("Crédito") || saleDTO.getTypeSale().equals("CREDITO")) ? "C" : "")+ " " + (saleDTO.getStatusStr().equals("CANCELED") ? "CANCELADO" : "");
                    if (saleDTO.getTypeSale().equals("CREDITO") || saleDTO.getTypeSale().equals("Crédito")) {
                        credito += saleDTO.getAmount();
                    } else if (saleDTO.getTypeSale().equals("Transferencia")) {
                        transferencia += saleDTO.getAmount();
                    } else if (saleDTO.getTypeSale().equals("Efectivo")) {
                        efectivo += saleDTO.getAmount();
                    } else if (saleDTO.getTypeSale().equals("Cheque")) {
                        cheque += saleDTO.getAmount();
                    }
                }
            }
        }

        if(viewModelStore.getStore().getDebts()!=null){
            for(SaleOfflineMode saleOfflineDeb : viewModelStore.getStore().getDebts()) {
                if(!saleOfflineDeb.getStatus()){
                    creditCob+=saleOfflineDeb.getAmount();
                }
            }
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
            if(totalPieces==null) {
                ticket += "\n" + sku + " " + productName + "\n" + String.format("%.02f", weight) + " $" + price + " $" + String.format("%.02f", amount);
            }else{
                ticket += "\n" + sku + " " + productName + "\n"+Integer.parseInt(totalPieces.toString())+ " pz " + String.format("%.02f", weight) + " $" + price + " $" + String.format("%.02f", amount);
            }
        }
        ticket+="\n-----------------------------------------\nDOC NOMBRE  CLIENTE IMPORTE TIPOVENTA\n-----------------------------------------\n";
        ticket+=clientsStr+"\n\n";
        ticket+="VENTAS POR CONCEPTO\nEFECTIVO: $"+String.format("%.02f",efectivo)+"\nCREDITO: $"+String.format("%.02f",credito)+"\nTRANSFERENCIA: $"+String.format("%.02f",transferencia)+"\nCHEQUE: $"+String.format("%.02f",cheque)+"\n";
        ticket+="TOTAL KILOS: "+String.format("%.02f",weightG)+"\nVENTA TOTAL:$ "+String.format("%.02f",efectivo+transferencia+cheque+credito)+"\n";
        ticket+="Recup. Cobranza\n$ "+String.format("%.02f",creditCob)+"\n\n\n\n";
        isLoading = false;
        this.circularProgressIndicator.setVisibility(View.INVISIBLE);
        printTiket(ticket);
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
                        SaleDTO saleDTO = null;
                        saleDTO = generateRequestSale(textInputLayout.getEditText().getText().toString(), 0);
                        cobrarButton.setEnabled(false);
                        circularProgressIndicator.setVisibility(View.VISIBLE);
                        if(!offlineActive) {
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
                            viewModelStore.getStore().setSalesMaked(salesMaked);
                            System.out.println("Tamaño de ventas: "+viewModelStore.getStore().getSalesMaked().size()+"-"+salesMaked.size());
                            doTicketSaleOffline(saleDTO);
                            setModeOffline(viewModelStore.getStore());

                        }
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


    @RequiresApi(api = Build.VERSION_CODES.N)
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
        if(loadModal!=null && loadModal.isShowing()){
            dismissLoadModal();
        }
    }
    void doTicketSaleOffline(SaleDTO saleDTO){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        String ticket = "ROVIANDA SAPI DE CV\nAV.1 #5 Esquina Calle 1\nCongregación Donato Guerra\nParque Industrial Valle de Orizaba\nC.P 94780\nRFC 8607056P8\nTEL 272 72 46077, 72 4 5690\n";
        ticket+="Pago en una Sola Exhibición\nLugar de Expedición: Ruta\nNota No. "+saleDTO.getFolio()+"\nFecha: "+dateParsed+"\n\n";
        ticket+="Vendedor:"+viewModelStore.getStore().getUsername()+"\n\nCliente: "+currentClient.getName()+"\nClave: "+currentClient.getKeyClient()+"\n";
        ticket+="Tipo de venta: "+ saleDTO.getTypeSale() +"\n--------------------------------\nDESCR   CANT    PRECIO  IMPORTE\n--------------------------------\n";
        Float total = Float.parseFloat("0");
        for(ProductSaleDTO product : saleDTO.getProducts()){
            if(product.getTypeUnid().equals("PZ")) {
                ticket += product.getProductName() + " " + product.getProductPresentation() + "\n" + product.getPrice() +" "+ Math.round(product.getQuantity()) + "pz "  +" "+String.format("%.02f",product.getPrice()*product.getQuantity()) +"\n";
            }else{
                ticket += product.getProductName() + " " + product.getProductPresentation() + "\n"+ Math.round(product.getPrice()) +" " + product.getQuantity() + "kg "+ " "+String.format("%.02f",product.getPrice()*product.getQuantity())+ "\n";
            }

            total+=product.getPrice()*product.getQuantity();
        }
        ticket+="--------------------------------\nTOTAL: $ "+String.format("%.02f",total)+"\n\n\n";
        //ticket+="ticket+=`Piezas:  \n\n*** GRACIAS POR SU COMPRA ***\n";
        if(saleDTO.getTypeSale().equals("Crédito") || saleDTO.getTypeSale().equals("CREDITO")){
           ticket+="Esta venta se incluye en la\nventa global del dia, por el\npresente reconozco deber\ny me obligo a pagar en esta\nciudad y cualquier otra que\nse me de pago a la orden de\nROVIANDA S.A.P.I. de C.V. la\ncantidad que se estipula como\ntotal en el presente documento.\n-------------------\n      Firma\n\n";
            ticket+=(saleDTO.getStatus())?"\nSE ADEUDA\n\n\n\n\n":"\nPAGADO\n\n\n\n\n";
        }
        saleSuccess(ticket);
    }


    SaleDTO generateRequestSale(String amountPayed,int days){
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
        circularProgressIndicator.setVisibility(View.GONE);
        isLoading=false;
        presenter.findUser(0);
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



    @Override
    public void setCounterTimer(CounterTime counterTimer) {
        if(counterTimer.getHours()==0 && counterTimer.getMinutes()==0 && counterTimer.getSeconds()==0) {
            System.out.println("No iniciado");
            System.out.println("hour: " + counterTimer.getHours() + " minutes: " + counterTimer.getMinutes() + " seconds: " + counterTimer.getSeconds());
            this.confirmEatTime();
        }else{
            System.out.println("Ya iniciado");
            System.out.println("hour: " + counterTimer.getHours() + " minutes: " + counterTimer.getMinutes() + " seconds: " + counterTimer.getSeconds());
            showModal(counterTimer.getHours(),counterTimer.getMinutes(),counterTimer.getSeconds());
        }
    }

    TextView counter=null;
    long miliseconds=0;
    AlertDialog dialogTimer;
   void showModal(int hours,int minutes,int seconds){
       this.circularProgressIndicator.setVisibility(View.GONE);
       isLoading=false;
       TimeUnit.HOURS.toMillis(hours);
        miliseconds = (TimeUnit.HOURS.toMillis(hours)+TimeUnit.MINUTES.toMillis(minutes)+TimeUnit.SECONDS.toMillis(seconds));
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view= layoutInflater.inflate(R.layout.counter_timer, null);
        counter=view.findViewById(R.id.timer_counter);
        counter.setText("00:00:00");
        stopEatTimeButton=view.findViewById(R.id.terminar_timer);
        stopEatTimeButton.setOnClickListener(this);

       dialogTimer= new MaterialAlertDialogBuilder(getContext()).setTitle("Contador tiempo de comida.")
                .setView(view).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timerActive=false;
                        dialog.dismiss();
                    }
                }).setCancelable(false).create();
       dialogTimer.show();
       runnable();
       this.timerActive=true;
    }



    void resetTime(){
        int minutes = (int) (miliseconds / 1000) / 60 % 60;
        int seconds = (int) (miliseconds / 1000) % 60;

        int hours = (int) (miliseconds/1000) / 60 / 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,minutes, seconds);
        counter.setText(timeLeftFormatted);
    }

   boolean timerActive=false;
    void runnable(){
        new Thread(
               new Runnable() {
                   @Override
                   public void run() {
                       try {
                           while(timerActive==true) {
                               sleep(1000);
                               if(miliseconds==0) {
                                   miliseconds=1000;
                               }else{
                                   miliseconds+=1000;
                               }
                               resetTime();
                           }
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
               }
       ).start();;
    }

    public void showLoadModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        View viewModal =inflater.inflate(R.layout.dialog_load_message,null);
        TextView textLoadMessage = viewModal.findViewById(R.id.message_load);
        textLoadMessage.setText("Se detecto Internet, Sincronizando con servidor...");
        builder.setCancelable(false);
        builder.setView(viewModal);
        loadModal =  builder.create();
        loadModal.show();

    }
    @Override
    public void dismissLoadModal(){
        isLoading = false;
        circularProgressIndicator.setVisibility(View.GONE);
        if(loadModal!=null && loadModal.isShowing()) {
            loadModal.dismiss();
        }

    }
    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
