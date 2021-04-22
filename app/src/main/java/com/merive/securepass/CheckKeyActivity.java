package com.merive.securepass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securepass.elements.TypingTextView;

public class CheckKeyActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TypingTextView title, key_hint;
    EditText key;
    int times;
    boolean deleting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_key);

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        times = 15;
        deleting = sharedPreferences.getBoolean("delete", false);

        title = findViewById(R.id.title_check);
        typingAnimation(title, getResources().getString(R.string.welcome_to_securepass));
        key_hint = findViewById(R.id.key_hint);
        typingAnimation(key_hint, getResources().getString(R.string.enter_the_key_in_the_field));

        key = findViewById(R.id.key);


        checkKeyOnEmpty();
        checkDeletingEdit();
    }

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    @SuppressLint("SetTextI18n")
    public void checkKeyOnEmpty() {
        if (sharedPreferences.getInt("key", -1) == -1) {
            typingAnimation(key_hint, "Create a key\nTip: Use more than 4 numbers." +
                    "\nIn the future, you can't change him.");
        }
    }

    public void checkDeletingEdit() {
        if (getIntent().getBooleanExtra("status", false)) {
            sharedPreferences.edit().putBoolean("delete",
                    getIntent().getBooleanExtra("deleting", false)).apply();
            deleting = getIntent().getBooleanExtra("deleting", false);
            finish();
        }
    }

    /* Click methods */
    @SuppressLint("SetTextI18n")
    public void checkKey(View view) {
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

                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(this, MainActivity.class)
                                    .putExtra("status", false)
                                    .putExtra("deleting", deleting)
                                    .putExtra("key", Integer.parseInt(key.getText().toString()))
                            );
                            finish();
                        }, 3250);
                    } else {
                        typingAnimation(key_hint, "Invalid key. Try again.");
                        if (deleting)
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

    public int hashKey(int key) {
        return String.valueOf(key).hashCode();
    }
}
