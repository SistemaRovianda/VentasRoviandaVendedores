package com.example.ventasrovianda.Utils.Models;

import java.util.List;

public class ModeOfflineSM {

    private String folio;
    private String date;
    private Float amount;
    private Float payedWith;
    private Float credit;
    private String typeSale;
    private String statusStr;
    private Boolean status;
    private String sellerId;
    private Integer clientId;
    private List<ModeOfflineSMP> products;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getPayedWith() {
        return payedWith;
    }

    public void setPayedWith(Float payedWith) {
        this.payedWith = payedWith;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    public String getTypeSale() {
        return typeSale;
    }

    public void setTypeSale(String typeSale) {
        this.typeSale = typeSale;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public List<ModeOfflineSMP> getProducts() {
        return products;
    }

    public void setProducts(List<ModeOfflineSMP> products) {
        this.products = products;
    }
}
