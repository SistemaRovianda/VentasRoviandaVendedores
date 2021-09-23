package com.example.ventasrovianda.Utils.Models;

public class SaleResponseDTO {
    private Long saleId;
    private String date;
    private String hour;
    private Float amount;
    private String folio;
    private ClientDTO client;
    private String typeSale;
    private String statusStr;
    private Boolean status;
    private String cancelAutorized;
    private Integer devolutionid;
    private String devolutionStatus;

    public String getDevolutionStatus() {
        return devolutionStatus;
    }

    public void setDevolutionStatus(String devolutionStatus) {
        this.devolutionStatus = devolutionStatus;
    }

    public Integer getDevolutionid() {
        return devolutionid;
    }

    public void setDevolutionid(Integer devolutionid) {
        this.devolutionid = devolutionid;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getCancelAutorized() {
        return cancelAutorized;
    }

    public void setCancelAutorized(String cancelAutorized) {
        this.cancelAutorized = cancelAutorized;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getTypeSale() {
        return typeSale;
    }

    public void setTypeSale(String typeSale) {
        this.typeSale = typeSale;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }
}
