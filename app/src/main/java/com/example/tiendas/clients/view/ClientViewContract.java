package com.example.tiendas.clients.view;

import com.example.tiendas.Utils.Models.ClientDTO;

import java.util.List;

public interface ClientViewContract {

    void setClients(List<ClientDTO> clients);
    void goToLogin();
}
