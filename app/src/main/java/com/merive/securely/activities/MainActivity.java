package com.merive.securely.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.zxing.integration.android.IntentResult;
import com.merive.securely.BuildConfig;
import com.merive.securely.R;
import com.merive.securely.adapter.PasswordAdapter;
import com.merive.securely.database.Password;
import com.merive.securely.database.PasswordDB;
import com.merive.securely.fragments.BarFragment;
import com.merive.securely.fragments.ConfirmFragment;
import com.merive.securely.fragments.EmptyFragment;
import com.merive.securely.fragments.PasswordFragment;
import com.merive.securely.fragments.PasswordSharingFragment;
import com.merive.securely.fragments.SettingsFragment;
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
import java.util.Objects;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    RecyclerView passwords;
    PasswordAdapter adapter;
    PasswordDB db;
    PreferencesManager preferencesManager;
    int key;

    /**
     * This method is the start point at the MainActivity.
     *
     * @param savedInstanceState Used by super.onCreate method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);

        initLayoutVariables();
        setBarFragment();

        preferencesManager = new PreferencesManager(this.getBaseContext());
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords")
                .allowMainThreadQueries().build();
        key = getIntent().getIntExtra("key", 0);

        checkLoginStatus();
        checkVersion();
    }

    /**
     * This method started after Activity is resumed.
     * There are checking database on empty and loading values for Password RecyclerView.
     *
     * @see RecyclerView
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkEmpty();
        new GetPasswordData().execute();
    }

    /**
     * This method checks result of QR scanning.
     *
     * @param requestCode The code what was requested.
     * @param resultCode  The code what was returned.
     * @param intent      Intent object.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            try {
                checkPatternQR(intent.getStringExtra("SCAN_RESULT").replace("Securely:", ""));
            } catch (Exception exc) {
                makeToast(getResources().getString(R.string.error));
            }
        }
    }

    /**
     * This method checks QR result on pattern.
     *
     * @param result QR result.
     * @see Pattern
     */
    private void checkPatternQR(String result) {
        if (result.split("-").length == 3) {
            Bundle values = new Bundle();
            values.putString("name", Hex.decrypt(result.split("-")[0]));
            values.putString("login", Hex.decrypt(result.split("-")[1]));
            values.putString("password", Hex.decrypt(result.split("-")[2]));
            values.putString("description", "");
            addNewPassword(values);
        } else makeToast(getResources().getString(R.string.error));
    }

    /**
     * This method is assigns main layout variables.
     */
    private void initLayoutVariables() {
        passwords = findViewById(R.id.password_recycler_view);
    }

    /**
     * This method set Fragment to main_fragment element in layout.
     *
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
     * This method set Fragment to bar_fragment element in layout.
     *
     * @see Fragment
     */
    private void setBarFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.bar_fragment, new BarFragment(), null)
                .commit();
    }

    /**
     * This method set Fragment to pad_fragment element in layout.
     *
     * @see Fragment
     */
    public void setPadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.pad_fragment, fragment, null)
                .commit();
    }

    /**
     * This method remove Fragment to pad_fragment element in layout.
     *
     * @see Fragment
     */
    public void removePadFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.pad_fragment, new BarFragment(), null)
                .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.pad_fragment)))
                .commit();
    }

    /**
     * This method is checking key status on breaking in LoginActivity.
     *
     * @see LoginActivity
     */
    private void checkLoginStatus() {
        if (getIntent().getBooleanExtra("status", false)) {
            db.passwordDao().deleteAll();
            finish();
        }
    }

    /**
     * This method is checking actual version on website.
     * If Securely got update on website, will open UpdateFragment.
     *
     * @see UpdateFragment
     */
    private void checkVersion() {
        new Thread(() -> {
            try {
                if (!getVersionOnSite().equals(BuildConfig.VERSION_NAME))
                    openUpdateFragment(getVersionOnSite());
            } catch (Exception ignored) {
            }
        }).start();
    }

    /**
     * This method is getting actual Securely version on website.
     *
     * @return Actual Version Code.
     * @throws IOException Input/Output Exception.
     * @see IOException
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
     * This method is opening UpdateFragment.
     *
     * @param newVersion Actual Application Version.
     * @see UpdateFragment
     */
    private void openUpdateFragment(String newVersion) {
        UpdateFragment.newInstance(BuildConfig.VERSION_NAME, newVersion).show(getSupportFragmentManager(), "update_fragment");
    }

    /**
     * This method is checking, that database is empty.
     * If database is empty, TextView empty set Visible state.
     * Else TextView empty set Invisible state.
     *
     * @see android.widget.TextView
     */
    private void checkEmpty() {
        if (db.passwordDao().checkEmpty()) {
            setFragment(new EmptyFragment());
            findViewById(R.id.main_fragment_container).setVisibility(View.VISIBLE);
        } else findViewById(R.id.main_fragment_container).setVisibility(View.INVISIBLE);
    }

    /**
     * This method is making vibration.
     * If checkSoundMode() is true, VibrationManager will make Vibration.
     *
     * @see VibrationManager
     */
    public void makeVibration() {
        if (checkSoundMode())
            VibrationManager.makeVibration(getApplicationContext());
    }

    /**
     * This method is checking ringer mode of device.
     *
     * @return If mode isn't mute, return True.
     */
    private boolean checkSoundMode() {
        return (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0);
    }

    /**
     * This method is using by BarFragment.
     * The method is make vibration and open PasswordFragment for adding new Password to Database.
     *
     * @see BarFragment
     * @see PasswordFragment
     */
    public void clickAdd() {
        makeVibration();
        setPadFragment(PasswordFragment.newInstance(preferencesManager.getLength(), preferencesManager.getShow()));
    }

    /**
     * This method makes toast message
     *
     * @param message Toast message text
     */
    public void makeToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is adding new password to database.
     * If name already used, the password isn't adding.
     * Else password will be adding to database, will be making Toast and will be reloading RecyclerView.
     *
     * @param data Bundle with Password Data.
     * @see PasswordFragment
     * @see RecyclerView
     */
    public void addNewPassword(Bundle data) {
        if (checkNotExist(getData(data, "name"))) {
            addPasswordInDatabase(data);
            makeToast(getData(data, "name") + " was " + "added");
            new GetPasswordData().execute();
            checkEmpty();
        }
    }

    /**
     * This method is getting values for Bundle.
     *
     * @param data Contains Password Values.
     * @param name Value Name.
     * @return Value from Bundle by Value Name.
     * @see Bundle
     */
    private String getData(Bundle data, String name) {
        return data.getString(name);
    }

    /**
     * This method is adding Password Data to Database.
     * If it necessary, Login and Password values will encrypt.
     *
     * @param data Contains Password Data.
     * @see Bundle
     * @see Crypt
     */
    private void addPasswordInDatabase(Bundle data) {
        db.passwordDao().insertItem(
                new Password(
                        getData(data, "name"),
                        preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "login")) :
                                getData(data, "login"),
                        preferencesManager.getEncrypt() ? new Crypt(key).encrypt(getData(data, "password")) :
                                getData(data, "password"),
                        getData(data, "description")
                ));
    }

    /**
     * This method is using by RecyclerView Rows.
     * The method is opening PasswordFragment for editing Password Data.
     *
     * @param name Row Name Value
     * @see PasswordDB
     * @see RecyclerView
     * @see PasswordFragment
     */
    private void clickEditPassword(String name) {
        makeVibration();
        setPadFragment(PasswordFragment.newInstance(
                name,
                preferencesManager.getEncrypt() ?
                        new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) :
                        db.passwordDao().getLoginByName(name),
                preferencesManager.getEncrypt() ?
                        new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) :
                        db.passwordDao().getPasswordByName(name),
                db.passwordDao().getDescriptionByName(name),
                preferencesManager.getLength(),
                preferencesManager.getShow()));
    }

    /**
     * This method is checking Edited Password Data and loading them to Database.
     * After Loading Edited Data to Database, executes makeToast() and RecyclerView is reloading.
     *
     * @param data Contains edited Password Data
     * @see PasswordFragment
     * @see PasswordDB
     * @see RecyclerView
     * @see Bundle
     */
    public void editPassword(Bundle data) {
        if (getData(data, "name_before").equals(getData(data, "edited_name"))
                || checkNotExist(getData(data, "edited_name"))) {
            editPasswordInDatabase(data);
            makeToast(getData(data, "edited_name") + " was " + "edited");
            new GetPasswordData().execute();
        }
    }

    /**
     * This method is rewrite Edited Password Data to Database.
     * If it necessary, Crypt is Encrypting Login and Password Values.
     *
     * @param data Contains edited Password Data.
     * @see PasswordDB
     * @see Crypt
     * @see Bundle
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
     * This method is deleting Password from Database by Password Name in Database.
     * After deleting RecyclerView is reloading, Database is checking on empty and will making Toast.
     *
     * @param name Password Name in Database.
     * @see PasswordDB
     * @see RecyclerView
     */
    public void deletePasswordByName(String name) {
        db.passwordDao().deleteByName(name);
        new GetPasswordData().execute();
        checkEmpty();
        makeToast(name + " was deleted");
    }

    /**
     * This method is using by BarFragment. The method executes after clicking on Lock Button.
     * After click, will execute makeVibration() and will start opening LoginActivity.
     * MainActivity will close.
     *
     * @see BarFragment
     * @see LoginActivity
     */
    public void clickLock() {
        makeVibration();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * This method is using by BarFragment. The method executes after long clicking on Lock Button.
     * After click, will make vibration, will open ConfirmFragment for changing key.
     *
     * @see BarFragment
     * @see ConfirmFragment
     */
    public void longClickLock() {
        ConfirmFragment.newInstance(true).show(getSupportFragmentManager(), "confirm_fragment");
    }

    /**
     * This method is using by BarFragment. The method executes after clicking on Settings Button.
     * After click, will make vibration and will open SettingsFragment.
     *
     * @see BarFragment
     * @see SettingsFragment
     */
    public void clickSettings() {
        makeVibration();
        SettingsFragment.newInstance(
                preferencesManager.getLength(),
                preferencesManager.getShow(),
                getIntent().getBooleanExtra("delete", false),
                preferencesManager.getEncrypt()).show(getSupportFragmentManager(), "settings_fragment");
    }

    /**
     * This method is saving Settings Values.
     * The method is update showPassword, deleting, encrypting, and password length values in sharedPreference.
     *
     * @param length   Password Generator length.
     * @param show     Always show password in PasswordFragment.
     * @param deleting Delete all passwords after 15 errors in LoginActivity.
     * @param encrypt  Encrypt Login and Password Values in Database.
     * @see SettingsFragment
     * @see PasswordFragment
     * @see LoginActivity
     * @see PasswordDB
     */
    public void saveSettings(int length, boolean show, boolean deleting, boolean encrypt) {
        updateShowPassword(show);
        updateLengthOfPassword(length);
        updateDeleting(deleting);
        updateEncrypting(encrypt);
        makeToast("Settings saved");
    }

    /**
     * This method is deleting all passwords from Database.
     * The method can be executing in SettingsFragment or LoginActivity after 15 errors.
     *
     * @see PasswordDB
     * @see SettingsFragment
     * @see LoginActivity
     */
    public void deleteAllPasswords() {
        db.passwordDao().deleteAll();
        new GetPasswordData().execute();
        checkEmpty();
        makeToast("All passwords have been deleted");
    }

    /**
     * This method is updating Password Generator Length in sharedPreference.
     * If length is null, Generator Length is 16 (Default Value).
     *
     * @param length New Password Generator Length.
     * @see PasswordFragment
     * @see SharedPreferences
     */
    private void updateLengthOfPassword(int length) {
        if (!String.valueOf(length).isEmpty())
            preferencesManager.setLength(length);
        else preferencesManager.setLength();
    }

    /**
     * This method is updating Always Show Password in sharedPreference.
     *
     * @param show New Always Show Password Value.
     * @see PasswordFragment
     * @see SharedPreferences
     */
    private void updateShowPassword(boolean show) {
        preferencesManager.setShow(show);
    }

    /**
     * This method is updating Delete After 15 Errors in sharedPreference.
     *
     * @param deleting New Delete After 15 Errors Value.
     * @see LoginActivity
     * @see SharedPreferences
     */
    private void updateDeleting(boolean deleting) {
        if (deleting != getIntent().getBooleanExtra("delete", false)) {
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("status", true)
                    .putExtra("delete", deleting));
        }
    }

    /**
     * This method is updating Encrypt Login and Password Values in sharedPreference.
     *
     * @see SettingsFragment
     * @see PasswordFragment
     * @see SharedPreferences
     */
    public void updateEncrypting(boolean encrypting) {
        if (encrypting != preferencesManager.getEncrypt()) {
            if (encrypting) {
                encryptAllLogins();
                encryptAllPasswords();
            } else {
                decryptAllLogins();
                decryptAllPasswords();
            }
            preferencesManager.setEncrypt(encrypting);
        }
    }

    /**
     * This method is encrypting all Logins in Database.
     *
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void encryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) encryptLogin(s);
    }

    /**
     * This method is encrypting Login in Database.
     *
     * @param name Password name in Database for encrypting Login
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void encryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).encrypt(db.passwordDao().getLoginByName(name)));
    }

    /**
     * This method is encrypting all Passwords in Database.
     *
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void encryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) encryptPassword(s);
    }

    /**
     * This method is encrypting Password in Database.
     *
     * @param name Password name in Database for encrypting Password
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void encryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).encrypt(db.passwordDao().getPasswordByName(name)));
    }

    /**
     * This method is decrypting all Logins in Database.
     *
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void decryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) decryptLogin(s);
    }

    /**
     * This method is decrypting Login in Database.
     *
     * @param name Password name in Database for decrypting Login
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void decryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)));
    }

    /**
     * This method is decrypting all Passwords in Database.
     *
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void decryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) decryptPassword(s);
    }

    /**
     * This method is decrypting Password in Database.
     *
     * @param name Password name in Database for decrypting Password
     * @see PasswordDB
     * @see com.merive.securely.database.PasswordDao
     */
    private void decryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)));
    }

    /**
     * This method is adding Password Value to Clipboard.
     * After click will make vibration and Password Value will add to Clipboard.
     *
     * @param name Name of Password Row
     * @see ClipboardManager
     */
    public void addToClipboard(String name) {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText(name,
                        preferencesManager.getEncrypt() ?
                                new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) :
                                db.passwordDao().getPasswordByName(name)));
        makeToast(name + " was copied");
    }

    /**
     * This method is checking Password Name is exist in Database.
     *
     * @param name New Password Name.
     * @return True is Password Name is exist.
     * @see PasswordDB
     * @see PasswordFragment
     * @see com.merive.securely.database.PasswordDao
     */
    private boolean checkNotExist(String name) {
        if (!db.passwordDao().checkNotExist(name)) makeToast("Name already taken");
        return db.passwordDao().checkNotExist(name);
    }

    /**
     * This method is opening ConfirmFragment for confirming password deleting.
     *
     * @param name Password Name what will be deleting.
     * @see ConfirmFragment
     */
    public void openConfirmPasswordDelete(String name) {
        ConfirmFragment.newInstance(name).show(getSupportFragmentManager(), "confirm_fragment");
    }

    /**
     * This method is opening ConfirmFragment for confirming all passwords deleting.
     *
     * @see ConfirmFragment
     */
    public void openConfirmAllPasswordsDelete() {
        ConfirmFragment.newInstance().show(getSupportFragmentManager(), "confirm_fragment");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getEncryptedValues(String name) {
        return ("Securely:" + Hex.encrypt(name) + "-" +
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) : db.passwordDao().getLoginByName(name)) + "-" +
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) : db.passwordDao().getPasswordByName(name)));
    }

    /**
     * This method is loading RecyclerView, load values, set clickListeners.
     *
     * @see RecyclerView
     * @see PasswordAdapter
     * @see Crypt
     * @see PasswordDB
     */
    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(passwordList,
                this::clickEditPassword,
                this::openPasswordSharingFragment);
        passwords.setAdapter(adapter);
    }

    private void openPasswordSharingFragment(String name) {
        makeVibration();
        setPadFragment(PasswordSharingFragment.newInstance(name));
    }

    private class GetPasswordData extends AsyncTask<Void, Void, List<Password>> {

        /**
         * This method is loading Password Data.
         *
         * @param params Not using
         * @return Password Data
         */
        @Override
        protected List<Password> doInBackground(Void... params) {
            return db.passwordDao().getAll();
        }

        /**
         * This method is executing after doInBackground() and loadValues to RecyclerView.
         *
         * @param passwords Password Data
         */
        @Override
        protected void onPostExecute(List<Password> passwords) {
            super.onPostExecute(passwords);
            loadRecyclerView(passwords);
        }
    }
}
