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
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface ShoeDao {
    @Insert
    Long insertShoe(Shoe shoe);

    @Query("SELECT * FROM Shoe WHERE styleId = :styleId")
    Shoe getShoeByStyleId(String styleId);

    @Insert
    Long insertShoeScan(ShoeScan shoeScan);

    @Update
    void updateShoeScan(ShoeScan shoeScan);

    @Query("SELECT * FROM SHOESCAN WHERE shoeScanId = :shoeScanId")
    ShoeScan getShoeScanById(long shoeScanId);

    @Insert
    void insertShoeScanResult(ShoeScanResult result);

    @Transaction
    @Query("SELECT * FROM ShoeScan ORDER BY scanDate DESC")
    LiveData<List<ShoeScanWithShoes>> getAllShoeScansWithShoes();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    LiveData<List<ShoeScanResultWithShoe>> getShoeScanResultsWithShoes(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId AND isTopResult = 0 ORDER BY confidence DESC")
    LiveData<List<ShoeScanResultWithShoe>> getSimilarShoes(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult r1 WHERE shoeScanId = (" +
            "SELECT shoeScanId FROM ShoeScanResult r2 " +
            "WHERE isTopResult = 0 AND confidence > 0.01 AND r1.shoeId = r2.shoeId " +
            "ORDER BY confidence LIMIT 1" +
            ") ORDER BY confidence DESC LIMIT 50")
    LiveData<List<ShoeScanResultWithShoe>> getRecommendedShoes();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    List<ShoeScanResultWithShoe> getShoeScanResults(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    LiveData<List<ShoeScanResultWithShoeAndScan>> getShoeScanResultsWithShoesAndScans(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult ORDER BY confidence DESC")
    LiveData<List<ShoeScanResultWithShoeAndScan>> getShoeScanResultsWithShoesAndScans();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId AND shoeId = :shoeId")
    LiveData<ShoeScanResultWithShoeAndScan> getShoeScanResult(long shoeScanId, long shoeId);

    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId AND isTopResult = 1")
    LiveData<ShoeScanResult> getTopResult(long shoeScanId);
}
