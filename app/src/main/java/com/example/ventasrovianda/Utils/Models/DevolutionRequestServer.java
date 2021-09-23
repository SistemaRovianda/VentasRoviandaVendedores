package com.example.ventasrovianda.Utils.Models;

import java.util.List;

public class DevolutionRequestServer {
    private Integer devolutionId;
    private String typeDevolution;
    private String observations;
    private String folio;
    private String createAt;
    private List<DevolutionSubSaleRequestServer> productsNew;
    private List<DevolutionSubSaleRequestServer> productsOld;

    public Integer getDevolutionId() {
        return devolutionId;
    }

    public void setDevolutionId(Integer devolutionId) {
        this.devolutionId = devolutionId;
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

    public List<DevolutionSubSaleRequestServer> getProductsNew() {
        return productsNew;
    }

    public void setProductsNew(List<DevolutionSubSaleRequestServer> productsNew) {
        this.productsNew = productsNew;
    }

    public List<DevolutionSubSaleRequestServer> getProductsOld() {
        return productsOld;
    }

    public void setProductsOld(List<DevolutionSubSaleRequestServer> productsOld) {
        this.productsOld = productsOld;
    }
}
