package com.merive.securely.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {

    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getErrors() {
        return sharedPreferences.getInt("errors", 15);
    }

    public void setErrors(int value) {
        sharedPreferences.edit().putInt("errors", value).apply();
    }

    public void setErrors() {
        sharedPreferences.edit().putInt("errors", 15).apply();
    }

    public boolean getDelete() {
        return sharedPreferences.getBoolean("delete", false);
    }

    public void setDelete(boolean value) {
        sharedPreferences.edit().putBoolean("delete", value).apply();
    }

    public String getHash() {
        return sharedPreferences.getString("hash", "-1");
    }

    public void setHash(String value) {
        sharedPreferences.edit().putString("hash", value).apply();
    }

    public void setHash() {
        sharedPreferences.edit().putString("hash", "-1").apply();
    }

    public boolean getEncrypt() {
        return sharedPreferences.getBoolean("encrypt", false);
    }

    public void setEncrypt(boolean value) {
        sharedPreferences.edit().putBoolean("encrypt", value).apply();
    }

    public void setEncrypt() {
        sharedPreferences.edit().putBoolean("encrypt", !getEncrypt()).apply();
    }

    public int getLength() {
        return sharedPreferences.getInt("length", 16);
    }

    public void setLength(int value) {
        sharedPreferences.edit().putInt("length", value).apply();
    }

    public void setLength() {
        sharedPreferences.edit().putInt("length", 16).apply();
    }

    public boolean getShow() {
        return sharedPreferences.getBoolean("show", false);
    }

    public void setShow(boolean value) {
        sharedPreferences.edit().putBoolean("show", value).apply();
    }

    public void setShow() {
        sharedPreferences.edit().putBoolean("show", !getShow()).apply();
    }
}
