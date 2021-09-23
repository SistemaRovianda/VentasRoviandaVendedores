package com.example.ventasrovianda.Utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;

import java.util.List;

@Dao
public interface DevolutionSubSaleDao {

    @Insert
    void insertAll(DevolutionSubSale ...devolutionSubSale);

    @Query("select * from devolution_sub_sales where devolution_request_id=:devolutionRequestId")
    List<DevolutionSubSale> findByDevolutionRequestId(int devolutionRequestId);


    @Query("select * from devolution_sub_sales where sub_sale_id=:subSaleId order by devolution_request_id desc limit 1")
    DevolutionSubSale findDevolutionSubSaleBySubSaleId(int subSaleId);

    @Query("select * from devolution_sub_sales where sub_sale_id=:subSaleId ")
    List<DevolutionSubSale> findAllDevolutionSubSaleBySubSaleId(int subSaleId);

    @Delete
    void deleteDevolutionSubSale(DevolutionSubSale devolutionSubSale);

}
