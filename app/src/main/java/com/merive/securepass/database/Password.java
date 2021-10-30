package com.merive.securepass.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"name"},
        unique = true)})
public class Password implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "description")
    private String description;

    /**
     * Password Constructor
     */
    public Password() {
        this.name = "name";
        this.login = "login";
        this.password = "password";
        this.description = "description";
    }

    /**
     * Password Constructor with arguments.
     *
     * @param name        Item name.
     * @param login       Login value.
     * @param password    Password value.
     * @param description Description value.
     */
    @Ignore
    public Password(String name, String login, String password, String description) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.description = description;
    }

    /**
     * This method returns item name value.
     *
     * @return Item name.
     */
    public String getName() {
        return name;
    }

    /**
     * This method is setting name value.
     *
     * @param name Item name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns item login value.
     *
     * @return Login value.
     */
    public String getLogin() {
        return login;
    }

    /**
     * This method is setting item login value.
     *
     * @param login Login value.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * This method returns item password value.
     *
     * @return Password value.
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method is setting item password value.
     *
     * @param password Password value.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method returns item description value.
     *
     * @return Description value.
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method is setting item description value.
     *
     * @param description Description value.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}