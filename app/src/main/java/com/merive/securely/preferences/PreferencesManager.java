package com.merive.securely.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {

    private final SharedPreferences sharedPreferences;

    /**
     * PreferencesManager constructor
     *
     * @param context Context of SharedPreferences
     */
    public PreferencesManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return Return count of login errors from SharedPreferences
     */
    public int getErrors() {
        return sharedPreferences.getInt("errors", 15);
    }

    /**
     * Set count of login errors in SharedPreferences
     *
     * @param value Count of login errors value
     */
    public void setErrors(int value) {
        sharedPreferences.edit().putInt("errors", value).apply();
    }

    /**
     * Set default count of login errors value in SharedPreferences (default value is 15)
     */
    public void setErrors() {
        sharedPreferences.edit().putInt("errors", 15).apply();
    }

    /**
     * @return Return delete after errors value from SharedPreferences
     */
    public boolean getDelete() {
        return sharedPreferences.getBoolean("delete", false);
    }

    /**
     * Set delete after errors value in SharedPreferences
     *
     * @param value Delete after errors value
     */
    public void setDelete(boolean value) {
        sharedPreferences.edit().putBoolean("delete", value).apply();
    }

    /**
     * @return Return key hash value from SharedPreferences
     */
    public String getHash() {
        return sharedPreferences.getString("hash", "-1");
    }

    /**
     * Set key hash value to SharedPreferences
     *
     * @param value Key hash value
     */
    public void setHash(String value) {
        sharedPreferences.edit().putString("hash", value).apply();
    }

    /**
     * Set default key hash value to SharedPreferences (default value is "-1")
     */
    public void setHash() {
        sharedPreferences.edit().putString("hash", "-1").apply();
    }

    /**
     * @return Return encrypt value from SharedPreferences
     */
    public boolean getEncrypt() {
        return sharedPreferences.getBoolean("encrypt", false);
    }

    /**
     * Set encrypt value in SharedPreferences
     *
     * @param value Encrypt value
     */
    public void setEncrypt(boolean value) {
        sharedPreferences.edit().putBoolean("encrypt", value).apply();
    }

    /**
     * @return Return password length for generator from SharedPreferences
     */
    public int getLength() {
        return sharedPreferences.getInt("length", 16);
    }

    /**
     * Set password length for generator in SharedPreferences
     *
     * @param value Password length for generator value
     */
    public void setLength(int value) {
        sharedPreferences.edit().putInt("length", value).apply();
    }

    /**
     * Set default password length for generator in SharedPreferences (default value is 16)
     */
    public void setLength() {
        sharedPreferences.edit().putInt("length", 16).apply();
    }

    /**
     * @return Return show password value from SharedPreferences
     */
    public boolean getShow() {
        return sharedPreferences.getBoolean("show", false);
    }

    /**
     * Set show password value in SharedPreferences
     *
     * @param value Show password value
     */
    public void setShow(boolean value) {
        sharedPreferences.edit().putBoolean("show", value).apply();
    }
}
