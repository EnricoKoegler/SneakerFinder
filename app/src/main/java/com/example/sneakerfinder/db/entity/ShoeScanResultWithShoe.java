package com.example.sneakerfinder.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

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
}
