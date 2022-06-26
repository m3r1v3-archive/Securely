package com.merive.securely.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;

public class BarFragment extends Fragment {

    ImageView settings, add, lock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initVariables(view);

        setSettings();
        setAdd();
        setLock();
    }

    private void initVariables(View view) {
        settings = view.findViewById(R.id.bar_settings_button);
        add = view.findViewById(R.id.bar_add_button);
        lock = view.findViewById(R.id.bar_lock_button);
    }

    private void setSettings() {
        settings.setOnClickListener(v -> ((MainActivity) getActivity()).clickSettings());
    }

    private void setAdd() {
        add.setOnClickListener(v -> ((MainActivity) getActivity()).clickAdd());
    }

    private void setLock() {
        lock.setOnClickListener(v -> ((MainActivity) getActivity()).clickLock());
        lock.setOnLongClickListener(v -> {
            ((MainActivity) getActivity()).longClickLock();
            return true;
        });
    }
}
