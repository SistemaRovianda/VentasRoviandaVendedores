package com.example.ventasrovianda.Utils.Models;

public class InventoryOfflineMode {

    private String codeSae;
    private String productName;
    private String uniMed;
    private Float weight;
    private Float pieces;
    private Float price;
    private String presentation;
    private Integer presentationId;
    private Float weightOriginal;
    private Integer productId;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Float getWeightOriginal() {
        return weightOriginal;
    }

    public void setWeightOriginal(Float weightOriginal) {
        this.weightOriginal = weightOriginal;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(Integer presentationId) {
        this.presentationId = presentationId;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getCodeSae() {
        return codeSae;
    }

    public void setCodeSae(String codeSae) {
        this.codeSae = codeSae;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUniMed() {
        return uniMed;
    }

    public void setUniMed(String uniMed) {
        this.uniMed = uniMed;
    }

    public Float getPieces() {
        return pieces;
    }

    public void setPieces(Float pieces) {
        this.pieces = pieces;
    }
}
