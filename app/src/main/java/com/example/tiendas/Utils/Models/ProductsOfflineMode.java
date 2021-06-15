package com.example.tiendas.Utils.Models;

public class ProductsOfflineMode {

        private String productKey;
        private Float quantity;
        private String type;
        private Float price;
        private Float weightStandar;
        private String status;
        private String date;
        private String productName;
        private String productPresentationType;

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getWeightStandar() {
        return weightStandar;
    }

    public void setWeightStandar(Float weightStandar) {
        this.weightStandar = weightStandar;
    }

    public String getProductPresentationType() {
        return productPresentationType;
    }

    public void setProductPresentationType(String productPresentationType) {
        this.productPresentationType = productPresentationType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
