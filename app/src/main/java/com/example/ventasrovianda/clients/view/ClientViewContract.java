package com.example.ventasrovianda.clients.view;

import com.example.ventasrovianda.Utils.Models.ClientDTO;

import java.util.List;

public interface ClientViewContract {

    void setClients(List<ClientDTO> clients);
    void goToLogin();
}
