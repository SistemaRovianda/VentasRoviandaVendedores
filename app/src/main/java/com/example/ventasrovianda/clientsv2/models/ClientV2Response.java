package com.example.ventasrovianda.clientsv2.models;

import java.io.Serializable;

public class ClientV2Response implements Serializable {

    private Integer clientId;
    private Integer clientMobileId;

    public Integer getClientMobileId() {
        return clientMobileId;
    }

    public void setClientMobileId(Integer clientMobileId) {
        this.clientMobileId = clientMobileId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
}
