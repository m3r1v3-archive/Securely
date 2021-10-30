package com.merive.securepass.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Password.class}, version = 1)
public abstract class PasswordDB extends RoomDatabase {
    /**
     * This method needs for executing PasswordDao methods.
     *
     * @return PasswordDao object.
     * @see PasswordDao
     */
    public abstract PasswordDao passwordDao();
}
