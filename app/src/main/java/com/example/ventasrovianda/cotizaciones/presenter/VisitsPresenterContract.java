package com.example.ventasrovianda.cotizaciones.presenter;

import com.example.ventasrovianda.Utils.Models.ClientDTO;
import com.example.ventasrovianda.Utils.Models.DebPayedRequest;
import com.example.ventasrovianda.Utils.Models.DevolutionRequestServer;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSM;
import com.example.ventasrovianda.Utils.Models.ModeOfflineSincronize;
import com.example.ventasrovianda.Utils.Models.ProductRovianda;

import java.util.List;

public interface VisitsPresenterContract {

    void getClientsVisits(String uid);

    void getPresentations(ProductRovianda productRovianda);

    void startVisit(int clientId, ClientDTO clientVisit);
    void endVisit(int clientId);

    void setClientVisit(ClientDTO clientVisit);
    void logout();

    //void getStockOnline();

    void UploadChanges(ModeOfflineSincronize ModeOfflineSincronize,String uid);
    void updateStatusSincronizedClient(List<Integer> clients);
    void getDataInitial(String sellerUid,String date);

    void sincronizeSales(List<ModeOfflineSM> ModeOfflineSMS, List<DebPayedRequest> debtsPayedRequest, List<DevolutionRequestServer> devolutionRequestServers,String sellerId);

}
