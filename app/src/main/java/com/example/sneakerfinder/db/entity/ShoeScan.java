package com.example.sneakerfinder.db.entity;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ShoeScan {
    @PrimaryKey(autoGenerate = true) public long shoeScanId;
    public Date scanDate;
    public String scanImageFilePath;
}
