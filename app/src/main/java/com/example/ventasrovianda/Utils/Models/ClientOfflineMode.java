package com.example.ventasrovianda.Utils.Models;

public class ClientOfflineMode {

    private String keyClient;
    private String clientName;
    private String rfc;
    private String type;
    private Integer clientId;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(String keyClient) {
        this.keyClient = keyClient;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
