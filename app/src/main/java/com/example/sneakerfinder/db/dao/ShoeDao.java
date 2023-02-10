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

/**
 * Dao to query shoe related information from the database.
 */
@Dao
public abstract class ShoeDao {
    @Insert
    public abstract Long insertShoe(Shoe shoe);

    @Query("SELECT * FROM Shoe WHERE styleId = :styleId")
    public abstract Shoe getShoeByStyleId(String styleId);

    @Insert
    public abstract Long insertShoeScan(ShoeScan shoeScan);

    @Update
    public abstract void updateShoeScan(ShoeScan shoeScan);

    @Query("DELETE FROM ShoeScan WHERE shoeScanId = :shoeScanId")
    abstract void deleteShoeScan(long shoeScanId);

    @Query("DELETE FROM ShoeScanResult WHERE shoeScanId = :shoeScanId")
    abstract void deleteShoeScanResults(long shoeScanId);

    @Transaction
    public void deleteShoeScanAndResults(long shoeScanId) {
        deleteShoeScanResults(shoeScanId);
        deleteShoeScan(shoeScanId);
    }

    @Query("SELECT * FROM SHOESCAN WHERE shoeScanId = :shoeScanId")
    public abstract ShoeScan getShoeScanById(long shoeScanId);

    @Insert
    public abstract void insertShoeScanResult(ShoeScanResult result);

    @Transaction
    @Query("SELECT * FROM ShoeScan ORDER BY scanDate DESC")
    public abstract LiveData<List<ShoeScanWithShoes>> getAllShoeScansWithShoes();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    public abstract LiveData<List<ShoeScanResultWithShoe>> getShoeScanResultsWithShoes(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId AND isTopResult = 0 ORDER BY confidence DESC")
    public abstract LiveData<List<ShoeScanResultWithShoe>> getSimilarShoes(long shoeScanId);

    /**
     * Recommends shoes which are not the top result of a shoe scan, but still could be reasonable results.
     */
    @Transaction
    @Query("SELECT * FROM ShoeScanResult r1 WHERE shoeScanId = (" +
            "SELECT shoeScanId FROM ShoeScanResult r2 " +
            "WHERE isTopResult = 0 AND confidence > 0.01 AND r1.shoeId = r2.shoeId " +
            "ORDER BY confidence LIMIT 1" +
            ") ORDER BY confidence DESC LIMIT 50")
    public abstract LiveData<List<ShoeScanResultWithShoe>> getRecommendedShoes();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    public abstract List<ShoeScanResultWithShoe> getShoeScanResults(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId ORDER BY confidence DESC")
    public abstract LiveData<List<ShoeScanResultWithShoeAndScan>> getShoeScanResultsWithShoesAndScans(long shoeScanId);

    @Transaction
    @Query("SELECT * FROM ShoeScanResult ORDER BY confidence DESC")
    public abstract LiveData<List<ShoeScanResultWithShoeAndScan>> getShoeScanResultsWithShoesAndScans();

    @Transaction
    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId AND shoeId = :shoeId")
    public abstract LiveData<ShoeScanResultWithShoeAndScan> getShoeScanResult(long shoeScanId, long shoeId);

    @Query("SELECT * FROM ShoeScanResult WHERE shoeScanId = :shoeScanId AND isTopResult = 1")
    public abstract LiveData<ShoeScanResult> getTopResult(long shoeScanId);
}
