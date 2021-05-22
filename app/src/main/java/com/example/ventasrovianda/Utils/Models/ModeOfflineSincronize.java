package com.example.ventasrovianda.Utils.Models;

import java.util.List;

public class ModeOfflineSincronize {

    private List<ModeOfflineSM> salesMaked;
    private List<ModeOfflineS> sales;
    private List<ModeOfflineDebts> debts;

    public List<ModeOfflineSM> getSalesMaked() {
        return salesMaked;
    }

    public void setSalesMaked(List<ModeOfflineSM> salesMaked) {
        this.salesMaked = salesMaked;
    }

    public List<ModeOfflineS> getSales() {
        return sales;
    }

    public void setSales(List<ModeOfflineS> sales) {
        this.sales = sales;
    }

    public List<ModeOfflineDebts> getDebts() {
        return debts;
    }

    public void setDebts(List<ModeOfflineDebts> debts) {
        this.debts = debts;
    }
}
