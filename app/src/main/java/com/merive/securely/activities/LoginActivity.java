package com.merive.securely.activities;

import static com.merive.securely.components.TypingTextView.typingAnimation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securely.R;
import com.merive.securely.components.TypingTextView;
import com.merive.securely.preferences.PreferencesManager;
import com.merive.securely.utils.VibrationManager;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    public static PreferencesManager preferencesManager;

    private ImageView loginButton, restoreButton, passwordButton;
    private TypingTextView titleTypingText, hintTypingText;
    private EditText passwordEditText;
    private boolean pressed = false, passwordEdit = false, restore = false;

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

        checkPasswordOnAbsence();
        checkDeleteEdit();
    }

    /**
     * Initialize basic layout components
     */
    private void initComponents() {
        titleTypingText = findViewById(R.id.login_title_text);
        hintTypingText = findViewById(R.id.login_hint_text);
        passwordEditText = findViewById(R.id.login_password_edit);
        loginButton = findViewById(R.id.login_button);
        restoreButton = findViewById(R.id.restore_button);
        passwordButton = findViewById(R.id.password_button);
    }

    /**
     * Set components listeners (onClickListener for loginButton and onEditorActionListener for passwordEditText()
     */
    private void setListeners() {
        loginButton.setOnClickListener(v -> clickLogin());
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clickLogin();
                hideKeyboard();
                return false;
            }
            return true;
        });
        restoreButton.setOnClickListener(v -> clickRestore());
        passwordButton.setOnClickListener(v -> clickPassword());
    }

    /**
     * Set texts to TypingTextView components using typingAnimation() method
     */
    private void setTexts() {
        typingAnimation(titleTypingText, getResources().getString(R.string.welcome_to_securely));
        typingAnimation(hintTypingText, getResources().getString(R.string.enter_the_password_in_the_field));
    }

    /**
     * Set hint message if hash in SharedPreferences value is "-1" (Default value)
     */
    private void checkPasswordOnAbsence() {
        if (preferencesManager.getHash().equals("-1"))
            typingAnimation(hintTypingText, getResources().getString(R.string.create_new_password));
        else {
            restoreButton.setVisibility(View.VISIBLE);
            passwordButton.setVisibility(View.VISIBLE);
        }
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
     * Make vibration, check passwordEdit value: if true start editPassword() method, else start login() method
     *
     * @see LoginActivity
     */
    private void clickLogin() {
        VibrationManager.makeVibration(getApplicationContext());
        if (passwordEdit) editPassword();
        else if (restore && passwordEditText.getText().toString().equals("0000")) restore();
        else if (restore) cancelRestore();
        else login();
    }

    private void clickPassword() {
        typingAnimation(hintTypingText, getResources().getString(R.string.enter_old_password));
        passwordEdit = true;
        restore = false;
    }

    private void clickRestore() {
        typingAnimation(hintTypingText, getResources().getString(R.string.enter_code_to_restore));
        restore = true;
        passwordEdit = false;
    }

    private void cancelRestore() {
        typingAnimation(hintTypingText, getResources().getString(R.string.restore_canceled));
        restore = false;
    }

    /**
     * Reset password if checkPasswordHash() return true, start setting the password.
     * Else set hintTypingText message using typingAnimation() method
     */
    private void editPassword() {
        if (checkPasswordHash()) {
            openMainToDisableEncrypt(passwordEditText.getText().toString());
            preferencesManager.setHash();
            checkPasswordOnAbsence();
            passwordEditText.setText("");
            passwordEdit = false;
            pressed = false;
        } else typingAnimation(hintTypingText, getResources().getString(R.string.invalid_password));
    }

    /**
     * Check password hash from passwordEditText and return result value.
     * If result is true, sets hintTypingText message using typingAnimation() method and change pressed variable value
     *
     * @return Result of hash checking
     */
    private boolean checkPasswordHash() {
        if (BCrypt.checkpw(passwordEditText.getText().toString(), preferencesManager.getHash())) {
            typingAnimation(hintTypingText, getResources().getString(R.string.successful_login));
            pressed = true;
            return true;
        }
        return false;
    }

    private void restore() {
        deleteAllPasswords();
        preferencesManager.setHash();
        typingAnimation(hintTypingText, getResources().getString(R.string.successful_restore));
        passwordEditText.setText("");
        restore = false;
    }

    /**
     * Login in MainActivity if pressed isn't true, passwordEditText isn't empty.
     * Check password hash value, if it is default value, set password hash and open MainActivity, else differ passwordEditText text hash and hash from SharedPreferences
     * Else if password is invalid, check errors value and set message to hintTypingText.
     * If errors count equals 0, delete all passwords
     */
    private void login() {
        if (!pressed) {
            if (!passwordEditText.getText().toString().isEmpty()) {
                if (setPassword()) openMain();
                else if (checkPasswordHash()) openMain();
                else {
                    if (preferencesManager.getErrors() > 0) {
                        typingAnimation(hintTypingText, getResources().getString(R.string.invalid_password));
                        if (preferencesManager.getDelete())
                            preferencesManager.setErrors(preferencesManager.getErrors() - 1);
                    } else deleteAllPasswords();
                }
            }
        }
    }

    /**
     * Set password hash to SharedPreferences if hash value is "-1" (Default value).
     * Set hintTypingText message using typingAnimation() method and change pressed value to true
     *
     * @return True if sets new password, else false
     */
    private boolean setPassword() {
        if (preferencesManager.getHash().equals("-1")) {
            preferencesManager.setHash(generateHash(passwordEditText.getText().toString()));
            typingAnimation(hintTypingText, getResources().getString(R.string.password_set));
            pressed = true;
            return true;
        }
        return false;
    }

    /**
     * Generate password hash
     *
     * @param password Password value, that will be hashed
     * @return Hashed String value
     */
    private String generateHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
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
                    .putExtra("encrypt_value", false)
                    .putExtra("delete_value", preferencesManager.getDelete())
                    .putExtra("password_value", passwordEditText.getText().toString()));
            finish();
        }, 3250);
    }

    private void openMainToDisableEncrypt(String password) {
        new Handler().postDelayed(() -> {
            preferencesManager.setErrors();
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("delete_all", false)
                    .putExtra("encrypt_value", true)
                    .putExtra("delete_value", preferencesManager.getDelete())
                    .putExtra("password_value", passwordEditText.getText().toString()));
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
