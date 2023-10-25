package com.example.ventasrovianda.Utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ventasrovianda.Utils.bd.entities.Debt;
import com.example.ventasrovianda.Utils.bd.entities.EndingDay;

import java.util.List;
@Dao
public interface EndingDayDao {
    @Query("select * from ending_days where date=:date")
    EndingDay getEndingDayByDate(String date);

    @Insert
    void saveEndingDay(EndingDay... endingDay);
}
