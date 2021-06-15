package com.example.tiendas.Utils.Models;

import java.util.List;

public class DebtsOfflineMode {

    private String folio;
    private Integer keyClient;
    private List<InventoryOfflineMode> products;
    private Double payed;
    private Double amount;
    private Double credit;
    private String typeSale;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(Integer keyClient) {
        this.keyClient = keyClient;
    }

    public List<InventoryOfflineMode> getProducts() {
        return products;
    }

    public void setProducts(List<InventoryOfflineMode> products) {
        this.products = products;
    }

    public Double getPayed() {
        return payed;
    }

    public void setPayed(Double payed) {
        this.payed = payed;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getTypeSale() {
        return typeSale;
    }

    public void setTypeSale(String typeSale) {
        this.typeSale = typeSale;
    }
}
