package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.merive.securely.BuildConfig;
import com.merive.securely.R;
import com.merive.securely.activities.LoginActivity;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.elements.TypingTextView;
import com.merive.securely.utils.VibrationManager;


public class SettingsFragment extends Fragment {

    TypingTextView title, info;
    EditText passwordLengthEdit;
    SwitchCompat showPasswordSwitch, deletingSwitch, encryptingSwitch;
    ImageView cancel, deleteAll, save;

    public static SettingsFragment newInstance(int length, boolean show,
                                               boolean delete, boolean encrypting) {
        SettingsFragment frag = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt("length", length);
        args.putBoolean("show", show);
        args.putBoolean("delete", delete);
        args.putBoolean("encrypting", encrypting);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();

        setTitle();
        setInfo();

        setLengthEdit();
        setSwitches();

        cancel.setOnClickListener(this::clickCancel);
        deleteAll.setOnClickListener(this::clickDeleteAllPasswords);
        save.setOnClickListener(this::clickSave);
    }

    private void initVariables() {
        title = getView().findViewById(R.id.settings_title);
        info = getView().findViewById(R.id.settings_info_text);

        passwordLengthEdit = getView().findViewById(R.id.settings_password_length_edit);

        showPasswordSwitch = getView().findViewById(R.id.settings_show_switch);
        deletingSwitch = getView().findViewById(R.id.settings_delete_switch);
        encryptingSwitch = getView().findViewById(R.id.settings_encrypt_switch);

        cancel = getView().findViewById(R.id.settings_cancel_button);
        deleteAll = getView().findViewById(R.id.delete_passwords_button);
        save = getView().findViewById(R.id.save_settings_button);
    }

    private void setTitle() {
        typingAnimation(title, getResources().getString(R.string.settings));
    }

    private void setInfo() {
        typingAnimation(info, "Securely / " + BuildConfig.VERSION_NAME);
    }

    private void setLengthEdit() {
        passwordLengthEdit.setText(String.valueOf(getArguments().getInt("length")));
    }

    private void setSwitches() {
        showPasswordSwitch.setChecked(getArguments().getBoolean("show"));
        deletingSwitch.setChecked(getArguments().getBoolean("delete"));
        encryptingSwitch.setChecked(getArguments().getBoolean("encrypting"));
    }

    private void clickCancel(View view) {
        view.clearFocus();
        VibrationManager.makeVibration(getContext());
        ((MainActivity) getActivity()).setBarFragment();
    }

    private void clickDeleteAllPasswords(View view) {
        view.clearFocus();
        VibrationManager.makeVibration(getContext());
        ((MainActivity) getActivity()).setBarFragment();
        openConfirmAllPasswordsDelete();
    }

    private void clickSave(View view) {
        view.clearFocus();
        VibrationManager.makeVibration(getContext());
        ((MainActivity) getActivity()).saveSettings(
                passwordLengthEdit.getText().toString().isEmpty() ?
                        16 : Integer.parseInt(passwordLengthEdit.getText().toString()),
                showPasswordSwitch.isChecked(),
                deletingSwitch.isChecked(),
                encryptingSwitch.isChecked());
        ((MainActivity) getActivity()).setBarFragment();
    }

    private void openConfirmAllPasswordsDelete() {
        ((MainActivity) getActivity()).setBarFragment(new ConfirmFragment());
    }
}
