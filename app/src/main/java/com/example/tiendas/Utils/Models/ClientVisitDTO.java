package com.example.tiendas.Utils.Models;

import com.example.tiendas.Utils.enums.ClientVisitStatus;

public class ClientVisitDTO {

    private ClientDTO client;
    private int clientVisitId;
    private ClientVisitStatus visitedStatus;

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public int getClientVisitId() {
        return clientVisitId;
    }

    public void setClientVisitId(int clientVisitId) {
        this.clientVisitId = clientVisitId;
    }

    public ClientVisitStatus getVisitedStatus() {
        return visitedStatus;
    }

    public void setVisitedStatus(ClientVisitStatus visitedStatus) {
        this.visitedStatus = visitedStatus;
    }
}
