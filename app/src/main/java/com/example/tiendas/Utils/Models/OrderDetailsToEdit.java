package com.example.tiendas.Utils.Models;

public class OrderDetailsToEdit {

    private Long subOrderId;
    private Integer quantity;
    private String name;
    private String presentation;
    private Integer outOfStock;

    public Integer getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(Integer outOfStock) {
        this.outOfStock = outOfStock;
    }

    public Long getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(Long subOrderId) {
        this.subOrderId = subOrderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }
}
