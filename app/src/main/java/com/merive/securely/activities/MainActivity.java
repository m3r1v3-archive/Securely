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
import com.merive.securely.fragments.PasswordShareFragment;
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
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    RecyclerView passwords;
    PasswordAdapter adapter;
    PasswordDB db;
    PreferencesManager preferencesManager;
    int key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);

        initLayoutVariables();
        setBarFragment(new BarFragment());

        preferencesManager = new PreferencesManager(this.getBaseContext());
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords")
                .allowMainThreadQueries().build();
        key = getIntent().getIntExtra("key_value", 0);

        checkLoginStatus();
        checkVersion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEmpty();
        new GetPasswordData().execute();
    }

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

    private void initLayoutVariables() {
        passwords = findViewById(R.id.password_recycler_view);
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.main_fragment, fragment, null)
                .commit();
    }

    public void setBarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.breath_in, R.anim.breath_out)
                .replace(R.id.bar_fragment, fragment, null)
                .commit();
    }

    private void checkLoginStatus() {
        if (getIntent().getBooleanExtra("delete_all", false)) {
            db.passwordDao().deleteAll();
            finish();
        }
    }

    private void checkVersion() {
        new Thread(() -> {
            try {
                if (!getVersionOnSite().equals(BuildConfig.VERSION_NAME))
                    openUpdateFragment(getVersionOnSite());
            } catch (Exception ignored) {
            }
        }).start();
    }

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

    private void openUpdateFragment(String newVersion) {
        setBarFragment(UpdateFragment.newInstance(BuildConfig.VERSION_NAME, newVersion));
    }

    private void checkEmpty() {
        if (db.passwordDao().checkEmpty()) {
            setFragment(new EmptyFragment());
            findViewById(R.id.main_fragment).setVisibility(View.VISIBLE);
        } else findViewById(R.id.main_fragment).setVisibility(View.INVISIBLE);
    }

    public void makeVibration() {
        if (checkSoundMode())
            VibrationManager.makeVibration(getApplicationContext());
    }

    private boolean checkSoundMode() {
        return (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0);
    }

    public void clickAdd() {
        makeVibration();
        setBarFragment(PasswordFragment.newInstance(preferencesManager.getLength(), preferencesManager.getShow()));
    }

    public void makeToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void addNewPassword(Bundle data) {
        if (checkNotExist(getData(data, "name"))) {
            addPasswordInDatabase(data);
            makeToast(getData(data, "name") + " was " + "added");
            new GetPasswordData().execute();
            checkEmpty();
        }
    }

    private String getData(Bundle data, String name) {
        return data.getString(name);
    }

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

    private void clickEditPassword(String name) {
        makeVibration();
        setBarFragment(PasswordFragment.newInstance(
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

    public void editPassword(Bundle data) {
        if (getData(data, "name_before").equals(getData(data, "edited_name"))
                || checkNotExist(getData(data, "edited_name"))) {
            editPasswordInDatabase(data);
            makeToast(getData(data, "edited_name") + " was " + "edited");
            new GetPasswordData().execute();
        }
    }

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

    public void deletePasswordByName(String name) {
        db.passwordDao().deleteByName(name);
        new GetPasswordData().execute();
        checkEmpty();
        makeToast(name + " was deleted");
    }

    public void clickLock() {
        makeVibration();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void longClickLock() {
        setBarFragment(ConfirmFragment.newInstance(true));
    }

    public void clickSettings() {
        makeVibration();
        setBarFragment(SettingsFragment.newInstance(
                preferencesManager.getLength(),
                preferencesManager.getShow(),
                getIntent().getBooleanExtra("delete_value", false),
                preferencesManager.getEncrypt()));
    }

    public void saveSettings(int length, boolean show, boolean deleting, boolean encrypt) {
        updateShowPassword(show);
        updateLengthOfPassword(length);
        updateDeleting(deleting);
        updateEncrypting(encrypt);
        makeToast("Settings saved");
    }

    public void deleteAllPasswords() {
        db.passwordDao().deleteAll();
        new GetPasswordData().execute();
        checkEmpty();
        makeToast("All passwords have been deleted");
    }

    private void updateLengthOfPassword(int length) {
        if (!String.valueOf(length).isEmpty())
            preferencesManager.setLength(length);
        else preferencesManager.setLength();
    }

    private void updateShowPassword(boolean show) {
        preferencesManager.setShow(show);
    }

    private void updateDeleting(boolean deleting) {
        if (deleting != getIntent().getBooleanExtra("delete_value", false)) {
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("delete_edit", true)
                    .putExtra("delete_value", deleting));
        }
    }

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

    private void encryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) encryptLogin(s);
    }

    private void encryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).encrypt(db.passwordDao().getLoginByName(name)));
    }

    private void encryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) encryptPassword(s);
    }

    private void encryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).encrypt(db.passwordDao().getPasswordByName(name)));
    }

    private void decryptAllLogins() {
        for (String s : db.passwordDao().getAllNames()) decryptLogin(s);
    }

    private void decryptLogin(String name) {
        db.passwordDao().updateLoginByName(name, new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)));
    }

    private void decryptAllPasswords() {
        for (String s : db.passwordDao().getAllNames()) decryptPassword(s);
    }

    private void decryptPassword(String name) {
        db.passwordDao().updatePasswordByName(name, new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)));
    }

    public void addToClipboard(String name) {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText(name,
                        preferencesManager.getEncrypt() ?
                                new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) :
                                db.passwordDao().getPasswordByName(name)));
        makeToast(name + " was copied");
    }

    private boolean checkNotExist(String name) {
        if (!db.passwordDao().checkNotExist(name)) makeToast("Name already taken");
        return db.passwordDao().checkNotExist(name);
    }

    public void openConfirmPasswordDelete(String name) {
        setBarFragment(ConfirmFragment.newInstance(name));
    }

    public void openConfirmAllPasswordsDelete() {
        setBarFragment(ConfirmFragment.newInstance());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getEncryptedValues(String name) {
        return ("Securely:" + Hex.encrypt(name) + "-" +
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) : db.passwordDao().getLoginByName(name)) + "-" +
                Hex.encrypt(preferencesManager.getEncrypt() ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) : db.passwordDao().getPasswordByName(name)));
    }

    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(passwordList,
                this::clickEditPassword,
                this::openPasswordShareFragment);
        passwords.setAdapter(adapter);
    }

    private void openPasswordShareFragment(String name) {
        makeVibration();
        setBarFragment(PasswordShareFragment.newInstance(name));
    }

    private class GetPasswordData extends AsyncTask<Void, Void, List<Password>> {

        @Override
        protected List<Password> doInBackground(Void... params) {
            return db.passwordDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Password> passwords) {
            super.onPostExecute(passwords);
            loadRecyclerView(passwords);
        }
    }
}
