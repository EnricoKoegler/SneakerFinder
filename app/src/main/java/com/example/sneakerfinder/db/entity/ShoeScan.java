package com.example.sneakerfinder.db.entity;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ShoeScan implements Comparable<ShoeScan> {
    @PrimaryKey(autoGenerate = true) public long shoeScanId;
    public Date scanDate;
    public String scanImageFilePath;

    @Override
    public int compareTo(ShoeScan other) {
        return (int) (other.scanDate.getTime() - this.scanDate.getTime());
    }
}
