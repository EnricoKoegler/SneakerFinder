package com.example.sneakerfinder.db.entity;

import androidx.room.Entity;
import androidx.room.Index;

/**
 * The {@link ShoeScanResult} connects one-or-many {@link ShoeScan} to one-or-many {@link Shoe}
 * (many-to-many relationship).
 * This was especially hard to model in room, because there is no way in room to retrieve the
 * additional attributes of a n-to-n-relation (here: confidence, isTopResult) with one entity.
 */
@Entity(
        primaryKeys = {"shoeId", "shoeScanId"},
        indices = {
                @Index(name="shoeScanResultIndex", value={"shoeId", "shoeScanId"}, unique = true)
        }
)
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
        return Float.compare(o.confidence, this.confidence);
    }
}
