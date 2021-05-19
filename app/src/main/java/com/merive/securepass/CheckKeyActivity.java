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

public class CheckKeyActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TypingTextView title, key_hint;
    EditText key;
    int times;
    boolean deletingAfterErrors;

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


        checkKeyOnDefault();
        checkEditOfDeletingAfterErrors();


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
        if (sharedPreferences.getInt("key", -1) == -1) {
            typingAnimation(key_hint, "Create a key\nTip: Use more than 4 numbers." +
                    "\nIn the future, you can't change him.");
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

    /* Click methods */
    @SuppressLint("SetTextI18n")
    public void checkKey(View view) {
        /* Check key method */
        if (!key.getText().toString().equals("")) {
            if (sharedPreferences.getInt("key", hashKey(0)) == hashKey(0)) {
                sharedPreferences.edit().putInt("key", hashKey(Integer.parseInt(key.getText().toString()))).apply();

                typingAnimation(key_hint, "Key set. Welcome!");

                new Handler().postDelayed(() -> {
                    startActivity(new Intent(CheckKeyActivity.this, MainActivity.class));
                    finish();
                }, 3250);
            } else {
                if (times > 1) {
                    if (sharedPreferences.getInt("key", hashKey(0)) == hashKey(Integer.parseInt(key.getText().toString()))) {
                        typingAnimation(key_hint, "All right. Welcome!");
                        view.clearFocus();

                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(this, MainActivity.class)
                                    .putExtra("status", false)
                                    .putExtra("deleting", deletingAfterErrors)
                                    .putExtra("key", Integer.parseInt(key.getText().toString()))
                            );
                            finish();
                        }, 3250);
                    } else {
                        typingAnimation(key_hint, "Invalid key. Try again.");
                        if (deletingAfterErrors)
                            times -= 1;
                    }
                } else {
                    startActivity(new Intent(this, MainActivity.class)
                            .putExtra("status", true)
                    );
                    typingAnimation(key_hint, "All Passwords was deleted. Have a nice day :-)");
                }
            }
        } else {
            typingAnimation(key_hint, "Enter key.");
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public int hashKey(int key) {
        /* Return hash of key */
        return String.valueOf(key).hashCode();
    }
}
