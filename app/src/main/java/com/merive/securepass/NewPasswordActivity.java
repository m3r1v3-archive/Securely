package com.merive.securepass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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

    public void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(150);
        view.animateText(text);
    }

    public void add(View view) {
        if (checkEdits()) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("name", nameEdit.getText().toString());
            intent.putExtra("login", loginEdit.getText().toString());
            intent.putExtra("password", passwordEdit.getText().toString());
            intent.putExtra("description", descriptionEdit.getText().toString());
            startActivity(intent);
            finish();
        }
    }

    public boolean checkEdits() {
        return !nameEdit.getText().toString().isEmpty() ||
                !loginEdit.getText().toString().isEmpty() ||
                !passwordEdit.getText().toString().isEmpty();
    }
}