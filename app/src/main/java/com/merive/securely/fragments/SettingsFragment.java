package com.merive.securely.fragments;

import static com.merive.securely.components.TypingTextView.typingAnimation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.merive.securely.BuildConfig;
import com.merive.securely.R;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.components.TypingTextView;
import com.merive.securely.utils.VibrationManager;


public class SettingsFragment extends Fragment {

    private TypingTextView titleTypingText, infoTypingText;
    private EditText lengthEdit;
    private SwitchCompat showSwitch, deleteSwitch, encryptSwitch;
    private ImageView cancelButton, deleteAllButton, saveButton, reloadButton;
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
        return inflater.inflate(R.layout.fragment_settings, parent, false);
    }

    /**
     * Called immediately after onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) has returned, but before any saved state has been restored in to the view
     *
     * @param view               The View returned by onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();
        setTypingTexts();
        setListeners();

        setEdits();
        setSwitches();
    }

    /**
     * Initialize SettingsFragment components
     */
    private void initComponents() {
        titleTypingText = getView().findViewById(R.id.settings_title);
        infoTypingText = getView().findViewById(R.id.settings_info_text);
        lengthEdit = getView().findViewById(R.id.settings_password_length_edit);
        showSwitch = getView().findViewById(R.id.settings_show_switch);
        deleteSwitch = getView().findViewById(R.id.settings_delete_switch);
        encryptSwitch = getView().findViewById(R.id.settings_encrypt_switch);
        cancelButton = getView().findViewById(R.id.settings_cancel_button);
        deleteAllButton = getView().findViewById(R.id.delete_passwords_button);
        saveButton = getView().findViewById(R.id.save_settings_button);
        reloadButton = getView().findViewById(R.id.settings_reload_button);
        mainActivity = (MainActivity) getActivity();
    }

    /**
     * Set texts to TypingTextView components
     */
    private void setTypingTexts() {
        typingAnimation(titleTypingText, getResources().getString(R.string.settings));
        typingAnimation(infoTypingText, getString(R.string.securely_info, BuildConfig.VERSION_NAME));
    }

    /**
     * Set onClick listeners for components
     */
    private void setListeners() {
        cancelButton.setOnClickListener(v -> clickCancel());
        deleteAllButton.setOnClickListener(v -> clickDeletePasswords());
        saveButton.setOnClickListener(v -> clickSave());
        reloadButton.setOnClickListener(v -> clickReload());
    }

    /**
     * Set length value to lengthEdit component
     */
    private void setEdits() {
        lengthEdit.setText(String.valueOf(mainActivity.preferencesManager.getLength()));
    }

    /**
     * Set values to SwitchCompat components
     */
    private void setSwitches() {
        showSwitch.setChecked(mainActivity.preferencesManager.getShow());
        deleteSwitch.setChecked(mainActivity.preferencesManager.getDelete());
        encryptSwitch.setChecked(mainActivity.preferencesManager.getEncrypt());
    }

    /**
     * Make vibration and set BarFragment to bar_fragment component
     *
     * @see BarFragment
     */
    private void clickCancel() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment();
    }

    private void clickReload() {
        VibrationManager.makeVibration(getContext());
        mainActivity.checkVersion();
    }

    /**
     * Make vibration effect and set ConfirmFragment to bar_fragment component for confirm delete all password
     *
     * @see ConfirmFragment
     */
    private void clickDeletePasswords() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment(new ConfirmFragment());
    }

    /**
     * Make vibration effect and save settings in SharedPreferences
     *
     * @see android.content.SharedPreferences
     */
    private void clickSave() {
        VibrationManager.makeVibration(getContext());
        mainActivity.saveSettings(
                lengthEdit.getText().toString().isEmpty() ? 16 : Integer.parseInt(lengthEdit.getText().toString()),
                showSwitch.isChecked(),
                deleteSwitch.isChecked(),
                encryptSwitch.isChecked());
        mainActivity.setBarFragment();
    }
}
