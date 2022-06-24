package com.merive.securely.activities;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    /**
     * This method is the start point at the LoginActivity.
     *
     * @param savedInstanceState Used by super.onCreate method.
     */
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

    /**
     * This method is assigns main layout variables.
     */
    private void initLayoutVariables() {
        title = findViewById(R.id.login_title_text);
        key = findViewById(R.id.login_key_edit);
        keyHint = findViewById(R.id.login_hint_text);
    }

    /**
     * This method is checking key in sharedPreference on absence.
     * If key's hash in memory is -1, will set special hint.
     *
     * @see SharedPreferences
     */
    private void checkKeyOnAbsence() {
        if (preferencesManager.getHash().equals("-1")) {
            typingAnimation(keyHint, getResources().getString(R.string.create_a_new_key));
        }
    }

    /**
     * This method is checking if Delete After Errors was edit in MainActivity.
     *
     * @see MainActivity
     */
    private void checkDeleteAfterErrorsEdit() {
        if (getIntent().getBooleanExtra("status", false)) {
            preferencesManager.setDelete(getIntent().getBooleanExtra("delete", false));
            finish();
        }
    }

    /**
     * This method is checking if was confirm key editing in MainActivity.
     *
     * @see MainActivity
     * @see com.merive.securely.fragments.ConfirmFragment
     */
    private void checkKeyEdit() {
        if (getIntent().getBooleanExtra("changeKey", false)) {
            typingAnimation(keyHint, getResources().getString(R.string.enter_previous_key));
            changeKey = true;
        }
    }

    /**
     * This method is setting listener for done button on keyboard.
     * If after dialing the key you press the button done, key will checked.
     */
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

    /**
     * This method is executing after click check button.
     * The method is making vibration and changing key (if changeKey is true) or start logging.
     *
     * @param view Not use
     * @see View
     */
    public void clickLogin(View view) {
        makeVibration();
        if (changeKey) changeKey();
        else login();
    }

    /**
     * This method is making vibration.
     * If checkSoundMode() is true, VibrationManager will make Vibration.
     *
     * @see VibrationManager
     */
    private void makeVibration() {
        if (checkSoundMode())
            VibrationManager.makeVibration(getApplicationContext());
    }

    /**
     * This method is checking ringer mode of device.
     *
     * @return If mode isn't mute, return true.
     */
    private boolean checkSoundMode() {
        return (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0);
    }

    /**
     * This method is changing key in sharedPreference.
     * The method is checking inputted key hash with old key hash in memory.
     * If hash is okay, will start key resetting and next input value will be new key.
     * Else key_hint message will change to invalid.
     *
     * @see SharedPreferences
     */
    private void changeKey() {
        if (checkKeyHash()) {
            resetKey();
            checkKeyOnAbsence();
            key.setText("");
            changeKey = false;
            pressed = false;
        } else typingAnimation(keyHint, getResources().getString(R.string.invalid_key));
    }

    /**
     * This method is checking inputted key hash and sharedPreference Key Hash.
     *
     * @return true if hashes are equal, else False.
     */
    private boolean checkKeyHash() {
        if (BCrypt.checkpw(key.getText().toString(), preferencesManager.getHash())) {
            typingAnimation(keyHint, getResources().getString(R.string.successful_login));
            pressed = true;
            return true;
        }
        return false;
    }

    /**
     * This method is resetting key in sharedPreference to default ("-1")
     */
    private void resetKey() {
        preferencesManager.setHash();
    }

    /**
     * This method is logging you to application.
     * If check_key button wasn't pressed, key field isn't empty,
     * if key in sharedPreference equals "-1", key will hash, will assign to shared and will open MainActivity.
     * Else if hash in sharedPreference equals to Key Hash in Field, will open MainActivity.
     * Else if Errors counts more than 15, key_hint will edit to invalid and if DeleteAfterErrors is true, will execute errorsIns().
     * Else will execute deleteAllPasswords().
     *
     * @see MainActivity
     */
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

    /**
     * This method is checking key hash in sharedPreference.
     *
     * @return If hash equals "-1", will set hash from key field, will edit key_hint,
     * pressed will assign true and return true, else will return false.
     */
    private boolean checkOnNoKey() {
        if (preferencesManager.getHash().equals("-1")) {
            preferencesManager.setHash(generateHash(key.getText().toString()));
            typingAnimation(keyHint, getResources().getString(R.string.key_set));
            pressed = true;
            return true;
        }
        return false;
    }

    /**
     * This method generates and return hash string.
     *
     * @param key Hashing value
     * @return Hash String
     */
    private String generateHash(String key) {
        return BCrypt.hashpw(key, BCrypt.gensalt());
    }

    /**
     * This method is opening MainActivity and put necessary values for it.
     */
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

    /**
     * This method is resetting errors value.
     * The method is assigning 15 to errors and edit errors sharedPreference value.
     */
    private void resetErrors() {
        preferencesManager.setErrors();
    }

    /**
     * This method is checking errors more than 0.
     *
     * @return True if errors more than 0.
     */
    private boolean checkErrorsCount() {
        return preferencesManager.getErrors() > 0;
    }

    /**
     * This method is decrement for errors and edit errors value in sharedPreference.
     */
    private void errorDec() {
        preferencesManager.setErrors(preferencesManager.getErrors() - 1);
    }

    /**
     * This method is opening MainActivity for deleting all passwords.
     * It edit key_hint and assign true to pressed.
     */
    private void deleteAllPasswords() {
        startActivity(new Intent(this, MainActivity.class).putExtra("status", true));
        typingAnimation(keyHint, getResources().getString(R.string.all_passwords_was_deleted));
        pressed = true;
    }

    /**
     * This method is hiding keyboard from screen.
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
