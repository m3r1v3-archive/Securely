package com.merive.securepass;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

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
    boolean deleteAfterErrors, pressed, changeKey;

    /**
     * This method is the start point at the CheckKeyActivity.
     *
     * @param savedInstanceState Used by super.onCreate method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_check_key);

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        errors = sharedPreferences.getInt("errors", 15);

        deleteAfterErrors = sharedPreferences.getBoolean("delete", false);
        changeKey = false;
        pressed = false;

        initLayoutVariables();

        typingAnimation(title, getResources().getString(R.string.welcome_to_securepass));
        typingAnimation(key_hint, getResources().getString(R.string.enter_the_key_in_the_field));

        checkKeyOnAbsence();
        checkDeleteAfterErrorsEdit();
        checkKeyEdit();

        setKeyOnEditorListener();
    }

    /**
     * This method is assigns main layout variables.
     */
    public void initLayoutVariables() {
        title = findViewById(R.id.check_key_title);
        key = findViewById(R.id.key_edit);
        key_hint = findViewById(R.id.check_key_hint);
    }

    /**
     * This method is checking key in sharedPreference on absence.
     * If key's hash in memory is -1, will set special hint.
     *
     * @see SharedPreferences
     */
    public void checkKeyOnAbsence() {
        if (sharedPreferences.getString("hash", "-1").equals("-1")) {
            typingAnimation(key_hint, getResources().getString(R.string.create_a_new_key));
        }
    }

    /**
     * This method is checking if Delete After Errors was edit in MainActivity.
     *
     * @see MainActivity
     */
    public void checkDeleteAfterErrorsEdit() {
        if (getIntent().getBooleanExtra("status", false)) {
            sharedPreferences.edit().putBoolean("delete",
                    getIntent().getBooleanExtra("delete", false)).apply();
            deleteAfterErrors = getIntent().getBooleanExtra("delete", false);
            finish();
        }
    }

    /**
     * This method is checking if was confirm key editing in MainActivity.
     *
     * @see MainActivity
     * @see com.merive.securepass.fragments.ConfirmFragment
     */
    public void checkKeyEdit() {
        if (getIntent().getBooleanExtra("changeKey", false)) {
            typingAnimation(key_hint, getResources().getString(R.string.enter_old_key));
            changeKey = true;
        }
    }

    /**
     * This method is setting listener for done button on keyboard.
     * If after dialing the key you press the button done, key will checked.
     */
    public void setKeyOnEditorListener() {
        key.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clickCheckKey(key);
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
    public void clickCheckKey(View view) {
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
    public void makeVibration() {
        if (checkSoundMode())
            VibrationManager.makeVibration(getApplicationContext());
    }

    /**
     * This method is checking ringer mode of device.
     *
     * @return If mode isn't mute, return true.
     */
    public boolean checkSoundMode() {
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
    public void changeKey() {
        if (checkKeyHash()) {
            resetKey();
            checkKeyOnAbsence();
            key.setText("");
            changeKey = false;
            pressed = false;
        } else typingAnimation(key_hint, getResources().getString(R.string.invalid_key));
    }

    /**
     * This method is checking inputted key hash and sharedPreference Key Hash.
     *
     * @return true if hashes are equal, else False.
     */
    public boolean checkKeyHash() {
        if (BCrypt.checkpw(key.getText().toString(), sharedPreferences.getString("hash", "-1"))) {
            typingAnimation(key_hint, getResources().getString(R.string.all_right));
            pressed = true;
            return true;
        }
        return false;
    }

    /**
     * This method is resetting key in sharedPreference to default ("-1")
     */
    public void resetKey() {
        sharedPreferences.edit().putString("hash", "-1").apply();
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
    public void login() {
        if (!pressed) {
            if (!key.getText().toString().equals("")) {
                if (checkOnNoKey()) openMain();
                else if (checkKeyHash()) openMain();
                else {
                    if (checkErrorsCount()) {
                        typingAnimation(key_hint, getResources().getString(R.string.invalid_key));
                        if (deleteAfterErrors) errorDec();
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
    public boolean checkOnNoKey() {
        if (sharedPreferences.getString("hash", "-1").equals("-1")) {
            sharedPreferences.edit().putString("hash", generateHash(key.getText().toString())).apply();
            typingAnimation(key_hint, getResources().getString(R.string.key_set));
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
    public String generateHash(String key) {
        return BCrypt.hashpw(key, BCrypt.gensalt());
    }

    /**
     * This method is opening MainActivity and put necessary values for it.
     */
    public void openMain() {
        new Handler().postDelayed(() -> {
            resetErrors();
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("status", false)
                    .putExtra("delete", deleteAfterErrors)
                    .putExtra("key", Integer.parseInt(key.getText().toString()))
            );
            finish();
        }, 3250);
    }

    /**
     * This method is resetting errors value.
     * The method is assigning 15 to errors and edit errors sharedPreference value.
     */
    public void resetErrors() {
        errors = 15;
        sharedPreferences.edit().putInt("errors", errors).apply();
    }

    /**
     * This method is checking errors more than 0.
     *
     * @return True if errors more than 0.
     */
    public boolean checkErrorsCount() {
        return errors > 0;
    }

    /**
     * This method is decrement for errors and edit errors value in sharedPreference.
     */
    public void errorDec() {
        sharedPreferences.edit().putInt("errors", --errors).apply();
    }

    /**
     * This method is opening MainActivity for deleting all passwords.
     * It edit key_hint and assign true to pressed.
     */
    public void deleteAllPasswords() {
        startActivity(new Intent(this, MainActivity.class)
                .putExtra("status", true)
        );
        typingAnimation(key_hint, getResources().getString(R.string.all_passwords_was_deleted));
        pressed = true;
    }

    /**
     * This method is hiding keyboard from screen.
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
