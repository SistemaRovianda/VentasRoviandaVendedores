package com.example.tiendas.clients.presenter;

import com.example.tiendas.Utils.Models.ClientModel;
import com.example.tiendas.Utils.Models.DaysVisited;

public interface RegisterClientPresenterContract {

    void registClient(ClientModel clientModel, DaysVisited daysVisited);
    void getCurrentCountClient();
}
