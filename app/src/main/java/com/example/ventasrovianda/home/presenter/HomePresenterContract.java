package com.example.ventasrovianda.home.presenter;

import com.example.ventasrovianda.Utils.Models.CounterTime;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.SaleDTO;

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
