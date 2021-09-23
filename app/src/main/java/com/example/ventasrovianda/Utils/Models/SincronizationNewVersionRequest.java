package com.example.ventasrovianda.Utils.Models;

import java.util.List;

public class SincronizationNewVersionRequest {

    private List<ModeOfflineSM> sales;
    private List<DebPayedRequest> debts;
    private List<DevolutionRequestServer> devolutions;

    public List<DevolutionRequestServer> getDevolutions() {
        return devolutions;
    }

    public void setDevolutions(List<DevolutionRequestServer> devolutions) {
        this.devolutions = devolutions;
    }

    public List<ModeOfflineSM> getSales() {
        return sales;
    }

    public void setSales(List<ModeOfflineSM> sales) {
        this.sales = sales;
    }

    public List<DebPayedRequest> getDebts() {
        return debts;
    }

    public void setDebts(List<DebPayedRequest> debts) {
        this.debts = debts;
    }
}
