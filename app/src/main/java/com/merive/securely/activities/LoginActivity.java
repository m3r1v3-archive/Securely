package com.merive.securely.activities;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securely.R;
import com.merive.securely.elements.TypingTextView;
import com.merive.securely.preferences.PreferencesManager;
import com.merive.securely.utils.VibrationManager;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    public static PreferencesManager preferencesManager;

    private ImageView loginButton;
    private TypingTextView titleTypingText, hintTypingText;
    private EditText keyEditText;
    private boolean pressed = false, keyEdit = false;

    /**
     * Called by the system when the service is first created
     *
     * @param savedInstanceState Using by super.onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_login);

        preferencesManager = new PreferencesManager(this.getBaseContext());

        initComponents();
        setListeners();
        setTexts();

        checkKeyOnAbsence();
        checkDeleteEdit();
        checkKeyEdit();
    }

    /**
     * Initialize basic layout components
     */
    private void initComponents() {
        titleTypingText = findViewById(R.id.login_title_text);
        hintTypingText = findViewById(R.id.login_hint_text);
        keyEditText = findViewById(R.id.login_key_edit);
        loginButton = findViewById(R.id.login_button);
    }

    /**
     * Set components listeners (onClickListener for loginButton and onEditorActionListener for keyEditText)
     */
    private void setListeners() {
        loginButton.setOnClickListener(v -> clickLogin());
        keyEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clickLogin();
                hideKeyboard();
                return false;
            }
            return true;
        });
    }

    /**
     * Set texts to TypingTextView components using typingAnimation() method
     */
    private void setTexts() {
        typingAnimation(titleTypingText, getResources().getString(R.string.welcome_to_securely));
        typingAnimation(hintTypingText, getResources().getString(R.string.enter_the_key_in_the_field));
    }

    /**
     * Set hint message if hash in SharedPreferences value is "-1" (Default value)
     */
    private void checkKeyOnAbsence() {
        if (preferencesManager.getHash().equals("-1"))
            typingAnimation(hintTypingText, getResources().getString(R.string.create_new_key));
    }

    /**
     * Change delete value in SharedPreferences and close Activity if intent "delete_edit" boolean extra is true
     */
    private void checkDeleteEdit() {
        if (getIntent().getBooleanExtra("delete_edit", false)) {
            preferencesManager.setDelete(getIntent().getBooleanExtra("delete_value", false));
            finish();
        }
    }

    /**
     * Set hintTypingText message and change keyEdit variable value to true if intent "key_edit" boolean extra is true
     */
    private void checkKeyEdit() {
        if (getIntent().getBooleanExtra("key_edit", false)) {
            typingAnimation(hintTypingText, getResources().getString(R.string.enter_old_key));
            keyEdit = true;
        }
    }

    /**
     * Make vibration, check keyEdit value: if true start editKey() method, else start login() method
     *
     * @see LoginActivity
     */
    private void clickLogin() {
        makeVibration();
        if (keyEdit) editKey();
        else login();
    }

    /**
     * Make vibration effect if Ringer Mode isn't Mute
     */
    private void makeVibration() {
        if ((((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0))
            VibrationManager.makeVibration(getApplicationContext());
    }

    /**
     * Reset key if checkKeyHash() return true, start setting the key.
     * Else set hintTypingText message using typingAnimation() method
     */
    private void editKey() {
        if (checkKeyHash()) {
            preferencesManager.setHash();
            checkKeyOnAbsence();
            keyEditText.setText("");
            keyEdit = false;
            pressed = false;
        } else typingAnimation(hintTypingText, getResources().getString(R.string.invalid_key));
    }

    /**
     * Check Key Hash from keyEditText and return result value.
     * If result is true, sets hintTypingText message using typingAnimation() method and change pressed variable value
     *
     * @return Result of hash checking
     */
    private boolean checkKeyHash() {
        if (BCrypt.checkpw(keyEditText.getText().toString(), preferencesManager.getHash())) {
            typingAnimation(hintTypingText, getResources().getString(R.string.successful_login));
            pressed = true;
            return true;
        }
        return false;
    }

    /**
     * Login in MainActivity if pressed isn't true, keyEditText isn't empty.
     * Check key hash value, if it is default value, set key hash and open MainActivity, else differ keyEditText text hash and hash from SharedPreferences
     * Else if key is invalid, check errors value and set message to hintTypingText.
     * If errors count equals 0, delete all passwords
     */
    private void login() {
        if (!pressed) {
            if (!keyEditText.getText().toString().isEmpty()) {
                if (setKey()) openMain();
                else if (checkKeyHash()) openMain();
                else {
                    if (preferencesManager.getErrors() > 0) {
                        typingAnimation(hintTypingText, getResources().getString(R.string.invalid_key));
                        if (preferencesManager.getDelete())
                            preferencesManager.setErrors(preferencesManager.getErrors() - 1);
                    } else deleteAllPasswords();
                }
            }
        }
    }

    /**
     * Set key hash to SharedPreferences if hash value is "-1" (Default value).
     * Set hintTypingText message using typingAnimation() method and change pressed value to true
     *
     * @return True if sets new key, else false
     */
    private boolean setKey() {
        if (preferencesManager.getHash().equals("-1")) {
            preferencesManager.setHash(generateHash(keyEditText.getText().toString()));
            typingAnimation(hintTypingText, getResources().getString(R.string.key_set));
            pressed = true;
            return true;
        }
        return false;
    }

    /**
     * Generate key hash
     *
     * @param key Key value, that will be hashed
     * @return Hashed String value
     */
    private String generateHash(String key) {
        return BCrypt.hashpw(key, BCrypt.gensalt());
    }

    /**
     * Reset error value, start MainActivity and close LoginActivity
     *
     * @see MainActivity
     */
    private void openMain() {
        new Handler().postDelayed(() -> {
            preferencesManager.setErrors();
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("delete_all", false)
                    .putExtra("delete_value", preferencesManager.getDelete())
                    .putExtra("key_value", Integer.parseInt(keyEditText.getText().toString())));
            finish();
        }, 3250);
    }

    /**
     * Open MainActivity for deleting all passwords and set hintTypingView message
     */
    private void deleteAllPasswords() {
        startActivity(new Intent(this, MainActivity.class).putExtra("delete_all", true));
        typingAnimation(hintTypingText, getResources().getString(R.string.all_passwords_deleted));
    }

    /**
     * Hide keyboard from screen
     */
    private void hideKeyboard() {
        if (this.getCurrentFocus() != null)
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
}
