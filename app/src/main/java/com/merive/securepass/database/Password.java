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

    /* ************ */
    /* Constructors */
    /* ************ */

    public Password() {
        /* Default constructor */
        this.name = "name";
        this.login = "login";
        this.password = "password";
        this.description = "description";
    }

    @Ignore
    public Password(String name, String login, String password, String description) {
        /* Constructor with parameters */
        this.name = name;
        this.login = login;
        this.password = password;
        this.description = description;
    }

    /* *************** */
    /* Get/Set methods */
    /* *************** */

    public String getName() {
        /* Get name */
        return name;
    }

    public void setName(String name) {
        /* Set name */
        this.name = name;
    }

    public String getLogin() {
        /* Get login */
        return login;
    }

    public void setLogin(String login) {
        /* Set login */
        this.login = login;
    }

    public String getPassword() {
        /* Get password */
        return password;
    }

    public void setPassword(String password) {
        /* Set password */
        this.password = password;
    }

    public String getDescription() {
        /* Get description */
        return description;
    }

    public void setDescription(String description) {
        /* Set description */
        this.description = description;
    }
}