package com.merive.securepass;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.merive.securepass.adapter.PasswordAdapter;
import com.merive.securepass.database.Password;
import com.merive.securepass.database.PasswordDB;
import com.merive.securepass.elements.TypingTextView;
import com.merive.securepass.utils.Crypt;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TypingTextView title, empty;
    RecyclerView passwords;

    PasswordAdapter adapter;
    PasswordDB db;

    SharedPreferences sharedPreferences;

    boolean deleting, encrypting;
    int key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.mainTitle);
        typingAnimation(title, getResources().getString(R.string.app_name));

        empty = findViewById(R.id.empty);


        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        passwords = findViewById(R.id.password_recycle_view);
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords")
                .allowMainThreadQueries().build();
        checkKeyStatus();

        deleting = getIntent().getBooleanExtra("deleting", false);

        key = getIntent().getIntExtra("key", 0);

        encrypting = sharedPreferences.getBoolean("encrypting", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEmpty();

        new GetData().execute();
    }

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        /* Typing animation for TextViews */
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    /* Click methods */
    public void clickAdd(View view) {
        /* OnClick Add */
        FragmentManager fm = getSupportFragmentManager();
        PasswordFragment passwordFragment = PasswordFragment.newInstance(
                sharedPreferences.getInt("length", 16));
        passwordFragment.show(fm, "password_fragment");
    }


    public void clickEditPassword(String name) {
        /* OnClick on password row */
        FragmentManager fm = getSupportFragmentManager();
        PasswordFragment passwordFragment = PasswordFragment.newInstance(
                name,
                encrypting ? new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)) :
                        db.passwordDao().getLoginByName(name),
                encrypting ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)) :
                        db.passwordDao().getPasswordByName(name),
                db.passwordDao().getDescriptionByName(name),
                sharedPreferences.getInt("length", 16));
        passwordFragment.show(fm, "password_fragment");
    }

    public void clickLock(View view) {
        /* OnClick Lock */
        startActivity(new Intent(this, CheckKeyActivity.class));
        finish();

    }

    public void clickSettings(View view) {
        /* OnClick Settings */
        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsFragment = SettingsFragment.newInstance(
                sharedPreferences.getInt("length", 16),
                sharedPreferences.getString("copyingMessage", "was copied."),
                deleting, sharedPreferences.getBoolean("encrypting", false));
        settingsFragment.show(fm, "settings_fragment");
    }


    /* Activities event methods */
    public void addNewPassword(Bundle data) {
        /* Add password in database */
        if (checkExist(getData(data, "name"), false)) {
            addPassword(data);
            new GetData().execute();
            checkEmpty();
        }
    }

    public boolean checkExist(String name, boolean edit) {
        /* Check password name on exist */
        if (db.passwordDao().checkExist(name)) {
            Toast.makeText(getBaseContext(),
                    name + " was " + (edit ? "edited" : "added") + ".", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getBaseContext(),
                    name + " already in database. Try replace name.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void addPassword(Bundle data) {
        /* Add password in database */
        db.passwordDao().insertItem(
                new Password(
                        getData(data, "name"),
                        encrypting ? new Crypt(key).encrypt(getData(data, "login")) :
                                getData(data, "login"),
                        encrypting ? new Crypt(key).encrypt(getData(data, "password")) :
                                getData(data, "password"),
                        getData(data, "description")
                ));
    }


    public void editPassword(Bundle data) {
        /* Edit password in database */
        if (checkExist(getData(data, "edited_name"), true) ||
                getData(data, "name_before").equals(getData(data, "edited_name"))) {
            updatePassword(data);
            new GetData().execute();
        }
    }

    public void updatePassword(Bundle data) {
        /* Update password in database */
        db.passwordDao().updateItem(
                getData(data, "name_before"),
                getData(data, "edited_name"),
                encrypting ? new Crypt(key).encrypt(getData(data, "edited_login"))
                        : getData(data, "edited_login"),
                encrypting ? new Crypt(key).encrypt(getData(data, "edited_password"))
                        : getData(data, "edited_password"),
                getData(data, "edited_description")
        );
    }

    public void deletePasswordFragment(String name) {
        /* Open fragment for confirm deleting */
        FragmentManager fm = getSupportFragmentManager();
        ConfirmFragment confirmFragment = ConfirmFragment.newInstance(
                name);
        confirmFragment.show(fm, "confirm_fragment");
    }

    public void deletePasswordByName(String name) {
        /* Delete password by name */
        db.passwordDao().deleteByName(name);
        Toast.makeText(getBaseContext(),
                name + " was deleted.",
                Toast.LENGTH_SHORT).show();
        new GetData().execute();
        checkEmpty();
    }


    /* Check methods */
    public void checkEmpty() {
        /* Check database on empty and if true, set visibility for TextView */
        if (db.passwordDao().checkEmpty()) {
            empty.setVisibility(View.VISIBLE);
            typingAnimation(empty, getResources().getString(R.string.list_is_empty));
        } else {
            empty.setVisibility(View.INVISIBLE);
        }
    }

    public void checkKeyStatus() {
        /* If u execute 15 errors in CheckKeyActivity, all passwords deleting */
        if (getIntent().getBooleanExtra("status", false)) {
            db.passwordDao().deleteAll();
            finish();
        }
    }

    public String getData(Bundle data, String name) {
        /* Get name from bundle */
        return data.getString(name);
    }

    /* Settings methods */
    public void deleteAllFragment() {
        /* Open fragment for confirm deleting */
        FragmentManager fm = getSupportFragmentManager();
        ConfirmFragment confirmFragment = ConfirmFragment.newInstance();
        confirmFragment.show(fm, "confirm_fragment");
    }

    public void deleteAllPasswords() {
        /* Delete all passwords from database */
        db.passwordDao().deleteAll();

        new GetData().execute();
        checkEmpty();

        Toast.makeText(getBaseContext(),
                "All passwords have been deleted.",
                Toast.LENGTH_SHORT).show();
    }

    public void saveSettings(int length, String message, boolean deleting, boolean encrypt) {
        /* Save settings changes */
        updateCopyingMessage(message);
        updateLengthOfPassword(length);
        updateDeleting(deleting);
        updateEncrypt(encrypt);
        Toast.makeText(getBaseContext(), "Settings saved.", Toast.LENGTH_SHORT).show();
    }

    public void updateCopyingMessage(String message) {
        /* Update copingMessage in sharedPreferences */
        sharedPreferences.edit()
                .putString("copyingMessage", message)
                .apply();
    }

    public void updateLengthOfPassword(int length) {
        /* Update length in sharedPreferences */
        if (!String.valueOf(length).isEmpty())
            sharedPreferences.edit()
                    .putInt("length", length)
                    .apply();
        else sharedPreferences.edit()
                .putInt("length", 16)
                .apply();
    }

    public void updateDeleting(boolean deleting) {
        /* Update deleting in sharedPreferences */
        if (deleting !=
                getIntent().getBooleanExtra("deleting", false)) {
            startActivity(new Intent(this, CheckKeyActivity.class)
                    .putExtra("status", true)
                    .putExtra("deleting", deleting));

            this.deleting = deleting;
        }
    }

    public void updateEncrypt(boolean encrypting) {
        /* Update encrypting in sharedPreferences */
        if (encrypting != sharedPreferences.getBoolean("encrypting", false)) {
            if (encrypting) {
                encryptAllLogins();
                encryptAllPasswords();
            } else {
                decryptAllLogins();
                decryptAllPasswords();
            }
            sharedPreferences.edit().putBoolean("encrypting", encrypting).apply();
            this.encrypting = encrypting;
        }
    }

    public void encryptAllLogins() {
        /* Encrypt all logins in db */
        List<String> data = db.passwordDao().getAllNames();
        for (String s : data) {
            encryptLogin(s);
        }
    }

    public void encryptAllPasswords() {
        /* Encrypt all passwords in db */
        List<String> data = db.passwordDao().getAllNames();
        for (String s : data) {
            encryptPassword(s);
        }
    }

    public void encryptLogin(String name) {
        /* Encrypt login */
        db.passwordDao().updateLoginByName(name, new Crypt(key).encrypt(db.passwordDao().getLoginByName(name)));
    }

    public void encryptPassword(String name) {
        /* Encrypt password */
        db.passwordDao().updatePasswordByName(name, new Crypt(key).encrypt(db.passwordDao().getPasswordByName(name)));
    }

    public void decryptAllLogins() {
        /* Decrypt all logins in db */
        List<String> data = db.passwordDao().getAllNames();
        for (String s : data) {
            decryptLogin(s);
        }
    }

    public void decryptAllPasswords() {
        /* Decrypt all passwords in db */
        List<String> data = db.passwordDao().getAllNames();
        for (String s : data) {
            decryptPassword(s);
        }
    }

    public void decryptLogin(String name) {
        /* Decrypt login */
        db.passwordDao().updateLoginByName(name, new Crypt(key).decrypt(db.passwordDao().getLoginByName(name)));
    }

    public void decryptPassword(String name) {
        /* Decrypt password */
        db.passwordDao().updatePasswordByName(name, new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name)));
    }

    /* Clipboard methods */
    public void addInClipboard(String label, String value) {
        /* Add password to clipboard */
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getBaseContext(), label + " Password " +
                        sharedPreferences.getString("copyingMessage", "was copied."),
                Toast.LENGTH_SHORT).show();
    }

    /* RecyclerView methods/classes */
    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(passwordList,
                this::clickEditPassword,
                name -> addInClipboard(
                        name,
                        encrypting ? new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name))
                                : db.passwordDao().getPasswordByName(name)));

        passwords.setAdapter(adapter);
    }

    public class GetData extends AsyncTask<Void, Void, List<Password>> {

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
