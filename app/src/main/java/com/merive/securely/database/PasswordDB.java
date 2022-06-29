package com.merive.securely.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Password.class}, version = 1)
public abstract class PasswordDB extends RoomDatabase {

    public abstract PasswordDao passwordDao();
}
