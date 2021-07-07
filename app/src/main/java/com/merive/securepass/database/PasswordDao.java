package com.merive.securepass.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface PasswordDao {

    /* ************** */
    /* Insert methods */
    /* ************** */

    @Insert(onConflict = IGNORE)
    void insertItem(Password item);
    /* Insert item to database */

    /* ************** */
    /* Update methods */
    /* ************** */

    @Query("UPDATE password SET " +
            "name = :name, " +
            "login = :login, " +
            "password = :password, " +
            "description = :description " +
            "WHERE name = :nameBefore")
    void updateItem(String nameBefore, String name, String login, String password, String description);
    /* Update item from database */

    @Query("UPDATE password SET " +
            "login = :login WHERE name = :name")
    void updateLoginByName(String name, String login);
    /* Update login from database by name */

    @Query("UPDATE password SET " +
            "password = :password WHERE name = :name")
    void updatePasswordByName(String name, String password);
    /* Update password from database by name */

    /* *********** */
    /* Get methods */
    /* *********** */

    @Query("SELECT login FROM password WHERE name = :name")
    String getLoginByName(String name);
    /* Get login from database by name */

    @Query("SELECT password FROM password WHERE name = :name")
    String getPasswordByName(String name);
    /* Get password from database by name */

    @Query("SELECT description FROM password WHERE name = :name")
    String getDescriptionByName(String name);
    /* Get description from database by name */

    @Query("SELECT * FROM password ORDER BY name")
    List<Password> getAll();
    /* Get all items from database */

    @Query("SELECT name FROM password")
    List<String> getAllNames();
    /* Get all names from database */

    /* ************* */
    /* Check methods */
    /* ************* */

    @Query("SELECT NOT EXISTS(SELECT 1 FROM password WHERE name= :name)")
    boolean checkNotExist(String name);
    /* Check not exist name in database */

    @Query("SELECT\n" +
            "    (\n" +
            "       CASE WHEN NOT EXISTS(SELECT NULL FROM password)\n" +
            "       THEN 1\n" +
            "       ELSE 0\n" +
            "       END\n" +
            "    ) AS isEmpty")
    boolean checkEmpty();
    /* Check database on empty */

    /* ************** */
    /* Delete methods */
    /* ************** */

    @Query("DELETE FROM password WHERE name = :name")
    void deleteByName(String name);
    /* Delete item from database by name */

    @Query("DELETE FROM Password")
    void deleteAll();
    /* Delete all items from database */
}
