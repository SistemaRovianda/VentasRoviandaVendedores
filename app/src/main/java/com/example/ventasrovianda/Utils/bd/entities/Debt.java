package com.example.ventasrovianda.Utils.bd.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "debts")
public class Debt {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "deb_id")
    public int debId;

    @ColumnInfo(name = "create_at")
    public String createAt;

    @ColumnInfo(name = "payed_type")
    public String payedType;

    @ColumnInfo(name="folio")
    public String folio;

    @ColumnInfo(name = "solped")
    public Boolean solped;

    @ColumnInfo(name = "sincronized")
    public Boolean sincronized;

    @ColumnInfo(name = "deleted")
    public Boolean deleted;
}
