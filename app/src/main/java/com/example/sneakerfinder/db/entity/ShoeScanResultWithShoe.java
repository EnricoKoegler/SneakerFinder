package com.example.sneakerfinder.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Helper model to query a {@link ShoeScanResult} with its associated shoe.
 */
public class ShoeScanResultWithShoe implements Comparable<ShoeScanResultWithShoe> {
    @Embedded public ShoeScanResult shoeScanResult;
    @Relation(
            parentColumn = "shoeId",
            entityColumn = "shoeId"
    )
    public Shoe shoe;

    @Override
    public int compareTo(ShoeScanResultWithShoe o) {
        return shoeScanResult.compareTo(o.shoeScanResult);
    }

    public ShoeScanResultWithShoe(ShoeScanResult shoeScanResult, Shoe shoe) {
        this.shoeScanResult = shoeScanResult;
        this.shoe = shoe;
    }
}
