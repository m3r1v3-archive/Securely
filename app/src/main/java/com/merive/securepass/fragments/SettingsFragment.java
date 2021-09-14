package com.merive.securepass.fragments;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.merive.securepass.BuildConfig;
import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;

import java.util.Calendar;


public class SettingsFragment extends DialogFragment {

    TypingTextView title, info;
    EditText passwordLengthEdit;
    SwitchCompat showPasswordSwitch, deletingSwitch, encryptingSwitch;
    ImageView cancel, deleteAll, save;

    public SettingsFragment() {
        /* Empty constructor (Needs) */
    }

    public static SettingsFragment newInstance(int length, boolean show,
                                               boolean deleting, boolean encrypting) {
        /* newInstance method */
        SettingsFragment frag = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt("length", length);
        args.putBoolean("show", show);
        args.putBoolean("deleting", deleting);
        args.putBoolean("encrypting", encrypting);
        frag.setArguments(args);
        return frag;
    }

    /* **************** */
    /* Override methods */
    /* **************** */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.settings_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables(view);
        typingAnimation(title, getResources().getString(R.string.settings));
        typingAnimation(info, "SecurePass " + BuildConfig.VERSION_NAME + ", " + Calendar.getInstance().get(Calendar.YEAR));

        setEdits();
        setSwitches();

        cancel.setOnClickListener(this::clickCancel);
        deleteAll.setOnClickListener(this::clickDeleteAllPasswords);
        save.setOnClickListener(this::clickSave);
    }

    /* ************ */
    /* Init methods */
    /* ************ */

    public void initVariables(View view) {
        /* Init main variables */
        title = view.findViewById(R.id.settingsTitle);
        info = view.findViewById(R.id.info);
        passwordLengthEdit = view.findViewById(R.id.passwordLengthEdit);
        showPasswordSwitch = view.findViewById(R.id.showPasswordSwitch);
        deletingSwitch = view.findViewById(R.id.deletingSwitch);
        encryptingSwitch = view.findViewById(R.id.encryptingSwitch);
        cancel = view.findViewById(R.id.cancelSettings);
        deleteAll = view.findViewById(R.id.deletePasswords);
        save = view.findViewById(R.id.saveSettings);
    }

    /* *********** */
    /* Set methods */
    /* *********** */

    public void setEdits() {
        passwordLengthEdit.setText(String.valueOf(getArguments().getInt("length")));
    }

    public void setSwitches() {
        showPasswordSwitch.setChecked(getArguments().getBoolean("show"));
        deletingSwitch.setChecked(getArguments().getBoolean("deleting"));
        encryptingSwitch.setChecked(getArguments().getBoolean("encrypting"));
    }

    /* ************* */
    /* Click methods */
    /* ************* */

    public void clickCancel(View view) {
        /* Click Cancel Button */
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        dismiss();
    }

    public void clickDeleteAllPasswords(View view) {
        /* Click DeleteAllPasswords Button */
        ((MainActivity) getActivity()).openConfirmAllPasswordsDelete();
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        dismiss();
    }

    public void clickSave(View view) {
        /* Click Save Button */
        ((MainActivity) getActivity()).makeVibration();
        int length = passwordLengthEdit.getText().toString().isEmpty() ?
                16 : Integer.parseInt(passwordLengthEdit.getText().toString());
        ((MainActivity) getActivity()).saveSettings(
                length, showPasswordSwitch.isChecked(),
                deletingSwitch.isChecked(), encryptingSwitch.isChecked());
        view.clearFocus();
        dismiss();
    }
}
