package com.example.ventasrovianda.clients.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
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
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.clients.presenter.ClientPresenter;
import com.example.ventasrovianda.clients.presenter.ClientPresenterContract;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ClientView extends Fragment implements View.OnClickListener,ClientViewContract{
    BottomNavigationView homeButton;
    MaterialButton nuevoCliente,buscarCliente;

    NavController navController;
    TextView logoutButton,endDayButton,eatTimeButton;
    ListView listaClients;
    ClientPresenterContract presenter;
    TextInputLayout inputSearch;
    ClientDTO[] clientsToFilter;
    ImageView printerButton;
    ClientDTO clientDTO=null;
    BluetoothDeviceSerializable bluetoothDeviceSerializable=null;
    String userName;
    TextView userNameTextView;
    ViewModelStore viewModelStore;
    Gson parser;
    boolean modeOffline;

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_register_layout,null);
        this.navController = NavHostFragment.findNavController(this);
        homeButton = view.findViewById(R.id.bottom_navigation_client);
        homeButton.setSelectedItemId(R.id.cliente_section);
        this.clientDTO = ClientViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = ClientViewArgs.fromBundle(getArguments()).getUserName();
        this.userNameTextView = view.findViewById(R.id.userName);
        this.userNameTextView.setText("Usuario: "+this.userName);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));
        this.printerButton = view.findViewById(R.id.printerButton);
        this.printerButton.setOnClickListener(this);
        this.printerButton.setVisibility(View.INVISIBLE);
        this.logoutButton=view.findViewById(R.id.Logout_button);
        this.logoutButton.setOnClickListener(this);
        this.nuevoCliente = view.findViewById(R.id.nuevoCliente);
        this.nuevoCliente.setOnClickListener(this);
        this.listaClients = view.findViewById(R.id.listaClientes);
        this.presenter = new ClientPresenter(getContext(),this);

        this.buscarCliente = view.findViewById(R.id.buscarClienteButton);
        this.buscarCliente.setOnClickListener(this);
        this.inputSearch = view.findViewById(R.id.cliente_input_search);

        this.endDayButton = view.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.eatTimeButton = view.findViewById(R.id.eat_time_button);
        this.eatTimeButton.setOnClickListener(this);
        this.endDayButton.setVisibility(View.GONE);
        this.eatTimeButton.setVisibility(View.GONE);

        bluetoothDeviceSerializable = ClientViewArgs.fromBundle(getArguments()).getPrinterDevice();
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
                        System.out.println("No cambia de seccion");
                        goToHome();
                        break;
                    case R.id.visitas_section:
                        goToCotizaciones();
                        break;
                    case R.id.cliente_section:
                        System.out.println("No cambia de seccion");
                        break;
                    case R.id.pedidos_section:
                        goToPedidos();
                        break;
                    case R.id.ventas_section:
                        goToSales();
                        break;
                }
                return false;
            }
        });
        checkInternetConnection();
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);

        setClientVisits("",false);
    }


    void goToHome(){
        this.navController.navigate(ClientViewDirections.actionClientViewToHomeView(this.userName).setUserName(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(this.bluetoothDeviceSerializable));
    }
    void goToCotizaciones(){
        this.navController.navigate(ClientViewDirections.actionClientViewToCotizacionesView(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable).setUserName(this.userName));
    }
    void goToPedidos(){
        this.navController.navigate(ClientViewDirections.actionClientViewToPedidoView(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable).setUserName(this.userName));
    }
    @Override
    public void goToLogin(){
        this.navController.navigate(ClientViewDirections.actionClientViewToLoginView());
    }

    void goToRegister(){
        this.navController.navigate(ClientViewDirections.actionClientViewToRegisterClientView(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable).setUserName(this.userName));
    }

    void goToSales(){
        this.navController.navigate(ClientViewDirections.actionClientViewToSalesView(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable).setUserName(this.userName));
    }

    void logout(){
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesion")
                .setMessage("¿Está seguro que desea cerrar sesion?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    viewModelStore.getAppDatabase().userDataInitialDao().updateAllLogedInFalse();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            presenter.logout();
                                        }
                                    });
                                }
                            });

                    }
                }).setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Logout_button:
                logout();
                break;
            case R.id.nuevoCliente:
                if (isConnected) {
                    goToRegister();
                }else{
                    genericMessage("Sin conexión","Debes tener internet");
                }
                break;
            case R.id.buscarClienteButton:
                    search();
                break;
        }
    }

    public void genericMessage(String title,String msg){
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

    Boolean isConnected=false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    void checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                isConnected=true;
            }

            @Override
            public void onLost(@NonNull Network network) {
                isConnected=false;
            }
        });
    }


    boolean filtered=false;
    String currentHint="";
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

            setClientVisits(this.currentHint,filtered);

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setClientVisits(String hint,Boolean filter) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Client> clientsToVisit=new ArrayList<>();

                List<Client> clientsFromSql= viewModelStore.getAppDatabase().clientDao().getClientsBySellerUid(viewModelStore.getStore().getSellerId());
                for(Client client : clientsFromSql){
                    if(filter){
                        if(client.name.toLowerCase().contains(hint.toLowerCase()) || String.valueOf(client.clientKey).contains(hint)){
                            clientsToVisit.add(client);
                        }
                    }else{
                        clientsToVisit.add(client);
                    }
                }

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
                            listaClients.setAdapter(customAdapter);
                        }
                    }
                });
            }
        });
    }
}
