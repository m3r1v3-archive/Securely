package com.merive.securepass;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.merive.securepass.database.Password;
import com.merive.securepass.database.PasswordDB;
import com.merive.securepass.adapter.PasswordAdapter;
import com.merive.securepass.elements.TypingTextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TypingTextView title;
    RecyclerView passwords;
    PasswordAdapter adapter;
    PasswordDB db;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title_main);
        typingAnimation(title, getResources().getString(R.string.app_name));

        passwords = findViewById(R.id.password_recycle_view);
        db = Room.databaseBuilder(MainActivity.this, PasswordDB.class, "passwords").allowMainThreadQueries().build();
        addPassword();
        new GetData().execute();
    }


    public void typingAnimation(TypingTextView view, String text) {
        view.setText("");
        view.setCharacterDelay(150);
        view.animateText(text);
    }

    public void clickAdd(View view) {
        startActivity(new Intent(MainActivity.this, NewPasswordActivity.class));
        finish();
    }

    public void addPassword() {
        if (checkEdits(getNameData(), getLoginData(), getPasswordData())) {
            db.passwordDao().insertItem(
                    new Password(
                            getNameData(),
                            getLoginData(),
                            getPasswordData(),
                            getDescriptionData()
                    ));
        }
    }

    public String getNameData() {
        return getIntent().getStringExtra("name");
    }

    public String getLoginData() {
        return getIntent().getStringExtra("login");
    }

    public String getPasswordData() {
        return getIntent().getStringExtra("password");
    }

    public String getDescriptionData() {
        return getIntent().getStringExtra("description");
    }

    public boolean checkEdits(String name, String login, String password) {
        try {
            return !name.isEmpty() || !login.isEmpty() || !password.isEmpty();
        } catch (NullPointerException exc) {
            return false;
        }
    }

    private void loadRecyclerView(List<Password> passwordList) {
        passwords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(passwordList, position -> addInClipboard(
                db.passwordDao().getNameById(position + 1),
                db.passwordDao().getPasswordById(position + 1)));
        passwords.setAdapter(adapter);
    }

    public void addInClipboard(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, value);
        clipboard.setPrimaryClip(clip);
        Log.i("", "Copied");
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