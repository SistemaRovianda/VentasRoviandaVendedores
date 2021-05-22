package com.example.ventasrovianda.login.view;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ventasrovianda.R;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SaleOfflineMode;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.login.presenter.LoginPresenter;
import com.example.ventasrovianda.login.presenter.LoginPresenterContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LoginView extends Fragment implements View.OnClickListener,LoginViewPresenter{

    MaterialButton login=null;
    CircularProgressIndicator loadingSpinner;
    TextInputEditText emailInput,passwordInput;
    LoginPresenterContract presenter;
    ViewModelStore viewModelStore=null;
    Gson parser;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loginfragment,null);
        this.login = view.findViewById(R.id.Loginbutton);
        this.login.setOnClickListener(this);
        this.loadingSpinner = view.findViewById(R.id.loginLoadingSpinner);
        this.emailInput = view.findViewById(R.id.emailInput);
        this.passwordInput = view.findViewById(R.id.passwordInput);
        presenter = new LoginPresenter(getContext(),this);
        this.parser= new Gson();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Loginbutton:
                this.login.setVisibility(View.GONE);
                this.loadingSpinner.setVisibility(View.VISIBLE);
                presenter.doLogin(this.emailInput.getText().toString(),this.passwordInput.getText().toString());
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void checkSincronizedDataExist() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateParsed = dateFormat.format(calendar.getTime());
        if(calendar.get(Calendar.DAY_OF_WEEK)==2) {
            calendar.add(Calendar.DATE, -2);
        }else{
            calendar.add(Calendar.DATE, -1);
        }
        String dateWihoutDay = dateFormat.format(calendar.getTime());

        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()+"/offline","offline-"+dateParsed+".rovi");
        File fileAlter = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()+"/offline","offline-"+dateWihoutDay+".rovi");
        System.out.println("File1Existe:"+file.exists());
        System.out.println("File2Existe:"+fileAlter.exists());
        System.out.println("Storage: "+viewModelStore.getStore().getClients()==null);
        if(file.exists()){
                String data = readFileFromPath(file);
                ModeOfflineModel modeOfflineData = this.parser.fromJson(data, ModeOfflineModel.class);
                if(modeOfflineData.getClients()!=null) {
                    viewModelStore.saveStore(modeOfflineData);
                }else{
                    viewModelStore.saveStore(null);
                }
        }else if(fileAlter.exists()){
            file.delete();
            String data2 = readFileFromPath(fileAlter);
            try {
                ModeOfflineModel modeOfflineData2 = this.parser.fromJson(data2, ModeOfflineModel.class);

                    viewModelStore.saveStore(modeOfflineData2);

                    Boolean modified = false;
                    if (modeOfflineData2 != null) {
                        if (modeOfflineData2.getSalesMaked() != null && modeOfflineData2.getSalesMaked().size() > 0) {
                            modified = true;
                        }
                        if (modeOfflineData2.getDebts() != null) {
                                    modified = true;
                        }
                        if (modeOfflineData2.getSales() != null) {
                            for (SaleOfflineMode saleOfflineMode : modeOfflineData2.getSales()) {
                                if (saleOfflineMode.getStatusStr().equals("CANCELED")) {
                                    modified = true;
                                }
                            }
                        }
                    }
                    if (modified) {
                        if(viewModelStore.getStore().getSales()==null){
                            viewModelStore.getStore().setSales(new ArrayList<>());
                        }

                        if(viewModelStore.getStore().getDebts()==null){
                            viewModelStore.getStore().setDebts(new ArrayList<>());
                        }

                        if(viewModelStore.getStore().getSalesMaked()==null){
                            viewModelStore.getStore().setSalesMaked(new ArrayList<>());
                        }
                        for (SaleOfflineMode item : modeOfflineData2.getSales()) {
                            viewModelStore.getStore().getSales().add(item);
                        }
                        for (SaleOfflineMode item : modeOfflineData2.getDebts()) {
                            viewModelStore.getStore().getDebts().add(item);
                        }
                        for (SaleDTO sale : modeOfflineData2.getSalesMaked()) {
                            viewModelStore.getStore().getSalesMaked().add(sale);
                        }
                    }

            }catch(Exception e){
                System.out.print("No se pudo leer el archivo secundario");
            }
        }else{
            fileAlter.delete();
        }
        if(viewModelStore.getStore()!=null && viewModelStore.getStore().getClients()!=null) {

            this.setModeOffline(viewModelStore.getStore());
            String uid = this.presenter.checkLoginStr();
            if (uid != null) {
                goToHome(viewModelStore.getStore().getUsername());
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setModeOffline(ModeOfflineModel modeOffline) {

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


    }

    @Override
    public String readFileFromPath(File file) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        checkSincronizedDataExist();
    }

    @Override
    public void setEmailInputError(String msg) {
        this.emailInput.setError(msg);
        this.loadingSpinner.setVisibility(View.INVISIBLE);
        this.login.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPasswordInputError(String msg) {
        this.passwordInput.setError(msg);
        this.loadingSpinner.setVisibility(View.INVISIBLE);
        this.login.setVisibility(View.VISIBLE);
    }

    @Override
    public void goToHome(String nameUser){
        this.viewModelStore.getStore().setUsername(nameUser);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(LoginViewDirections.actionLoginViewToHomeView(nameUser).setUserName(nameUser));
    }

    @Override
    public void showErrors(String msg) {
        this.passwordInput.setError(msg);
        this.login.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.INVISIBLE);
    }




}
