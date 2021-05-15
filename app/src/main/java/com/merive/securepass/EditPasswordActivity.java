package com.merive.securepass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securepass.elements.TypingTextView;
import com.merive.securepass.generators.PasswordGenerator;

public class EditPasswordActivity extends AppCompatActivity {

    TypingTextView title;
    EditText nameEdit, loginEdit, passwordEdit, descriptionEdit;
    String nameBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        title = findViewById(R.id.addTitle);
        typingAnimation(title, "Edit Password");

        nameBefore = getIntent().getStringExtra("name_for_edit");

        nameEdit = findViewById(R.id.PasswordNameEditView);
        loginEdit = findViewById(R.id.loginEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);

        setEditsData();
    }

    public void typingAnimation(TypingTextView view, String text) {
        /* Typing animation for text elements */
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    public void setEditsData() {
        /* Set data to EditTexts */
        nameEdit.setText(getIntent().getStringExtra("name_for_edit"));
        loginEdit.setText(getIntent().getStringExtra("login_for_edit"));
        passwordEdit.setText(getIntent().getStringExtra("password_for_edit"));
        descriptionEdit.setText(getIntent().getStringExtra("description_for_edit"));
    }

    public void generatePassword(View view) {
        /* Generate password and set to passwordEdit */
        passwordEdit.setText(new PasswordGenerator(getIntent().getIntExtra("length", 16)).generatePassword());
    }

    /* Click methods */
    public void cancelChanges(View view) {
        /* Close Activity */
        finish();
    }

    public void saveChanges(View view) {
        /* Put values to MainActivity and upload to database */
        if (checkEditsOnEmpty()) {
            Intent intent = getIntent();

            intent.putExtra("name_before", nameBefore);
            intent.putExtra("edited_name", nameEdit.getText().toString());
            intent.putExtra("edited_login", loginEdit.getText().toString());
            intent.putExtra("edited_password", passwordEdit.getText().toString());
            intent.putExtra("edited_description", descriptionEdit.getText().toString());

            setResult(2, intent);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "You have empty edits.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void delete(View view) {
        /* Delete password from database by name */
        Intent intent = getIntent();

        intent.putExtra("deleted_name", nameBefore);

        setResult(3, intent);
        finish();
    }

    public boolean checkEditsOnEmpty() {
        return !nameEdit.getText().toString().isEmpty() &&
                !loginEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty();
    }
}