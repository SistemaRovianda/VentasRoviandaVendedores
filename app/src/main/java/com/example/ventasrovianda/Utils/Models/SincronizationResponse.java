package com.example.ventasrovianda.Utils.Models;

import java.util.List;

public class SincronizationResponse {

    private List<SincronizeSingleSaleSuccess> salesSincronized;
    private List<String> debtsSicronized;
    private List<String> devolutionsSincronized;
    private List<String> devolutionsAccepted;
    private List<String> devolutionsRejected;

    public List<String> getDevolutionsSincronized() {
        return devolutionsSincronized;
    }

    public void setDevolutionsSincronized(List<String> devolutionsSincronized) {
        this.devolutionsSincronized = devolutionsSincronized;
    }

    public List<String> getDevolutionsAccepted() {
        return devolutionsAccepted;
    }

    public void setDevolutionsAccepted(List<String> devolutionsAccepted) {
        this.devolutionsAccepted = devolutionsAccepted;
    }

    public List<String> getDevolutionsRejected() {
        return devolutionsRejected;
    }

    public void setDevolutionsRejected(List<String> devolutionsRejected) {
        this.devolutionsRejected = devolutionsRejected;
    }

    public List<SincronizeSingleSaleSuccess> getSalesSincronized() {
        return salesSincronized;
    }

    public void setSalesSincronized(List<SincronizeSingleSaleSuccess> salesSincronized) {
        this.salesSincronized = salesSincronized;
    }

    public List<String> getDebtsSicronized() {
        return debtsSicronized;
    }

    public void setDebtsSicronized(List<String> debtsSicronized) {
        this.debtsSicronized = debtsSicronized;
    }
}
