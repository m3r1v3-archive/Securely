package com.merive.securely.activities;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securely.R;
import com.merive.securely.elements.TypingTextView;
import com.merive.securely.preferences.PreferencesManager;
import com.merive.securely.utils.VibrationManager;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    public static PreferencesManager preferencesManager;
    private TypingTextView title, keyHint;
    private EditText key;
    private boolean pressed = false, changeKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_login);

        preferencesManager = new PreferencesManager(this.getBaseContext());

        initLayoutVariables();

        typingAnimation(title, getResources().getString(R.string.welcome_to_securely));
        typingAnimation(keyHint, getResources().getString(R.string.enter_the_key_in_the_field));

        checkKeyOnAbsence();
        checkDeleteAfterErrorsEdit();
        checkKeyEdit();

        setKeyOnEditorListener();
    }

    private void initLayoutVariables() {
        title = findViewById(R.id.login_title_text);
        key = findViewById(R.id.login_key_edit);
        keyHint = findViewById(R.id.login_hint_text);
    }

    private void checkKeyOnAbsence() {
        if (preferencesManager.getHash().equals("-1")) {
            typingAnimation(keyHint, getResources().getString(R.string.create_new_key));
        }
    }

    private void checkDeleteAfterErrorsEdit() {
        if (getIntent().getBooleanExtra("status", false)) {
            preferencesManager.setDelete(getIntent().getBooleanExtra("delete", false));
            finish();
        }
    }

    private void checkKeyEdit() {
        if (getIntent().getBooleanExtra("changeKey", false)) {
            typingAnimation(keyHint, getResources().getString(R.string.enter_old_key));
            changeKey = true;
        }
    }

    private void setKeyOnEditorListener() {
        key.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clickLogin(key);
                hideKeyboard();
                handled = true;
            }
            return handled;
        });
    }

    public void clickLogin(View view) {
        makeVibration();
        if (changeKey) changeKey();
        else login();
    }

    private void makeVibration() {
        if (checkSoundMode())
            VibrationManager.makeVibration(getApplicationContext());
    }

    private boolean checkSoundMode() {
        return (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0);
    }

    private void changeKey() {
        if (checkKeyHash()) {
            resetKey();
            checkKeyOnAbsence();
            key.setText("");
            changeKey = false;
            pressed = false;
        } else typingAnimation(keyHint, getResources().getString(R.string.invalid_key));
    }

    private boolean checkKeyHash() {
        if (BCrypt.checkpw(key.getText().toString(), preferencesManager.getHash())) {
            typingAnimation(keyHint, getResources().getString(R.string.successful_login));
            pressed = true;
            return true;
        }
        return false;
    }

    private void resetKey() {
        preferencesManager.setHash();
    }

    private void login() {
        if (!pressed) {
            if (!key.getText().toString().equals("")) {
                if (checkOnNoKey()) openMain();
                else if (checkKeyHash()) openMain();
                else {
                    if (checkErrorsCount()) {
                        typingAnimation(keyHint, getResources().getString(R.string.invalid_key));
                        if (preferencesManager.getDelete()) errorDec();
                    } else deleteAllPasswords();
                }
            }
        }
    }

    private boolean checkOnNoKey() {
        if (preferencesManager.getHash().equals("-1")) {
            preferencesManager.setHash(generateHash(key.getText().toString()));
            typingAnimation(keyHint, getResources().getString(R.string.key_set));
            pressed = true;
            return true;
        }
        return false;
    }

    private String generateHash(String key) {
        return BCrypt.hashpw(key, BCrypt.gensalt());
    }

    private void openMain() {
        new Handler().postDelayed(() -> {
            resetErrors();
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("status", false)
                    .putExtra("delete", preferencesManager.getDelete())
                    .putExtra("key", Integer.parseInt(key.getText().toString()))
            );
            finish();
        }, 3250);
    }

    private void resetErrors() {
        preferencesManager.setErrors();
    }

    private boolean checkErrorsCount() {
        return preferencesManager.getErrors() > 0;
    }

    private void errorDec() {
        preferencesManager.setErrors(preferencesManager.getErrors() - 1);
    }

    private void deleteAllPasswords() {
        startActivity(new Intent(this, MainActivity.class).putExtra("status", true));
        typingAnimation(keyHint, getResources().getString(R.string.all_passwords_deleted));
        pressed = true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
