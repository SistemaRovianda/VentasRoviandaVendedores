package com.example.ventasrovianda.Utils.bd.entities;

public class DevolutionSubSalesResponseInitData {


    private Float quantity;
    private String loteId;
    private Float amount;
    private String createAt;
    private Integer saleId;
    private Integer productId;
    private Integer presentationId;
    private Integer subSaleIdIdentifier;
    private Integer devolutionRequestId;

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getLoteId() {
        return loteId;
    }

    public void setLoteId(String loteId) {
        this.loteId = loteId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(Integer presentationId) {
        this.presentationId = presentationId;
    }

    public Integer getSubSaleIdIdentifier() {
        return subSaleIdIdentifier;
    }

    public void setSubSaleIdIdentifier(Integer subSaleIdIdentifier) {
        this.subSaleIdIdentifier = subSaleIdIdentifier;
    }

    public Integer getDevolutionRequestId() {
        return devolutionRequestId;
    }

    public void setDevolutionRequestId(Integer devolutionRequestId) {
        this.devolutionRequestId = devolutionRequestId;
    }
}
