package com.example.ventasrovianda.home.view;

import android.bluetooth.BluetoothDevice;

import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.CounterTime;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;

import java.util.Set;

public interface HomeViewContract {
    void goToLogin();

    void showErrorConnectingPrinter();
    void connectionPrinterSuccess();

    void setErrorClientInput(String msg);
    void setClient(ClientDTO client);

    void addProductToSaleCar(ProductRoviandaToSale productRoviandaToSale);

    void setErrorProductkeyInput(String msg);
    void setErrorProductWeightInput(String msg);

    void saleSuccess(String ticket);
    void saleError(String msg);
    void setCounterTimer(CounterTime counterTimer);
    void genericMessage(String title,String msg);
    void confirmEatTime();

    void isCountingTime(int typeUsed);

    void sincronizeComplete();
    void sincronizeError();
    void setModeOffline(ModeOfflineModel modeOffline);
    //void setModeOffline(ModeOfflineModel modeOffline);
    void dismissLoadModal();
    boolean isNetworkAvailable();
}
