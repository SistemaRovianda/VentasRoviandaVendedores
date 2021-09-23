package com.example.ventasrovianda.Utils.Models;

import com.example.ventasrovianda.Utils.bd.entities.DevolutionResponseInitData;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSalesResponseInitData;

import java.util.List;

public class ModeOfflineNewVersion {

    private List<ClientToSaveEntity> clients;
    private List<ProductToSaveEntity> products;
    private String uid;
    private Integer count;
    private String nomenclature;
    private String name;
    private String lastSicronization;
    private String email;
    private String password;
    private List<DebtOfflineNewVersion> debts;
    private List<DebtOfflineNewVersion> salesOfDay;
    private List<DevolutionResponseInitData> devolutionsRequest;
    private List<DevolutionSubSalesResponseInitData> devolutionsSubSales;

    public List<DevolutionResponseInitData> getDevolutionsRequest() {
        return devolutionsRequest;
    }

    public void setDevolutionsRequest(List<DevolutionResponseInitData> devolutionsRequest) {
        this.devolutionsRequest = devolutionsRequest;
    }

    public List<DevolutionSubSalesResponseInitData> getDevolutionsSubSales() {
        return devolutionsSubSales;
    }

    public void setDevolutionsSubSales(List<DevolutionSubSalesResponseInitData> devolutionsSubSales) {
        this.devolutionsSubSales = devolutionsSubSales;
    }

    public List<DebtOfflineNewVersion> getSalesOfDay() {
        return salesOfDay;
    }

    public void setSalesOfDay(List<DebtOfflineNewVersion> salesOfDay) {
        this.salesOfDay = salesOfDay;
    }

    public List<DebtOfflineNewVersion> getDebts() {
        return debts;
    }

    public void setDebts(List<DebtOfflineNewVersion> debts) {
        this.debts = debts;
    }

    public List<ClientToSaveEntity> getClients() {
        return clients;
    }

    public void setClients(List<ClientToSaveEntity> clients) {
        this.clients = clients;
    }

    public List<ProductToSaveEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductToSaveEntity> products) {
        this.products = products;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNomenclature() {
        return nomenclature;
    }

    public void setNomenclature(String nomenclature) {
        this.nomenclature = nomenclature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastSicronization() {
        return lastSicronization;
    }

    public void setLastSicronization(String lastSicronization) {
        this.lastSicronization = lastSicronization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
