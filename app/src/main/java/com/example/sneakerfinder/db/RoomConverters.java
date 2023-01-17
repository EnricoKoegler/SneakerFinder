package com.example.sneakerfinder.db;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @TypeConverter
    public static Long toLong(Duration value) {
        return value == null ? null : value.getSeconds();
    }

    @TypeConverter
    public static Duration toDuration(Long value) {
        return value == null ? null : Duration.ofSeconds(value);
    }

    @TypeConverter
    public static LocalDate toLocalDate(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long toLong(LocalDate value) {
        return value == null ? null : value.toEpochDay();
    }

    @TypeConverter
    public static LocalTime toLocalTime(Long value) {
        return value == null ? null : LocalTime.ofNanoOfDay(value);
    }

    @TypeConverter
    public static Long toLong(LocalTime value) {
        return value == null ? null : value.toNanoOfDay();
    }

    public static Integer toInteger(DayOfWeek value) {
        return value == null ? null : value.getValue();
    }

    public static DayOfWeek toDayOfWeek(Integer value) {
        return value == null ? null : DayOfWeek.of(value);
    }

    public static int dayOfWeekToCalendarDay(DayOfWeek day) {
        return (day.getValue() % 7) + 1;
    }

    public static DayOfWeek calendarDayToDayOfWeek(int calendarDay) {
        int temp = (calendarDay + 6) % 7;
        if (temp == 0) {
            return DayOfWeek.SUNDAY;
        } else {
            return DayOfWeek.of(temp);
        }
    }
}