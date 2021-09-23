package com.example.ventasrovianda.home.presenter;

import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.SaleDTO;

import java.util.List;

public interface HomePresenterContract {
    void doLogout();

    //void findUser(Integer userId);

    //void findProduct(String code);

    void doSale(SaleDTO saleDTO);

    void getEndDayTicket();

    //void getCounterTimer(int intent);

    //void startEatTime();

    //void endEatTime();
    void getStockOnline();
    //void getStockOnline();

    //void UploadChanges(ModeOfflineSincronize ModeOfflineSincronize);

    void sincronizeSales(List<ModeOfflineSM> ModeOfflineSMS, List<DebPayedRequest> debtsPayedRequest, List<DevolutionRequestServer> devolutionRequestServers,String sellerId);
}
