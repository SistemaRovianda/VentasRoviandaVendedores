package com.example.ventasrovianda.Utils.bd.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "devolution_sub_sales")
public class DevolutionSubSale {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "devolution_sub_sale_id")
    public int devolutionSubSaleId;

    @ColumnInfo(name="devolution_request_id")
    public int devolutionRequestId;

    @ColumnInfo(name = "sub_sale_id")
    public int subSaleId;

    @ColumnInfo(name="product_key")
    public String productKey;

    @ColumnInfo(name="quantity")
    public Float quantity;

    @ColumnInfo(name="price")
    public Float price;

    @ColumnInfo(name="weight_standar")
    public Float weightStandar;

    @ColumnInfo(name="product_name")
    public String productName;

    @ColumnInfo(name="product_presentation_type")
    public String productPresentationType;

    @ColumnInfo(name="presentation_id")
    public int presentationId;

    @ColumnInfo(name="product_id")
    public int productId;

    @ColumnInfo(name="uni_med")
    public String uniMed;
}
