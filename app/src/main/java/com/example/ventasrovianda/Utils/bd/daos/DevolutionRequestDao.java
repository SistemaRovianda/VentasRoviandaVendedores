package com.example.ventasrovianda.Utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.Sale;

import java.util.List;

@Dao
public interface DevolutionRequestDao {
    @Insert
    void insertAll(DevolutionRequest ...devolutionRequest);

    @Query("select * from devolution_requests where devolution_request_id=:devolutionRequestId")
    DevolutionRequest findDevolutionRequestById(Integer devolutionRequestId);

    @Query("select * from devolution_requests where folio=:folio order by devolution_request_id desc limit 1")
    DevolutionRequest findDevolutionRequestByFolioRegister(String folio);

    @Query("select * from devolution_requests where folio=:folio and status='PENDING' and sincronized=0 limit 1")
    DevolutionRequest findDevolutionRequestByFolio(String folio);

    @Query("select * from devolution_requests where folio=:folio and status='PENDING' limit 1")
    DevolutionRequest findDevolutionRequestPendingByFolio(String folio);

    @Query("select * from devolution_requests where folio=:folio and status='ACCEPTED' limit 1")
    DevolutionRequest findDevolutionRequestByFolioAndAccepted(String folio);

    @Query("select * from devolution_requests where sincronized=0")
    List<DevolutionRequest> getAllUnsincronized();

    @Query("select * from sales where folio in (select folio from devolution_requests where  create_at between :date1 and :date2)")
    List<Sale> getAllBetweenDate(String date1, String date2);

    @Query("select * from devolution_requests where  create_at between :date1 and :date2")
    List<DevolutionRequest> getAllBetweenDateRegisters(String date1, String date2);

    @Query("select * from devolution_requests where sincronized=1")
    List<DevolutionRequest> findAllSincronized();

    @Update
    void updateDevolutionRequest(DevolutionRequest devolutionRequest);
}
