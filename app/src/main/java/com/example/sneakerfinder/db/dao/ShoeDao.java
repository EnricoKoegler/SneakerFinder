package com.example.sneakerfinder.db.dao;

import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoes;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface ShoeDao {
    @Insert
    Long insertShoe(Shoe shoe);

    @Insert
    Long insertShoeScan(ShoeScan shoeScan);

    @Insert
    void insertShoeScanResult(ShoeScanResult result);

    @Transaction
    @Query("SELECT * FROM ShoeScan")
    LiveData<List<ShoeScanWithShoes>> getAllShoeScans();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId")
    LiveData<List<ShoeScanResultWithShoe>> getShoeScanResults(long shoeScanId);
}
