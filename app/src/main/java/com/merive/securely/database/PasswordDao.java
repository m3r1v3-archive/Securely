package com.merive.securely.database;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PasswordDao {

    /**
     * Insert Password item to database
     *
     * @param item Password object item
     */
    @Insert(onConflict = IGNORE)
    void insertItem(Password item);

    /**
     * Update Password data in database
     *
     * @param nameBefore  Password name before update value
     * @param name        Current password name value
     * @param login       Password login value
     * @param password    Password value
     * @param description Password description value
     */
    @Query("UPDATE password SET " +
            "name = :name, " +
            "login = :login, " +
            "password = :password, " +
            "description = :description " +
            "WHERE name = :nameBefore")
    void updateItem(String nameBefore, String name, String login, String password, String description);

    /**
     * Update password login by password name
     *
     * @param name  Password name value
     * @param login Password login value
     */
    @Query("UPDATE password SET " +
            "login = :login WHERE name = :name")
    void updateLoginByName(String name, String login);

    /**
     * Update password by password name
     *
     * @param name     Password name value
     * @param password Password value
     */
    @Query("UPDATE password SET " +
            "password = :password WHERE name = :name")
    void updatePasswordByName(String name, String password);

    /**
     * Return password login by name
     *
     * @param name Password name value
     * @return Password login value
     */
    @Query("SELECT login FROM password WHERE name = :name")
    String getLoginByName(String name);

    /**
     * Return password by name
     *
     * @param name Password name value
     * @return Password value
     */
    @Query("SELECT password FROM password WHERE name = :name")
    String getPasswordByName(String name);

    /**
     * Return password description by name
     *
     * @param name Password name value
     * @return Password description value
     */
    @Query("SELECT description FROM password WHERE name = :name")
    String getDescriptionByName(String name);

    /**
     * Return all password data values
     *
     * @return Password name data values
     */
    @Query("SELECT * FROM password ORDER BY name")
    List<Password> getAll();

    /**
     * Return all password name values
     *
     * @return Password name values
     */
    @Query("SELECT name FROM password")
    List<String> getAllNames();

    /**
     * Return true if name isn't exist in database
     *
     * @param name Name to check
     * @return True if name isn't exist in database, else false
     */
    @Query("SELECT NOT EXISTS(SELECT 1 FROM password WHERE name= :name)")
    boolean checkNotExist(String name);

    /**
     * Return true if database is empty
     *
     * @return True if database is empty, else false
     */
    @Query("SELECT\n" +
            "    (\n" +
            "       CASE WHEN NOT EXISTS(SELECT NULL FROM password)\n" +
            "       THEN 1\n" +
            "       ELSE 0\n" +
            "       END\n" +
            "    ) AS isEmpty")
    boolean checkEmpty();

    /**
     * Delete password row by name
     *
     * @param name Name of password row to delete
     */
    @Query("DELETE FROM password WHERE name = :name")
    void deleteByName(String name);

    /**
     * Delete all password row from database
     */
    @Query("DELETE FROM Password")
    void deleteAll();
}
