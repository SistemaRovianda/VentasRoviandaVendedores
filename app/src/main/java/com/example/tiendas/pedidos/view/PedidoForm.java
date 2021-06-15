package com.example.tiendas.pedidos.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tiendas.R;
import com.example.tiendas.Utils.Models.BluetoothDeviceSerializable;
import com.example.tiendas.Utils.Models.ClientDTO;
import com.example.tiendas.Utils.Models.OrderDTO;
import com.example.tiendas.Utils.Models.OrderToSendDTO;
import com.example.tiendas.Utils.Models.ProductRoviandaToSale;
import com.example.tiendas.Utils.NumberDecimalFilter;
import com.example.tiendas.pedidos.Adapters.NewAdapterListProductSaleOrders;
import com.example.tiendas.pedidos.presenter.PedidoFormPresenter;
import com.example.tiendas.pedidos.presenter.PedidoFormPresenterContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PedidoForm extends Fragment implements  PedidoFormContract,View.OnClickListener{

    MaterialButton cancelarPedidoButton,agregarAPedido,registrarOrder;
    NavController navController;
    ListView listaItemsOrder;
    TextInputLayout codeProductOrder,quantityProductOrder;
    CheckBox urgentOrder;

    List<ProductRoviandaToSale> productsRoviandaToOrder;
    PedidoFormPresenterContract presenter;

    CircularProgressIndicator registeringOrderSpinner;

    ClientDTO clientDTO=null;

    CircularProgressIndicator loginLoadingSpinner;
    boolean isLoading =false;


    String userName;

    BluetoothDeviceSerializable bluetoothDeviceSerializable=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pedido_form,null);
        this.cancelarPedidoButton = view.findViewById(R.id.cancelarPedidoButton);
        this.cancelarPedidoButton.setOnClickListener(this);
        this.navController = NavHostFragment.findNavController(this);
        this.clientDTO = PedidoFormArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = PedidoFormArgs.fromBundle(getArguments()).getUserName();

        this.listaItemsOrder = view.findViewById(R.id.listCreatingOrder);
        this.agregarAPedido = view.findViewById(R.id.agregarAlPedido);
        this.registrarOrder = view.findViewById(R.id.registerOrder);
        this.agregarAPedido.setOnClickListener(this);
        this.registrarOrder.setOnClickListener(this);
        this.productsRoviandaToOrder = new ArrayList<>();
        codeProductOrder = view.findViewById(R.id.inputProductPedido);
        quantityProductOrder = view.findViewById(R.id.inputQuantityPedido);
        quantityProductOrder.getEditText().setFilters(new InputFilter[]{new NumberDecimalFilter()});
        urgentOrder = view.findViewById(R.id.isUrgent);
        this.registeringOrderSpinner = view.findViewById(R.id.registeringOrderSpinner);
        this.presenter = new PedidoFormPresenter(getContext(),this);

        bluetoothDeviceSerializable=PedidoFormArgs.fromBundle(getArguments()).getPrinterDevice();

        loginLoadingSpinner = view.findViewById(R.id.loginLoadingSpinner);
        urgentOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    Calendar calendar = Calendar.getInstance();
                    if(calendar.get(Calendar.HOUR_OF_DAY)<12 && calendar.get(Calendar.HOUR_OF_DAY)>14) {
                            urgentOrder.setChecked(false);
                            genericMessage("Error", "Solo puedes hacer ordenes urgentes a partir de las 12pm hasta hasta las 2pm.");
                    }
                }
            }
        });
        return view;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelarPedidoButton:

                goToPedidoView();
                break;
            case R.id.agregarAlPedido:
                if(!this.quantityProductOrder.getEditText().getText().toString().trim().isEmpty() && isLoading==false) {
                    this.loginLoadingSpinner.setVisibility(View.VISIBLE);
                    isLoading=true;
                    presenter.findProduct(this.codeProductOrder.getEditText().getText().toString().trim(),this.quantityProductOrder.getEditText().getText().toString());
                }else{
                    this.setQuantityProductError("Por favor indica un peso o numero de piezas.");
                }
                break;
            case R.id.registerOrder:
                if(productsRoviandaToOrder.size()==0){
                    genericMessage("Incompleto","No puede registrar una orden sin productos.");
                }else{
                    register();
                }


                break;
        }
    }



    void register(){
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle("Registro de orden saliente")
                .setMessage("¿Seguro que desea registrar la orden?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrderDTO order = generateObjetRequest();
                        registeringOrderSpinner.setVisibility(View.VISIBLE);
                        registrarOrder.setVisibility(View.GONE);
                        presenter.registerOrder(order);
                    }
                }).setNegativeButton("Cancelar",null).create();
        dialog.show();
    }

    OrderDTO generateObjetRequest(){
        OrderDTO orderToSendDTO = new OrderDTO();
        orderToSendDTO.setUrgent(this.urgentOrder.isChecked());
        List<OrderToSendDTO> products= new ArrayList<>();
        for(ProductRoviandaToSale item : productsRoviandaToOrder){
            OrderToSendDTO orderToSendDTO1 = new OrderToSendDTO();
            orderToSendDTO1.setKeySae(item.getKeySae());
            orderToSendDTO1.setQuantity(item.getWeight());
            orderToSendDTO1.setObservations(item.getObservations());
            orderToSendDTO1.setOutOfStock(item.getOutOfStock());
            products.add(orderToSendDTO1);
        }
        orderToSendDTO.setProducts(products);
        return orderToSendDTO;
    }

    public void fillList(){
        this.codeProductOrder.getEditText().setText(null);
        this.quantityProductOrder.getEditText().setText(null);
        ProductRoviandaToSale[] productRoviandaToSales = new ProductRoviandaToSale[productsRoviandaToOrder.size()];
        for(int i=0;i<productsRoviandaToOrder.size();i++){
            ProductRoviandaToSale product = productsRoviandaToOrder.get(i);
            productRoviandaToSales[i]=product;
        }
        NewAdapterListProductSaleOrders adapterListProductSale = new NewAdapterListProductSaleOrders(getContext(),productRoviandaToSales,this);
        listaItemsOrder.setAdapter(adapterListProductSale);

    }

    @Override
    public void removeItemOfList(int position) {
        System.out.println("Position: "+position);
        productsRoviandaToOrder.remove(position);
        fillList();
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
    }

    @Override
    public void goToPedidoView() {
        this.navController.navigate(PedidoFormDirections.actionPedidoFormToPedidoView(this.userName).setUserName(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(bluetoothDeviceSerializable));
    }

    @Override
    public void setProductCodeError(String msg) {
        this.loginLoadingSpinner.setVisibility(View.GONE);
        isLoading=false;
        this.codeProductOrder.getEditText().setError(msg);
    }

    @Override
    public void setQuantityProductError(String msg) {
        this.loginLoadingSpinner.setVisibility(View.GONE);
        isLoading=false;
        this.quantityProductOrder.getEditText().setError(msg);
    }

    @Override
    public void addProductToPedido(ProductRoviandaToSale productRoviandaToSale) {
        this.loginLoadingSpinner.setVisibility(View.GONE);
        isLoading=false;
        Float countRequested = Float.parseFloat(this.quantityProductOrder.getEditText().getText().toString());
        Float countResguarded = Float.parseFloat("0");
        for (int i = 0; i < productsRoviandaToOrder.size(); i++) {
            ProductRoviandaToSale item = productsRoviandaToOrder.get(i);
            if (item.getPresentationId() == productRoviandaToSale.getPresentationId()) {
                countResguarded+=item.getWeight();
            }
        }

        if(productRoviandaToSale.getQuantity()>=(countResguarded+countRequested)) {
            showModalObservations(productRoviandaToSale,countRequested,false);
        }else{
            Float diference = productRoviandaToSale.getQuantity()-countResguarded;
            withoutStock(productRoviandaToSale,countRequested,true,diference);
            //genericMessage("Error en stock","Por ahora solo puedes solicitar: "+(productRoviandaToSale.getQuantity()-countResguarded));
        }
    }

    public void withoutStock(ProductRoviandaToSale productRoviandaToSale,Float countRequested,Boolean outOfStock,Float diference){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Producto fuera de stock: "+diference+ " piezas/paquetes disponibles." ).setMessage("¿Desea continuar el pedido sin stock? (posiblemente se entregue este producto incompleto).");
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showModalObservations(productRoviandaToSale,countRequested,outOfStock);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setTextColor(Color.BLACK);
        negative.setTextColor(Color.BLACK);
    }


    public void showModalObservations(ProductRoviandaToSale productRoviandaToSale,Float countRequested,Boolean outOfStock){
       /*
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext());

        builder.setTitle(productRoviandaToSale.getNameProduct()+"\n"+productRoviandaToSale.getPresentationType());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view= layoutInflater.inflate(R.layout.modalobservations, null);
        TextInputLayout observations = view.findViewById(R.id.observationsOrder);

        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //int index = -1;

                for (int i = 0; i < productsRoviandaToOrder.size(); i++) {
                    ProductRoviandaToSale item = productsRoviandaToOrder.get(i);
                    if (item.getPresentationId() == productRoviandaToSale.getPresentationId()) {
                        index = i;
                        item.setWeight(
                                item.getWeight() + countRequested
                        );
                        item.setObservations(observations.getEditText().getText().toString());
                    }
                }
                if (index == -1) {
                    productRoviandaToSale.setObservations(observations.getEditText().getText().toString());
                    productRoviandaToSale.setWeight(countRequested);
                    productsRoviandaToOrder.add(productRoviandaToSale);
                //}
                fillList();
            }
        }).setNegativeButton("Omitir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    int index = -1;
                    for (int i = 0; i < productsRoviandaToOrder.size(); i++) {
                        ProductRoviandaToSale item = productsRoviandaToOrder.get(i);
                        if (item.getPresentationId() == productRoviandaToSale.getPresentationId()) {
                            index = i;
                            item.setWeight(
                                    item.getWeight() + countRequested
                            );
                            item.setObservations("");
                        }
                    }
                    if (index == -1) {
                        productRoviandaToSale.setObservations("");
                        productRoviandaToSale.setWeight(countRequested);
                        productsRoviandaToOrder.add(productRoviandaToSale);
                    }
                    fillList();

            }
        });
        builder.show();
*/

        int index = -1;
        for (int i = 0; i < productsRoviandaToOrder.size(); i++) {
            ProductRoviandaToSale item = productsRoviandaToOrder.get(i);
            if (item.getPresentationId() == productRoviandaToSale.getPresentationId()) {
                index = i;
                item.setWeight(
                        item.getWeight() + countRequested
                );
                item.setOutOfStock(outOfStock);
                item.setObservations("");
            }
        }
        if (index == -1) {
            productRoviandaToSale.setObservations("");
            productRoviandaToSale.setWeight(countRequested);
            productRoviandaToSale.setOutOfStock(outOfStock);
            productsRoviandaToOrder.add(productRoviandaToSale);
        }
        fillList();

    }

    @Override
    public void registerSuccess() {
        this.registeringOrderSpinner.setVisibility(View.GONE);
        this.registrarOrder.setVisibility(View.VISIBLE);
        this.goToPedidoView();
    }

    @Override
    public void registerError() {
        this.registeringOrderSpinner.setVisibility(View.GONE);
        this.registrarOrder.setVisibility(View.VISIBLE);
    }
}
