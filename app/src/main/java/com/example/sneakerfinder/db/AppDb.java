package com.example.sneakerfinder.db;

import android.content.Context;

import com.example.sneakerfinder.db.dao.ShoeDao;
import com.example.sneakerfinder.db.entity.Shoe;
import com.example.sneakerfinder.db.entity.ShoeScan;
import com.example.sneakerfinder.db.entity.ShoeScanResult;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Shoe.class, ShoeScan.class, ShoeScanResult.class}, version = 4)
@TypeConverters({RoomConverters.class})
public abstract class AppDb extends RoomDatabase {
    private static volatile AppDb INSTANCE;

    public static AppDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDb.class, "app_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ShoeDao getShoeDao();
}
