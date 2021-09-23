package com.example.ventasrovianda.Utils.bd.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "devolution_requests")
public class DevolutionRequest {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "devolution_request_id")
    public int devolutionRequestId;

    @ColumnInfo(name = "folio")
    public String folio;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "type_devolution")
    public String typeDevolution;

    @ColumnInfo(name="create_at")
    public String createAt;

    @ColumnInfo(name = "sincronized",defaultValue = "0")
    public int sincronized;

    @ColumnInfo(name = "status",defaultValue = "PENDING")
    public String status;
}
