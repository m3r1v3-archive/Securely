package com.merive.securepass.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface PasswordDao {

    @Insert(onConflict = IGNORE)
    void insertItem(Password item);

    @Delete
    void deleteItem(Password person);

    @Query("SELECT name FROM password WHERE id = :itemId")
    String getNameById(long itemId);

    @Query("SELECT password FROM password WHERE id = :itemId")
    String getPasswordById(long itemId);

    @Query("DELETE FROM password WHERE id = :itemId")
    void deleteByItemId(long itemId);

    @Query("SELECT * FROM password")
    List<Password> getAll();

    @Query("DELETE FROM Password")
    void deleteAll();
}
