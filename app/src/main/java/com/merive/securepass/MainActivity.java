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

    TypingTextView title;
    RecyclerView passwords;
    PasswordAdapter adapter;
    PasswordDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title_main);
        typingAnimation(title, getResources().getString(R.string.app_name));

        passwords = findViewById(R.id.password_recycle_view);
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords")
                .allowMainThreadQueries().build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAddNewPassword();
        checkChanges();
        checkDelete();

        new GetData().execute();
    }

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }

    /* Click methods */
    public void clickAdd(View view) {
        startActivity(new Intent(MainActivity.this, NewPasswordActivity.class));
        finish();
    }

    public void clickEditPassword(String name) {
        Intent intent = new Intent(MainActivity.this, EditPasswordActivity.class);

        intent.putExtra("name_for_edit",
                name);
        intent.putExtra("login_for_edit",
                db.passwordDao().getLoginByName(name));
        intent.putExtra("password_for_edit",
                db.passwordDao().getPasswordByName(name));
        intent.putExtra("description_for_edit",
                db.passwordDao().getDescriptionByName(name));

        startActivity(intent);
        finish();
    }

    /* Check changes methods */
    public void checkAddNewPassword() {
        if (checkNullable(getData("name"), getData("login"),
                getData("password"), getData("description"))) {
            if (db.passwordDao().checkExist(getData("name"))) {
                db.passwordDao().insertItem(
                        new Password(
                                getData("name"),
                                getData("login"),
                                getData("password"),
                                getData("description")
                        ));
                Toast.makeText(getBaseContext(),
                        getData("name") + " was added.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(),
                        getData("name") + " already in database. Try replace name.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkChanges() {
        try {
            if (!getData("status").isEmpty()) {
                if (getData("status").equals("edited")) {
                    if (checkNullable(getData("edited_name"),
                            getData("edited_login"),
                            getData("edited_password"),
                            getData("edited_description"))) {
                        db.passwordDao().updateItem(
                                getData("name_before"),
                                getData("edited_name"),
                                getData("edited_login"),
                                getData("edited_password"),
                                getData("edited_description")
                        );
                        Toast.makeText(getBaseContext(),
                                getData("edited_name") + " was edited.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (NullPointerException exc) {
            Log.i("Wasn't edit", "Password wasn't edit");
        }
    }

    public void checkDelete() {
        try {
            if (!getData("status").isEmpty()) {
                if (getData("status").equals("deleted")) {
                    if (!getData("deleted_name").equals("")) {
                        db.passwordDao().deleteByName(getData("deleted_name"));
                        Toast.makeText(getBaseContext(),
                                getData("deleted_name") + " was deleted.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (NullPointerException exp) {
            Log.i("No deleted items", "Nothing wasn't deleted");
        }
    }


    /* Methods for check changes methods */
    public String getData(String name) {
        return getIntent().getStringExtra(name);
    }

    public boolean checkNullable(String name, String login, String password, String des) {
        try {
            return !name.isEmpty() || !login.isEmpty() || !password.isEmpty() || !des.isEmpty();
        } catch (NullPointerException exc) {
            return false;
        }
    }

    public void addInClipboard(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), label + " Password was copied.",
                Toast.LENGTH_SHORT).show();
    }

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