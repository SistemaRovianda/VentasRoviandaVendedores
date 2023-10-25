package com.example.ventasrovianda.login.view;


import android.content.Context;

import android.net.ConnectivityManager;
import android.net.Network;

import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import androidx.navigation.fragment.NavHostFragment;
import androidx.room.RoomDatabase;

import com.example.ventasrovianda.R;

import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.AppDatabase;
import com.example.ventasrovianda.Utils.bd.entities.UserDataInitial;
import com.example.ventasrovianda.login.presenter.LoginPresenter;
import com.example.ventasrovianda.login.presenter.LoginPresenterContract;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;


import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginView extends Fragment implements View.OnClickListener,LoginViewPresenter{

    MaterialButton login=null;
    ProgressBar progressBar;
    TextInputEditText emailInput,passwordInput;
    CheckBox seePassword;
    LoginPresenterContract presenter;
    ViewModelStore viewModelStore=null;
    Gson parser;
    Boolean isConnected=false;
    String currentPassword="";
    String currentPasswordCoded ="";
    Boolean isLoading=false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loginfragment,null);
        this.login = view.findViewById(R.id.Loginbutton);
        this.login.setOnClickListener(this);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.emailInput = view.findViewById(R.id.emailInput);
        this.passwordInput = view.findViewById(R.id.passwordInput);
        this.seePassword = view.findViewById(R.id.seePassword);
        presenter = new LoginPresenter(getContext(),this);
        this.parser= new Gson();

        this.passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("Secuence: "+charSequence.toString()+" show: "+seePassword.isActivated());
                String newValue = charSequence.toString();

                if(!seePassword.isChecked()) {
                    if (!newValue.equals(currentPasswordCoded)) {
                        if(newValue.length()>currentPasswordCoded.length()){
                            String valueParsed = "";
                            for(char letter : newValue.toCharArray()){
                                if(letter!='*'){
                                    valueParsed+=letter;
                                }
                            }
                            currentPassword+=valueParsed;
                            currentPasswordCoded = repeat("*",currentPassword.length());
                            System.out.println("UNCODED: "+currentPassword);
                            System.out.println("CODED: "+currentPasswordCoded);
                            passwordInput.setText(currentPasswordCoded);
                        }else{
                            if(newValue.length()>0) {
                                currentPassword = currentPassword.substring(0, newValue.length());
                                currentPasswordCoded = repeat("*", currentPassword.length());
                                passwordInput.setText(currentPasswordCoded);
                            }else{
                                currentPassword="";
                                currentPasswordCoded="";
                            }
                        }
                    }
                }else {
                    if(newValue.contains("*")){
                        passwordInput.setError("'*' no permitido en contraseña");
                        passwordInput.setText(currentPassword);
                    }else {
                        if (!newValue.equals(currentPassword)) {
                            currentPassword = newValue;
                            currentPasswordCoded = repeat("*", currentPassword.length());
                            passwordInput.setText(currentPassword);
                        }
                    }

                }
                passwordInput.setSelection(currentPassword.length());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        this.seePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                System.out.println("Checked: "+b);
                if(b){
                    passwordInput.setText(currentPassword);
                }else{
                    passwordInput.setText(currentPasswordCoded);
                }
            }
        });

        return view;
    }


    ConnectivityManager.NetworkCallback networkCallback = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    void checkConnection() {
        if (this.networkCallback != null) {
            this.unregisteredNetworkCallback();
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        this.networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isConnected = true;
                hasInternet = false;
                presenter.checkCommunicationToServer();
            }

            @Override
            public void onLost(@NonNull Network network) {
                isConnected = false;
                setStatusConnectionServer(false);
            }
        };
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void unregisteredNetworkCallback() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);
        networkCallback = null;
    }

    Boolean hasInternet = false;

    @Override
    public void setStatusConnectionServer(Boolean statusConnectionServer) {
        hasInternet = statusConnectionServer;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Loginbutton:

                if(isConnected && hasInternet) {
                    if(presenter.validateInputs()) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase conexion = AppDatabase.getInstance(getContext());
                                UserDataInitial userDataInitial = conexion.userDataInitialDao().getByEmailAndPasswordOffline("%" + emailInput.getText().toString() + "%", "%" + currentPassword + "%");
                                if (userDataInitial != null) {
                                    conexion.userDataInitialDao().updateAllLogedInTrue(userDataInitial.uid);
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (userDataInitial != null) {
                                            viewModelStore.getStore().setSellerId(userDataInitial.uid);
                                            goToHome(userDataInitial.name, userDataInitial.uid);
                                        } else {
                                            login.setVisibility(View.GONE);
                                            presenter.doLogin();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }else{
                    if(presenter.validateInputs()) {
                        checkCredentialsOffline(this.emailInput.getText().toString().trim(), currentPassword);
                    }
                }
                break;
        }
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    void checkCredentialsOffline(String email,String password){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial= conexion.userDataInitialDao().getByEmailAndPasswordOffline("%"+email+"%","%"+password+"%");
                if(userDataInitial!=null) {
                    conexion.userDataInitialDao().updateAllLogedInTrue(userDataInitial.uid);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(userDataInitial!=null){
                            viewModelStore.getStore().setSellerId(userDataInitial.uid);
                            goToHome(userDataInitial.name,userDataInitial.uid);
                        }else{
                            setEmailInputError("Conección a internet necesaria.");
                        }
                    }
                });
            }
        });
    }

    void checkIfAlreadyLogedIn(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<UserDataInitial> userDataInitial = conexion.userDataInitialDao().getAnyLogedIn();
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        if(userDataInitial.size()>0) {
                            goToHome(userDataInitial.get(0).name,userDataInitial.get(0).uid);
                        }else{
                            checkConnection();
                        }
                    }
                });
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        checkIfAlreadyLogedIn();
    }

    @Override
    public void setEmailInputError(String msg) {
        this.emailInput.setError(msg);
    }

    @Override
    public void setPasswordInputError(String msg) {
        this.passwordInput.setError(msg);
    }

    @Override
    public void goToHome(String nameUser,String uid){
        this.viewModelStore.getStore().setUsername(nameUser);
        this.viewModelStore.getStore().setSellerId(uid);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(LoginViewDirections.actionLoginViewToHomeView(nameUser).setUserName(nameUser));
    }

    @Override
    public void showErrors(String msg) {
        this.passwordInput.setError(msg);
    }


    public String repeat(String val, int count){
        StringBuilder buf = new StringBuilder(val.length() * count);
        while (count-- > 0) {
            buf.append(val);
        }
        return buf.toString();
    }

    @Override
    public String getEmailInputText() {
        return this.emailInput.getText().toString();
    }

    @Override
    public String getPasswordText() {
        return this.currentPassword;
    }

    @Override
    public void setStatusLogin(Boolean isLoading) {
        if(isLoading){
            login.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            login.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void disableButtonLogin(Boolean disable) {
        this.login.setActivated(!disable);
    }
}
