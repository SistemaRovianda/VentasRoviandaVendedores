package com.example.ventasrovianda.cotizaciones.models;

public class SaleCreditPayedResponse {
    private String folio;
    private boolean payed;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }
}
