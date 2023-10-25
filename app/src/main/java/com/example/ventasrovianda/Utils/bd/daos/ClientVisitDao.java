package com.example.ventasrovianda.Utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.Utils.bd.entities.ClientVisit;

import java.util.List;

@Dao
public interface ClientVisitDao {

    @Insert
    void insertClientVisit(ClientVisit... clientVisit);

    @Query("select * from clients_visits where client_id=:clientId and date=:date limit 1")
    ClientVisit getClientVisitByIdAndDate(Integer clientId,String date);

    @Query("select * from clients_visits where date=:date")
    List<ClientVisit> getClientVisitByDate(String date);

    @Query("select * from clients_visits where date=:date and sincronized=0")
    List<ClientVisit> getClientVisitByDateUnsincronized(String date);

    @Query("select * from clients_visits where sincronized=0")
    List<ClientVisit> getClientVisitUnsincronized();

    @Update
    void updateClientVisit(ClientVisit... clientVisit);
}
