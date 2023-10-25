package com.example.ventasrovianda.pedidos.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.OrderDetails;
import com.example.ventasrovianda.Utils.Models.OrderDetailsToEdit;
import com.example.ventasrovianda.Utils.Models.UpdateOrderRequest;
import com.example.ventasrovianda.pedidos.Adapters.AdapterListUpdateOrder;
import com.example.ventasrovianda.pedidos.presenter.PedidoDetailsPresentarContract;
import com.example.ventasrovianda.pedidos.presenter.PedidoDetailsPresenter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PedidoDetails extends Fragment implements PedidoDetailsContract,View.OnClickListener {


    PedidoDetailsPresentarContract presenter;
    ListView listDetails;
    Button updateBtn;
    Boolean isLoading=false;
    Boolean isUpdatable=false;
    CheckBox editOrder;
    CircularProgressIndicator loadingProgress;
    OrderDetailsToEdit[] details;
    Integer[] valuesEdited;
    BluetoothDeviceSerializable bluetoothDeviceSerializable;
    ClientDTO clientInVisit;
    String userName;
    NavController  navController;
    ImageView backArrow;
    TextView orderIdTextView;
    Long orderId;
    TextView logoutButton;
    String dateOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_details_edit,null,false);
        listDetails= view.findViewById(R.id.listDetails);
        this.logoutButton = view.findViewById(R.id.Logout_button);
        logoutButton.setOnClickListener(this);
        updateBtn= view.findViewById(R.id.updateOrder);
        updateBtn.setOnClickListener(this);
        editOrder= view.findViewById(R.id.editOrder);
        loadingProgress= view.findViewById(R.id.loginLoadingSpinner);
        navController = NavHostFragment.findNavController(this);
        backArrow = view.findViewById(R.id.backArrow);
        orderId = PedidoDetailsArgs.fromBundle(getArguments()).getOrderId();
        orderIdTextView= view.findViewById(R.id.orderId);
        orderIdTextView.setText("Orden: "+orderId);
        backArrow.setOnClickListener(this);
        bluetoothDeviceSerializable=PedidoViewArgs.fromBundle(getArguments()).getPrinterDevice();
        this.clientInVisit = PedidoViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = PedidoViewArgs.fromBundle(getArguments()).getUserName();
        dateOrder=PedidoDetailsArgs.fromBundle(getArguments()).getDateOrder();
        dateOrder=dateOrder.replace("T"," ");
        editOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(dateOrder);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal.setTime(date);
                System.out.println(cal.getTime());
                System.out.println(cal2.getTime());
                if(cal.get(Calendar.MONTH)==cal2.get(Calendar.MONTH) &&
                        cal.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH) && cal2.get(Calendar.HOUR_OF_DAY)<23) {
                    if (isLoading) {
                        isUpdatable = true;
                    } else {

                        isUpdatable = isChecked;
                        if (details != null) {
                            setDetails(details);
                        }

                    }
                }else{
                    AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Operación no permitida")
                            .setMessage("Ya no es posible actualizar la orden")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setCancelable(false).create();
                    dialog.show();
                    editOrder.setChecked(false);
                }
            }
        });
        this.presenter = new PedidoDetailsPresenter(getContext(),this);

        this.presenter.getOrderDetailsToEdit(orderId);
        return view;
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
    public void goToLogin() {
        navController.navigate(PedidoDetailsDirections.actionPedidoDetailsToLoginView());
    }

    @Override
    public void setDetails(OrderDetailsToEdit[] orderDetailsToEdit) {

        if(this.details==null){
            this.editOrder.setChecked(false);
            this.valuesEdited=new Integer[orderDetailsToEdit.length];
        }
        this.details=orderDetailsToEdit;
        AdapterListUpdateOrder customAdapter = new AdapterListUpdateOrder(getContext(),orderDetailsToEdit,isUpdatable,this);
        listDetails.setAdapter(customAdapter);
    }

    @Override
    public void setValuesToRow(int position, int value) {
        this.valuesEdited[position]=value;
        System.out.println("Setting: "+value+ " position: "+position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updateOrder:
                if(!this.isLoading && this.isUpdatable){
                    this.updateBtn.setEnabled(false);
                    this.updateBtn.setVisibility(View.GONE);
                    this.loadingProgress.setVisibility(View.VISIBLE);
                    loadItemsEdited();
                }
                break;
            case R.id.backArrow:
                if(!isLoading) {
                    this.goBack();
                }
                break;
            case R.id.Logout_button:
                logout();
                break;
        }
    }

    void loadItemsEdited(){
        List<UpdateOrderRequest> request = new ArrayList<UpdateOrderRequest>();
        for(int i=0;i<this.details.length;i++) {
            UpdateOrderRequest req = new UpdateOrderRequest();
            req.setSubOrderId(details[i].getSubOrderId());
            req.setQuantity(valuesEdited[i]);
            System.out.println("Loading: "+valuesEdited[i]+ " index: "+i);
            request.add(req);
        }
        presenter.udpateOrderDetails(request,orderId);
    }
    AlertDialog modalSuccess=null;
    @Override
    public void successUpdate(String msg) {
        isLoading=false;
        this.loadingProgress.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.modal_success_operation,null);
        TextView messageLoad = view.findViewById(R.id.message_load);
        Button btnAccept = view.findViewById(R.id.acceptButtonModal);
        messageLoad.setText(msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        this.modalSuccess=builder.create();
        this.modalSuccess.show();
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modalSuccess.dismiss();
                goBack();
            }
        });
    }

    @Override
    public void goBack() {
        navController.navigate(PedidoDetailsDirections.actionPedidoDetailsToPedidoView(this.userName).setClientInVisit(clientInVisit).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @Override
    public void deleteItem(int position) {
        isUpdatable=true;
        this.editOrder.setChecked(true);
        List<OrderDetailsToEdit> newList = new ArrayList<>();
        for(int i=0;i<details.length;i++){
            if(i!=position){
                newList.add(details[i]);
            }
        }
        OrderDetailsToEdit[] itemsReorded = new OrderDetailsToEdit[newList.size()];
        for(int i=0;i<newList.size();i++){
            itemsReorded[i]=newList.get(i);
        }

        setDetails(itemsReorded);
    }
}
