package com.merive.securely.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.zxing.integration.android.IntentIntegrator;
import com.merive.securely.BuildConfig;
import com.merive.securely.R;
import com.merive.securely.api.API;
import com.merive.securely.database.Password;
import com.merive.securely.database.PasswordDB;
import com.merive.securely.fragments.BarFragment;
import com.merive.securely.fragments.EmptyFragment;
import com.merive.securely.fragments.PasswordFragment;
import com.merive.securely.fragments.PasswordListFragment;
import com.merive.securely.fragments.PasswordOptionsFragment;
import com.merive.securely.fragments.UpdateFragment;
import com.merive.securely.preferences.PreferencesManager;
import com.merive.securely.utils.Crypt;
import com.merive.securely.utils.Hex;
import com.merive.securely.utils.VibrationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public PreferencesManager preferencesManager;


    public PasswordDB db;
    private int key;

    /**
     * Called by the system when the service is first created
     *
     * @param savedInstanceState Using by super.onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);

        initVariables();

        setFragment(new PasswordListFragment());
        setBarFragment();

        checkLoginStatus();
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause()
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkEmpty();
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from
     * @param resultCode  The integer result code returned by the child activity through its setResult()
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras")
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (IntentIntegrator.parseActivityResult(requestCode, resultCode, intent) != null) {
            try {
                checkQRPattern(intent.getStringExtra("SCAN_RESULT").replace("Securely:", ""));
            } catch (Exception exc) {
                makeToast(getResources().getString(R.string.error));
            }
        }
    }

    /**
     * Check QR result pattern, if pattern is okay, add password to database.
     * Else make error toast message
     *
     * @param result QR Code result
     */
    private void checkQRPattern(String result) {
        if (result.split("-").length == 3) {
            Bundle values = new Bundle();
            values.putString("name_value", Hex.decrypt(result.split("-")[0]));
            values.putString("login_value", Hex.decrypt(result.split("-")[1]));
            values.putString("password_value", Hex.decrypt(result.split("-")[2]));
            values.putString("description_value", "");
            checkPasswordName(values);
        } else makeToast(getResources().getString(R.string.error));
    }

    /**
     * Initialize basic variables
     */
    private void initVariables() {
        preferencesManager = new PreferencesManager(this.getBaseContext());
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords").allowMainThreadQueries().build();
        key = getIntent().getIntExtra("key_value", 0);
    }

    /**
     * Set fragment to main_fragment component
     *
     * @param fragment A fragment, that will be set to main_fragment
     * @see Fragment
     */
    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.frame_layout, fragment, null).commit();
    }

    /**
     * Set BarFragment to bar_fragment component
     *
     * @see Fragment
     */
    public void setBarFragment() {
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.bar_frame_layout, new BarFragment(), null).commit();
    }

    /**
     * Set fragment to bar_fragment component
     *
     * @param fragment A fragment, that will be set to bar_fragment
     * @see Fragment
     */
    public void setBarFragment(Fragment fragment) {
        if (fragment.getArguments() == null) fragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.bar_frame_layout, fragment, null).commit();
    }

    /**
     * Check login status, if intent "delete_all" extra is true, delete all passwordsRecycler and finish MainActivity
     */
    private void checkLoginStatus() {
        if (getIntent().getBooleanExtra("delete_all", false)) {
            db.passwordDao().deleteAll();
            finish();
        } else if (getIntent().getBooleanExtra("encrypt_value", false)) {
            updateEncrypt(false);
            finish();
        }
    }

    /**
     * Checks installed version and compares it with actual version on website
     * If Securely have new version on website, will have opened UpdateFragment
     *
     * @see UpdateFragment
     */
    public void checkVersion() {
        new Thread(() -> {
            try {
                if (!(new JSONObject(new API().get()).get("version")).toString().substring(1).equals(BuildConfig.VERSION_NAME))
                    setBarFragment(UpdateFragment.newInstance(new JSONObject(new API().get())));
            } catch (IOException | JSONException ignored) {
            }
        }).start();
    }

    /**
     * Check PasswordDB on empty.
     * If database is empty, set EmptyFragment to main_fragment component and make component visible.
     * Else make component invisible
     *
     * @see PasswordDB
     * @see EmptyFragment
     */
    private void checkEmpty() {
        if (db.passwordDao().checkEmpty()) setFragment(new EmptyFragment());
    }

    /**
     * Make toast message
     *
     * @param message Toast message value
     */
    public void makeToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Add password to PasswordDB using bundle.
     * If "name" isn't in database, execute addPassword() method
     *
     * @param data Bundle with new password data
     */
    public void checkPasswordName(Bundle data) {
        if (checkNotExist(getData(data, "name_value"))) addPassword(data);
    }

    /**
     * Return string value by name from Bundle data
     *
     * @param data Bundle with data
     * @param name Name of extra in Bundle
     * @return String value by name from Bundle data
     */
    private String getData(Bundle data, String name) {
        return data.getString(name);
    }

    /**
     * Add password to database, make toast message, reload password_recycler_view and check on empty database
     *
     * @param data Bundle with new password data
     */
    private void addPassword(Bundle data) {
        addPasswordToDatabase(data);
        makeToast(getString(R.string.password_added, getData(data, "name_value")));
        setFragment(new PasswordListFragment());
        checkEmpty();
    }

    /**
     * Add Password data from Bundle to database.
     * If preferencesManager.getEncrypt() return true, encrypt login and password data
     *
     * @param data Bundle with new password data
     */
    private void addPasswordToDatabase(Bundle data) {
        db.passwordDao().insertItem(new Password(getData(data, "name_value"), preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "login_value")) : getData(data, "login_value"), preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "password_value")) : getData(data, "password_value"), getData(data, "description_value")));
    }

    /**
     * Execute when click on password row in password_recycler_view.
     * Make vibration effect and set PasswordFragment in edit mode to BarFragment
     *
     * @param name Password name what has clicked
     */
    public void clickPasswordRow(String name) {
        VibrationManager.makeVibration(getApplicationContext());
        setBarFragment(PasswordFragment.newInstance(name, preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) : db.passwordDao().getLoginByName(name), preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) : db.passwordDao().getPasswordByName(name), db.passwordDao().getDescriptionByName(name)));
    }

    /**
     * Edit password in PasswordDB using bundle.
     * If "name_before" equals "edited_name" or "edited_name" isn't in database, execute editPassword() method
     *
     * @param data Bundle with edited password data
     */
    public void checkEditPasswordName(Bundle data) {
        if (getData(data, "name_before_value").equals(getData(data, "edited_name_value")) || checkNotExist(getData(data, "edited_name_value")))
            editPassword(data);
    }

    /**
     * Edit password in database, make toast message, reload password_recycler_view and check on empty database
     *
     * @param data Bundle with new password data
     */
    private void editPassword(Bundle data) {
        editPasswordInDatabase(data);
        makeToast(getString(R.string.password_edited, getData(data, "edited_name_value")));
        setFragment(new PasswordListFragment());
    }

    /**
     * Add Password data from Bundle to database.
     * If preferencesManager.getEncrypt() return true, encrypt login and password data
     *
     * @param data Bundle with new password data
     */
    private void editPasswordInDatabase(Bundle data) {
        db.passwordDao().updateItem(getData(data, "name_before_value"), getData(data, "edited_name_value"), preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "edited_login_value")) : getData(data, "edited_login_value"), preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "edited_password_value")) : getData(data, "edited_password_value"), getData(data, "edited_description_value"));
    }

    /**
     * Delete password in database using name, reload password_recycler_view, execute checkEmpty() method and make toast message
     *
     * @param name Password name, what will be deleted
     */
    public void deletePasswordByName(String name) {
        db.passwordDao().deleteByName(name);
        setFragment(new PasswordListFragment());
        checkEmpty();
        makeToast(getString(R.string.password_deleted, name));
    }

    /**
     * Update setting data in SharedPreferences and make toast
     *
     * @param length  Password length for generator value
     * @param show    Show password value
     * @param delete  Delete password after login errors value
     * @param encrypt Encrypt password data value
     */
    public void saveSettings(int length, boolean show, boolean delete, boolean encrypt) {
        updateShow(show);
        updateLength(length);
        updateDelete(delete);
        updateEncrypt(encrypt);
        makeToast(getString(R.string.settings_saved));
    }

    /**
     * Delete all passwordsRecycler, reload password_recycler_view, execute checkEmpty() method and make toast message
     */
    public void deleteAllPasswords() {
        db.passwordDao().deleteAll();
        setFragment(new PasswordListFragment());
        checkEmpty();
        makeToast(getString(R.string.all_passwords_deleted));
    }

    /**
     * Update password length for generator value in SharedPreferences
     *
     * @param length Password length for generator value
     */
    private void updateLength(int length) {
        if (!String.valueOf(length).isEmpty()) preferencesManager.setLength(length);
        else preferencesManager.setLength();
    }

    /**
     * Update show password value in SharedPreferences
     *
     * @param show Show password value
     */
    private void updateShow(boolean show) {
        preferencesManager.setShow(show);
    }

    /**
     * Update delete password value in LoginActivity
     *
     * @param delete Delete password after login errors value
     */
    private void updateDelete(boolean delete) {
        if (delete != getIntent().getBooleanExtra("delete_value", false)) {
            startActivity(new Intent(this, LoginActivity.class).putExtra("delete_edit", true).putExtra("delete_value", delete));
        }
    }

    /**
     * Update encrypt password data value in SharedPreferences
     *
     * @param encrypt Encrypt password data value
     */
    public void updateEncrypt(boolean encrypt) {
        if (encrypt != preferencesManager.getEncrypt()) {
            preferencesManager.setEncrypt(encrypt);
            if (encrypt) encryptData();
            else decryptData();
        }
    }

    /**
     * Encrypt data in database
     */
    private void encryptData() {
        encryptAllLogins();
        encryptAllPasswords();
    }

    /**
     * Decrypt data in database
     */
    private void decryptData() {
        decryptAllLogins();
        decryptAllPasswords();
    }

    /**
     * Encrypt all login values in database
     */
    private void encryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) encryptLogin(s);
    }

    /**
     * Encrypt login value in database
     */
    private void encryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).encrypt(db.passwordDao().getLoginByName(name)));
    }

    /**
     * Encrypt all password values in database
     */
    private void encryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) encryptPassword(s);
    }

    /**
     * Encrypt password value in database
     */
    private void encryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).encrypt(db.passwordDao().getPasswordByName(name)));
    }

    /**
     * Decrypt all login values in database
     */
    private void decryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) decryptLogin(s);
    }

    /**
     * Decrypt login value in database
     */
    private void decryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)));
    }

    /**
     * Decrypt all password values in database
     */
    private void decryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) decryptPassword(s);
    }

    /**
     * Decrypt password value in database
     */
    private void decryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)));
    }

    /**
     * Add password value to clipboard and makes toast message
     *
     * @param name Password name, that will be added to clipboard
     */
    public void addToClipboard(String name) {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(name, preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) : db.passwordDao().getPasswordByName(name)));
        makeToast(getString(R.string.password_added_to_clipboard, name));
    }

    /**
     * Check what password name isn't in database
     * Make toast message if it's in database
     *
     * @param name Password name to check
     * @return Password name is in database or isn't (true/false value)
     */
    private boolean checkNotExist(String name) {
        if (!db.passwordDao().checkNotExist(name))
            makeToast(getResources().getString(R.string.password_name_already_taken));
        return db.passwordDao().checkNotExist(name);
    }

    /**
     * Encrypt password data for QR Code
     *
     * @param name Password what will be encrypted
     * @return Encrypted password data for QR Code
     */
    public String getEncryptPasswordValues(String name) {
        return getString(R.string.securely_hex, Hex.encrypt(name), Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) : db.passwordDao().getLoginByName(name)), Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) : db.passwordDao().getPasswordByName(name)));
    }

    /**
     * Open PasswordOptionsFragment by password name
     *
     * @param name Password name what will show in PasswordOptionsFragment
     * @see PasswordOptionsFragment
     */
    public void clickPasswordOptions(String name) {
        VibrationManager.makeVibration(getApplicationContext());
        setBarFragment(PasswordOptionsFragment.newInstance(name));
    }
}
