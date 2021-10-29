package com.merive.securepass;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securepass.elements.TypingTextView;
import com.merive.securepass.utils.VibrationManager;

import org.mindrot.jbcrypt.BCrypt;

public class CheckKeyActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TypingTextView title, key_hint;
    EditText key;
    int errors;
    boolean deletingAfterErrors, pressed, changeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_check_key);

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        errors = sharedPreferences.getInt("errors", 15);

        deletingAfterErrors = sharedPreferences.getBoolean("delete", false);
        changeKey = false;
        pressed = false;

        initLayoutVariables();
        typingAnimation(title, getResources().getString(R.string.welcome_to_securepass));
        typingAnimation(key_hint, getResources().getString(R.string.enter_the_key_in_the_field));


        checkKeyOnDefault();
        checkEditOfDeletingAfterErrors();
        checkOnChangeKey();

        setFocus();

        key.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkKey(key);
                hideKeyboard();
                handled = true;
            }
            return handled;
        });
    }

    /* ************ */
    /* Init methods */
    /* ************ */

    public void initLayoutVariables() {
        /* Init layout variables */
        title = findViewById(R.id.check_key_title);
        key = findViewById(R.id.key_edit);
        key_hint = findViewById(R.id.check_key_hint);
    }

    /* ************* */
    /* Check methods */
    /* ************* */

    @SuppressLint("SetTextI18n")
    public void checkKeyOnDefault() {
        /* Check key from data on default value */
        if (sharedPreferences.getString("hash", "-1").equals("-1")) {
            typingAnimation(key_hint, getResources().getString(R.string.create_a_new_name));
        }
    }

    public void checkEditOfDeletingAfterErrors() {
        /* Check edits of deletingAfterErrors variable */
        if (getIntent().getBooleanExtra("status", false)) {
            sharedPreferences.edit().putBoolean("delete",
                    getIntent().getBooleanExtra("deleting", false)).apply();
            deletingAfterErrors = getIntent().getBooleanExtra("deleting", false);
            finish();
        }
    }


    public void checkOnChangeKey() {
        /* Check on key changing */
        if (getIntent().getBooleanExtra("changeKey", false)) {
            typingAnimation(key_hint, getResources().getString(R.string.enter_old_key));
            changeKey = true;
        }
    }

    /* Click methods */
    @SuppressLint("SetTextI18n")
    public void checkKey(View view) {
        /* Check key method */
        makeVibration();
        if (changeKey) changeKey();
        else login();
    }

    public boolean checkOnNewUser() {
        /* Check on default key */
        if (sharedPreferences.getString("hash", "-1").equals("-1")) {
            sharedPreferences.edit().putString("hash", generateHash(key.getText().toString())).apply();
            typingAnimation(key_hint, getResources().getString(R.string.key_set));
            pressed = true;
            return true;
        }
        return false;
    }

    public boolean checkKey() {
        /* Check key hash */
        if (BCrypt.checkpw(key.getText().toString(), sharedPreferences.getString("hash", "-1"))) {
            typingAnimation(key_hint, getResources().getString(R.string.all_right));
            pressed = true;
            return true;
        }
        return false;
    }

    public boolean checkErrorsCount() {
        return errors > 0;
    }

    /* *************** */
    /* Another methods */
    /* *************** */

    public void changeKey() {
        /* Change Key Operation */
        if (checkKey()) {
            resetKey();
            checkKeyOnDefault();
            key.setText("");
            changeKey = false;
            pressed = false;
        } else typingAnimation(key_hint, getResources().getString(R.string.invalid_key));
    }

    public void login() {
        /* Login Operation */
        if (!pressed) {
            if (!key.getText().toString().equals("")) {
                if (checkOnNewUser()) openMain();
                else if (checkKey()) openMain();
                else {
                    if (checkErrorsCount()) {
                        typingAnimation(key_hint, getResources().getString(R.string.invalid_key));
                        if (deletingAfterErrors) errorIns();
                    } else deleteAllPasswords();
                }
            }
        }
    }

    public void resetKey() {
        /* Reset Key to default */
        sharedPreferences.edit().putString("hash", "-1").apply();
    }

    public void errorIns() {
        errors--;
        sharedPreferences.edit().putInt("errors", errors).apply();
    }

    public void resetErrors() {
        errors = 15;
        sharedPreferences.edit().putInt("errors", 15).apply();
    }

    public void openMain() {
        /* Open MainActivity */
        new Handler().postDelayed(() -> {
            resetErrors();
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("status", false)
                    .putExtra("deleting", deletingAfterErrors)
                    .putExtra("key", Integer.parseInt(key.getText().toString()))
            );
            finish();
        }, 3250);
    }

    public void hideKeyboard() {
        /* Hide Screen Keyboard */
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setFocus() {
        key.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(key, InputMethodManager.SHOW_FORCED);
    }

    public void deleteAllPasswords() {
        /* Delete all passwords if have 15 errors */
        startActivity(new Intent(this, MainActivity.class)
                .putExtra("status", true)
        );
        typingAnimation(key_hint, getResources().getString(R.string.all_passwords_was_deleted));
        pressed = true;
    }

    public String generateHash(String key) {
        /* Generate Key hash */
        return BCrypt.hashpw(key, BCrypt.gensalt());
    }

    public void makeVibration() {
        if (checkSoundMode())
            VibrationManager.makeVibration(getApplicationContext());
    }

    public boolean checkSoundMode() {
        return (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0);
    }
}
