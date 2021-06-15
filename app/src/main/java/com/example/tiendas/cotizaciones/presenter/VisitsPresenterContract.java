package com.example.tiendas.cotizaciones.presenter;

import com.example.tiendas.Utils.Models.ClientDTO;
import com.example.tiendas.Utils.Models.ModeOfflineSincronize;
import com.example.tiendas.Utils.Models.ProductRovianda;

public interface VisitsPresenterContract {

    void getClientsVisits();

    void getPresentations(ProductRovianda productRovianda);

    void startVisit(int clientId, ClientDTO clientVisit);
    void endVisit(int clientId);

    void setClientVisit(ClientDTO clientVisit);
    void logout();

    void getStockOnline();

    void UploadChanges(ModeOfflineSincronize ModeOfflineSincronize);
}
