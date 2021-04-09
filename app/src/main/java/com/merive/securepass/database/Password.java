package com.merive.securepass.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Password implements Serializable {
    @PrimaryKey()
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "description")
    private String description;


    @Ignore
    public Password(long id, String name, String login, String password, String description) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.description = description;
    }

    public Password() {
        this.name = "name";
        this.login = "login";
        this.password = "password";
        this.description = "description";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}