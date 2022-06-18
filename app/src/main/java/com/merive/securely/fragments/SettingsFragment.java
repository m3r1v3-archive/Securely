package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.merive.securely.BuildConfig;
import com.merive.securely.activities.LoginActivity;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;
import com.merive.securely.elements.TypingTextView;


public class SettingsFragment extends DialogFragment {

    TypingTextView title, info;
    EditText passwordLengthEdit;
    SwitchCompat showPasswordSwitch, deletingSwitch, encryptingSwitch;
    ImageView cancel, deleteAll, save;

    /**
     * SettingsFragment Constructor.
     * Using for creating DialogFragment in MainActivity.
     *
     * @see DialogFragment
     * @see MainActivity
     */
    public SettingsFragment() {
    }

    /**
     * This method is setting SettingsFragment Arguments.
     *
     * @param length     Password Generator Length.
     * @param show       Always Show Password in PasswordFragment.
     * @param delete     Delete all passwords after 15 errors in LoginActivity.
     * @param encrypting Encrypt Login and Password Values in Database.
     * @return SettingsFragment with necessary arguments.
     * @see PasswordFragment
     * @see LoginActivity
     * @see com.merive.securely.utils.Crypt
     * @see com.merive.securely.database.PasswordDB
     */
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

    /**
     * This method is creating SettingsFragment.
     *
     * @param inflater           Needs for getting Fragment View.
     * @param parent             Argument of inflater.inflate().
     * @param savedInstanceState Saving Fragment Values.
     * @return Fragment View.
     * @see View
     * @see Bundle
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_settings, parent);
    }

    /**
     * This method is executing after Fragment View was created.
     * In this method will be setting DialogAnimation, layout variables will be initializing,
     * will set title and info values, will set length edit and switches values.
     * Also will be setting onClickListeners for Buttons.
     *
     * @param view               Fragment View Value.
     * @param savedInstanceState Saving Fragment Values.
     * @see View
     * @see Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        initVariables();

        setTitle();
        setInfo();

        setLengthEdit();
        setSwitches();

        cancel.setOnClickListener(this::clickCancel);
        deleteAll.setOnClickListener(this::clickDeleteAllPasswords);
        save.setOnClickListener(this::clickSave);
    }

    /**
     * This method is initializing layout variables.
     */
    private void initVariables() {
        title = getView().findViewById(R.id.settings_title);
        info = getView().findViewById(R.id.info_text);

        passwordLengthEdit = getView().findViewById(R.id.password_length_edit);

        showPasswordSwitch = getView().findViewById(R.id.show_password_switch);
        deletingSwitch = getView().findViewById(R.id.delete_password_switch);
        encryptingSwitch = getView().findViewById(R.id.encrypt_data_switch);

        cancel = getView().findViewById(R.id.settings_cancel_button);
        deleteAll = getView().findViewById(R.id.delete_passwords_button);
        save = getView().findViewById(R.id.save_settings_button);
    }

    /**
     * This method is setting title.
     */
    private void setTitle() {
        typingAnimation(title, getResources().getString(R.string.settings));
    }

    /**
     * This method is setting info.
     */
    private void setInfo() {
        typingAnimation(info, "Securely / " + BuildConfig.VERSION_NAME);
    }

    /**
     * This method is setting Password Generator Length value to passwordLengthEdit.
     */
    private void setLengthEdit() {
        passwordLengthEdit.setText(String.valueOf(getArguments().getInt("length")));
    }

    /**
     * This method is setting Switches states.
     */
    private void setSwitches() {
        showPasswordSwitch.setChecked(getArguments().getBoolean("show"));
        deletingSwitch.setChecked(getArguments().getBoolean("delete"));
        encryptingSwitch.setChecked(getArguments().getBoolean("encrypting"));
    }

    /**
     * This method is executing after clicking on Cancel Button.
     *
     * @param view Needs for clear focus from Fragment.
     * @see MainActivity
     */
    private void clickCancel(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        dismiss();
    }

    /**
     * This method is executing after clicking on Delete All Passwords Button.
     * The method is making vibration and open ConfirmFragment for confirming All Password Deleting.
     *
     * @param view Needs for clear focus from Fragment.
     * @see MainActivity
     */
    private void clickDeleteAllPasswords(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).openConfirmAllPasswordsDelete();
        dismiss();
    }

    /**
     * This method is executing after clicking on Save Settings Button.
     * If passwordLength is empty will be saving default password generator value (16).
     * Will be saving Password Generator Length, Always Show Password,
     * Delete after 15 errors and Encrypt Login and Password Values in MainActivity SharedPreference.
     *
     * @param view Needs for clear focus from Fragment.
     * @see MainActivity
     * @see android.content.SharedPreferences
     */
    private void clickSave(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).saveSettings(
                passwordLengthEdit.getText().toString().isEmpty() ?
                        16 : Integer.parseInt(passwordLengthEdit.getText().toString()),
                showPasswordSwitch.isChecked(),
                deletingSwitch.isChecked(),
                encryptingSwitch.isChecked());
        dismiss();
    }
}
