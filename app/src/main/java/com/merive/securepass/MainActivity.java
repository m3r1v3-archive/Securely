package com.merive.securepass;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.merive.securepass.fragments.ConfirmFragment;
import com.merive.securepass.fragments.PasswordFragment;
import com.merive.securepass.fragments.SettingsFragment;
import com.merive.securepass.fragments.UpdateFragment;
import com.merive.securepass.utils.Crypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TypingTextView title, empty;
    RecyclerView passwords;
    ImageView lock;

    PasswordAdapter adapter;
    PasswordDB db;

    SharedPreferences sharedPreferences;

    boolean deleting, encrypting;
    int key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayoutVariables();

        typingAnimation(title, getResources().getString(R.string.app_name));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords")
                .allowMainThreadQueries().build();

        checkKeyStatus();
        getSettingsData();
        checkVersion();

        lock.setOnLongClickListener(v -> {
            longClickLock();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEmpty();

        new GetData().execute();
    }

    /* ************ */
    /* Init methods */
    /* ************ */

    public void initLayoutVariables() {
        /* Init layout variables */
        title = findViewById(R.id.mainTitle);
        empty = findViewById(R.id.empty);
        passwords = findViewById(R.id.password_recycle_view);
        lock = findViewById(R.id.lock);
    }

    /* ************* */
    /* Click methods */
    /* ************* */

    public void clickAdd(View view) {
        /* Click Add Button */
        FragmentManager fm = getSupportFragmentManager();
        PasswordFragment passwordFragment = PasswordFragment.newInstance(
                sharedPreferences.getInt("length", 16));
        passwordFragment.show(fm, "password_fragment");
    }

    public void clickEditPassword(String name) {
        /* Click Password Row */
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
        /* Click Lock Button */
        startActivity(new Intent(this, CheckKeyActivity.class));
        finish();
    }

    public void longClickLock() {
        /* Long Click Lock Button */
        FragmentManager fm = getSupportFragmentManager();
        ConfirmFragment confirmFragment = ConfirmFragment.newInstance(
                true);
        confirmFragment.show(fm, "confirm_fragment");
    }

    public void clickSettings(View view) {
        /* Click Settings Button */
        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsFragment = SettingsFragment.newInstance(
                sharedPreferences.getInt("length", 16),
                sharedPreferences.getString("copyingMessage", "was copied."),
                deleting, sharedPreferences.getBoolean("encrypting", false));
        settingsFragment.show(fm, "settings_fragment");
    }

    public void clickAddInClipboard(String label, String value) {
        /* Add password to clipboard */
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);

        makeToast(label + " Password " +
                sharedPreferences.getString("copyingMessage", "was copied."));
    }

    /* ************* */
    /* Check methods */
    /* ************* */

    public void checkVersion() {
        /* Make fragment if application was updated */
        Thread thread = new Thread(() -> {
            try {
                if (!getVersionOnSite().equals(BuildConfig.VERSION_NAME))
                    openUpdateFragment(BuildConfig.VERSION_NAME, getVersionOnSite());
            } catch (Exception e) {
                Log.e("CHECK VERSION ERROR ", "NOT POSSIBLE CHECK VERSION" + " (" + e + ") ");
            }
        });

        thread.start();
    }

    public boolean checkNotExist(String name) {
        /* Check password name on exist */
        if (db.passwordDao().checkNotExist(name)) return true;
        else {
            makeToast(name + " already in database.");
            return false;
        }
    }

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

    /* *********** */
    /* Get methods */
    /* *********** */

    public String getData(Bundle data, String name) {
        /* Get name from bundle */
        return data.getString(name);
    }

    public void getSettingsData() {
        deleting = getIntent().getBooleanExtra("deleting", false);
        key = getIntent().getIntExtra("key", 0);
        encrypting = sharedPreferences.getBoolean("encrypting", false);
    }

    public String getVersionOnSite() throws IOException {
        /* Get version of actual application on site */
        URL url = new URL("https://merive.herokuapp.com/SecurePass/download");
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            for (String line; (line = reader.readLine()) != null; ) builder.append(line.trim());
        } finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
        return builder.substring(builder.indexOf("<i>") + "<i>".length()).substring(1,
                builder.substring(builder.indexOf("<i>") + "<i>".length()).indexOf("</i>"));
    }

    /* ********************** */
    /* Open Fragments methods */
    /* ********************** */

    public void openUpdateFragment(String oldVersion, String newVersion) {
        /* Open UpdateFragment */
        FragmentManager fm = getSupportFragmentManager();
        UpdateFragment updateFragment = UpdateFragment.newInstance(oldVersion, newVersion);
        updateFragment.show(fm, "update_fragment");
    }

    public void openConfirmPasswordDelete(String name) {
        /* Open fragment for confirm deleting */
        FragmentManager fm = getSupportFragmentManager();
        ConfirmFragment confirmFragment = ConfirmFragment.newInstance(
                name);
        confirmFragment.show(fm, "confirm_fragment");
    }

    public void openConfirmAllPasswordsDelete() {
        /* Open fragment for confirm deleting all passwords */
        FragmentManager fm = getSupportFragmentManager();
        ConfirmFragment confirmFragment = ConfirmFragment.newInstance();
        confirmFragment.show(fm, "confirm_fragment");
    }

    /* ***************** */
    /* Fragments methods */
    /* ***************** */

    public void saveSettings(int length, String message, boolean deleting, boolean encrypt) {
        /* Save settings changes */
        makeToast("Settings saved.");
        updateCopyingMessage(message);
        updateLengthOfPassword(length);
        updateDeleting(deleting);
        updateEncrypting(encrypt);
    }

    public void addNewPassword(Bundle data) {
        /* Add password using data from bundle */
        if (checkNotExist(getData(data, "name"))) {
            addPasswordInDatabase(data);
            makeToast(getData(data, "name") + " was " + "added" + ".");
            new GetData().execute();
            checkEmpty();
        }
    }

    public void addPasswordInDatabase(Bundle data) {
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
        /* Edit password using data from bundle */
        if (getData(data, "name_before").equals(getData(data, "edited_name"))
                || checkNotExist(getData(data, "edited_name"))) {
            editPasswordInDatabase(data);
            makeToast(getData(data, "edited_name") + " was " + "edited" + ".");
            new GetData().execute();
        }
    }

    public void editPasswordInDatabase(Bundle data) {
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

    /* ************** */
    /* Update methods */
    /* ************** */

    public void updateLengthOfPassword(int length) {
        /* Update length in sharedPreferences */
        if (!String.valueOf(length).isEmpty())
            sharedPreferences.edit().putInt("length", length).apply();
        else sharedPreferences.edit().putInt("length", 16).apply();
    }

    public void updateCopyingMessage(String message) {
        /* Update copingMessage in sharedPreferences */
        sharedPreferences.edit()
                .putString("copyingMessage", message)
                .apply();
    }

    public void updateDeleting(boolean deleting) {
        /* Update deleting in sharedPreferences */
        if (deleting != getIntent().getBooleanExtra("deleting", false)) {
            startActivity(new Intent(this, CheckKeyActivity.class)
                    .putExtra("status", true)
                    .putExtra("deleting", deleting));
            this.deleting = deleting;
        }
    }

    /* ***************************** */
    /* Encrypting/Decrypting methods */
    /* ***************************** */

    public void updateEncrypting(boolean encrypting) {
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
        for (String s : data) encryptLogin(s);
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

    /* ************** */
    /* Delete methods */
    /* ************** */

    public void deletePasswordByName(String name) {
        /* Delete password by name */
        db.passwordDao().deleteByName(name);
        new GetData().execute();
        checkEmpty();
        makeToast(name + " was deleted.");
    }

    public void deleteAllPasswords() {
        /* Delete all passwords from database */
        db.passwordDao().deleteAll();
        new GetData().execute();
        checkEmpty();
        makeToast("All passwords have been deleted.");
    }

    /* ************* */
    /* Other methods */
    /* ************* */

    public void makeToast(String message) {
        /* Make custom toast */
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toastLayout));
        TextView text = layout.findViewById(R.id.message);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 80);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    /* **************************** */
    /* RecyclerView methods/classes */
    /* **************************** */

    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(passwordList,
                this::clickEditPassword,
                name -> clickAddInClipboard(
                        name,
                        encrypting ?
                                new Crypt(key).decrypt(db.passwordDao().getPasswordByName(name))
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
