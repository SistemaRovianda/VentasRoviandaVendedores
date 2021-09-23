package com.example.ventasrovianda.Utils.Models;

public class DebPayedRequest {

    private String folio;
    private String payedType;
    private String datePayed;
    private Float amountPayed;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getPayedType() {
        return payedType;
    }

    public void setPayedType(String payedType) {
        this.payedType = payedType;
    }

    public String getDatePayed() {
        return datePayed;
    }

    public void setDatePayed(String datePayed) {
        this.datePayed = datePayed;
    }

    public Float getAmountPayed() {
        return amountPayed;
    }

    public void setAmountPayed(Float amountPayed) {
        this.amountPayed = amountPayed;
    }
}
