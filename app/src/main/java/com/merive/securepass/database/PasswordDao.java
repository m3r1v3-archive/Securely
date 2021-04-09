package com.merive.securepass.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface PasswordDao {

    @Insert(onConflict = IGNORE)
    void insertItem(Password item);

    @Query("UPDATE password SET " +
            "name = :name, " +
            "login = :login, " +
            "password = :password, " +
            "description = :description " +
            "WHERE id = :itemId")
    void updateItem(long itemId, String name, String login, String password, String description);

    @Query("SELECT name FROM password WHERE id = :itemId")
    String getNameById(long itemId);

    @Query("SELECT login FROM password WHERE id = :itemId")
    String getLoginById(long itemId);

    @Query("SELECT password FROM password WHERE id = :itemId")
    String getPasswordById(long itemId);

    @Query("SELECT description FROM password WHERE id = :itemId")
    String getDescriptionById(long itemId);

    @Query("DELETE FROM password WHERE id = :itemId")
    void deleteByItemId(long itemId);

    @Query("SELECT * FROM password")
    List<Password> getAll();

    @Query("SELECT * FROM password WHERE id = ( SELECT MAX(id) FROM password )")
    long getMaxId();

    @Query("DELETE FROM Password")
    void deleteAll();
}
