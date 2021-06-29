package com.merive.securepass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securepass.elements.TypingTextView;

import org.mindrot.jbcrypt.BCrypt;

public class CheckKeyActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TypingTextView title, key_hint;
    EditText key;
    int times;
    boolean deletingAfterErrors, pressed, changeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_key);

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        times = 15;
        deletingAfterErrors = sharedPreferences.getBoolean("delete", false);

        title = findViewById(R.id.title_check);
        typingAnimation(title, getResources().getString(R.string.welcome_to_securepass));
        key_hint = findViewById(R.id.key_hint);
        typingAnimation(key_hint, getResources().getString(R.string.enter_the_key_in_the_field));

        key = findViewById(R.id.key);

        changeKey = false;
        checkKeyOnDefault();
        checkEditOfDeletingAfterErrors();
        checkOnChangeKey();

        pressed = false;

        key = findViewById(R.id.key);
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

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        /* Typing animation for text elements */
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    @SuppressLint("SetTextI18n")
    public void checkKeyOnDefault() {
        /* Check key from data on default value */
        if (sharedPreferences.getString("hash", "-1").equals("-1")) {
            typingAnimation(key_hint, "Create a new key\nTip: Use more than 4 numbers.");
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
            typingAnimation(key_hint, "Enter old key.");
            changeKey = true;
        }
    }

    /* Click methods */
    @SuppressLint("SetTextI18n")
    public void checkKey(View view) {
        /* Check key method */
        if (changeKey) {
            if (checkKey()) {
                resetKey();
                checkKeyOnDefault();
                key.setText("");
                changeKey = false;
                pressed = false;
            } else {
                typingAnimation(key_hint, "Invalid key. Try again.");
            }
        } else {
            if (!pressed) {
                if (!key.getText().toString().equals("")) {
                    if (checkOnNewUser()) {
                        openMain();
                    } else if (checkKey())
                        openMain();
                    else {
                        if (checkTimes()) {
                            typingAnimation(key_hint, "Invalid key. Try again.");
                            if (deletingAfterErrors) times--;
                        } else deleteAllPasswords();
                    }
                }
            }
        }
    }


    public boolean checkOnNewUser() {
        if (sharedPreferences.getString("hash", "-1").equals("-1")) {

            sharedPreferences.edit().putString("hash", generateHash(key.getText().toString())).apply();

            typingAnimation(key_hint, "Key set. Welcome!");
            pressed = true;
            return true;
        }
        return false;
    }

    public void resetKey() {
        sharedPreferences.edit().putString("hash", "-1").apply();
    }


    public void openMain() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("status", false)
                    .putExtra("deleting", deletingAfterErrors)
                    .putExtra("key", Integer.parseInt(key.getText().toString()))
            );
            finish();
        }, 3250);
    }

    public boolean checkKey() {
        if (BCrypt.checkpw(key.getText().toString(), sharedPreferences.getString("hash", "-1"))) {
            typingAnimation(key_hint, "All right. Welcome!");
            pressed = true;

            return true;
        }
        return false;
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean checkTimes() {
        return times > 0;
    }

    public void deleteAllPasswords() {
        startActivity(new Intent(this, MainActivity.class)
                .putExtra("status", true)
        );
        typingAnimation(key_hint, "All Passwords was deleted. Have a nice day :-)");
        pressed = true;
    }

    public String generateHash(String key) {
        return BCrypt.hashpw(key, BCrypt.gensalt());
    }
}
