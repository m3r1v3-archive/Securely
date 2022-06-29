package com.merive.securely.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
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
     * Password empty constructor
     */
    public Password() {
        this.name = "name";
        this.login = "login";
        this.password = "password";
        this.description = "description";
    }

    /**
     * Password constructor with parameters
     */
    @Ignore
    public Password(String name, String login, String password, String description) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.description = description;
    }

    /**
     * Return password name value
     *
     * @return Password name value
     */
    public String getName() {
        return name;
    }

    /**
     * Set name value of password
     *
     * @param name Password name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return password login value
     *
     * @return Password login value
     */
    public String getLogin() {
        return login;
    }

    /**
     * Set login value of password
     *
     * @param login Password login value
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Return password value
     *
     * @return Password value
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password value
     *
     * @param password Password value
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Return description value of password
     *
     * @return Password description value
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description value of password
     *
     * @param description Password description value
     */
    public void setDescription(String description) {
        this.description = description;
    }
}