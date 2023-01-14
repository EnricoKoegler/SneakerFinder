package com.example.sneakerfinder.db.entity;

import java.util.Collections;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ShoeScanWithShoeScanResults {
    @Embedded public ShoeScan shoeScan;
    @Relation(
            parentColumn = "shoeScanId",
            entityColumn = "shoeScanId"
    )
    public List<ShoeScanResultWithShoe> shoeScanResults;

    public List<ShoeScanResultWithShoe> getSortedShoeScanResults() {
        Collections.sort(shoeScanResults);
        return shoeScanResults;
    }

    public ShoeScanResultWithShoe getTopResult() {
        return Collections.max(shoeScanResults);
    }
}
