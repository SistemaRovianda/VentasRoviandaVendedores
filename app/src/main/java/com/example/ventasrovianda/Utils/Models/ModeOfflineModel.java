package com.example.ventasrovianda.Utils.Models;

import java.util.ArrayList;
import java.util.List;

public class ModeOfflineModel {

    public ModeOfflineModel(){
        this.logedIn=false;
    }

    private Boolean logedIn;
    private String username;
    private String lastSincronization;
    private String sellerId;
    private Double cashAcumulated;
    private Double WeightSolded;
    private List<InventoryOfflineMode> inventory;
    private String folioNomenclature;
    private Integer folioCount;
    private List<SaleOfflineMode> sales;
    private List<SaleOfflineMode> debts;
    private List<ClientOfflineMode> clients;
    private List<ClientOfflineMode> clientsToVisit;
    private List<SaleDTO> salesMaked;


    public List<ClientOfflineMode> getClientsToVisit() {
        return clientsToVisit;
    }

    public void setClientsToVisit(List<ClientOfflineMode> clientsToVisit) {
        this.clientsToVisit = clientsToVisit;
    }

    public List<SaleDTO> getSalesMaked() {
        return salesMaked;
    }

    public void setSalesMaked(List<SaleDTO> salesMaked) {
        this.salesMaked = salesMaked;
    }

    public Boolean getLogedIn() {
        return logedIn;
    }

    public void setLogedIn(Boolean logedIn) {
        this.logedIn = logedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastSincronization() {
        return lastSincronization;
    }

    public void setLastSincronization(String lastSincronization) {
        this.lastSincronization = lastSincronization;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Double getCashAcumulated() {
        return cashAcumulated;
    }

    public void setCashAcumulated(Double cashAcumulated) {
        this.cashAcumulated = cashAcumulated;
    }

    public Double getWeightSolded() {
        return WeightSolded;
    }

    public void setWeightSolded(Double weightSolded) {
        WeightSolded = weightSolded;
    }

    public List<InventoryOfflineMode> getInventory() {
        return inventory;
    }

    public void setInventory(List<InventoryOfflineMode> inventory) {
        this.inventory = inventory;
    }

    public String getFolioNomenclature() {
        return folioNomenclature;
    }

    public void setFolioNomenclature(String folioNomenclature) {
        this.folioNomenclature = folioNomenclature;
    }

    public Integer getFolioCount() {
        return folioCount;
    }

    public void setFolioCount(Integer folioCount) {
        this.folioCount = folioCount;
    }

    public List<SaleOfflineMode> getSales() {
        return sales;
    }

    public void setSales(List<SaleOfflineMode> sales) {
        this.sales = sales;
    }

    public List<SaleOfflineMode> getDebts() {
        return debts;
    }

    public void setDebts(List<SaleOfflineMode> debts) {
        this.debts = debts;
    }

    public List<ClientOfflineMode> getClients() {
        return clients;
    }

    public void setClients(List<ClientOfflineMode> clients) {
        this.clients = clients;
    }
}
