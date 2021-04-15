package com.merive.securepass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

        title = findViewById(R.id.add_title);
        typingAnimation(title, "Add New Password");

        nameEdit = findViewById(R.id.PasswordNameEditView);
        loginEdit = findViewById(R.id.loginEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
    }

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }


    /* Click methods */
    public void generatePassword(View view) {
        passwordEdit.setText(new PasswordGenerator(16).generatePassword());
    }


    public void add(View view) {
        if (checkEditsOnEmpty()) {
            Intent intent = getIntent();

            intent.putExtra("name", nameEdit.getText().toString());
            intent.putExtra("login", loginEdit.getText().toString());
            intent.putExtra("password", passwordEdit.getText().toString());
            intent.putExtra("description", descriptionEdit.getText().toString());

            setResult(1, intent);
            finish();
        }
    }

    public void cancel(View view) {
        finish();
    }

    /* Methods for clicks */
    public boolean checkEditsOnEmpty() {
        return !nameEdit.getText().toString().isEmpty() ||
                !loginEdit.getText().toString().isEmpty() ||
                !passwordEdit.getText().toString().isEmpty();
    }
}