package com.merive.securely.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merive.securely.R;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.adapter.PasswordAdapter;
import com.merive.securely.database.Password;
import com.merive.securely.database.PasswordDB;

import java.util.List;

public class PasswordListFragment extends Fragment {

    public MainActivity mainActivity;
    private RecyclerView passwordsRecycler;
    private PasswordDB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_list, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();
        initVariables();
        setRecyclerViewData();
    }

    private void initComponents() {
        mainActivity = ((MainActivity) getActivity());
        passwordsRecycler = getView().findViewById(R.id.password_recycler_view);
    }

    private void initVariables() {
        db = mainActivity.db;
    }

    private void setRecyclerViewData() {
        new GetPasswordData().execute();
    }

    public void loadRecyclerView(List<Password> passwordList) {
        passwordsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        passwordsRecycler.setAdapter(new PasswordAdapter(passwordList, name -> mainActivity.clickPasswordRow(name), name -> mainActivity.clickPasswordOptions(name)));
    }

    public class GetPasswordData extends AsyncTask<Void, Void, List<Password>> {

        /**
         * Load password data
         *
         * @param params Ignored
         * @return Password data in List object
         */
        @Override
        protected List<Password> doInBackground(Void... params) {
            return db.passwordDao().getAll();
        }

        /**
         * Execute after doInBackground() method and loads password data to password_recycler_view component
         *
         * @param passwords Password data in List object
         */
        @Override
        protected void onPostExecute(List<Password> passwords) {
            super.onPostExecute(passwords);
            loadRecyclerView(passwords);
        }
    }


}
