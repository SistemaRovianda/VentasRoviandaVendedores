package com.example.ventasrovianda.Utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ventasrovianda.Utils.bd.entities.Debt;

import java.util.List;

@Dao
public interface DebtDao {

    @Query("select * from debts where sincronized=1 and create_at between :sale1 and :sale2")
    List<Debt> getAllSalesDebtsBetweenDates(String sale1,String sale2);


    @Query("select * from debts where sincronized=0")
    List<Debt> getAllDebsWithoutSincronization();


    @Query("select * from debts where folio=:folio limit 1")
    Debt getDebtByFolio(String folio);

    @Insert
    void insertDebts(Debt... debt);

    @Query("update debts set deleted=1,sincronized=0 where folio=:folio")
    void deleteDebtForDeleteSale(String folio);

    @Update
    void updateDebtSincronization(Debt... debts);

}
