package com.example.sneakerfinder.db.entity;

import java.time.LocalDateTime;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ShoeScan {
    @PrimaryKey public long shoeScanId;
    public LocalDateTime scanDate;
    public String scanImageFilePath;
}
