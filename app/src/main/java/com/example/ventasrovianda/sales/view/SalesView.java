package com.example.ventasrovianda.sales.view;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientVisitDTO;
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
import com.example.ventasrovianda.sales.adapter.SaleDebtListAdapter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SalesView  extends Fragment implements  View.OnClickListener,SaleViewContract{
    TextView logoutButton,endDayButton,eatTimeButton,fromDate;
    BottomNavigationView homeButton;
    NavController navController;
    ClientDTO clientDTO = null;
    ListView listSales;
    SalePresenterContract salesPresenter;
    BluetoothDeviceSerializable bluetoothDeviceSerializable=null;
    ImageView printerButton;
    boolean printerConnected=false;
    PrinterUtil printerUtil=null;
    BluetoothDevice printer=null;
    BluetoothAdapter bluetoothAdapter=null;
    CircularProgressIndicator circularProgressIndicator;
    boolean isLoading=false;

    MaterialButton printResguardButton;

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
        //this.printerButton.setEnabled(false);
        //this.printerButton.setVisibility(View.GONE);
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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        this.fromDate = view.findViewById(R.id.fromDate);
        fromDate.setText("Ventas: "+dateParsed);
        if(bluetoothDeviceSerializable!=null){
            this.printerConnected = bluetoothDeviceSerializable.isPrinterConnected();
            if(this.printerConnected){
                printerConnected();
                if(printerUtil==null){
                    printerUtil = new PrinterUtil(getContext());
                }
                printer = bluetoothDeviceSerializable.getBluetoothDevice();
            }
        }
        this.printResguardButton=view.findViewById(R.id.printResguardButton);
        this.printResguardButton.setOnClickListener(this);
        this.endDayButton.setText("PESO");
        this.eatTimeButton.setText("VENDIDO");
        /*this.endDayButton.setVisibility(View.GONE);
        this.eatTimeButton.setVisibility(View.GONE);*/
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        if(checkOffline()){
            modeOffline=true;
            fillSalesOffline();
        }else{
            modeOffline=false;
            this.salesPresenter.checkAccumulated();
            this.salesPresenter.getAllSaleOfDay();
        }

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    void fillSalesOffline(){

            List<SaleResponseDTO> saleResponseDTOS = viewModelStore.getStore().getSales().stream().map(sale->{
                SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
                saleResponseDTO.setAmount(Float.parseFloat(sale.getAmount().toString()));
                ClientDTO clientDTO = new ClientDTO();
                clientDTO.setKeyClient(Integer.parseInt(sale.getKeyClient().toString()));
                clientDTO.setName(sale.getClientName());
                saleResponseDTO.setClient(clientDTO);
                saleResponseDTO.setFolio(sale.getFolio());
                saleResponseDTO.setSaleId(Long.parseLong("0"));
                saleResponseDTO.setDate(sale.getDate());

                if(viewModelStore.getStore().getDebts()!=null && viewModelStore.getStore().getDebts().size()>0) {
                    for (SaleOfflineMode deb : viewModelStore.getStore().getDebts()) {
                        if(deb.getFolio().equals(sale.getFolio()) && !deb.getStatus()){
                            saleResponseDTO.setStatus(false);
                        }else{
                            saleResponseDTO.setStatus(true);
                        }
                    }
                }else{
                    saleResponseDTO.setStatus(false);
                }

                saleResponseDTO.setStatusStr(sale.getStatusStr());
                saleResponseDTO.setTypeSale(sale.getTypeSale());
                return saleResponseDTO;
            }).collect(Collectors.toList());
            if(viewModelStore.getStore().getSalesMaked()!=null && viewModelStore.getStore().getSalesMaked().size()>0){
                System.out.println("SalesMaked size: "+viewModelStore.getStore().getSalesMaked().size());
                List<SaleResponseDTO> saleMakedResponseDTOS = viewModelStore.getStore().getSalesMaked().stream().map(sale->{
                    SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
                    saleResponseDTO.setAmount(Float.parseFloat(sale.getAmount().toString()));
                    ClientDTO clientDTO = new ClientDTO();
                    clientDTO.setKeyClient(sale.getKeyClient());
                    clientDTO.setName(sale.getClientName());
                    saleResponseDTO.setClient(clientDTO);
                    saleResponseDTO.setFolio(sale.getFolio());
                    saleResponseDTO.setSaleId(Long.parseLong("0"));
                    saleResponseDTO.setDate("");
                    System.out.println("Folios mapped: "+sale.getFolio());
                    if(viewModelStore.getStore().getDebts()!=null && viewModelStore.getStore().getDebts().size()>0) {
                        for (SaleOfflineMode deb : viewModelStore.getStore().getDebts()) {
                            if(deb.getFolio().equals(sale.getFolio()) && !deb.getStatus()){
                                saleResponseDTO.setStatus(false);
                            }else{
                                saleResponseDTO.setStatus(true);
                            }
                        }
                    }else{
                        saleResponseDTO.setStatus(false);
                    }

                    saleResponseDTO.setStatusStr(sale.getStatusStr());
                    saleResponseDTO.setTypeSale(sale.getTypeSale());
                    return saleResponseDTO;
                }).collect(Collectors.toList());
                if(saleMakedResponseDTOS.size()>0) {
                    for (SaleResponseDTO item : saleMakedResponseDTOS) {
                        saleResponseDTOS.add(item);
                    }
                }
            }
            SaleResponseDTO[] sales = new SaleResponseDTO[saleResponseDTOS.size()];
            saleResponseDTOS.toArray(sales);
            this.setSalesOfDay(sales);
            this.checkAccumulatedOffline();

        isLoading=false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void fillSalesDebOffline(){
        if(viewModelStore.getStore()!=null && viewModelStore.getStore().getDebts()!=null){
            modeOffline=true;

            List<SaleResponseDTO> saleResponseDTOS = viewModelStore.getStore().getDebts().stream().map(sale->{
                SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
                saleResponseDTO.setAmount(Float.parseFloat(sale.getAmount().toString()));
                ClientDTO clientDTO = new ClientDTO();
                clientDTO.setKeyClient(Integer.parseInt(sale.getKeyClient().toString()));
                clientDTO.setName(sale.getClientName());
                saleResponseDTO.setClient(clientDTO);
                saleResponseDTO.setFolio(sale.getFolio());
                saleResponseDTO.setSaleId(Long.parseLong("0"));
                saleResponseDTO.setDate(sale.getDate());
                if(viewModelStore.getStore().getDebts()!=null && viewModelStore.getStore().getDebts().size()>0) {
                    for (SaleOfflineMode deb : viewModelStore.getStore().getDebts()) {
                        if(deb.getFolio().equals(sale.getFolio()) && !deb.getStatus()){
                            saleResponseDTO.setStatus(false);
                        }else{
                            saleResponseDTO.setStatus(true);
                        }
                    }
                }else{
                    saleResponseDTO.setStatus(false);
                }
                saleResponseDTO.setStatusStr(sale.getStatusStr());
                saleResponseDTO.setTypeSale(sale.getTypeSale());
                return saleResponseDTO;
            }).collect(Collectors.toList());

            SaleResponseDTO[] sales = new SaleResponseDTO[saleResponseDTOS.size()];
            saleResponseDTOS.toArray(sales);
            this.setSalesDebtsOfDay(sales);
            this.checkAccumulatedOffline();
        }else{
            modeOffline=false;
            this.salesPresenter.checkAccumulated();
            this.salesPresenter.getAllSaleDebtsOfDay();
        }
        isLoading=false;
    }

    void checkAccumulatedOffline(){
        Float weightG=Float.parseFloat("0");
        Float AmountG=Float.parseFloat("0");
       /* Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        String ticket = "\nReporte de cierre\nVendedor: "+viewModelStore.getStore().getUsername()+"\nFecha: "+dateParsed+"\n------------------------\n";
        ticket+="ART.   DESC    CANT    PRECIO  IMPORTE\n";
        Map<String,String> skus = new HashMap<>();
        Map<String,Double> pricesBySku = new HashMap<>();
        Map<String,Double> weightTotal = new HashMap<>();
        Map<String,Double> amountTotal = new HashMap<>();
        Double efectivo=Double.parseDouble("0");
        Double credito=Double.parseDouble("0");
        Double transferencia=Double.parseDouble("0");
        Double cheque=Double.parseDouble("0");
        String clientsStr="";*/


        for(SaleOfflineMode sale : viewModelStore.getStore().getSales()){
            if(!sale.getStatusStr().equals("CANCELED")) {
                for (ProductsOfflineMode productOffline : sale.getProducts()) {
                /*String productName= skus.get(productOffline.getProductKey());
                if(productName==null){
                    skus.put(productOffline.getProductKey(),productOffline.getProductName()+" "+productOffline.getProductPresentationType());
                }*/
               /* Double weight = weightTotal.get(productOffline.getProductKey());
                if(weight==null){
                    Double weightByProduct = (productOffline.getType().equals("PZ")?productOffline.getQuantity()*productOffline.getWeightStandar():productOffline.getQuantity());
                    weightTotal.put(productOffline.getProductKey(),weightByProduct);
                }else{
                    weight+= (productOffline.getType().equals("PZ")?productOffline.getQuantity()*productOffline.getWeightStandar():productOffline.getQuantity());
                    weightTotal.put(productOffline.getProductKey(),weight);
                }*/
                    if (productOffline.getType().equals("PZ")) {
                        weightG += productOffline.getWeightStandar() * productOffline.getQuantity();
                    } else {
                        weightG += productOffline.getQuantity();
                    }

                /*Double amountByProduct = pricesBySku.get(productOffline.getProductKey());
                if(amountByProduct==null){
                    Double amount = productOffline.getPrice();
                    amountTotal.put(productOffline.getProductKey(),amount);
                }*/

                /*Double amountSubSale = amountTotal.get(productOffline.getProductKey());
                if(amountSubSale==null){
                    Double amount = productOffline.getPrice()*productOffline.getQuantity();
                    amountTotal.put(productOffline.getProductKey(),amount);
                }else{
                    amountSubSale+=productOffline.getPrice();
                    amountTotal.put(productOffline.getProductKey(),amountSubSale);
                }*/
                }
                AmountG += sale.getAmount();
           /* clientsStr+="\n"+sale.getFolio()+" "+sale.getClientName()+" $"+sale.getAmount()+" "+sale.getTypeSale()+" " +(sale.getStatusStr().equals("CANCELED")?"CANCELADO":"");
            if(sale.getTypeSale().equals("CREDITO")){
                credito+=sale.getAmount();
            }else if(sale.getTypeSale().equals("Transferencia")){
                transferencia+=sale.getAmount();
            }else if(sale.getTypeSale().equals("Efectivo")){
                efectivo+=sale.getAmount();
            }else if(sale.getTypeSale().equals("Cheque")){
                cheque+=sale.getAmount();
            }*/
            }
        }
        if(viewModelStore.getStore().getSalesMaked()!=null) {
            for (SaleDTO saleDTO : viewModelStore.getStore().getSalesMaked()) {
                if(!saleDTO.getStatusStr().equals("CANCELED")) {
                    for (ProductSaleDTO productSaleDTO : saleDTO.getProducts()) {
                    /*String productName= skus.get(productSaleDTO.getProductKey());
                    if(productName==null){
                        skus.put(productSaleDTO.getProductKey(),productSaleDTO.getProductName()+" "+productSaleDTO.getProductPresentation());
                    }
                    Float weight = weightTotal.get(productSaleDTO.getProductKey());
                    if(weight==null){
                        Float weightByProduct = (productSaleDTO.getTypeUnid().equals("PZ")?productSaleDTO.getQuantity()*productSaleDTO.getWeightOriginal():productSaleDTO.getQuantity());
                        weightTotal.put(productSaleDTO.getProductKey(),weightByProduct);
                        weightG+=weightByProduct;
                    }else{
                        weight+= (productSaleDTO.getTypeUnid().equals("PZ")?productSaleDTO.getQuantity()*productSaleDTO.getWeightOriginal():productSaleDTO.getQuantity());
                        weightTotal.put(productSaleDTO.getProductKey(),weight);
                        weightG+=(productSaleDTO.getTypeUnid().equals("PZ")?productSaleDTO.getQuantity()*productSaleDTO.getWeightOriginal():productSaleDTO.getQuantity());;
                    }

                    Float amountByProduct = pricesBySku.get(productSaleDTO.getProductKey());
                    if(amountByProduct==null){
                        Float amount = productSaleDTO.getPrice();
                        pricesBySku.put(productSaleDTO.getProductKey(),amount);
                    }

                    Float amountSubSale = amountTotal.get(productSaleDTO.getProductKey());
                    if(amountSubSale==null){
                        Float amount = productSaleDTO.getPrice()*productSaleDTO.getQuantity();
                        amountTotal.put(productSaleDTO.getProductKey(),amount);
                    }else{
                        amountSubSale+=productSaleDTO.getPrice();
                        amountTotal.put(productSaleDTO.getProductKey(),amountSubSale);
                    }*/
                        if (productSaleDTO.getTypeUnid().equals("PZ")) {
                            weightG += productSaleDTO.getWeightOriginal() * productSaleDTO.getQuantity();
                        } else {
                            weightG += productSaleDTO.getQuantity();
                        }
                    }

               /* clientsStr+="\n"+saleDTO.getFolio()+" "+saleDTO.getClientName()+" $"+saleDTO.getAmount()+" "+saleDTO.getTypeSale()+" " +(saleDTO.getStatusStr().equals("CANCELED")?"CANCELADO":"");
                if(saleDTO.getTypeSale().equals("CREDITO")){
                    credito+=saleDTO.getAmount();
                }else if(saleDTO.getTypeSale().equals("Transferencia")){
                    transferencia+=saleDTO.getAmount();
                }else if(saleDTO.getTypeSale().equals("Efectivo")){
                    efectivo+=saleDTO.getAmount();
                }else if(saleDTO.getTypeSale().equals("Cheque")){
                    cheque+=saleDTO.getAmount();
                }*/
                    AmountG += saleDTO.getAmount();
                }
            }
        }
        TotalSoldedDTO totalSoldedDTO = new TotalSoldedDTO();
        totalSoldedDTO.setTotalSolded(String.format("%.02f",AmountG));
        totalSoldedDTO.setTotalWeight(weightG);
        setAcumulated(totalSoldedDTO);
       /* List<String> allSkus = new ArrayList<>();
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
            Double weight = weightTotal.get(sku);
            Double price = pricesBySku.get(sku);
            Double amount = amountTotal.get(sku);
            ticket+=sku+"\n"+productName+" "+weight+" $"+price+" $"+amount;
        }
        ticket+="\n-----------------------------------------\nDOC NOMBRE  CLIENTE IMPORTE TIPOVENTA\n-----------------------------------------\n";
        ticket+=clientsStr+"\n\n";
        ticket+="VENTAS POR CONCEPTO\nEFECTIVO: $"+efectivo+"\nCREDITO: $"+credito+"\nTRANSFERENCIA: $"+transferencia+"\nCHEQUE: $"+cheque+"\n";
        ticket+="VENTA TOTAL:$ "+(efectivo+transferencia+cheque+credito);*/
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
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesion")
                .setMessage("¿Está seguro que desea cerrar sesion?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                        bluetoothDeviceSerializable.setPrinterConnected(false);
                        this.printerConnected=false;
                        this.printerUtil.desconect();
                        this.printerUtil=null;
                    }else if(isLoading==false && this.printerConnected==false){
                        activatePrinter();
                    }
                break;
            case R.id.printResguardButton:
                    if(isLoading==false && this.printerConnected==true) {
                        this.circularProgressIndicator.setVisibility(View.VISIBLE);
                        isLoading=true;
                        this.salesPresenter.getResguardedTicket();
                    }else{
                        genericMessage("Impresora no econtrada.","Favor de conectar una impresora");
                    }
                break;
            case R.id.buttonCobranzaHistory:
                System.out.println("Cobranza: "+cobranza);
                System.out.println("IsLoading: "+isLoading);
                if(!isLoading) {
                    if (!cobranza) {
                        this.isLoading=true;
                        cobranza=true;
                        sales=null;
                        this.buttonCobranzaHistory.setText("Historial");
                        if(!modeOffline) {
                            this.salesPresenter.getAllSaleDebtsOfDay();
                        }else{
                            this.fillSalesDebOffline();
                        }
                    } else {
                        this.isLoading=true;
                        cobranza=false;
                        sales=null;
                        this.buttonCobranzaHistory.setText("Cobranza");
                        if(!modeOffline) {
                            this.salesPresenter.getAllSaleOfDay();
                        }else{
                            this.fillSalesOffline();
                        }
                    }
                }
                break;
            case R.id.buscarClienteButton:
                search();
                break;
        }
    }

    Boolean cobranza=false;

    void printerNoConnected(){
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#BDB5B5")));
    }

    void printerConnected(){
        ImageViewCompat.setImageTintList(printerButton,ColorStateList.valueOf(Color.parseColor("#39ED20")));
    }

    @Override
    public void setSalesOfDay(SaleResponseDTO[] salestemp) {
        isLoading=false;
        if(sales==null){
            sales=salestemp;
        }
        listSales.removeAllViewsInLayout();
        System.out.println("Sales length: "+sales.length);
        SaleListAdapter saleListAdapter = new SaleListAdapter(getContext(),salestemp,salesPresenter,modeOffline);
        listSales.setAdapter(saleListAdapter);
        this.salesPresenter.checkAccumulated();
    }

    @Override
    public void setSalesDebtsOfDay(SaleResponseDTO[] salestemp) {
        isLoading=false;
        if(sales==null){
            sales=salestemp;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void printTicketSale(String folio){
        SaleOfflineMode saleOfflineMode = null;
        SaleDTO saleDTO =null;
        if(viewModelStore.getStore().getDebts()!=null){
            for(SaleOfflineMode sub : viewModelStore.getStore().getDebts()){
                if(sub.getFolio().equals(folio)){
                    saleOfflineMode=sub;
                }
            }
        }

        if(viewModelStore.getStore().getSales()!=null) {
            for (SaleOfflineMode sale : viewModelStore.getStore().getSales()) {
                if (sale.getFolio().equals(folio)) {
                    saleOfflineMode = sale;
                }
            }
        }
        if(viewModelStore.getStore().getSalesMaked()!=null){
        for(SaleDTO sale : viewModelStore.getStore().getSalesMaked()){
            if(sale.getFolio().equals(folio)){
                saleDTO=sale;
            }
        }
        }

        if(saleDTO==null && saleOfflineMode==null){
            Long saleId = null;
            for(SaleResponseDTO item : sales){
                if(item.getFolio().equals(folio)){
                    saleId=item.getSaleId();
                }
            }
            if(saleId!=null) {
                salesPresenter.getTicket(saleId);
            }
        }

        if(saleDTO!=null){
            doTicketSaleOffline(saleDTO);
        }else if(saleOfflineMode!=null){
            SaleDTO saleDTO1= new SaleDTO();
            saleDTO1.setClientName(saleOfflineMode.getClientName());
            saleDTO1.setStatusStr(saleOfflineMode.getStatusStr());
            saleDTO1.setSellerId(saleOfflineMode.getSellerId());
            saleDTO1.setKeyClient(Integer.parseInt(saleOfflineMode.getKeyClient().toString()));
            saleDTO1.setTypeSale(saleOfflineMode.getTypeSale());
            saleDTO1.setCredit(saleOfflineMode.getCredit());
            saleDTO1.setDays(8);
            saleDTO1.setPayed(saleOfflineMode.getPayed());
            saleDTO1.setAmount(saleOfflineMode.getAmount());
            saleDTO1.setFolio(saleOfflineMode.getFolio());
            saleDTO1.setStatus(saleOfflineMode.getStatus());
            List<ProductSaleDTO> productSaleDTOS = saleOfflineMode.getProducts().stream().map(product->{
                ProductSaleDTO productSaleDTO = new ProductSaleDTO();
                productSaleDTO.setWeightOriginal(product.getWeightStandar());
                productSaleDTO.setTypeUnid(product.getType());
                productSaleDTO.setProductPresentation(product.getProductPresentationType());
                productSaleDTO.setProductName(product.getProductName());
                productSaleDTO.setQuantity(product.getQuantity());
                productSaleDTO.setPrice(product.getPrice());
                productSaleDTO.setProductKey(product.getProductKey());
                return productSaleDTO;
            }).collect(Collectors.toList());
            saleDTO1.setProducts(productSaleDTOS);
            doTicketSaleOffline(saleDTO1);
        }
        /*System.out.println("Se encontro la venta en linea: "+(saleOfflineMode!=null));
        System.out.println("Se encontro la venta en offline: "+(saleDTO!=null));*/
    }


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

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void cancelSale(String folio) {
        if(viewModelStore.getStore().getSales()!=null) {
            viewModelStore.getStore().getSales().stream().map(sale -> {
                if (sale.getFolio().equals(folio)) {
                    sale.setStatusStr("CANCELED");
                    sale.setStatus(false);
                }
                return sale;
            }).collect(Collectors.toList());
        }
        if(viewModelStore.getStore().getSalesMaked()!=null) {
            viewModelStore.getStore().getSalesMaked().stream().map(sale -> {
                if (sale.getFolio().equals(folio)) {
                    sale.setStatusStr("CANCELED");
                    sale.setStatus(false);
                }
                return sale;
            }).collect(Collectors.toList());
        }
    if(viewModelStore.getStore().getDebts()!=null) {
        viewModelStore.getStore().getDebts().stream().map(sub -> {
            if (sub.getFolio().equals(folio)) {
                sub.setStatusStr("CANCELED");
                sub.setStatus(false);
            }
            return sub;
        }).collect(Collectors.toList());
    }
        setModeOffline(viewModelStore.getStore());
        fillSalesOffline();
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
                        if(!modeOffline) {
                            salesPresenter.getAllSaleDebtsOfDay();
                        }else{
                            fillSalesDebOffline();
                        }
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
                        genericMessage("Error en la conexión","No se pudo conectar a una impresora.");
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

                                genericMessage("Conexión exitosa","Se conecto a la impresora: "+printer.getName());
                                printerConnected();
                            }else{
                                genericMessage("Error en la conexión","No se pudo conectar a una impresora.");
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



    int selectionPay=0;
    Boolean paying=false;
    Float amount;
    String[] contadoOptions = {"Efectivo","Transferencia","Cheque"};
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
                    PayDebtsModel payDebtsModel = new PayDebtsModel();
                    payDebtsModel.setTypePayed(contadoOptions[selectionPay]);
                    if(!modeOffline) {
                        salesPresenter.doPayDebt(sale.getSaleId(), payDebtsModel);
                    }else{
                        setSalePayed(sale.getFolio(),contadoOptions[selectionPay]);
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
    void setSalePayed(String folio, String typePay){
        if(viewModelStore.getStore().getDebts()!=null) {
            viewModelStore.getStore().getDebts().stream().map(deb -> {
                if (deb.getFolio().equals(folio)) {
                    deb.setStatus(false);
                    //deb.setTypeSale(typePay);
                }
                return deb;
            }).collect(Collectors.toList());
        }
        if(viewModelStore.getStore().getSalesMaked()!=null) {
            viewModelStore.getStore().getSalesMaked().stream().map(sale -> {
                if (sale.getFolio().equals(folio)) {
                    //sale.setTypeSale(typePay);
                    sale.setStatus(false);
                }
                return sale;
            }).collect(Collectors.toList());
        }
        if(viewModelStore.getStore().getSales()!=null) {
            viewModelStore.getStore().getSales().stream().map(sale -> {
                if (sale.getFolio().equals(folio)) {
                    sale.setStatus(false);
                    //sale.setTypeSale(typePay);
                }
                return sale;
            }).collect(Collectors.toList());
        }
        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);
        setModeOffline(viewModelStore.getStore());
        printTicketSale(folio);
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
}
