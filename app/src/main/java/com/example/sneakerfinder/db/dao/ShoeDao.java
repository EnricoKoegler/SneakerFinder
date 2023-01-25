package com.example.sneakerfinder.db.dao;

import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoe;
import com.example.sneakerfinder.db.entity.ShoeScanResultWithShoeAndScan;
import com.example.sneakerfinder.db.entity.ShoeScanWithShoes;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface ShoeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertShoe(Shoe shoe);

    @Insert
    Long insertShoeScan(ShoeScan shoeScan);

    @Insert
    void insertShoeScanResult(ShoeScanResult result);

    @Transaction
    @Query("SELECT * FROM ShoeScan ORDER BY scanDate DESC")
    LiveData<List<ShoeScanWithShoes>> getAllShoeScansWithShoes();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    LiveData<List<ShoeScanResultWithShoe>> getShoeScanResultsWithShoes(long shoeScanId);

    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    List<ShoeScanResultWithShoe> getShoeScanResults(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    LiveData<List<ShoeScanResultWithShoeAndScan>> getShoeScanResultsWithShoesAndScans(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult ORDER BY confidence DESC")
    LiveData<List<ShoeScanResultWithShoeAndScan>> getShoeScanResultsWithShoesAndScans();

    @Query("SELECT * FROM ShoeScan WHERE shoeScanId = :shoeScanId")
    LiveData<ShoeScan> getShoeScan(long shoeScanId);
}
