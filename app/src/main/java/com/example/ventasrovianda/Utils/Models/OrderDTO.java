package com.example.ventasrovianda.Utils.Models;

import java.util.List;

public class OrderDTO {

    private int orderId;
    private String date;
    private boolean urgent;
    private List<OrderToSendDTO> products;

    public List<OrderToSendDTO> getProducts() {
        return products;
    }

    public void setProducts(List<OrderToSendDTO> products) {
        this.products = products;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}
