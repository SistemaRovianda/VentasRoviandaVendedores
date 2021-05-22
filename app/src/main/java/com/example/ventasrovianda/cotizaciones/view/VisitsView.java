package com.example.ventasrovianda.cotizaciones.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientOfflineMode;
import com.example.ventasrovianda.Utils.Models.ClientVisitDTO;
import com.example.ventasrovianda.Utils.Models.ModeOfflineDebts;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ModeOfflineS;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSMP;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.ProductPresentation;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleOfflineMode;
import com.example.ventasrovianda.Utils.ViewModelStore;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Boolean checkIfOffline(){
        File root = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "offline");
        if (!root.exists()) {
            root.mkdirs();
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        File gpxfile = new File(root, "offline-"+dateParsed+".rovi");
        Boolean offline=false;
        Boolean modified=false;
        if(gpxfile.exists()){
            offline=true;
        }
        if(viewModelStore.getStore()!=null){
            if(viewModelStore.getStore().getSalesMaked()!=null && viewModelStore.getStore().getSalesMaked().size()>0){
                modified=true;
            }
            if(viewModelStore.getStore().getDebts()!=null){
                for(SaleOfflineMode saleOfflineMode : viewModelStore.getStore().getDebts()){
                    if(saleOfflineMode.getStatus()==false){
                        modified=true;
                    }
                }
            }
            if(viewModelStore.getStore().getSales()!=null){
                for(SaleOfflineMode saleOfflineMode : viewModelStore.getStore().getSales()){
                    if (saleOfflineMode.getStatusStr().equals("CANCELED")) {
                        modified=true;
                    }
                }
        }
        }
        if(offline && modified ){
                download.setEnabled(false);
                download.setColorFilter(Color.GRAY);
                upload.setEnabled(true);
                upload.setColorFilter(Color.parseColor("#2FBF34"));
            return false;
        }else{
            download.setEnabled(true);
            download.setColorFilter(Color.parseColor("#2FBF34"));
            upload.setColorFilter(Color.GRAY);
            upload.setEnabled(false);
            return  true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setModeOffline(ModeOfflineModel modeOffline) {
        isLoading=false;
        circularProgressIndicator.setVisibility(View.GONE);
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
            this.sincronized=true;
            viewModelStore.saveStore(modeOffline);
            if(viewModelStore.getStore()!=null && viewModelStore.getStore().getClientsToVisit()!=null){
                ClientOfflineMode[] array = new ClientOfflineMode[viewModelStore.getStore().getClientsToVisit().size()];
                viewModelStore.getStore().getClientsToVisit().toArray(array);
                clientsVisits=array;
                setClientVisits(array);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(),"Modo Offline Activado",Toast.LENGTH_SHORT).show();
        //viewModelStore.saveStore(modeOffline);
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

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        if(checkOffline()){
            this.setModeOffline(viewModelStore.getStore());
            checkIfOffline();
            ClientOfflineMode[] array = new ClientOfflineMode[viewModelStore.getStore().getClientsToVisit().size()];
            viewModelStore.getStore().getClientsToVisit().toArray(array);
            clientsVisits=array;
            this.setClientVisits(array);
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
    void search(){

        if(clientsVisits!=null && clientsVisits.length>0){
            List<ClientOfflineMode> clients = new ArrayList<>();
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
                for(int i=0;i<clientsVisits.length;i++){
                    if(clientsVisits[i].getClientName().toLowerCase().contains(this.currentHint.toLowerCase()) || String.valueOf(clientsVisits[i].getKeyClient()).toLowerCase().contains(this.currentHint.toLowerCase())){
                        System.out.println("Se encontro");
                        clients.add(clientsVisits[i]);
                    }
                }
                System.out.println("Filtrado tamaño:"+clients.size());
                clientsVisitsTemp = new ClientOfflineMode[clients.size()];
                for(int i=0;i<clients.size();i++){
                    clientsVisitsTemp[i]=clients.get(i);
                }
            }else{
                clientsVisitsTemp = new ClientOfflineMode[clientsVisits.length];
                for(int i=0;i<clientsVisits.length;i++){
                    clientsVisitsTemp[i]=clientsVisits[i];
                }
            }
        setClientVisits(clientsVisitsTemp);
        }

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
    @RequiresApi(api = Build.VERSION_CODES.N)
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
                if(checkIfOffline()){
                    Toast.makeText(getContext(),"Sincronizando con servidor...",Toast.LENGTH_SHORT).show();
                    if(!isLoading) {
                        isLoading=true;
                        circularProgressIndicator.setVisibility(View.VISIBLE);
                        presenter.getStockOnline();
                    }
                }else{
                    Toast.makeText(getContext(),"Primero sube cambios al servidor",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.uploadChanges:
                if(!isLoading){
                    isLoading=true;
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    ModeOfflineSincronize modeOfflineSincronize = generateModeOfflineRequest();
                    presenter.UploadChanges(modeOfflineSincronize);
                }
                break;
        }
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
        this.presenter.getClientsVisits();
        isLoading=true;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setClientVisits(ClientOfflineMode[] visits) {

        if(visits!=null) {

            //AdapterListClientVisit customAdapter = new AdapterListClientVisit(this.getContext(), visits,presenter,inVisit);
            List<String> clients = Arrays.stream(visits).map(client->client.getKeyClient()+" "+client.getClientName()).collect(Collectors.toList());
            String[] arr = new String[visits.length];
            clients.toArray(arr);
            ArrayAdapter<String> customAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,arr){
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
}
