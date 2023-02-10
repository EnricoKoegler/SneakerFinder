package com.example.sneakerfinder.db.entity;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class ShoeScanWithShoes {
    @Embedded public ShoeScan shoeScan;

    /**
     * There is no way to get additional attributes of the Junction table in room...
     */
    @Relation(
            parentColumn = "shoeScanId",
            entityColumn = "shoeId",
            associateBy = @Junction(ShoeScanResult.class)
    )
    public List<Shoe> shoes;
}
