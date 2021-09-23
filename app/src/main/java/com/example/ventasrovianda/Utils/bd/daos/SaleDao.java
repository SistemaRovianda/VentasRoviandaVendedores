package com.example.ventasrovianda.Utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ventasrovianda.Utils.bd.entities.Sale;

import java.util.List;

@Dao
public interface SaleDao {

    @Query("delete from sales")
    void deleteAllSales();

    @Query("select * from sales where date between :date1 and :date2")
    List<Sale> getAllSalesByDate(String date1,String date2);

    @Query("select * from sales where (sincronized=0) or ( sincronized=1 and modified=1)")
    List<Sale> getAllSalesUnsincronized();

    @Query("select * from sales where  date between :date1 and :date2")
    List<Sale> getAllSalesUnsincronizedByDate(String date1,String date2);

    @Query("select * from sales where sale_server_id=:saleId ")
    Sale getBySaleId(int saleId);

    @Query("select * from sales where folio=:folio ")
    Sale getByFolio(String folio);

    @Query("update sales set status_str=:status,modified=1 where folio=:folio ")
    void updateStatusStr(String status,String folio);

    @Query("update sales set sale_server_id=:saleId,sincronized=1,modified=0 where folio=:folio ")
    void updateSaleId(int saleId,String folio);

    @Insert
    void insertAll(Sale... sales);

    @Query("update sales set sale_server_id = :saleServerId , sincronized=1 where folio=:folio")
    void updateStatusStr(int saleServerId,String folio);

    @Query("select * from sales where status=1")
    List<Sale> getAllDebts();

    @Update
    void updateSale(Sale... sale);

}
