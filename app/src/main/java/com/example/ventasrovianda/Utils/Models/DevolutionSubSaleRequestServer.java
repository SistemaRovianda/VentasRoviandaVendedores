package com.example.ventasrovianda.Utils.Models;

public class DevolutionSubSaleRequestServer {
    private Integer appSubSaleId;
    private Integer productId;
    private Integer presentationId;
    private Float quantity;
    private Float amount;
    private String createAt;

    public Integer getAppSubSaleId() {
        return appSubSaleId;
    }

    public void setAppSubSaleId(Integer appSubSaleId) {
        this.appSubSaleId = appSubSaleId;
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

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
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
}
