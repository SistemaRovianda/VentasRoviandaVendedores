package com.example.ventasrovianda.sales.view;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.DatePickerFragment;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.PayDebtsModel;
import com.example.ventasrovianda.Utils.Models.ProductSaleDTO;
import com.example.ventasrovianda.Utils.Models.ProductsOfflineMode;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleOfflineMode;
import com.example.ventasrovianda.Utils.Models.SaleResponseDTO;
import com.example.ventasrovianda.Utils.Models.TotalSoldedDTO;
import com.example.ventasrovianda.Utils.PrinterUtil;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.entities.Debt;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;
import com.example.ventasrovianda.Utils.bd.entities.Product;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.Utils.bd.entities.UserDataInitial;
import com.example.ventasrovianda.sales.adapter.SaleDebtListAdapter;
import com.example.ventasrovianda.sales.adapter.SaleDevolutionAdapter;
import com.example.ventasrovianda.sales.adapter.SaleListAdapter;
import com.example.ventasrovianda.sales.presenter.SalePresenterContract;
import com.example.ventasrovianda.sales.presenter.SalesPresenter;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SalesView  extends Fragment implements  View.OnClickListener,SaleViewContract{
    TextView logoutButton,endDayButton,eatTimeButton,fromDate;
    BottomNavigationView homeButton;
    NavController navController;
    ClientDTO clientDTO = null;
    ListView listSales;
    SalePresenterContract salesPresenter;
    BluetoothDeviceSerializable bluetoothDeviceSerializable=null;
    ImageView printerButton,changeDateButton;
    boolean printerConnected=false;
    PrinterUtil printerUtil=null;
    BluetoothDevice printer=null;
    BluetoothAdapter bluetoothAdapter=null;
    CircularProgressIndicator circularProgressIndicator;
    boolean isLoading=false;

    MaterialButton devolutionButton;

    String userName;
    TextView userNameTextView;
    Button buttonCobranzaHistory;

    SaleResponseDTO[] salesTemp;
    SaleResponseDTO[] sales;

    TextInputLayout inputSearch;
    Button buscarCliente;
    String currentHint="";
    ViewModelStore viewModelStore;
    Boolean modeOffline;
    Gson parser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sales_list,null);
        this.circularProgressIndicator = view.findViewById(R.id.loginLoadingSpinner);
        this.printerButton = view.findViewById(R.id.printerButton);
        this.printerButton.setOnClickListener(this);
        this.endDayButton = view.findViewById(R.id.end_day_button);
        this.eatTimeButton = view.findViewById(R.id.eat_time_button);
        this.logoutButton = view.findViewById(R.id.Logout_button);
        this.logoutButton.setOnClickListener(this);
        this.bluetoothDeviceSerializable = SalesViewArgs.fromBundle(getArguments()).getPrinterDevice();
        this.userName = SalesViewArgs.fromBundle(getArguments()).getUserName();
        this.userNameTextView = view.findViewById(R.id.userName);
        this.userNameTextView.setText("Usuario: "+this.userName);
        this.buttonCobranzaHistory = view.findViewById(R.id.buttonCobranzaHistory);
        this.buttonCobranzaHistory.setOnClickListener(this);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));

        this.fromDate = view.findViewById(R.id.fromDate);

        this.changeDateButton=view.findViewById(R.id.changeDateButton);
        this.changeDateButton.setOnClickListener(this);
        this.devolutionButton=view.findViewById(R.id.devolutionsButton);
        this.devolutionButton.setOnClickListener(this);
        this.endDayButton.setText("PESO");
        this.eatTimeButton.setText("VENDIDO");
        this.endDayButton.setVisibility(View.VISIBLE);
        this.eatTimeButton.setVisibility(View.VISIBLE);
        this.clientDTO = SalesViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.navController = NavHostFragment.findNavController(this);
        homeButton = view.findViewById(R.id.bottom_navigation_home);
        homeButton.setSelectedItemId(R.id.ventas_section);
        listSales = view.findViewById(R.id.listSales);
        this.salesPresenter = new SalesPresenter(getContext(),this);

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.buscarCliente = view.findViewById(R.id.buscarClienteButton);
        this.buscarCliente.setOnClickListener(this);
        this.inputSearch = view.findViewById(R.id.cliente_input_search);
        this.parser = new Gson();



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
                        goToVisits();

                        break;
                    case R.id.cliente_section:
                        goToClients();

                        break;
                    case R.id.pedidos_section:
                        goToOrder();
                        break;
                    case R.id.ventas_section:
                        System.out.println("No cambia de seccion");
                        break;
                }
                return false;
            }
        });
        return view;
    }
    Boolean filtered=false;
    void search(){

        if(sales!=null && sales.length>0){
            List<SaleResponseDTO> salesNew = new ArrayList<>();
            boolean filtrable=false;
            if(filtered==false) {
                filtered = true;
                this.buscarCliente.setText("Cancelar");
                filtrable=true;
                this.currentHint = this.inputSearch.getEditText().getText().toString();
            }else{
                if(!currentHint.equals(this.inputSearch.getEditText().getText().toString())) {
                    filtered = true;
                    filtrable=true;
                    this.buscarCliente.setText("Cancelar");
                    this.currentHint = this.inputSearch.getEditText().getText().toString();
                }else {
                    filtered = false;
                    filtrable=false;
                    this.buscarCliente.setText("Buscar");
                }
            }
            if(filtrable==true && filtered==true){
                System.out.println("Filtrado por:"+this.currentHint);
                for(int i=0;i<sales.length;i++){
                    if(sales[i].getClient().getName().toLowerCase().contains(this.currentHint.toLowerCase()) || String.valueOf(sales[i].getClient().getKeyClient()).toLowerCase().contains(this.currentHint.toLowerCase())){
                        System.out.println("Se encontro");
                        salesNew.add(sales[i]);
                    }
                }

                salesTemp = new SaleResponseDTO[salesNew.size()];
                for(int i=0;i<salesNew.size();i++){
                    salesTemp[i]=salesNew.get(i);
                }
            }else{
                salesTemp = new SaleResponseDTO[sales.length];
                for(int i=0;i<sales.length;i++){
                    salesTemp[i]=sales[i];
                }
            }
            if(cobranza) {
                setSalesDebtsOfDay(salesTemp);
            }else{
                setSalesOfDay(salesTemp);
            }
        }

    }


    String dateSelected="";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        /*if(checkOffline()){
            modeOffline=true;

        }else{
            modeOffline=false;
            this.salesPresenter.checkAccumulated();
            this.salesPresenter.getAllSaleOfDay();
        }*/
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("FECHA_DE_HOY: "+ldt);
        String dateParsed = formmat1.format(ldt);

        dateSelected=dateParsed;
        fromDate.setText("Ventas: "+dateParsed);
        fillSalesOffline(dateSelected);
        checkIfPrinterConfigured();

    }
    void showDatePicker(){
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = String.valueOf(month+1);
                String day = String.valueOf(dayOfMonth);
                if((month+1)<10) monthStr="0"+monthStr;
                if(dayOfMonth<10) day="0"+day;
                dateSelected = year+"-"+monthStr+"-"+day;
                System.out.println("Fecha seleccionada: "+dateSelected);
                fromDate.setText("Ventas: "+dateSelected);
                if(!cobranza) {
                    fillSalesOffline(dateSelected);
                }else{
                    fillSalesDebOffline(dateSelected);
                }
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
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
/*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Boolean checkOffline(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
        File gpxfile = new File(root, "offline-"+dateParsed+".rovi");
        return gpxfile.exists();
    }*/


    @RequiresApi(api = Build.VERSION_CODES.O)
    void fillSalesOffline(String date){
            fillOfSql(date);
        //isLoading=false;
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @RequiresApi(api = Build.VERSION_CODES.O)
    void fillOfSql(String date){
        String dateParsed1 = date+"T00:00:00.000Z";
        String dateParsed2 = date+"T23:59:59.000Z";
        executor.execute(new  Runnable() {
            @Override
            public void run() {

                //Background work here
                List<SaleResponseDTO> saleResponseDTOS=new ArrayList<>();
                List<SubSale> subSales = new ArrayList<>();
                List<Sale> sales = viewModelStore.getAppDatabase().saleDao().getAllSalesByDate(dateParsed1,dateParsed2);

                for(Sale sale : sales) {
                    SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
                    saleResponseDTO.setAmount(Float.parseFloat(sale.amount.toString()));
                    ClientDTO clientDTO = new ClientDTO();
                    clientDTO.setKeyClient(sale.keyClient);
                    clientDTO.setName(sale.clientName);
                    saleResponseDTO.setClient(clientDTO);
                    saleResponseDTO.setFolio(sale.folio);
                    saleResponseDTO.setSaleId(Long.parseLong("0"));
                    saleResponseDTO.setDate(sale.date);
                    saleResponseDTO.setStatus(sale.status);
                    saleResponseDTO.setStatusStr(sale.statusStr);
                    saleResponseDTO.setTypeSale(sale.typeSale);
                    saleResponseDTO.setSaleId(Long.parseLong(String.valueOf(sale.saleId)));
                    saleResponseDTO.setCancelAutorized(sale.cancelAutorized);
                    saleResponseDTOS.add(saleResponseDTO);

                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(sale.folio);
                        if(devolutionRequest!=null){
                            saleResponseDTO.setDevolutionStatus(devolutionRequest.status);
                            saleResponseDTO.setDevolutionid(devolutionRequest.devolutionRequestId);
                        }

                    System.out.println("Estatus: "+sale.statusStr);
                    if(!(sale.statusStr.equals("CANCELED") && sale.cancelAutorized!=null && sale.cancelAutorized.equals("true"))) {

                        List<SubSale> subSales1;
                        subSales1 = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(sale.folio);
                        if (devolutionRequest != null && devolutionRequest.status.equals("ACCEPTED")) {
                            for (SubSale subSale : subSales1) {
                                DevolutionSubSale devolutionSubSale = viewModelStore.getAppDatabase().devolutionSubSaleDao().findDevolutionSubSaleBySubSaleId(subSale.subSaleId);
                                subSale.price = (subSale.price / subSale.quantity) * devolutionSubSale.quantity;
                                subSale.quantity = devolutionSubSale.quantity;
                            }
                        }
                        for (SubSale subSale : subSales1) {
                            subSales.add(subSale);
                        }
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        SaleResponseDTO[] sales = new SaleResponseDTO[saleResponseDTOS.size()];
                        saleResponseDTOS.toArray(sales);
                        setSalesOfDay(sales);
                        checkAccumulatedOffline(subSales);
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void fillSalesDebOffline(String date){

            executor.execute(new  Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    String dateParsed1 = date+"T00:00:00.000Z";
                    String dateParsed2 = date+"T23:59:59.000Z";
                    //Background work here
                    List<SaleResponseDTO> saleResponseDTOS=new ArrayList<>();
                    List<SubSale> subSales = new ArrayList<>();
                    List<Sale> sales = viewModelStore.getAppDatabase().saleDao().getAllDebts();

                    List<Debt> debts = viewModelStore.getAppDatabase().debtDao().getAllSalesWithoutSincronization();
                    List<Debt> debtsPayed = viewModelStore.getAppDatabase().debtDao().getAllSalesDebtsBetweenDates(dateParsed1,dateParsed2);
                    List<String> listDebts = debts.stream().map(x->x.folio).collect(Collectors.toList());
                    for(Debt debt : debtsPayed){
                        if(!listDebts.contains(debt.folio)){
                            debts.add(debt);
                        }
                    }
                    List<String> salesIds = sales.stream().map(x->x.folio).collect(Collectors.toList());
                    for(Debt debt : debts){
                        if(!salesIds.contains(debt.folio)){
                            Sale sale= viewModelStore.getAppDatabase().saleDao().getByFolio(debt.folio);
                            sales.add(sale);
                        }
                    }
                    for(Sale sale : sales) {
                        if(!sale.statusStr.equals("CANCELED")) {
                        salesIds.add(sale.folio);
                        SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
                        saleResponseDTO.setAmount(Float.parseFloat(sale.amount.toString()));
                        ClientDTO clientDTO = new ClientDTO();
                        clientDTO.setKeyClient(sale.keyClient);
                        clientDTO.setName(sale.clientName);
                        saleResponseDTO.setClient(clientDTO);
                        saleResponseDTO.setFolio(sale.folio);
                        saleResponseDTO.setSaleId(Long.parseLong("0"));
                        saleResponseDTO.setDate(sale.date);
                        saleResponseDTO.setStatus(sale.status);
                        saleResponseDTO.setStatusStr(sale.statusStr);
                        saleResponseDTO.setTypeSale(sale.typeSale);
                        saleResponseDTO.setSaleId(Long.parseLong(String.valueOf(sale.saleId)));
                        saleResponseDTO.setCancelAutorized(sale.cancelAutorized);
                        System.out.println("Estatus: "+sale.statusStr);
                            saleResponseDTOS.add(saleResponseDTO);
                            List<SubSale> subSales1 = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(sale.folio);
                            System.out.println("Total subsales : "+subSales1.size());
                            for (SubSale subSale : subSales1) {
                                subSales.add(subSale);
                            }
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                                SaleResponseDTO[] sales = new SaleResponseDTO[saleResponseDTOS.size()];
                                saleResponseDTOS.toArray(sales);
                                setSalesDebtsOfDay(sales);

                        }
                    });
                }});
    }

    void checkAccumulatedOffline(List<SubSale> subSales){
        Float weightG=Float.parseFloat("0");
        Float AmountG=Float.parseFloat("0");

                for (SubSale subSale : subSales) {
                    System.out.println("Producto: "+subSale.productName);
                    System.out.println("quantity: "+subSale.quantity);
                    System.out.println("Price: "+subSale.price);
                    if (subSale.uniMed.toLowerCase().equals("pz")) {
                        weightG += subSale.weightStandar * subSale.quantity;
                    } else {
                        weightG += subSale.quantity;
                    }
                    AmountG += subSale.price;
                }
        TotalSoldedDTO totalSoldedDTO = new TotalSoldedDTO();
        totalSoldedDTO.setTotalSolded(String.format("%.02f",AmountG));
        totalSoldedDTO.setTotalWeight(weightG);
        setAcumulated(totalSoldedDTO);

    }

    void goToHome(){
        navController.navigate(SalesViewDirections.actionSalesViewToHomeView(this.userName).setUserName(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void goToVisits(){
        navController.navigate(SalesViewDirections.actionSalesViewToVisitsView(this.userName).setUserName(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void goToOrder(){
        navController.navigate(SalesViewDirections.actionSalesViewToPedidoView(this.userName).setUserName(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void goToClients(){
        navController.navigate(SalesViewDirections.actionSalesViewToClientView(this.userName).setUserName(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable));
    }

    void logout(){
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salesPresenter.logout();
                    }
                }).setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    @Override
    public void setLoading(boolean loading) {
        if(loading==true){
            circularProgressIndicator.setVisibility(View.VISIBLE);
        }else{
            circularProgressIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void goToLogin(){
        if(this.printerUtil!=null){
            this.printerUtil.desconect();
        }
        navController.navigate(SalesViewDirections.actionSalesViewToLoginView());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Logout_button:
                logout();
                break;
            case R.id.printerButton:
                    if(isLoading==false && this.printerConnected==true) {
                        // connect to printer
                        printerNoConnected();
                        this.printerConnected=false;
                        if(this.printerUtil!=null) {
                            this.printerUtil.desconect();
                            this.printerUtil = null;
                        }
                    }else if(isLoading==false && this.printerConnected==false){
                        activatePrinter();
                    }
                break;
            case R.id.devolutionsButton:
                    if(isLoading==false) {
                        if(devolutions==false) {
                                listSales.setAdapter(null);
                                findSalesToDevolution();
                                devolutionButton.setText("Historial");
                                devolutions=true;
                        }else{
                            listSales.setAdapter(null);
                            devolutionButton.setText("Devoluciones");

                            this.fillSalesOffline(dateSelected);
                            devolutions=false;
                        }
                    }
                break;
            case R.id.buttonCobranzaHistory:

                if(cobranza) {
                    System.out.println("Ventas habilitada");
                    fillSalesOffline(dateSelected);
                    cobranza=false;
                    buttonCobranzaHistory.setText("COBRANZA");
                }else{
                    System.out.println("Cobranza deshabilitada");
                    fillSalesDebOffline(dateSelected);
                    cobranza=true;
                    buttonCobranzaHistory.setText("HISTORIAL");
                }

                break;
            case R.id.buscarClienteButton:
                search();
                break;
            case R.id.changeDateButton:
                showDatePicker();
                break;
        }
    }
    Map<String,String> mapDevolutionsStatus;
    void findSalesToDevolution(){
        executor.execute(new  Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                String dateParsed1 = dateSelected + "T00:00:00.000Z";
                String dateParsed2 = dateSelected + "T23:59:59.000Z";
                List<Sale> sales = viewModelStore.getAppDatabase().devolutionRequestDao().getAllBetweenDate(dateParsed1,dateParsed2);
                mapDevolutionsStatus= new HashMap<>();
                for(Sale sale : sales) {
                    DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(sale.folio);
                    mapDevolutionsStatus.put(sale.folio,devolutionRequest.status);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(sales.size()>0){
                            fillSalesToDevolution(sales);
                        }
                    }
                });
            }
        });

    }
    void fillSalesToDevolution(List<Sale> sales){
        SaleDevolutionAdapter adapter = new SaleDevolutionAdapter(sales,getContext(),this,mapDevolutionsStatus);
        listSales.setAdapter(null);
        listSales.setAdapter(adapter);
    }

    void goToDevolutionSale(String folio){
        navController.navigate(SalesViewDirections.actionSalesViewToDevolutionsView(folio,"CREATE"));
    }
    @Override
    public void goToDevolutionSaleToView(String folio){
        navController.navigate(SalesViewDirections.actionSalesViewToDevolutionsView(folio,"EDIT"));
    }

    Boolean cobranza=false;
    Boolean devolutions=false;
    void printerNoConnected(){
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#BDB5B5")));
    }

    void printerConnected(){
        ImageViewCompat.setImageTintList(printerButton,ColorStateList.valueOf(Color.parseColor("#39ED20")));
    }

    @Override
    public void setSalesOfDay(SaleResponseDTO[] salestemp) {
        //isLoading=false;
        if(sales==null){
            sales=salestemp;
        }
        listSales.removeAllViewsInLayout();
        System.out.println("Sales length: "+sales.length);
        SaleListAdapter saleListAdapter = new SaleListAdapter(getContext(),salestemp,salesPresenter,modeOffline,this);
        listSales.setAdapter(saleListAdapter);

    }

    @Override
    public void setSalesDebtsOfDay(SaleResponseDTO[] salestemp) {
        //isLoading=false;
        if(sales==null){
            sales = salestemp;
        }
        listSales.removeAllViewsInLayout();
        SaleDebtListAdapter saleListAdapter = new SaleDebtListAdapter(getContext(),salestemp,salesPresenter);
        listSales.setAdapter(saleListAdapter);
    }

    @Override
    public void checkPrinterConnection(Long ticketId){
        if(this.printerUtil==null){
            this.printerUtil = new PrinterUtil(getContext());
        }
            salesPresenter.getTicket(ticketId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void checkPrinterConnectionOffline(String folio){
        if(this.printerUtil==null){
            this.printerUtil = new PrinterUtil(getContext());
        }
        this.printTicketSale(folio);
    }

    String statusDevolutionToEndDay="";
    String devolutionSubSalesToReprint="";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void printTicketSale(String folio){
        statusDevolutionToEndDay="";
        devolutionSubSalesToReprint="";
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Sale sale = viewModelStore.getAppDatabase().saleDao().getByFolio(folio);
                DevolutionRequest devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(sale.folio);
                List<SubSale> subSales = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(folio);
                Map<String,Product> productsMap = new HashMap<>();
                for(SubSale subSale : subSales){
                    Product product = viewModelStore.getAppDatabase().productDao().getProductByKey(subSale.productKey);
                    productsMap.put(subSale.productKey,product);
                }
                if(devolutionRequest!=null) {
                    sale.devolutionId = devolutionRequest.devolutionRequestId;
                    String devolutionStr="";
                    if(devolutionRequest.status.equals("PENDING")){
                        devolutionStr="PENDIENTE";
                    }else if(devolutionRequest.status.equals("DECLINED")){
                        devolutionStr="RECHAZADA";
                    }else if(devolutionRequest.status.equals("ACCEPTED")){
                        devolutionStr="ACEPTADA";
                    }
                    statusDevolutionToEndDay=devolutionStr;
                }
                if(devolutionRequest!=null ) {
                    if( devolutionRequest.status.equals("ACCEPTED")) {
                        devolutionSubSalesToReprint += "PRODUCTO DEVUELTO: \n";
                    }else if(devolutionRequest.status.equals("PENDING")){
                        devolutionSubSalesToReprint += "PRODUCTO A DEVOLVER: \n";
                    }else if(devolutionRequest.status.equals("DECLINED")){
                        devolutionSubSalesToReprint += "PRODUCTO NO DEVUELTO: \n";
                    }
                    for(SubSale subSale : subSales){
                        List<DevolutionSubSale> devolutionSubSales = viewModelStore.getAppDatabase().devolutionSubSaleDao().findAllDevolutionSubSaleBySubSaleId(subSale.subSaleId);
                        Float toDevolution=Float.parseFloat("0");
                        for(DevolutionSubSale devolutionSubSale1 : devolutionSubSales){
                            System.out.println("SubDevolutionId: "+devolutionSubSale1.quantity);
                            toDevolution+=devolutionSubSale1.quantity;
                        }
                        devolutionSubSalesToReprint+=subSale.productName+" "+subSale.productPresentationType+"\n"+(subSale.quantity-toDevolution)+" "+subSale.uniMed+"\n";
                        if(devolutionRequest.status.equals("ACCEPTED")) {
                            subSale.price = (subSale.price / subSale.quantity) * toDevolution;
                            subSale.quantity = toDevolution;
                        }
                    }
                    devolutionSubSalesToReprint+="MOTIVO: \n"+devolutionRequest.description;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(sale!=null){
                            doTicketSaleOffline(sale,subSales,productsMap);
                        }
                    }
                });

            }
        });
    }

    void doTicketSaleOffline(Sale sale,List<SubSale> subSales,Map<String,Product> productMap){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        String dateParsed = dateFormat.format(calendar.getTime());
        String ticket = "ROVIANDA SAPI DE CV\nAV.1 #5 Esquina Calle 1\nCongregación Donato Guerra\nParque Industrial Valle de Orizaba\nC.P 94780\nRFC 8607056P8\nTEL 272 72 46077, 72 4 5690\n";
        ticket+="Pago en una Sola Exhibición\nLugar de Expedición: Ruta\nNota No. "+sale.folio+"\nFecha: "+dateParsed+"\n\n";
        ticket+="Vendedor:"+viewModelStore.getStore().getUsername()+"\n";
        if(sale.statusStr.equals("CANCELED")){
            if(sale.cancelAutorized!=null && sale.cancelAutorized.equals("true")){
                ticket+="\nNOTA CANCELADA";
            }else{
                ticket+="\nCANCELACION SIN AUTORIZAR";
            }
        }
        if(sale.devolutionId!=null){
            ticket+="\nDEVOLUCIÓN "+statusDevolutionToEndDay;
        }
        ticket+="\nCliente: "+sale.clientName+"\nClave: "+sale.keyClient+"\n";
        ticket+="Tipo de venta: "+ sale.typeSale +"\n--------------------------------\nDESCR   PRECIO   CANT  IMPU.   IMPORTE \n--------------------------------\n";
        Float total = Float.parseFloat("0");
        Float totalImp = Float.parseFloat("0");
        for(SubSale product : subSales){
            Product product1 = productMap.get(product.productKey);
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
            totalImp+=((singleIva*product.quantity)+(singleIeps*product.quantity));
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
        ticket+=devolutionSubSalesToReprint;
        reprintTicket(ticket);

    }

    Float extractIva(Float amount){
        return (amount/116)*16;
    }

    Float extractIeps(Float amount,Float percent){
        return (amount/(100+percent))*percent;
    }

    /*void setSaleDetails(Long saleId,String folio){
        SaleDTO saleDTO=new SaleDTO();
        executor.execute(new  Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                Sale sale=viewModelStore.getAppDatabase().saleDao().getBySaleId(Integer.parseInt(saleId.toString()));
                if(sale==null){
                    sale = viewModelStore.getAppDatabase().saleDao().getByFolio(folio);
                }
                if(sale!=null) {
                    saleDTO.setAmount(sale.amount);
                    saleDTO.setClientName(sale.clientName);
                    saleDTO.setCredit(sale.credit);
                    saleDTO.setDate(sale.date);
                    saleDTO.setFolio(sale.folio);
                    saleDTO.setKeyClient(sale.keyClient);
                    saleDTO.setPayed(sale.payed);
                    saleDTO.setSellerId(sale.sellerId);
                    saleDTO.setStatus(sale.status);
                    saleDTO.setStatusStr(sale.statusStr);
                    saleDTO.setTypeSale(sale.typeSale);
                    saleDTO.setClientId(sale.clientId);
                    List<SubSale> subSales = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(folio);
                    List<ProductSaleDTO> productSaleDTOS = new ArrayList<>();
                    for(SubSale subSale : subSales){
                        ProductSaleDTO productSaleDTO = new ProductSaleDTO();
                        productSaleDTO.setPresentationId(subSale.presentationId);
                        productSaleDTO.setPrice(subSale.price);
                        productSaleDTO.setProductId(subSale.productId);
                        productSaleDTO.setProductKey(subSale.productKey);
                        productSaleDTO.setProductName(subSale.productName);
                        productSaleDTO.setProductPresentation(subSale.productPresentationType);
                        productSaleDTO.setQuantity(subSale.quantity);
                        productSaleDTO.setTypeUnid(subSale.uniMed);
                        productSaleDTO.setWeightOriginal(subSale.weightStandar);
                        productSaleDTOS.add(productSaleDTO);
                    }
                    saleDTO.setProducts(productSaleDTOS);
                }



                handler.post(new Runnable() {
                    @Override
                    public void run() {
                            if(saleDTO.getFolio()!=null) {
                                doTicketSaleOffline(saleDTO);
                            }
                    }
                });
            }
        });
    }*/

/*
    @RequiresApi(api = Build.VERSION_CODES.N)
    void doTicketSaleOffline(SaleDTO saleDTO){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        String ticket = "ROVIANDA SAPI DE CV\nAV.1 #5 Esquina Calle 1\nCongregación Donato Guerra\nParque Industrial Valle de Orizaba\nC.P 94780\nRFC 8607056P8\nTEL 272 72 46077, 72 4 5690\n";
        ticket+="Pago en una Sola Exhibición\nLugar de Expedición: Ruta\nNota No. "+saleDTO.getFolio()+"\nFecha: "+dateParsed+"\n\n";
        ticket+="Vendedor:"+viewModelStore.getStore().getUsername()+"\n\nCliente: "+ saleDTO.getClientName()+"\n";
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
        System.out.println("Tipo de venta"+saleDTO.getTypeSale());
        if(saleDTO.getTypeSale().equals("Crédito") || saleDTO.getTypeSale().equals("CREDITO")){
            ticket+="Esta venta se incluye en la\nventa global del dia, por el\npresente reconozco deber\ny me obligo a pagar en esta\nciudad y cualquier otra que\nse me de pago a la orden de\nROVIANDA S.A.P.I. de C.V. la\ncantidad que se estipula como\ntotal en el presente documento.\n-------------------\n      Firma\n\n";
            ticket+=(saleDTO.getStatus())?"\nSE ADEUDA\n\n\n\n\n":"\nPAGADO\n\n\n\n\n";

        }
        if(cobranza) {
            fillSalesDebOffline();
        }
        reprintTicket(ticket);

    }*/


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void cancelSale(String folio) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Sale sale = viewModelStore.getAppDatabase().saleDao().getByFolio(folio);
                if(sale!=null && !sale.statusStr.equals("CANCELED")){
                    sale.statusStr="CANCELED";
                    sale.modified=true;
                    sale.status=false;
                    viewModelStore.getAppDatabase().saleDao().updateSale(sale);
                    viewModelStore.getAppDatabase().debtDao().deleteDebtForDeleteSale(sale.folio);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fillSalesOffline(dateSelected);
                    }
                });

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setModeOffline(ModeOfflineModel modeOffline) {

        circularProgressIndicator.setVisibility(View.GONE);

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

    }

    int intentsToClose=0;
    @Override
    public void reprintTicket(String ticket) {
        this.circularProgressIndicator.setVisibility(View.GONE);
        isLoading=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("Imprimir ticket")
                .setNeutralButton("Terminar", null)
                .setPositiveButton("Reimprimir", null)
                .setCancelable(false).create();
        dialog.show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setTextColor(Color.parseColor("#000000"));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Printerconnected: " + printerConnected);

                printTiket(ticket);
            }
        });

        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setTextColor(Color.parseColor("#000000"));
        neutral.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                System.out.println("intents: " + intentsToClose);
                if (intentsToClose >= 2) {
                    intentsToClose = 0;
                    if(cobranza) {

                            fillSalesDebOffline(dateSelected);

                    }
                    dialog.dismiss();

                } else {
                    if(intentsToClose==0) {
                        Toast.makeText(getContext(), "Vuelve a presionar el botón para confirmar.", Toast.LENGTH_LONG).show();
                    }
                    intentsToClose += 1;
                }
            }
        });
    }
    @Override
    public void printTiket(String ticket){
            if(printerUtil==null){
                printerUtil = new PrinterUtil(getContext());
            }
            Toast.makeText(getContext(), "Imprimiendo", Toast.LENGTH_LONG).show();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        printerUtil.connectWithPrinter(printer);
                        sleep(3000);
                        printerUtil.IntentPrint(ticket);
                    } catch (InterruptedException e) {
                        System.out.println("Exception: " + e.getMessage());
                    }
                }
            }.start();

    }
    @Override
    public void genericMessage(String title,String msg){

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void activatePrinter(){
        if(!this.bluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth,0);
        }else{
            if(printerUtil==null) {
                printerUtil = new PrinterUtil(getContext());
            }
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
        if(requestCode==0){
            if(resultCode==-1){//activado
                System.out.println("Request permission");
                System.out.println("Permission: "+resultCode);
                if(this.printerUtil==null) {
                    this.printerUtil = new PrinterUtil(getContext());
                }
                Set<BluetoothDevice> deviceList = this.printerUtil.findDevices();
                if(deviceList.size()>0){
                    findPrinter(deviceList);
                }else{
                    genericMessage("Error al buscar dispositivos","No tienes dispositivos emparejados");
                }
            }else if(resultCode==0){//desactivado
                System.out.println("Request permission");
                System.out.println("Permission: "+resultCode);
            }

        }
    }

    int selectIndexPrinter;
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
                        genericMessage("Error en enlace","No se pudo enlazar a una impresora.");
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

                            printer = bluetoothDevicesMapped.get(which);

                            printerConnected = printerUtil.connectWithPrinter(printer);
                            circularProgressIndicator.setVisibility(View.GONE);
                            isLoading=false;

                            if(printerConnected==true) {

                                genericMessage("Enlace exitoso","Se enlazo a la impresora: "+printer.getName());
                                printerConnected();
                            }else{
                                genericMessage("Error en enlace","No se pudo enlazar a una impresora.");
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



    int selectionPay=0;
    Boolean paying=false;
    Float amount;
    String[] contadoOptions = {"EFECTIVO","TRANSFERENCIA","CHEQUE"};
    void showOptionsPayed(SaleResponseDTO sale){
        if(!paying) {
            String[] selectMode = contadoOptions;


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

                            paying = false;
                        }
                    }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            doSaleModalConfirmation(sale);
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            paying = false;
                        }
                    }).setCancelable(true).create();
            dialog.show();
        }
    }

    public void doSaleModalConfirmation(SaleResponseDTO sale){
        amount = sale.getAmount();

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
                    isLoading=true;
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    //PayDebtsModel payDebtsModel = new PayDebtsModel();
                    //payDebtsModel.setTypePayed(contadoOptions[selectionPay]);
                    setSalePayed(sale.getFolio(),contadoOptions[selectionPay]);
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
    void setSalePayed(String folio, String typePay){

        executor.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                Sale  sale = viewModelStore.getAppDatabase().saleDao().getByFolio(folio);
                List<SubSale> subSales = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(folio);
                Map<String,Product> productsMap = new HashMap<>();
                for(SubSale subSale : subSales){
                    Product product = viewModelStore.getAppDatabase().productDao().getProductByKey(subSale.productKey);
                    productsMap.put(subSale.productKey,product);
                }
                if(sale!=null && sale.status){
                    sale.status=false;
                    sale.modified=true;
                    viewModelStore.getAppDatabase().saleDao().updateSale(sale);
                    Debt checkAlreadyExist = viewModelStore.getAppDatabase().debtDao().getDebtByFolio(folio);
                    if(checkAlreadyExist==null) {
                        Debt debt = new Debt();
                        ZonedDateTime zdt = ZonedDateTime.now();
                        zdt=zdt.minusHours(5);
                        String nowAsISO = zdt.format(DateTimeFormatter.ISO_INSTANT);
                        debt.createAt = nowAsISO;
                        debt.folio = sale.folio;
                        debt.payedType = typePay;
                        debt.solped = true;
                        debt.sincronized=false;
                        debt.deleted=false;
                        viewModelStore.getAppDatabase().debtDao().insertDebts(debt);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                            doTicketSaleOffline(sale,subSales,productsMap);
                    }
                });

            }
        });

        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);

    }

    @Override
    public void checkPaydeb(SaleResponseDTO sale) {

            showOptionsPayed(sale);

    }

    @Override
    public void setAcumulated(TotalSoldedDTO totalSoldedDTO) {
        this.endDayButton.setText("TOTAL VENDIDO\n$ "+totalSoldedDTO.getTotalSolded());
        this.eatTimeButton.setText("PESO TOTAL\n"+String.format("%.02f",totalSoldedDTO.getTotalWeight())+" KG");
    }

    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    AlertDialog optionsDialog;

    @Override
    public void showOptionsSale(SaleResponseDTO sale){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view =layoutInflater.inflate(R.layout.option_sales_modal,null);
        builder.setView(view);
        optionsDialog=builder.create();
        optionsDialog.show();
        Button reprintButton = view.findViewById(R.id.optionReprintButton);
        Button cancelButton = view.findViewById(R.id.optionCancelButton);
        Button devolutionButton = view.findViewById(R.id.optionDevolutionButton);
        if(sale.getDevolutionid()==null) {
            if (sale.getStatusStr().equals("CANCELED") && sale.getCancelAutorized()!=null && sale.getCancelAutorized().equals("true")) {
                cancelButton.setVisibility(View.GONE);
                devolutionButton.setVisibility(View.GONE);
            }else if(sale.getStatusStr().equals("CANCELED") && sale.getCancelAutorized()==null){
                cancelButton.setVisibility(View.GONE);
                devolutionButton.setVisibility(View.GONE);
            }
        }else{
            if(sale.getDevolutionStatus().equals("PENDING")){
               devolutionButton.setVisibility(View.GONE);
               cancelButton.setVisibility(View.GONE);
            }
        }

        reprintButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                optionsDialog.dismiss();
                checkPrinterConnectionOffline(sale.getFolio());
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.dismiss();
                cancelSale(sale.getSaleId(), sale.getFolio());
            }
        });
        devolutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsDialog.dismiss();
                goToDevolutionSale(sale.getFolio());
            }
        });
    }

    public void cancelSale(Long saleId,String folio){

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cancelación de venta")
                .setMessage("¿Está seguro que desea cancelar la venta?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        salesPresenter.cancelSaleOffline(folio);

                    }
                }).setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        isLoading=false;
                    }
                }).create();
        dialog.show();
    }
}
