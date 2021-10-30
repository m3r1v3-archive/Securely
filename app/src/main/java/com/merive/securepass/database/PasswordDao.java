package com.merive.securepass.database;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PasswordDao {

    /**
     * This method is inserting password item to Database.
     *
     * @param item Password object with password values.
     */
    @Insert(onConflict = IGNORE)
    void insertItem(Password item);

    /**
     * This method is updating password item in database.
     *
     * @param nameBefore  Old name of password item.
     * @param name        New name of password item.
     * @param login       Login value.
     * @param password    Password Value.
     * @param description Description Value.
     */
    @Query("UPDATE password SET " +
            "name = :name, " +
            "login = :login, " +
            "password = :password, " +
            "description = :description " +
            "WHERE name = :nameBefore")
    void updateItem(String nameBefore, String name, String login, String password, String description);

    /**
     * This method updates Login Value in database by item name.
     *
     * @param name  Item name.
     * @param login Updated login value.
     */
    @Query("UPDATE password SET " +
            "login = :login WHERE name = :name")
    void updateLoginByName(String name, String login);

    /**
     * This method updates Password Value in database by item name.
     *
     * @param name     Item name.
     * @param password Updated password value.
     */
    @Query("UPDATE password SET " +
            "password = :password WHERE name = :name")
    void updatePasswordByName(String name, String password);

    /**
     * This method returns Login value by item name from database.
     *
     * @param name Item name.
     * @return Login value.
     */
    @Query("SELECT login FROM password WHERE name = :name")
    String getLoginByName(String name);

    /**
     * This method returns Password value by item name from database.
     *
     * @param name Item name.
     * @return Password value.
     */
    @Query("SELECT password FROM password WHERE name = :name")
    String getPasswordByName(String name);

    /**
     * This method returns Description value by item name from database.
     *
     * @param name Item name.
     * @return Description value.
     */
    @Query("SELECT description FROM password WHERE name = :name")
    String getDescriptionByName(String name);

    /**
     * This method returns all password items from database.
     *
     * @return Password items List.
     * @see List
     */
    @Query("SELECT * FROM password ORDER BY name")
    List<Password> getAll();

    /**
     * This method returns all item names from database.
     *
     * @return
     */
    @Query("SELECT name FROM password")
    List<String> getAllNames();

    /**
     * THis method is checking what name is not exist in database.
     *
     * @param name Item name.
     * @return True if name isn't exist in database.
     */
    @Query("SELECT NOT EXISTS(SELECT 1 FROM password WHERE name= :name)")
    boolean checkNotExist(String name);

    /**
     * This method is checking what database is empty.
     *
     * @return true if database is empty.
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
     * This method is deleting Password item by item name.
     *
     * @param name Item name.
     */
    @Query("DELETE FROM password WHERE name = :name")
    void deleteByName(String name);

    /**
     * This method is deleting all items from database.
     */
    @Query("DELETE FROM Password")
    void deleteAll();
}
