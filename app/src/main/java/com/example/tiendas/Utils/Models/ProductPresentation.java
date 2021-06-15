package com.example.tiendas.Utils.Models;

public class ProductPresentation {

    private int id;
    private int presentation;
    private String presentationType;
    private float presentationPricePublic;
    private float presentationPriceMin;
    private float presentationPriceLiquidation;
    private boolean status;
    private String typePrice;
    private String keySae;
    private boolean isPz;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPresentation() {
        return presentation;
    }

    public void setPresentation(int presentation) {
        this.presentation = presentation;
    }

    public String getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(String presentationType) {
        this.presentationType = presentationType;
    }

    public float getPresentationPricePublic() {
        return presentationPricePublic;
    }

    public void setPresentationPricePublic(float presentationPricePublic) {
        this.presentationPricePublic = presentationPricePublic;
    }

    public float getPresentationPriceMin() {
        return presentationPriceMin;
    }

    public void setPresentationPriceMin(float presentationPriceMin) {
        this.presentationPriceMin = presentationPriceMin;
    }

    public float getPresentationPriceLiquidation() {
        return presentationPriceLiquidation;
    }

    public void setPresentationPriceLiquidation(float presentationPriceLiquidation) {
        this.presentationPriceLiquidation = presentationPriceLiquidation;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTypePrice() {
        return typePrice;
    }

    public void setTypePrice(String typePrice) {
        this.typePrice = typePrice;
    }

    public String getKeySae() {
        return keySae;
    }

    public void setKeySae(String keySae) {
        this.keySae = keySae;
    }

    public boolean isPz() {
        return isPz;
    }

    public void setPz(boolean pz) {
        isPz = pz;
    }
}
