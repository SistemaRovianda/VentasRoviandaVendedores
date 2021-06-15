package com.example.tiendas.Utils.Models;

public class ClientModelRequest {

    public ClientModelRequest(){
        this.curp=null;
    }

    private Integer keyClient;
    private String name;
    private String rfc;
    private String phone;
    private String saleUid;
    private AddressClient addressClient;
    private String curp;
    private DaysVisited daysVisited;
    private int typeClient;

    public int getTypeClient() {
        return typeClient;
    }

    public void setTypeClient(int typeClient) {
        this.typeClient = typeClient;
    }

    public DaysVisited getDaysVisited() {
        return daysVisited;
    }

    public void setDaysVisited(DaysVisited daysVisited) {
        this.daysVisited = daysVisited;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public Integer getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(Integer keyClient) {
        this.keyClient = keyClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSaleUid() {
        return saleUid;
    }

    public void setSaleUid(String saleUid) {
        this.saleUid = saleUid;
    }

    public AddressClient getAddressClient() {
        return addressClient;
    }

    public void setAddressClient(AddressClient addressClient) {
        this.addressClient = addressClient;
    }
}
