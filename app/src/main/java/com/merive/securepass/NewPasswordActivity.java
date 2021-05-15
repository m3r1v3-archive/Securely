package com.merive.securepass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securepass.elements.TypingTextView;
import com.merive.securepass.generators.PasswordGenerator;

public class NewPasswordActivity extends AppCompatActivity {

    TypingTextView title;
    EditText nameEdit, loginEdit, passwordEdit, descriptionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        title = findViewById(R.id.addTitle);
        typingAnimation(title, "Add New Password");

        nameEdit = findViewById(R.id.PasswordNameEditView);
        loginEdit = findViewById(R.id.loginEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
    }

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        /* Typing animation for TextViews */
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }


    /* Click methods */
    public void generatePassword(View view) {
        /* Generate password and put him to passwordEdit */
        passwordEdit.setText(new PasswordGenerator(getIntent().getIntExtra("length", 16)).generatePassword());
    }


    public void add(View view) {
        /* Put values and add password to database */
        if (checkEditsOnEmpty()) {
            Intent intent = getIntent();

            intent.putExtra("name", nameEdit.getText().toString());
            intent.putExtra("login", loginEdit.getText().toString());
            intent.putExtra("password", passwordEdit.getText().toString());
            intent.putExtra("description", descriptionEdit.getText().toString());

            setResult(1, intent);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "You have empty edits.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View view) {
        finish();
    }

    /* Methods for clicks */
    public boolean checkEditsOnEmpty() {
        /* Check edits on empty */
        return !nameEdit.getText().toString().isEmpty() &&
                !loginEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty();
    }
}