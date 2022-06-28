package com.merive.securely.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.zxing.integration.android.IntentIntegrator;
import com.merive.securely.BuildConfig;
import com.merive.securely.R;
import com.merive.securely.adapter.PasswordAdapter;
import com.merive.securely.database.Password;
import com.merive.securely.database.PasswordDB;
import com.merive.securely.fragments.BarFragment;
import com.merive.securely.fragments.ConfirmFragment;
import com.merive.securely.fragments.EmptyFragment;
import com.merive.securely.fragments.PasswordFragment;
import com.merive.securely.fragments.PasswordShareFragment;
import com.merive.securely.fragments.UpdateFragment;
import com.merive.securely.preferences.PreferencesManager;
import com.merive.securely.utils.Crypt;
import com.merive.securely.utils.Hex;
import com.merive.securely.utils.VibrationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public PreferencesManager preferencesManager;

    private RecyclerView passwords;

    private PasswordDB db;
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

        initComponents();
        initVariables();

        setBarFragment();

        checkLoginStatus();
        checkVersion();
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause()
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkEmpty();
        new GetPasswordData().execute();
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
            values.putString("name", Hex.decrypt(result.split("-")[0]));
            values.putString("login", Hex.decrypt(result.split("-")[1]));
            values.putString("password", Hex.decrypt(result.split("-")[2]));
            values.putString("description", "");
            checkPasswordName(values);
        } else makeToast(getResources().getString(R.string.error));
    }

    /**
     * Initialize basic layout components
     */
    private void initComponents() {
        passwords = findViewById(R.id.password_recycler_view);
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
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.main_fragment, fragment, null)
                .commit();
    }

    /**
     * Set BarFragment to bar_fragment component
     *
     * @see Fragment
     */
    public void setBarFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.bar_fragment, new BarFragment(), null)
                .commit();
    }

    /**
     * Set fragment to bar_fragment component
     *
     * @param fragment A fragment, that will be set to bar_fragment
     * @see Fragment
     */
    public void setBarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.bar_fragment, fragment, null)
                .commit();
    }

    /**
     * Check login status, if intent "delete_all" extra is true, delete all passwords and finish MainActivity
     */
    private void checkLoginStatus() {
        if (getIntent().getBooleanExtra("delete_all", false)) {
            db.passwordDao().deleteAll();
            finish();
        }
    }

    /**
     * Check version on website and open UpdateFragment if installed version and version on website isn't the same
     *
     * @see UpdateFragment
     */
    private void checkVersion() {
        new Thread(() -> {
            try {
                if (!getVersionOnSite().equals(BuildConfig.VERSION_NAME))
                    setUpdateFragment(getVersionOnSite());
            } catch (Exception ignored) {
            }
        }).start();
    }

    /**
     * Get Securely version on website
     *
     * @return Version from website
     * @throws IOException Ignored
     */
    private String getVersionOnSite() throws IOException {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(new URL(getResources().getString(R.string.website)).openStream(), StandardCharsets.UTF_8));
            for (String line; (line = reader.readLine()) != null; ) builder.append(line.trim());
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
        }
        return builder.substring(builder.indexOf("<i>") + "<i>".length()).substring(1,
                builder.substring(builder.indexOf("<i>") + "<i>".length()).indexOf("</i>"));
    }

    /**
     * Set UpdateFragment to bar_fragment component
     *
     * @param version New application version
     * @see UpdateFragment
     */
    private void setUpdateFragment(String version) {
        setBarFragment(UpdateFragment.newInstance(version));
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
        if (db.passwordDao().checkEmpty()) {
            setFragment(new EmptyFragment());
            findViewById(R.id.main_fragment).setVisibility(View.VISIBLE);
        } else findViewById(R.id.main_fragment).setVisibility(View.INVISIBLE);
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
        new GetPasswordData().execute();
        checkEmpty();
    }

    /**
     * Add Password data from Bundle to database.
     * If preferencesManager.getEncrypt() return true, encrypt login and password data
     *
     * @param data Bundle with new password data
     */
    private void addPasswordToDatabase(Bundle data) {
        db.passwordDao().insertItem(new Password(
                getData(data, "name_value"),
                preferencesManager.getEncrypt() ?
                        new Crypt(key).encrypt(getData(data, "login_value")) :
                        getData(data, "login_value"),
                preferencesManager.getEncrypt() ?
                        new Crypt(key).encrypt(getData(data, "password_value")) :
                        getData(data, "password_value"),
                getData(data, "description_value")));
    }

    /**
     * Execute when click on password row in password_recycler_view.
     * Make vibration effect and set PasswordFragment in edit mode to BarFragment
     *
     * @param name Password name what has clicked
     */
    private void clickPasswordRow(String name) {
        VibrationManager.makeVibration(getApplicationContext());
        setBarFragment(PasswordFragment.newInstance(
                name,
                preferencesManager.getEncrypt() ?
                        new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) :
                        db.passwordDao().getLoginByName(name),
                preferencesManager.getEncrypt() ?
                        new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) :
                        db.passwordDao().getPasswordByName(name),
                db.passwordDao().getDescriptionByName(name)));
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
        new GetPasswordData().execute();
    }

    /**
     * Add Password data from Bundle to database.
     * If preferencesManager.getEncrypt() return true, encrypt login and password data
     *
     * @param data Bundle with new password data
     */
    private void editPasswordInDatabase(Bundle data) {
        db.passwordDao().updateItem(
                getData(data, "name_before_value"),
                getData(data, "edited_name_value"),
                preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "edited_login_value"))
                        : getData(data, "edited_login_value"),
                preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "edited_password_value"))
                        : getData(data, "edited_password_value"),
                getData(data, "edited_description_value")
        );
    }

    /**
     * Delete password in database using name, reload password_recycler_view, execute checkEmpty() method and make toast message
     *
     * @param name Password name, what will be deleted
     */
    public void deletePasswordByName(String name) {
        db.passwordDao().deleteByName(name);
        new GetPasswordData().execute();
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
     * Delete all passwords, reload password_recycler_view, execute checkEmpty() method and make toast message
     */
    public void deleteAllPasswords() {
        db.passwordDao().deleteAll();
        new GetPasswordData().execute();
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
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("delete_edit", true)
                    .putExtra("delete_value", delete));
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
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText(name,
                        preferencesManager.getEncrypt() ?
                                new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) :
                                db.passwordDao().getPasswordByName(name)));
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getEncryptPasswordValues(String name) {
        return getString(R.string.securely_hex, Hex.encrypt(name),
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) : db.passwordDao().getLoginByName(name)),
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) : db.passwordDao().getPasswordByName(name)));
    }

    /**
     * Load password data to password_recycler_view component and sets listeners for it
     *
     * @param passwordList List of password data
     */
    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        passwords.setAdapter(new PasswordAdapter(passwordList, this::clickPasswordRow, this::openPasswordShareFragment));
    }

    /**
     * Open PasswordShareFragment by password name
     *
     * @param name Password name what will show in PasswordShareFragment
     * @see PasswordShareFragment
     */
    private void openPasswordShareFragment(String name) {
        VibrationManager.makeVibration(getApplicationContext());
        setBarFragment(PasswordShareFragment.newInstance(name));
    }

    private class GetPasswordData extends AsyncTask<Void, Void, List<Password>> {

        /**
         * Load password data
         *
         * @param params Ignored
         * @return Password data in List object
         */
        @Override
        protected List<Password> doInBackground(Void... params) {
            return db.passwordDao().getAll();
        }

        /**
         * Execute after doInBackground() method and loads password data to password_recycler_view component
         *
         * @param passwords Password data in List object
         */
        @Override
        protected void onPostExecute(List<Password> passwords) {
            super.onPostExecute(passwords);
            loadRecyclerView(passwords);
        }
    }
}
