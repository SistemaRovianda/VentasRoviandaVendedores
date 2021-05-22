package com.example.ventasrovianda.Utils.Models;

public class UpdateOrderRequest {

    private Long subOrderId;
    private Integer quantity;

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
}
