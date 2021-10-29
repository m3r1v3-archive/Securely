package com.merive.securepass.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.merive.securepass.MainActivity;
import com.merive.securepass.R;

public class BarFragment extends Fragment {

    ImageView settings, add, lock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bar_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initVariables(view);

        setSettings();
        setAdd();
        setLock();
    }

    public void initVariables(View view) {
        settings = view.findViewById(R.id.settings_button);
        add = view.findViewById(R.id.add_button);
        lock = view.findViewById(R.id.lock_button);
    }

    public void setSettings() {
        settings.setOnClickListener(v -> ((MainActivity) getActivity()).clickSettings());
    }

    public void setAdd() {
        add.setOnClickListener(v -> ((MainActivity) getActivity()).clickAdd());
    }

    public void setLock() {
        lock.setOnClickListener(v -> ((MainActivity) getActivity()).clickLock());
        lock.setOnLongClickListener(v -> {
            ((MainActivity) getActivity()).longClickLock();
            return true;
        });
    }
}
