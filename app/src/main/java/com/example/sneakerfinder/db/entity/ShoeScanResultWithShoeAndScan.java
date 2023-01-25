package com.example.sneakerfinder.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ShoeScanResultWithShoeAndScan implements Comparable<ShoeScanResultWithShoeAndScan> {
    @Embedded
    public ShoeScanResult shoeScanResult;

    @Relation(
            parentColumn = "shoeId",
            entityColumn = "shoeId"
    )
    public Shoe shoe;

    @Relation(
            parentColumn = "shoeScanId",
            entityColumn = "shoeScanId"
    )
    public ShoeScan shoeScan;

    @Override
    public int compareTo(ShoeScanResultWithShoeAndScan o) {
        return shoeScanResult.compareTo(o.shoeScanResult);
    }
}
