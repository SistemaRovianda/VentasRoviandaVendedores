package com.example.tiendas.Utils.Models;

public class OrderDetails {

    private int product_id;
    private String name;
    private String imgS3;

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgS3() {
        return imgS3;
    }

    public void setImgS3(String imgS3) {
        this.imgS3 = imgS3;
    }
}
