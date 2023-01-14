package com.example.sneakerfinder.db.entity;

import androidx.room.Entity;

@Entity(primaryKeys = {"shoeId, shoeScanId"})
public class ShoeScanResult implements Comparable<ShoeScanResult> {
    public long shoeId;
    public long shoeScanId;
    public float confidence;
    public boolean isTopResult;

    @Override
    public int compareTo(ShoeScanResult o) {
        return Float.compare(this.confidence, o.confidence);
    }
}
