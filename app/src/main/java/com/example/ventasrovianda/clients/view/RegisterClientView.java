package com.example.ventasrovianda.clients.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.BluetoothDeviceSerializable;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.ClientModel;
import com.example.ventasrovianda.Utils.Models.DaysVisited;
import com.example.ventasrovianda.clients.presenter.RegisterClientPresenter;
import com.example.ventasrovianda.clients.presenter.RegisterClientPresenterContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterClientView extends Fragment implements View.OnClickListener,RegisterClientViewContract {

    MaterialButton cancelarRegistro,submitForm;
    CircularProgressIndicator spinner;
    NavController navController;

    TextInputLayout nombre,clave,telefono,rfc,calle,noExt,noInt,entreCalle,yCalle,colonia,referencia,localidad,municipio,cp,estado;
    RegisterClientPresenterContract presenter;
    ClientDTO clientDTO =null;

    BluetoothDeviceSerializable bluetoothDeviceSerializable=null;

    String userName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_register_form,null);
        this.clientDTO = RegisterClientViewArgs.fromBundle(getArguments()).getClientInVisit();
        this.userName = RegisterClientViewArgs.fromBundle(getArguments()).getUserName();
        this.cancelarRegistro = view.findViewById(R.id.cancelarRegistroCliente);
        this.cancelarRegistro.setOnClickListener(this);
        this.navController = NavHostFragment.findNavController(this);
        this.submitForm=view.findViewById(R.id.submit);
        this.submitForm.setOnClickListener(this);
        this.nombre=view.findViewById(R.id.nombreCliente);
        this.clave = view.findViewById(R.id.claveCliente);
        this.telefono = view.findViewById(R.id.telefono);
        this.rfc=view.findViewById(R.id.rfcCliente);
        this.calle = view.findViewById(R.id.calle);
        this.noExt = view.findViewById(R.id.noExterior);
        this.noInt = view.findViewById(R.id.noInterior);
        this.entreCalle = view.findViewById(R.id.entreCalle);
        this.yCalle = view.findViewById(R.id.yCalle);
        this.colonia = view.findViewById(R.id.colonia);
        this.referencia = view.findViewById(R.id.referencia);
        this.localidad = view.findViewById(R.id.localidad);
        this.municipio = view.findViewById(R.id.municipio);
        this.cp = view.findViewById(R.id.cp);
        this.estado = view.findViewById(R.id.estado);
        this.presenter = new RegisterClientPresenter(getContext(),this);
        this.presenter.getCurrentCountClient();
        this.spinner = view.findViewById(R.id.registerClientLoadingSpinner);
        this.navController = NavHostFragment.findNavController(this);
        bluetoothDeviceSerializable = RegisterClientViewArgs.fromBundle(getArguments()).getPrinterDevice();

        this.telefono.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Long.parseLong(s.toString());
                }catch (NumberFormatException e){
                    setTelefonoError("Solo se admiten numeros");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelarRegistroCliente:
                goToClientView();
                break;
            case R.id.submit:
                this.spinner.setVisibility(View.VISIBLE);
                this.submitForm.setVisibility(View.GONE);
                showOptionsDays();
                break;
        }
    }

    void goToClientView(){
        this.navController.navigate(RegisterClientViewDirections.actionRegisterClientViewToClientView(this.userName).setUserName(this.userName).setClientInVisit(this.clientDTO).setPrinterDevice(this.bluetoothDeviceSerializable));
    }

    @Override
    public void setNombreError(String msg) {
        this.nombre.getEditText().setError(msg);
    }

    @Override
    public void setClaveError(String msg) {
        this.clave.getEditText().setError(msg);
    }

    @Override
    public void setTelefonoError(String msg) {
        this.telefono.getEditText().setError(msg);
    }

    @Override
    public void setRfcError(String msg) {
        this.rfc.getEditText().setError(msg);
    }

    @Override
    public void setCalleError(String msg) {
        this.calle.getEditText().setError(msg);
    }

    @Override
    public void setNoExtError(String msg) {
        this.noExt.getEditText().setError(msg);
    }

    @Override
    public void setNoIntError(String msg) {
        this.noInt.getEditText().setError(msg);
    }

    @Override
    public void setExtreCalleError(String msg) {
        this.entreCalle.getEditText().setError(msg);
    }

    @Override
    public void setYCalleError(String msg) {
        this.yCalle.getEditText().setError(msg);
    }


    @Override
    public void setColoniaError(String msg) {
        this.colonia.getEditText().setError(msg);
    }

    @Override
    public void setReferenciaError(String msg) {
        this.referencia.getEditText().setError(msg);
    }

    @Override
    public void setLocalicadError(String msg) {
        this.localidad.getEditText().setError(msg);
    }

    @Override
    public void setMunicipioError(String msg) {
        this.municipio.getEditText().setError(msg);
    }

    @Override
    public void setCpError(String msg) {
        this.cp.getEditText().setError(msg);
    }

    @Override
    public void setEstadoError(String msg) {
        this.estado.getEditText().setError(msg);
    }

    void buildClient(boolean[] daysSeleted){
        ClientModel clientModel = new ClientModel();
        clientModel.setNombre(this.nombre.getEditText().getText().toString().trim());
        clientModel.setClave(this.clave.getEditText().getText().toString().trim());
        clientModel.setTelefono(this.telefono.getEditText().getText().toString().trim());
        clientModel.setRfc(this.rfc.getEditText().getText().toString().trim());
        clientModel.setCalle(this.calle.getEditText().getText().toString().trim());
        clientModel.setNoExt(this.noExt.getEditText().getText().toString().trim());
        clientModel.setNoInt(this.noInt.getEditText().getText().toString().trim());
        clientModel.setEntreCalle(this.entreCalle.getEditText().getText().toString().trim());
        clientModel.setyCalle(this.yCalle.getEditText().getText().toString().trim());
        clientModel.setPoblacion("");
        clientModel.setColonia(this.colonia.getEditText().getText().toString().trim());
        clientModel.setReferencia(this.referencia.getEditText().getText().toString().trim());
        clientModel.setLocalidad(this.localidad.getEditText().getText().toString().trim());
        clientModel.setMunicipio(this.municipio.getEditText().getText().toString().trim());
        clientModel.setCp(this.cp.getEditText().getText().toString().trim());
        clientModel.setEstado(this.estado.getEditText().getText().toString().trim());
        if(this.rfc.getEditText().getText().toString().isEmpty()) {
            clientModel.setTypeClient(1);
        }else{
            clientModel.setTypeClient(0);
        }
        DaysVisited daysVisited = new DaysVisited();
        daysVisited.assingDays(daysSeleted);
        this.presenter.registClient(clientModel,daysVisited);
    }

    @Override
    public void registroCompleto(String msg) {
        this.spinner.setVisibility(View.GONE);
        this.submitForm.setVisibility(View.VISIBLE);
        System.out.println("Registro completo");
        if(msg==null) {
            this.goToClientView();
        }else{
            this.genericMessage("Reasignacion de clave","Un vendedor acaba de registrar esa clave de cliente, se te reasigno a tu cliente la clave: "+msg);
        }

    }

    @Override
    public void registroFallido() {
        this.spinner.setVisibility(View.GONE);
        this.submitForm.setVisibility(View.VISIBLE);
        System.out.println("Registro fallido");
    }

    @Override
    public void setKeyClientText(String msg) {
        this.clave.getEditText().setText(msg);
    }
    @Override
    public void genericMessage(String title,String msg){
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext()).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToClientView();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).setCancelable(false).create();
        dialog.show();
    }

    String[] daysVisitedOptions = {"Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"};

    void showOptionsDays(){
        boolean[] selected ={false,false,false,false,false,false,false};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogDays).setTitle("Seleccione los dias de visita.")
                    .setCancelable(false)
                    .setPositiveButton("Registrar", null)
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            spinner.setVisibility(View.GONE);
                            submitForm.setVisibility(View.VISIBLE);
                        }
                    }).setMultiChoiceItems(daysVisitedOptions, selected, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            System.out.println("Se selecciono un día: "+which);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    boolean anySelected =false;
                    for(boolean day : selected) {
                        if(day==true){
                            anySelected=true;
                        }
                        System.out.println(day);
                    }


                    if(anySelected==false){
                        Toast.makeText(getContext(),"Debes seleccionar minimo un día",Toast.LENGTH_SHORT).show();
                    }else {
                        dialog.dismiss();
                        //selectTypeClient(selected);
                        buildClient(selected);
                    }
                }
            });
        }
        /*int selectedTypeClient =0;
        void selectTypeClient(boolean[] selectionsDays){
        String[] typeClient = {"FACTURA","NO FACTURA/PÚBLICO GENERAL"};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogDays2).setTitle("Selecciona el tipo de cliente")
                    .setSingleChoiceItems(typeClient, selectedTypeClient, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedTypeClient=which;
                            System.out.println("Prosigue registro: "+selectedTypeClient);
                        }
                    }).setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Prosigue registro: "+selectedTypeClient);
                            //buildClient(selectionsDays,selectedTypeClient);
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Se cancelo");
                            spinner.setVisibility(View.GONE);
                            submitForm.setVisibility(View.VISIBLE);
                        }
                    }).setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
        }*/
}


