package com.example.ventasrovianda.Utils.bd.entities;

public class DevolutionResponseInitData {


    private String sellerId;
    private Integer saleId;
    private String folio;
    private String createAt;
    private String status;
    private String dateAttended;
    private String typeDevolution;
    private String observations;
    private Integer devolutionAppRequestId;


    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateAttended() {
        return dateAttended;
    }

    public void setDateAttended(String dateAttended) {
        this.dateAttended = dateAttended;
    }

    public String getTypeDevolution() {
        return typeDevolution;
    }

    public void setTypeDevolution(String typeDevolution) {
        this.typeDevolution = typeDevolution;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Integer getDevolutionAppRequestId() {
        return devolutionAppRequestId;
    }

    public void setDevolutionAppRequestId(Integer devolutionAppRequestId) {
        this.devolutionAppRequestId = devolutionAppRequestId;
    }
}
