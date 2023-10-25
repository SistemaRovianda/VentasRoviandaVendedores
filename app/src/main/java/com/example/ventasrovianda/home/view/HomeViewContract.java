package com.example.ventasrovianda.home.view;

import android.bluetooth.BluetoothDevice;

import androidx.fragment.app.DialogFragment;

import com.example.ventasrovianda.Utils.Models.AddressCoordenatesResponse;
import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.CounterTime;
import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ProductRoviandaToSale;
import com.example.ventasrovianda.Utils.Models.SaleDTO;
import com.example.ventasrovianda.Utils.Models.SincronizationResponse;
import com.example.ventasrovianda.Utils.Models.SincronizeSingleSaleSuccess;

import java.util.List;
import java.util.Set;

public interface HomeViewContract {
    void goToLogin();

    void showErrorConnectingPrinter();
    void connectionPrinterSuccess();

    void setErrorClientInput(String msg);
    //void setClient(ClientDTO client);

    //void addProductToSaleCar(ProductRoviandaToSale productRoviandaToSale);

    void setErrorProductkeyInput(String msg);
    void setErrorProductWeightInput(String msg);

    void saleSuccess(String ticket);
    void saleError(String msg);
    //void setCounterTimer(CounterTime counterTimer);
    void genericMessage(String title,String msg);
    void setStatusConnectionServer(Boolean statusConnectionServer);
    //void setModeOffline(ModeOfflineModel modeOffline);
    //void setModeOffline(ModeOfflineModel modeOffline);
    //void dismissLoadModal();
    void showNotificationSincronization(String msg);
    void hiddeNotificationSincronizastion();
    //void markSincronizedSale(String folio, Integer saleId, List<ModeOfflineSM> ModeOfflineSMS, Integer index);

    void completeSincronzation(SincronizationResponse sincronizationResponse);

    public void onDialogPositiveClick(Double latitude,Double longitude);
    public void onDialogNegativeClick();
    public void setAddressForConfirm(AddressCoordenatesResponse addressForConfirm,Double latitude,Double longitude);
}
