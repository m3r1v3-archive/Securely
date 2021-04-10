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

        checkNewPassword();
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
    }

    /* Check changes methods */
    public void checkNewPassword() {
        if (checkNullable(getData("name"), getData("login"),
                getData("password"))) {
            db.passwordDao().insertItem(
                    new Password(
                            db.passwordDao().getMaxId() + 1,
                            getData("name"),
                            getData("login"),
                            getData("password"),
                            getData("description")
                    ));
        }
    }

    public void checkChanges() {
        try {
            if (!getData("status").isEmpty()) {
                if (getData("status").equals("edited")) {
                    if (getId("edited_id") > 0) {
                        if (checkNullable(getData("edited_name"), getData("edited_login"),
                                getData("edited_password"))) {
                            db.passwordDao().updateItem(getId("edited_id"),
                                    getData("edited_name"),
                                    getData("edited_login"),
                                    getData("edited_password"),
                                    getData("edited_description")
                            );
                        }
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
                    if (getId("deleted_id") > 0) {
                        db.passwordDao().deleteByItemId(getId("deleted_id"));
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

    public int getId(String name) {
        return getIntent().getIntExtra(name, -1);
    }

    public boolean checkNullable(String name, String login, String password) {
        try {
            return !name.isEmpty() || !login.isEmpty() || !password.isEmpty();
        } catch (NullPointerException exc) {
            return false;
        }
    }


    public void editPassword(int position) {
        Intent intent = new Intent(MainActivity.this, EditPasswordActivity.class);

        intent.putExtra("id_for_edit", position);

        intent.putExtra("name_for_edit",
                db.passwordDao().getNameById(position));
        intent.putExtra("login_for_edit",
                db.passwordDao().getLoginById(position));
        intent.putExtra("password_for_edit",
                db.passwordDao().getPasswordById(position));
        intent.putExtra("description_for_edit",
                db.passwordDao().getDescriptionById(position));

        startActivity(intent);
    }

    public void addInClipboard(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), label + " Password has been copied.", Toast.LENGTH_SHORT).show();
    }

    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(passwordList,
                position -> editPassword(position + 1),
                position -> addInClipboard(
                        db.passwordDao().getNameById(position + 1),
                        db.passwordDao().getPasswordById(position + 1)));


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