package com.example.sneakerfinder.db.entity;

import java.util.Collections;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ShoeScanWithShoeScanResults implements Comparable<ShoeScanWithShoeScanResults> {
    public ShoeScan shoeScan;
    public List<ShoeScanResultWithShoe> shoeScanResults;

    public ShoeScanWithShoeScanResults(ShoeScan shoeScan, List<ShoeScanResultWithShoe> shoeScanResults) {
        this.shoeScan = shoeScan;
        this.shoeScanResults = shoeScanResults;
    }

    public List<ShoeScanResultWithShoe> getSortedShoeScanResults() {
        Collections.sort(shoeScanResults);
        return shoeScanResults;
    }

    public ShoeScanResultWithShoe getTopResult() {
        return Collections.max(shoeScanResults);
    }

    @Override
    public int compareTo(ShoeScanWithShoeScanResults other) {
        return this.shoeScan.compareTo(other.shoeScan);
    }
}
