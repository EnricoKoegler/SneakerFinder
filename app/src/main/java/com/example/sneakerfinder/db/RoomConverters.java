package com.example.sneakerfinder.db;


import java.util.Date;

import androidx.room.TypeConverter;

public class RoomConverters {
    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}