package com.example.sneakerfinder.db.entity;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Shoe scan represents one single image that a user has either uploaded or captured using the
 * phones camera. The result quality tells how good the top result of the shoe scan is.
 * It also tells, if the shoe scan is still in progress which is especially used in the
 * {@link com.example.sneakerfinder.ui.scan_processing.ScanProcessingActivity} to propagate
 * the state of the scan to the view.
 */
@Entity
public class ShoeScan implements Comparable<ShoeScan> {
    @PrimaryKey(autoGenerate = true) public long shoeScanId;
    public Date scanDate;
    public String scanImageFilePath;

    @ColumnInfo(defaultValue = "2")
    public int resultQuality;

    public static final int RESULT_QUALITY_PROCESSING = -2;
    public static final int RESULT_QUALITY_ERROR = -1;
    public static final int RESULT_QUALITY_NO_RESULT = 0;
    public static final int RESULT_QUALITY_LOW = 1;
    public static final int RESULT_QUALITY_HIGH = 2;

    @Override
    public int compareTo(ShoeScan other) {
        return (int) (other.scanDate.getTime() - this.scanDate.getTime());
    }
}
