package com.merive.securepass;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.merive.securepass.adapter.PasswordAdapter;
import com.merive.securepass.database.Password;
import com.merive.securepass.database.PasswordDB;
import com.merive.securepass.elements.TypingTextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TypingTextView title, empty;
    RecyclerView passwords;
    PasswordAdapter adapter;
    PasswordDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title_main);
        empty = findViewById(R.id.empty);
        typingAnimation(title, getResources().getString(R.string.app_name));

        passwords = findViewById(R.id.password_recycle_view);
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords")
                .allowMainThreadQueries().build();


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEmpty();

        new GetData().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                addNewPassword(data);
                break;
            case 2:
                editPassword(data);
                break;
            case 3:
                deletePassword(data);
                break;
        }
    }

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    /* Click methods */
    public void clickAdd(View view) {
        startActivityForResult(new Intent(this, NewPasswordActivity.class), 1);
    }

    public void clickEditPassword(String name) {
        Intent intent = new Intent(this, EditPasswordActivity.class);

        intent.putExtra("name_for_edit",
                name);
        intent.putExtra("login_for_edit",
                db.passwordDao().getLoginByName(name));
        intent.putExtra("password_for_edit",
                db.passwordDao().getPasswordByName(name));
        intent.putExtra("description_for_edit",
                db.passwordDao().getDescriptionByName(name));

        startActivityForResult(intent, 2);
    }


    /* Activities event methods */
    public void addNewPassword(Intent intent) {
        if (db.passwordDao().checkExist(getData(intent, "name"))) {
            db.passwordDao().insertItem(
                    new Password(
                            getData(intent, "name"),
                            getData(intent, "login"),
                            getData(intent, "password"),
                            getData(intent, "description")
                    ));
            Toast.makeText(getBaseContext(),
                    getData(intent, "name") + " was added.",
                    Toast.LENGTH_SHORT).show();
            new GetData().execute();
        } else {
            Toast.makeText(getBaseContext(),
                    getData(intent, "name") + " already in database. Try replace name.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void editPassword(Intent intent) {
        if (db.passwordDao().checkExist(getData(intent, "edited_name")) ||
                getData(intent, "name_before").equals(getData(intent, "edited_name"))) {
            db.passwordDao().updateItem(
                    getData(intent, "name_before"),
                    getData(intent, "edited_name"),
                    getData(intent, "edited_login"),
                    getData(intent, "edited_password"),
                    getData(intent, "edited_description")
            );
            Toast.makeText(getBaseContext(),
                    getData(intent, "edited_name") + " was edited.",
                    Toast.LENGTH_SHORT).show();
            new GetData().execute();
        } else {
            Toast.makeText(getBaseContext(), getData(intent, "edited_name") + " already exist.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void deletePassword(Intent intent) {
        Log.i("", "In deletePassword");
        db.passwordDao().deleteByName(getData(intent, "deleted_name"));
        Toast.makeText(getBaseContext(),
                getData(intent, "deleted_name") + " was deleted.",
                Toast.LENGTH_SHORT).show();
        new GetData().execute();
    }


    /* Check methods */
    public void checkEmpty() {
        if (db.passwordDao().checkEmpty()) {
            empty.setVisibility(View.VISIBLE);
            typingAnimation(empty, getResources().getString(R.string.list_is_empty));
        }
    }

    public String getData(Intent intent, String name) {
        return intent.getStringExtra(name);
    }

    public boolean checkNullable(String name, String login, String password, String des) {
        try {
            return !name.isEmpty() || !login.isEmpty() || !password.isEmpty() || !des.isEmpty();
        } catch (NullPointerException exc) {
            return false;
        }
    }


    /* Clipboard methods */
    public void addInClipboard(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), label + " Password was copied.",
                Toast.LENGTH_SHORT).show();
    }

    /* RecyclerView methods/classes */
    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(passwordList,
                name -> clickEditPassword(name),
                name -> addInClipboard(
                        name,
                        db.passwordDao().getPasswordByName(name)));

        passwords.setAdapter(adapter);
    }

    public class GetData extends AsyncTask<Void, Void, List<Password>> {

        @Override
        protected List<Password> doInBackground(Void... params) {
            return db.passwordDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Password> passwords) {
            super.onPostExecute(passwords);
            loadRecyclerView(passwords);
        }
    }
}