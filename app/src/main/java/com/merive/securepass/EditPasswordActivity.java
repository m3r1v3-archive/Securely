package com.merive.securepass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.merive.securepass.elements.TypingTextView;
import com.merive.securepass.generators.PasswordGenerator;

public class EditPasswordActivity extends AppCompatActivity {

    TypingTextView title;
    EditText nameEdit, loginEdit, passwordEdit, descriptionEdit;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        title = findViewById(R.id.add_title);
        typingAnimation(title, "Edit Password");

        id = getIntent().getIntExtra("id_for_edit", -1);

        nameEdit = findViewById(R.id.PasswordNameEditView);
        loginEdit = findViewById(R.id.loginEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);

        setEdits();
    }

    public void setEdits() {
        nameEdit.setText(getIntent().getStringExtra("name_for_edit"));
        loginEdit.setText(getIntent().getStringExtra("login_for_edit"));
        passwordEdit.setText(getIntent().getStringExtra("password_for_edit"));
        descriptionEdit.setText(getIntent().getStringExtra("description_for_edit"));
    }

    public void generatePassword(View view) {
        passwordEdit.setText(new PasswordGenerator(16).generatePassword());
    }

    public void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    public void cancelChanges(View view) {
        Intent intent = new Intent(EditPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void saveChanges(View view) {
        Intent intent = new Intent(EditPasswordActivity.this, MainActivity.class);

        intent.putExtra("status", "edited");

        intent.putExtra("edited_id", id);
        intent.putExtra("edited_name", nameEdit.getText().toString());
        intent.putExtra("edited_login", loginEdit.getText().toString());
        intent.putExtra("edited_password", passwordEdit.getText().toString());
        intent.putExtra("edited_description", descriptionEdit.getText().toString());

        startActivity(intent);
    }

    public void delete(View view) {
        Intent intent = new Intent(EditPasswordActivity.this, MainActivity.class);
        Log.i("Deleting", String.valueOf(id));

        intent.putExtra("status", "deleted");
        intent.putExtra("deleted_id", id);

        startActivity(intent);
    }
}