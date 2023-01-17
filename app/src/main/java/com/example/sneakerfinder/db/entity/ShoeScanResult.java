package com.example.sneakerfinder.db.entity;

import androidx.room.Entity;

@Entity(primaryKeys = {"shoeId", "shoeScanId"})
public class ShoeScanResult implements Comparable<ShoeScanResult> {
    public long shoeId;
    public long shoeScanId;
    public float confidence;
    public boolean isTopResult;

    public ShoeScanResult(long shoeId, long shoeScanId, float confidence, boolean isTopResult) {
        this.shoeId = shoeId;
        this.shoeScanId = shoeScanId;
        this.confidence = confidence;
        this.isTopResult = isTopResult;
    }

    @Override
    public int compareTo(ShoeScanResult o) {
        return Float.compare(this.confidence, o.confidence);
    }
}
