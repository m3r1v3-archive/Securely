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
import com.merive.securely.utils.VibrationManager;

public class BarFragment extends Fragment {

    private ImageView settingsButton, addButton, lockButton;
    private MainActivity mainActivity;

    /**
     * Called to have the fragment instantiate its user interface view
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param parent             If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar, parent, false);
    }

    /**
     * Called immediately after onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) has returned, but before any saved state has been restored in to the view
     *
     * @param view               The View returned by onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initComponents();
        setListeners();
    }

    /**
     * Initialize BarFragment components
     */
    private void initComponents() {
        addButton = getView().findViewById(R.id.bar_add_button);
        lockButton = getView().findViewById(R.id.bar_lock_button);
        settingsButton = getView().findViewById(R.id.bar_settings_button);
        mainActivity = ((MainActivity) getActivity());
    }

    /**
     * Set onClick and onLongClick listeners for components
     */
    private void setListeners() {
        addButton.setOnClickListener(v -> clickAdd());
        settingsButton.setOnClickListener(v -> clickSettings());
        lockButton.setOnClickListener(v -> clickLock());
        lockButton.setOnLongClickListener(v -> {
            longClickLock();
            return true;
        });
    }

    /**
     * Make vibration effect and set PasswordFragment to bar_fragment
     *
     * @see PasswordFragment
     */
    private void clickAdd() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment(new PasswordFragment());
    }

    /**
     * Make vibration effect and lock application (start LoginActivity and finish MainActivity)
     *
     * @see MainActivity
     * @see LoginActivity
     */
    private void clickLock() {
        VibrationManager.makeVibration(getContext());
        startActivity(new Intent(mainActivity, LoginActivity.class));
        mainActivity.finish();
    }

    /**
     * Set ConfirmFragment to bar_fragment for changing key
     *
     * @see ConfirmFragment
     */
    private void longClickLock() {
        mainActivity.setBarFragment(ConfirmFragment.newInstance());
    }

    /**
     * Make vibration effect and open SettingsFragment
     *
     * @see SettingsFragment
     */
    public void clickSettings() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment(SettingsFragment.newInstance(
                mainActivity.preferencesManager.getLength(),
                mainActivity.preferencesManager.getShow(),
                mainActivity.getIntent().getBooleanExtra("delete_value", false),
                mainActivity.preferencesManager.getEncrypt()));
    }
}
