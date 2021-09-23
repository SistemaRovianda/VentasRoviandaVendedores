package com.example.ventasrovianda.Utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ventasrovianda.Utils.bd.entities.Client;

import java.util.List;

@Dao
public interface ClientDao {


    @Query("select * from clients where client_id=:clientId limit 1")
    Client getClientBydId(Integer clientId);

    @Query("select * from clients where client_key=:clientKey and seller_uid=:uid limit 1")
    Client getClientByKey(Integer clientKey,String uid);

    @Query("update clients set current_credit_used=:credit where client_key=:clientKey")
    void updateCreditToClient(Float credit,String clientKey);

    @Query("select * from clients where seller_uid=:sellerUid")
    List<Client> getClientsBySellerUid(String sellerUid);

    @Insert
    void insertClient(Client... client);

    @Query("select * from clients where monday=1 and seller_uid=:uid")
    List<Client> getClientsMonday(String uid);
    @Query("select * from clients where tuesday=1 and seller_uid=:uid")
    List<Client> getClientsTuesday(String uid);
    @Query("select * from clients where wednesday=1 and seller_uid=:uid")
    List<Client> getClientsWednesday(String uid);
    @Query("select * from clients where thursday=1 and seller_uid=:uid")
    List<Client> getClientsThursday(String uid);
    @Query("select * from clients where friday=1 and seller_uid=:uid")
    List<Client> getClientsFriday(String uid);
    @Query("select * from clients where saturday=1 and seller_uid=:uid")
    List<Client> getClientsSaturday(String uid);
    @Query("select * from clients where sunday=1 and seller_uid=:uid")
    List<Client> getClientsSunday(String uid);


    @Update
    void updateClient(Client client);
}
