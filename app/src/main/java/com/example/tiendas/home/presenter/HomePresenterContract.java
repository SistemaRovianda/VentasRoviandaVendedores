package com.example.tiendas.home.presenter;

import com.example.tiendas.Utils.Models.ModeOfflineSincronize;
import com.example.tiendas.Utils.Models.SaleDTO;

public interface HomePresenterContract {
    void doLogout();

    void findUser(Integer userId);

    void findProduct(String code);

    void doSale(SaleDTO saleDTO);

    void getEndDayTicket();

    void getCounterTimer(int intent);

    void startEatTime();

    void endEatTime();
    void getStockOnline();
    //void getStockOnline();

    void UploadChanges(ModeOfflineSincronize ModeOfflineSincronize);
}
