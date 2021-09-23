package com.example.ventasrovianda.devolutions.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.devolutions.adapter.ItemListDevolutionAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DevolutionsView extends Fragment implements View.OnClickListener,DevolutionsViewContract{

    TextView folioText,clientNameText,devolutionTotalTicket,textObservations;
    Button cancelDevolution,createDevolution;
    NavController navController;
    ViewModelStore viewModelStore;
    String folioSelected;
    ListView listProductsToDevolution;
    Sale saleEntity;
    List<SubSale> subSalesEntities;
    String type="";
    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.devolution_fragment,null);
        listProductsToDevolution=view.findViewById(R.id.listProductsToDevolution);
        this.folioText = view.findViewById(R.id.folioDevolutionText);
        this.clientNameText = view.findViewById(R.id.clientDevolutionText);
        this.devolutionTotalTicket=view.findViewById(R.id.devolutionTotalTicket);
        this.textObservations=view.findViewById(R.id.textObservations);
        String folio = DevolutionsViewArgs.fromBundle(getArguments()).getFolio();
        this.type=DevolutionsViewArgs.fromBundle(getArguments()).getType();
        this.folioText.setText("Folio: "+folio);
        this.folioSelected=folio;
        cancelDevolution = view.findViewById(R.id.cancelDevolution);
        this.cancelDevolution.setOnClickListener(this);
        createDevolution = view.findViewById(R.id.createDevolution);
        this.createDevolution.setOnClickListener(this);
        navController = NavHostFragment.findNavController(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        if(type.equals("CREATE")) {
            findSaleDetails();
        }else if(type.equals("EDIT")){
            findDevolutionDetails();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelDevolution:
                if(type.equals("CREATE")) {
                    confirmAction(1, null);
                }else if(type.equals("EDIT")){
                    cancelDevolution();
                }
                break;
            case R.id.createDevolution:
                    doDevolution();
                    break;
        }
    }

    void cancelDevolution(){
        navController.navigate(DevolutionsViewDirections.actionDevolutionsViewToSalesView(""));
    }
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    void findSaleDetails(){
        executor.execute(new Runnable() {
            @Override
            public void run() {

                Sale sale = viewModelStore.getAppDatabase().saleDao().getByFolio(folioSelected);
                List<SubSale> subSaleList = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(folioSelected);
                for(SubSale subSale : subSaleList){
                    DevolutionSubSale devolutionSubSale = viewModelStore.getAppDatabase().devolutionSubSaleDao().findDevolutionSubSaleBySubSaleId(subSale.subSaleId);
                    if(devolutionSubSale!=null){
                        subSale.price=(subSale.price/subSale.quantity)*devolutionSubSale.quantity;
                        subSale.quantity=devolutionSubSale.quantity;
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(sale!=null){
                            clientNameText.setText("Cliente: "+sale.clientName);
                            devolutionTotalTicket.setText("Total: $"+sale.amount);
                            saleEntity=sale;
                        }
                        if(subSaleList.size()>0){
                            subSalesEntities=subSaleList;
                            fillSubSales(subSaleList);
                        }
                    }
                });
            }
        });
    }

    void findDevolutionDetails(){
        executor.execute(new Runnable() {
            @Override
            public void run() {

                Sale sale = viewModelStore.getAppDatabase().saleDao().getByFolio(folioSelected);
                List<SubSale> subSales = viewModelStore.getAppDatabase().subSalesDao().getSubSalesBySale(folioSelected);
                DevolutionRequest  devolutionRequest = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolioRegister(folioSelected);
                List<DevolutionSubSale> devolutionSubSales = viewModelStore.getAppDatabase().devolutionSubSaleDao().findByDevolutionRequestId(devolutionRequest.devolutionRequestId);
                Float amount =Float.parseFloat("0");
                for(DevolutionSubSale devolutionSubSale : devolutionSubSales){
                    amount+=devolutionSubSale.price;
                }
                devolutionTotalTicket.setText("Total: $"+amount);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                            if(sale!=null){
                                clientNameText.setText("Cliente: "+sale.clientName);
                                saleEntity=sale;

                            }
                                mapSubSales=new HashMap<>();
                            if(subSales.size()>0 && devolutionSubSales.size()>0 && devolutionRequest!=null){
                                for(DevolutionSubSale devolutionSubSale : devolutionSubSales){
                                    mapSubSales.put(devolutionSubSale.subSaleId,devolutionSubSale.quantity);
                                }
                                setDevolutionViewToReview(subSales,devolutionRequest);
                            }
                    }
                });
            }
        });
    }

    Map<Integer,Float> mapSubSales;
    void fillSubSales(List<SubSale> subSaleList){
        mapSubSales = new HashMap<>();
        for(SubSale subSale : subSaleList){
            mapSubSales.put(subSale.subSaleId,subSale.quantity);
        }
        ItemListDevolutionAdapter adapter = new ItemListDevolutionAdapter(subSaleList,getContext(),this,mapSubSales,devolutionTotalTicket,type);
        listProductsToDevolution.setAdapter(adapter);
    }

    void setDevolutionViewToReview(List<SubSale> subSales,DevolutionRequest devolutionRequest){
        ItemListDevolutionAdapter adapter = new ItemListDevolutionAdapter(subSales,getContext(),this,mapSubSales,devolutionTotalTicket,type);
        listProductsToDevolution.setAdapter(adapter);
        textObservations.setVisibility(View.VISIBLE);
        textObservations.setText(devolutionRequest.description);
        textObservations.setTextColor(Color.WHITE);
        textObservations.setTextSize(20);
        cancelDevolution.setText("Regresar");
        createDevolution.setVisibility(View.GONE);
    }

    void confirmAction(int type,DevolutionRequest devolutionRequest){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Confirmación");
        if(type==1){
            builder.setMessage("¿Está seguro que desea cancelar la devolución?");
        }else{
            builder.setMessage("¿Está seguro que desea crear la devolución?");
        }
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(type==1){
                    cancelDevolution();
                }else if(type==2){
                    // send devolution
                    saveOfflineDevolution(devolutionRequest);
                }
            }
        });
        AlertDialog alertDialog =builder.create();
        alertDialog.show();
        Button positive= alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setTextColor(Color.BLACK);
        negative.setTextColor(Color.BLACK);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void doDevolution(){
        Boolean toDevolution=false;

        for(SubSale subSale : subSalesEntities){
            System.out.println("Producto :"+subSale.productName+" "+subSale.productPresentationType);
            System.out.println("Quantity original :"+subSale.quantity);
            System.out.println("Quantity modified : "+mapSubSales.get(subSale.subSaleId));
            if(subSale.quantity!=mapSubSales.get(subSale.subSaleId)){
                toDevolution=true;
            }

        }
        if(toDevolution) {
            DevolutionRequest devolutionRequest = new DevolutionRequest();
            ZonedDateTime zdt = ZonedDateTime.now();
            zdt=zdt.minusHours(5);
            String nowAsISO= zdt.format(DateTimeFormatter.ISO_INSTANT);
            devolutionRequest.createAt=nowAsISO;
            devolutionRequest.folio=saleEntity.folio;
            devolutionRequest.sincronized=0;
            devolutionRequest.status="PENDING";
            showOptionsDevolutions(devolutionRequest);
        }
    }
    AlertDialog optionsDevolutions;
    void showOptionsDevolutions(DevolutionRequest devolutionRequest){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.type_devolution_options,null);
        builder.setView(view);
        optionsDevolutions=builder.create();
        Button goodStateButton = view.findViewById(R.id.goodStateButton);
        Button badStateButton = view.findViewById(R.id.badStateButton);
        goodStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Good state");
                optionsDevolutions.dismiss();
                devolutionRequest.typeDevolution="GOOD_STATE";
                showModalDescription(devolutionRequest);
            }
        });
        badStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Bad state");
                //optionsDevolutions.dismiss();
                devolutionRequest.typeDevolution="BAD_STATE";
                showModalDescription(devolutionRequest);
            }
        });

        optionsDevolutions.show();
    }
    AlertDialog devolutionDescriptionModal;
    void showModalDescription(DevolutionRequest devolutionRequest){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.modal_description_devolution,null);
        TextInputLayout textInputLayout = view.findViewById(R.id.devolutionObservationsInput);
        devolutionDescriptionModal= builder.create();
        devolutionDescriptionModal.setView(view);
        devolutionDescriptionModal.setCancelable(false);
        Button continueButton= view.findViewById(R.id.continueDevolutionButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!textInputLayout.getEditText().getText().toString().isEmpty()) {
                    devolutionRequest.description = textInputLayout.getEditText().getText().toString();
                    if (optionsDevolutions != null && optionsDevolutions.isShowing()) {
                        optionsDevolutions.dismiss();
                    }
                    devolutionDescriptionModal.dismiss();
                    confirmAction(2, devolutionRequest);
                }
            }
        });
        devolutionDescriptionModal.show();
    }
    void saveOfflineDevolution(DevolutionRequest devolutionRequest){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DevolutionRequest devolutionRequest1 = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestPendingByFolio(devolutionRequest.folio);
                if(devolutionRequest1==null){
                    viewModelStore.getAppDatabase().devolutionRequestDao().insertAll(devolutionRequest);
                    DevolutionRequest devolutionRequest2 = viewModelStore.getAppDatabase().devolutionRequestDao().findDevolutionRequestByFolio(devolutionRequest.folio);
                    for(SubSale subSale : subSalesEntities){
                        DevolutionSubSale devolutionSubSale = new DevolutionSubSale();
                        devolutionSubSale.devolutionRequestId=devolutionRequest2.devolutionRequestId;
                        devolutionSubSale.presentationId=subSale.presentationId;
                        devolutionSubSale.price=(subSale.price/subSale.quantity)*mapSubSales.get(subSale.subSaleId);
                        devolutionSubSale.productKey=subSale.productKey;
                        devolutionSubSale.productId=subSale.productId;
                        devolutionSubSale.productName=subSale.productName;
                        devolutionSubSale.quantity=mapSubSales.get(subSale.subSaleId);
                        devolutionSubSale.subSaleId=subSale.subSaleId;
                        devolutionSubSale.uniMed=subSale.uniMed;
                        devolutionSubSale.weightStandar=subSale.weightStandar;
                        viewModelStore.getAppDatabase().devolutionSubSaleDao().insertAll(devolutionSubSale);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Registro de devoluciones");
                        if(devolutionRequest1==null){
                            builder.setMessage("Devolución registrada con exito.");
                        }else{
                            builder.setMessage("Ya existe una devolucion pendiente de esa venta.");
                        }
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(devolutionRequest1==null){
                                    cancelDevolution();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setTextColor(Color.BLACK);
                    }
                });
            }
        });
    }
}
