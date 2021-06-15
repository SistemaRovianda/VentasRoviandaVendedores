package com.example.tiendas.Utils.Models;

import java.util.List;

public class SaleOfflineMode {
    private Integer saleId;
    private String folio;
    private String sellerId;
    private Long keyClient;
    private List<ProductsOfflineMode> products;
    private Float payed;
    private Float amount;
    private Float credit;
    private String typeSale;
    private String clientName;
    private String date;
    private Boolean status;
    private String statusStr;


    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Float getPayed() {
        return payed;
    }

    public void setPayed(Float payed) {
        this.payed = payed;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Long getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(Long keyClient) {
        this.keyClient = keyClient;
    }

    public List<ProductsOfflineMode> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsOfflineMode> products) {
        this.products = products;
    }





    public String getTypeSale() {
        return typeSale;
    }

    public void setTypeSale(String typeSale) {
        this.typeSale = typeSale;
    }
}
