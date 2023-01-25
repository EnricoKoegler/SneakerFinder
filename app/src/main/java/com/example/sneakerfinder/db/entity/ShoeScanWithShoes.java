package com.example.sneakerfinder.db.entity;

import java.util.Collections;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class ShoeScanWithShoes {
    @Embedded public ShoeScan shoeScan;

    @Relation(
            parentColumn = "shoeScanId",
            entityColumn = "shoeId",
            associateBy = @Junction(ShoeScanResult.class)
    )
    public List<Shoe> shoes;
}
