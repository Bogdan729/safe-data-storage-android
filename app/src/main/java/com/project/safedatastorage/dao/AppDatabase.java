package com.project.safedatastorage.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FileEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FileDao fileDao();

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "files_db").allowMainThreadQueries().build();
        }

        return INSTANCE;
    }
}