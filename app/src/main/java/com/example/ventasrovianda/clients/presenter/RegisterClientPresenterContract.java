package com.example.ventasrovianda.clients.presenter;

import com.example.ventasrovianda.Utils.Models.ClientModel;
import com.example.ventasrovianda.Utils.Models.DaysVisited;

public interface RegisterClientPresenterContract {

    void registClient(ClientModel clientModel, DaysVisited daysVisited);
    void getCurrentCountClient();
}
