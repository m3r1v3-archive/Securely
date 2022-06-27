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
     * Checks QR result pattern, if pattern is okay, add password to database.
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
     * Initializes basic layout components
     */
    private void initComponents() {
        passwords = findViewById(R.id.password_recycler_view);
    }

    /**
     * Initializes basic variables
     */
    private void initVariables() {
        preferencesManager = new PreferencesManager(this.getBaseContext());
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords").allowMainThreadQueries().build();
        key = getIntent().getIntExtra("key_value", 0);
    }

    /**
     * Sets fragment to main_fragment component
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
     * Sets BarFragment to bar_fragment component
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
     * Sets fragment to bar_fragment component
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
     * Checks login status, if intent "delete_all" extra is true, deletes all passwords and finish MainActivity
     */
    private void checkLoginStatus() {
        if (getIntent().getBooleanExtra("delete_all", false)) {
            db.passwordDao().deleteAll();
            finish();
        }
    }

    /**
     * Checks version on website and opens UpdateFragment if installed version and version on website isn't the same
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
     * Gets Securely version on website
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
     * Sets UpdateFragment to bar_fragment component
     *
     * @param version New application version
     * @see UpdateFragment
     */
    private void setUpdateFragment(String version) {
        setBarFragment(UpdateFragment.newInstance(version));
    }

    /**
     * Checks PasswordDB on empty.
     * If database is empty, sets EmptyFragment to main_fragment component and makes component visible.
     * Else makes component invisible
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
     * Makes vibration effect if Ringer Mode isn't Mute
     */
    public void makeVibration() {
        if ((((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0))
            VibrationManager.makeVibration(getApplicationContext());
    }

    /**
     * Makes toast message
     *
     * @param message Toast message value
     */
    public void makeToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Adds password to PasswordDB using bundle.
     * If "name" isn't in database, executes addPassword() method
     *
     * @param data Bundle with new password data
     */
    public void checkPasswordName(Bundle data) {
        if (checkNotExist(getData(data, "name"))) addPassword(data);
    }

    /**
     * Returns string value by name from Bundle data
     *
     * @param data Bundle with data
     * @param name Name of extra in Bundle
     * @return String value by name from Bundle data
     */
    private String getData(Bundle data, String name) {
        return data.getString(name);
    }

    /**
     * Adds password to database, makes toast message, reloads password_recycler_view and check on empty database
     *
     * @param data Bundle with new password data
     */
    private void addPassword(Bundle data) {
        addPasswordToDatabase(data);
        makeToast(getData(data, "name") + " was " + "added");
        new GetPasswordData().execute();
        checkEmpty();
    }

    /**
     * Adds Password data from Bundle to database.
     * If preferencesManager.getEncrypt() returns true, encrypts login and password data
     *
     * @param data Bundle with new password data
     */
    private void addPasswordToDatabase(Bundle data) {
        db.passwordDao().insertItem(new Password(
                getData(data, "name"),
                preferencesManager.getEncrypt() ?
                        new Crypt(key).encrypt(getData(data, "login")) :
                        getData(data, "login"),
                preferencesManager.getEncrypt() ?
                        new Crypt(key).encrypt(getData(data, "password")) :
                        getData(data, "password"),
                getData(data, "description")));
    }

    /**
     * Executes when click on password row in password_recycler_view.
     * Makes vibration effect and sets PasswordFragment in edit mode to BarFragment
     *
     * @param name Password name what has clicked
     */
    private void clickPasswordRow(String name) {
        makeVibration();
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
     * Edits password in PasswordDB using bundle.
     * If "name_before" equals "edited_name" or "edited_name" isn't in database, executes editPassword() method
     *
     * @param data Bundle with edited password data
     */
    public void checkEditPasswordName(Bundle data) {
        if (getData(data, "name_before").equals(getData(data, "edited_name")) || checkNotExist(getData(data, "edited_name")))
            editPassword(data);
    }

    /**
     * Edits password in database, makes toast message, reloads password_recycler_view and check on empty database
     *
     * @param data Bundle with new password data
     */
    private void editPassword(Bundle data) {
        editPasswordInDatabase(data);
        makeToast(getData(data, "edited_name") + " was " + "edited");
        new GetPasswordData().execute();
    }

    /**
     * Adds Password data from Bundle to database.
     * If preferencesManager.getEncrypt() returns true, encrypts login and password data
     *
     * @param data Bundle with new password data
     */
    private void editPasswordInDatabase(Bundle data) {
        db.passwordDao().updateItem(
                getData(data, "name_before"),
                getData(data, "edited_name"),
                preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "edited_login"))
                        : getData(data, "edited_login"),
                preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "edited_password"))
                        : getData(data, "edited_password"),
                getData(data, "edited_description")
        );
    }

    /**
     * Deletes password in database using name, reload password_recycler_view, executes checkEmpty() method and makes toast message
     *
     * @param name Password name, what will be deleted
     */
    public void deletePasswordByName(String name) {
        db.passwordDao().deleteByName(name);
        new GetPasswordData().execute();
        checkEmpty();
        makeToast(name + " was deleted");
    }

    /**
     * Updates settings data in SharedPreferences and makes toast
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
        makeToast("Settings saved");
    }

    /**
     * Deletes all passwords, reloads password_recycler_view, executes checkEmpty() method and makes toast message
     */
    public void deleteAllPasswords() {
        db.passwordDao().deleteAll();
        new GetPasswordData().execute();
        checkEmpty();
        makeToast("All passwords have been deleted");
    }

    /**
     * Updates password length for generator value in SharedPreferences
     *
     * @param length Password length for generator value
     */
    private void updateLength(int length) {
        if (!String.valueOf(length).isEmpty()) preferencesManager.setLength(length);
        else preferencesManager.setLength();
    }

    /**
     * Updates show password value in SharedPreferences
     *
     * @param show Show password value
     */
    private void updateShow(boolean show) {
        preferencesManager.setShow(show);
    }

    /**
     * Updates delete password value in LoginActivity
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
     * Updates encrypt password data value in SharedPreferences
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
     * Encrypts data in database
     */
    private void encryptData() {
        encryptAllLogins();
        encryptAllPasswords();
    }

    /**
     * Decrypts data in database
     */
    private void decryptData() {
        decryptAllLogins();
        decryptAllPasswords();
    }

    /**
     * Encrypts all login values in database
     */
    private void encryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) encryptLogin(s);
    }

    /**
     * Encrypts login value in database
     */
    private void encryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).encrypt(db.passwordDao().getLoginByName(name)));
    }

    /**
     * Encrypts all password values in database
     */
    private void encryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) encryptPassword(s);
    }

    /**
     * Encrypts password value in database
     */
    private void encryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).encrypt(db.passwordDao().getPasswordByName(name)));
    }

    /**
     * Decrypts all login values in database
     */
    private void decryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) decryptLogin(s);
    }

    /**
     * Decrypts login value in database
     */
    private void decryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)));
    }

    /**
     * Decrypts all password values in database
     */
    private void decryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) decryptPassword(s);
    }

    /**
     * Decrypts password value in database
     */
    private void decryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)));
    }

    /**
     * Adds password value to clipboard and makes toast message
     *
     * @param name Password name, that will be added to clipboard
     */
    public void addToClipboard(String name) {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText(name,
                        preferencesManager.getEncrypt() ?
                                new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) :
                                db.passwordDao().getPasswordByName(name)));
        makeToast(name + " added to clipboard");
    }

    /**
     * Checks what password name isn't in database
     * Makes toast message if it's in database
     *
     * @param name Password name to check
     * @return Password name is in database or isn't (true/false value)
     */
    private boolean checkNotExist(String name) {
        if (!db.passwordDao().checkNotExist(name)) makeToast("Password name already taken");
        return db.passwordDao().checkNotExist(name);
    }

    /**
     * Encrypts password data for QR Code
     *
     * @param name Password what will be encrypted
     * @return Encrypted password data for QR Code
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getEncryptPasswordValues(String name) {
        return ("Securely:" + Hex.encrypt(name) + "-" +
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) : db.passwordDao().getLoginByName(name)) + "-" +
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) : db.passwordDao().getPasswordByName(name)));
    }

    /**
     * Loads password data to password_recycler_view component and sets listeners for it
     *
     * @param passwordList List of password data
     */
    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        passwords.setAdapter(new PasswordAdapter(passwordList, this::clickPasswordRow, this::openPasswordShareFragment));
    }

    /**
     * Opens PasswordShareFragment by password name
     *
     * @param name Password name what will show in PasswordShareFragment
     * @see PasswordShareFragment
     */
    private void openPasswordShareFragment(String name) {
        makeVibration();
        setBarFragment(PasswordShareFragment.newInstance(name));
    }

    private class GetPasswordData extends AsyncTask<Void, Void, List<Password>> {

        /**
         * Loads password data
         *
         * @param params Ignored
         * @return Password data in List object
         */
        @Override
        protected List<Password> doInBackground(Void... params) {
            return db.passwordDao().getAll();
        }

        /**
         * Executes after doInBackground() method and loads password data to password_recycler_view component
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
