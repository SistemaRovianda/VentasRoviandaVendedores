package com.example.tiendas.Utils.Models;

public class OrderPresentationDetails {

    private int subOrderId;
    private  int  productId;
    private Float units;
    private int presentationId;
    private int presentation;
    private String  typePresentation;
    private Float pricePresentationPublic;
    private Float pricePresentationMin;
    private Float pricePresentationLiquidation;

    public int getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(int subOrderId) {
        this.subOrderId = subOrderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Float getUnits() {
        return units;
    }

    public void setUnits(Float units) {
        this.units = units;
    }

    public int getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(int presentationId) {
        this.presentationId = presentationId;
    }

    public int getPresentation() {
        return presentation;
    }

    public void setPresentation(int presentation) {
        this.presentation = presentation;
    }

    public String getTypePresentation() {
        return typePresentation;
    }

    public void setTypePresentation(String typePresentation) {
        this.typePresentation = typePresentation;
    }

    public Float getPricePresentationPublic() {
        return pricePresentationPublic;
    }

    public void setPricePresentationPublic(Float pricePresentationPublic) {
        this.pricePresentationPublic = pricePresentationPublic;
    }

    public Float getPricePresentationMin() {
        return pricePresentationMin;
    }

    public void setPricePresentationMin(Float pricePresentationMin) {
        this.pricePresentationMin = pricePresentationMin;
    }

    public Float getPricePresentationLiquidation() {
        return pricePresentationLiquidation;
    }

    public void setPricePresentationLiquidation(Float pricePresentationLiquidation) {
        this.pricePresentationLiquidation = pricePresentationLiquidation;
    }
}
