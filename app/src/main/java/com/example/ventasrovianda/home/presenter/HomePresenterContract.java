package com.example.ventasrovianda.home.presenter;

import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.SaleDTO;

import java.util.List;

public interface HomePresenterContract {
    void doLogout();
    void checkCommunicationToServer();
    void sincronizeSales(List<ModeOfflineSM> ModeOfflineSMS, List<DebPayedRequest> debtsPayedRequest, List<DevolutionRequestServer> devolutionRequestServers,List<String> foliosWithoutPayment,String sellerId);
    void sendEndDayRecord(String date,String uid);
    void getAddressByCoordenates(Double latitude,Double longitude);
}
