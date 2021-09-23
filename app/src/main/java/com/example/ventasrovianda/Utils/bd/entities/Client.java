package com.example.ventasrovianda.Utils.bd.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients")
public class Client {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "client_id")
    public int clientId;

    @ColumnInfo(name="client_key")
    public int clientKey;

    @ColumnInfo(name="seller_uid")
    public String uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name="type")
    public String type;

    @ColumnInfo(name="current_credit_used")
    public Float currentCreditUsed;

    @ColumnInfo(name="credit_limit")
    public Float creditLimit;

    @ColumnInfo(name="monday")
    public Boolean monday;
    @ColumnInfo(name="tuesday")
    public Boolean tuesday;
    @ColumnInfo(name="wednesday")
    public Boolean wednesday;
    @ColumnInfo(name="thursday")
    public Boolean thursday;
    @ColumnInfo(name="friday")
    public Boolean friday;
    @ColumnInfo(name="saturday")
    public Boolean saturday;
    @ColumnInfo(name="sunday")
    public Boolean sunday;
    @ColumnInfo(name="cp")
    public String cp;
}
