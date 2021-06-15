package com.example.tiendas.pedidos.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.example.tiendas.R;
import com.example.tiendas.Utils.Models.BluetoothDeviceSerializable;
import com.example.tiendas.Utils.Models.ClientDTO;
import com.example.tiendas.Utils.Models.OrderDTO;
import com.example.tiendas.Utils.Models.OrderDetails;
import com.example.tiendas.Utils.Models.OrderPresentationDetails;
import com.example.tiendas.Utils.PrinterUtil;
import com.example.tiendas.Utils.ViewModelStore;

import com.example.tiendas.pedidos.Adapters.AdapterItemsOrders;
import com.example.tiendas.pedidos.presenter.PedidoPresenter;
import com.example.tiendas.pedidos.presenter.PedidoPresenterContract;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PedidoView extends Fragment implements View.OnClickListener,PedidoViewContract{
    BottomNavigationView homeButton;

    NavController navController;
    TextView logoutButton,endDayButton,eatTimeButton,printer;
    ListView listOrders;
    PedidoPresenterContract presenter;
    MaterialButton nuevoPedido;


    CircularProgressIndicator circularProgressIndicator;
    boolean isLoading=false;

    ClientDTO clientInVisit=null;

    BluetoothDeviceSerializable bluetoothDeviceSerializable=null;

    String userName;
    TextView userNameTextView;
    ViewModelStore viewModelStore;
    Boolean modeOffline;


    ImageView printerButton;
    boolean printerConnected=false;
    PrinterUtil printerUtil=null;
    BluetoothDevice printerDevice=null;
    BluetoothAdapter bluetoothAdapter=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pedidos_layout,null);

        homeButton = view.findViewById(R.id.bottom_navigation_pedidos);
        homeButton.setSelectedItemId(R.id.pedidos_section);
        logoutButton=view.findViewById(R.id.Logout_button);
        logoutButton.setOnClickListener(this);
        bluetoothDeviceSerializable=PedidoViewArgs.fromBundle(getArguments()).getPrinterDevice();
        this.clientInVisit = PedidoViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = PedidoViewArgs.fromBundle(getArguments()).getUserName();
        this.userNameTextView = view.findViewById(R.id.userName);
        this.userNameTextView.setText("Usuario: "+this.userName);
        this.userNameTextView.setTextColor(Color.parseColor("#236EF2"));
        this.circularProgressIndicator = view.findViewById(R.id.loginLoadingSpinner);

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.printerButton = view.findViewById(R.id.printerButton);
        this.printerButton.setOnClickListener(this);

        this.navController = NavHostFragment.findNavController(this);
        this.presenter = new PedidoPresenter(getContext(),this);

        circularProgressIndicator.setVisibility(View.INVISIBLE);
        this.listOrders=view.findViewById(R.id.listOrders);
        this.nuevoPedido = view.findViewById(R.id.nuevoPedido);
        this.nuevoPedido.setOnClickListener(this);

        this.endDayButton = view.findViewById(R.id.end_day_button);
        this.endDayButton = view.findViewById(R.id.end_day_button);
        this.endDayButton.setVisibility(View.INVISIBLE);
        this.endDayButton.setOnClickListener(this);
        this.eatTimeButton = view.findViewById(R.id.eat_time_button);
        this.eatTimeButton.setVisibility(View.INVISIBLE);
        this.eatTimeButton.setOnClickListener(this);

        bluetoothDeviceSerializable=PedidoFormArgs.fromBundle(getArguments()).getPrinterDevice();
        if(bluetoothDeviceSerializable!=null){
            this.printerConnected = bluetoothDeviceSerializable.isPrinterConnected();
            if(this.printerConnected){
                printerConnected();
                if(printerUtil==null){
                    printerUtil = new PrinterUtil(getContext());
                }
                printerDevice = bluetoothDeviceSerializable.getBluetoothDevice();
            }
        }
        homeButton.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_section:
                        goToHome();
                        break;
                    case R.id.visitas_section:
                        goToCotizaciones();
                        break;
                    case R.id.cliente_section:
                        goToClient();
                        break;
                    case R.id.pedidos_section:
                        System.out.println("No cambia de seccion");
                        break;
                    case R.id.ventas_section:
                            goToSales();
                        break;
                }
                return false;
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
            this.presenter.getOrders();
    }




    void goToCotizaciones(){
        navController.navigate(PedidoViewDirections.actionPedidoViewToCotizacionesView(this.userName).setUserName(this.userName).setClientInVisit(this.clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }
    void goToClient(){
        navController.navigate(PedidoViewDirections.actionPedidoViewToClientView(this.userName).setUserName(this.userName).setClientInVisit(this.clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }
    void goToHome(){
        navController.navigate(PedidoViewDirections.actionPedidoViewToHomeView(this.userName).setUserName(this.userName).setClientInVisit(this.clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }
    void goToSales(){
        navController.navigate(PedidoViewDirections.actionPedidoViewToSalesView(this.userName).setUserName(this.userName).setClientInVisit(this.clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
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
        navController.navigate(PedidoViewDirections.actionPedidoViewToLoginView());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Logout_button:
                logout();
                break;
            case R.id.nuevoPedido:

                    goToForm();

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
        }
    }

    @Override
    public void setOrders(OrderDTO[] orders) {
        if(orders.length>0) {
            AdapterItemsOrders adapterItemsOrders = new AdapterItemsOrders(getContext(), orders);
            listOrders.setAdapter(adapterItemsOrders);
            listOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*if(isLoading==false) {
                        presenter.getDetailsOfOrder(orders[position].getOrderId());
                        circularProgressIndicator.setVisibility(View.VISIBLE);
                        isLoading=true;
                    }*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = getLayoutInflater();
                    View viewMenu = inflater.inflate(R.layout.print_order_menu,null);
                    builder.setView(viewMenu);
                    builder.setCancelable(false);
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();


                    Button print=viewMenu.findViewById(R.id.buttonPrint);
                    Button modify = viewMenu.findViewById(R.id.buttonModify);

                    modify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            navController.navigate(PedidoViewDirections.actionPedidoViewToPedidoDetails(orders[position].getOrderId(),userName,orders[position].getDate())
                                    .setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
                        }
                    });
                    print.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            presenter.getOrderDetailsToEditToPrint(Long.parseLong(String.valueOf(orders[position].getOrderId())));
                        }
                    });
                    dialog.show();
                    Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    cancel.setTextColor(Color.BLACK);
                }
            });
        }
    }


    @Override
    public void printTicketOrder(String ticket) {
        reprintTicket(ticket);
    }

    int intentsToClose=0;

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
                    printerUtil.connectWithPrinter(printerDevice);
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
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).create();
        dialog.show();
        circularProgressIndicator.setVisibility(View.GONE);
        isLoading=false;
    }
    @Override
    public void goToForm() {
        this.navController.navigate(PedidoViewDirections.actionPedidoViewToPedidoForm(this.userName).setUserName(this.userName));
    }

    @Override
    public void showDetails(OrderDetails[] orderDetails,int orderId) {
        circularProgressIndicator.setVisibility(View.INVISIBLE);
        String[] products = new String[orderDetails.length];
        for(int i=0;i<orderDetails.length;i++){
            products[i]=orderDetails[i].getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,products){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };
        ListView listView = new ListView(getContext());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.getDetailsPresentation(orderId,orderDetails[position].getProduct_id());
                circularProgressIndicator.setVisibility(View.VISIBLE);
                isLoading=true;
            }
        });
        listView.setAdapter(adapter);
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Productos solicitados")
                .setView(listView).setPositiveButton("Aceptar",null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isLoading=false;
                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    void printerNoConnected(){
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#BDB5B5")));
    }

    void printerConnected(){
        ImageViewCompat.setImageTintList(printerButton,ColorStateList.valueOf(Color.parseColor("#39ED20")));
    }

    @Override
    public void showPresetationDetails(OrderPresentationDetails[] orderPresentationDetails) {
        circularProgressIndicator.setVisibility(View.INVISIBLE);
        String[] products = new String[orderPresentationDetails.length];
        for(int i=0;i<orderPresentationDetails.length;i++){
            products[i]=orderPresentationDetails[i].getTypePresentation() +" Cantidad: "+orderPresentationDetails[i].getUnits();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,products);
        ListView listView = new ListView(getContext());
        listView.setAdapter(adapter);
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Presentaciones del producto")
                .setView(listView).setPositiveButton("Aceptar",null).setCancelable(false).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isLoading=false;
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
            if(printerUtil==null) {
                printerUtil = new PrinterUtil(getContext());
            }
            final Set<BluetoothDevice> deviceList = printerUtil.findDevices();
            if(deviceList.size()>0) {
                findPrinter(deviceList);
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

                            printerDevice = bluetoothDevicesMapped.get(which);

                            printerConnected = printerUtil.connectWithPrinter(printerDevice);
                            circularProgressIndicator.setVisibility(View.GONE);
                            isLoading=false;
                            if(printerConnected==true) {

                                genericMessage("Conexión exitosa","Se conecto a la impresora: "+printerDevice.getName());
                                printerConnected();
                            }else{
                                genericMessage("Error en la conexión","No se pudo conectar a una impresora.");
                                printerNoConnected();
                            }
                            if(bluetoothDeviceSerializable==null) {
                                bluetoothDeviceSerializable = new BluetoothDeviceSerializable();
                            }
                            bluetoothDeviceSerializable.setBluetoothDevice(printerDevice);
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
}
