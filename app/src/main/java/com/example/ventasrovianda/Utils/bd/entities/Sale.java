package com.example.ventasrovianda.Utils.bd.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sales")
public class Sale {

    @PrimaryKey
    @NonNull
    public String folio;

    @ColumnInfo(name="sale_server_id")
    public int saleId;

    @ColumnInfo(name = "seller_id")
    public String sellerId;

    @ColumnInfo(name="key_client")
    public int keyClient;

    @ColumnInfo(name="payed")
    public Float payed;

    @ColumnInfo(name="amount")
    public Float amount;

    @ColumnInfo(name="credit")
    public Float credit;

    @ColumnInfo(name="type_sale")
    public String typeSale;

    @ColumnInfo(name="client_name")
    public String clientName;

    @ColumnInfo(name="date")
    public String date;

    @ColumnInfo(name="status")
    public Boolean status;

    @ColumnInfo(name="status_str")
    public String statusStr;

    @ColumnInfo(name="sincronized")
    public Boolean sincronized;

    @ColumnInfo(name="client_id")
    public int clientId;

    @ColumnInfo(name = "modified")
    public Boolean modified;

    @ColumnInfo(name="devolution_request_id")
    @Nullable
    public Integer devolutionId;

    @ColumnInfo(name="cancel_autorized")
    public String cancelAutorized;

}
