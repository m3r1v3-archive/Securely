package com.merive.securely.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.merive.securely.activities.LoginActivity;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;

public class BarFragment extends Fragment {

    ImageView settings, add, lock;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initVariables(view);
        setListeners();
        mainActivity = ((MainActivity) getActivity());
    }

    private void initVariables(View view) {
        settings = view.findViewById(R.id.bar_settings_button);
        add = view.findViewById(R.id.bar_add_button);
        lock = view.findViewById(R.id.bar_lock_button);
    }

    private void setListeners() {
        add.setOnClickListener(v -> clickAdd());
        settings.setOnClickListener(v -> clickSettings());
        lock.setOnClickListener(v -> clickLock());
        lock.setOnLongClickListener(v -> {
            longClickLock();
            return true;
        });
    }

    private void clickAdd() {
        mainActivity.makeVibration();
        mainActivity.setBarFragment(new PasswordFragment());
    }

    private void clickLock() {
        mainActivity.makeVibration();
        startActivity(new Intent(mainActivity, LoginActivity.class));
        mainActivity.finish();
    }

    private void longClickLock() {
        mainActivity.setBarFragment(ConfirmFragment.newInstance(true));
    }

    public void clickSettings() {
        mainActivity.makeVibration();
        mainActivity.setBarFragment(SettingsFragment.newInstance(
                mainActivity.preferencesManager.getLength(),
                mainActivity.preferencesManager.getShow(),
                mainActivity.getIntent().getBooleanExtra("delete_value", false),
                mainActivity.preferencesManager.getEncrypt()));
    }
}
