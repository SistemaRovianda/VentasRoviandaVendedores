package com.example.ventasrovianda.Utils.Models;

public class OrderToSendDTO {

    private String keySae;
    private Float quantity;
    private String observations;
    private Boolean outOfStock;

    public Boolean getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(Boolean outOfStock) {
        this.outOfStock = outOfStock;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getKeySae() {
        return keySae;
    }

    public void setKeySae(String keySae) {
        this.keySae = keySae;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }
}
