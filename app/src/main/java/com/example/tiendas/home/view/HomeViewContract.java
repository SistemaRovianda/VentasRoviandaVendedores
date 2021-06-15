package com.example.tiendas.home.view;

import com.example.tiendas.Utils.Models.ClientDTO;
import com.example.tiendas.Utils.Models.CounterTime;
import com.example.tiendas.Utils.Models.ModeOfflineModel;
import com.example.tiendas.Utils.Models.ProductRoviandaToSale;

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
