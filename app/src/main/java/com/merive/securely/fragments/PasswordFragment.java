package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.merive.securely.R;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.activities.ScannerActivity;
import com.merive.securely.elements.TypingTextView;
import com.merive.securely.utils.PasswordGenerator;
import com.merive.securely.utils.VibrationManager;

public class PasswordFragment extends Fragment {

    private MainActivity mainActivity;
    private TypingTextView titleTypingText;
    private EditText nameEdit, loginEdit, passwordEdit, descriptionEdit;
    private ImageView saveButton, cancelButton, scanButton, deleteButton, generateButton, showButton;
    private boolean show = false;

    /**
     * Create a new instance of PasswordFragment, initialized to edit password data
     *
     * @param name Password name that will be deleted
     * @return PasswordFragment object
     */
    public static PasswordFragment newInstance(String name, String login, String password, String description) {
        PasswordFragment frag = new PasswordFragment();
        Bundle args = new Bundle();
        args.putString("name_value", name);
        args.putString("login_value", login);
        args.putString("password_value", password);
        args.putString("description_value", description);
        args.putBoolean("edit_mode", true);
        frag.setArguments(args);
        return frag;
    }

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
        return inflater.inflate(R.layout.fragment_password, parent, false);
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
        setTitle();
        setListeners();
        checkEdit();
        setShow();
    }

    /**
     * Initialize PasswordFragment components
     */
    private void initComponents() {
        titleTypingText = getView().findViewById(R.id.password_title_text);
        nameEdit = getView().findViewById(R.id.password_name_edit);
        loginEdit = getView().findViewById(R.id.password_login_edit);
        passwordEdit = getView().findViewById(R.id.password_password_edit);
        descriptionEdit = getView().findViewById(R.id.password_description_edit);
        deleteButton = getView().findViewById(R.id.password_delete_button);
        scanButton = getView().findViewById(R.id.password_scan_button);
        cancelButton = getView().findViewById(R.id.password_cancel_button);
        saveButton = getView().findViewById(R.id.password_save_button);
        generateButton = getView().findViewById(R.id.password_generate_button);
        showButton = getView().findViewById(R.id.password_show_button);
        mainActivity = ((MainActivity) getActivity());
    }

    /**
     * Set title string to titleTypingText: if 'edit_mode' argument is true, set title for edit mode, else for add mode
     */
    private void setTitle() {
        typingAnimation(titleTypingText, getArguments().getBoolean("edit_mode", false) ? getString(R.string.edit_password) : getString(R.string.add_password));
    }

    /**
     * Set onClick and onLongClick listeners for components
     */
    private void setListeners() {
        cancelButton.setOnClickListener(v -> clickCancel());
        deleteButton.setOnClickListener(v -> clickDeletePassword());
        saveButton.setOnClickListener(v -> clickSave());
        scanButton.setOnClickListener(v -> clickScan());
        generateButton.setOnClickListener(v -> clickGeneratePassword());
        showButton.setOnClickListener(v -> clickShowPassword());
    }

    /**
     * Check 'edit_mode' argument: if it's true, make deleteButton visible, make scanButton invisible and set password data to edits
     */
    private void checkEdit() {
        if (getArguments().getBoolean("edit_mode", false)) {
            deleteButton.setVisibility(View.VISIBLE);
            scanButton.setVisibility(View.INVISIBLE);
            setPasswordData();
        }
    }

    /**
     * Set password data to EditText components
     */
    private void setPasswordData() {
        nameEdit.setText(getArguments().getString("name_value"));
        loginEdit.setText(getArguments().getString("login_value"));
        passwordEdit.setText(getArguments().getString("password_value"));
        descriptionEdit.setText(getArguments().getString("description_value"));
    }

    /**
     * Make showing password in passwordEdit, if getShow() method return true
     */
    private void setShow() {
        if (mainActivity.preferencesManager.getShow()) passwordEdit.setTransformationMethod(null);
    }

    /**
     * Makes vibration effect and set BarFragment to bar_fragment component
     *
     * @see BarFragment
     */
    private void clickCancel() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment();
    }

    /**
     * Makes vibration effect and set ConfirmFragment to bar_fragment component for confirm password delete
     *
     * @see ConfirmFragment
     */
    private void clickDeletePassword() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment(ConfirmFragment.newInstance(getArguments().getString("name_value")));
    }

    /**
     * Makes vibration effect, check 'edit_mode' argument: if it is true, save as edited password, else save as new password.
     * After set BarFragment to bar_fragment component
     *
     * @see BarFragment
     */
    private void clickSave() {
        VibrationManager.makeVibration(getContext());
        if (getArguments().getBoolean("edit_mode", false)) saveEditedPassword();
        else saveNewPassword();
        mainActivity.setBarFragment();
    }

    /**
     * Checks EditText fields on empty and if they aren't empty, execute checkPasswordName() method
     */
    private void saveNewPassword() {
        if (checkOnEmpty()) mainActivity.checkPasswordName(getNewPasswordBundle());
        else mainActivity.makeToast(getString(R.string.you_have_empty_fields));
    }

    /**
     * @return Return bundle with password data
     */
    private Bundle getNewPasswordBundle() {
        Bundle data = new Bundle();
        data.putString("name_value", nameEdit.getText().toString());
        data.putString("login_value", loginEdit.getText().toString());
        data.putString("password_value", passwordEdit.getText().toString());
        data.putString("description_value", descriptionEdit.getText().toString());
        return data;
    }

    /**
     * Checks EditText fields on empty and if they aren't empty, execute checkEditPasswordName() method
     */
    private void saveEditedPassword() {
        if (checkOnEmpty()) mainActivity.checkEditPasswordName(getEditedPasswordBundle());
        else mainActivity.makeToast(getString(R.string.you_have_empty_fields));
    }

    private Bundle getEditedPasswordBundle() {
        Bundle data = new Bundle();
        data.putString("name_before_value", getArguments().getString("name_value"));
        data.putString("edited_name_value", nameEdit.getText().toString());
        data.putString("edited_login_value", loginEdit.getText().toString());
        data.putString("edited_password_value", passwordEdit.getText().toString());
        data.putString("edited_description_value", descriptionEdit.getText().toString());
        return data;
    }

    /**
     * @return Return true if nameEdit, loginEdit, passwordEdit EditTexts isn't empty
     */
    private boolean checkOnEmpty() {
        return !nameEdit.getText().toString().isEmpty() && !loginEdit.getText().toString().isEmpty() && !passwordEdit.getText().toString().isEmpty();
    }

    /**
     * Makes vibration effect and set generated password to passwordEdit component
     */
    private void clickGeneratePassword() {
        VibrationManager.makeVibration(getContext());
        passwordEdit.setText(new PasswordGenerator(mainActivity.preferencesManager.getLength()).generatePassword());
    }

    /**
     * Makes vibration effect and make password in passwordEdit visible on 5 seconds
     */
    private void clickShowPassword() {
        VibrationManager.makeVibration(getContext());
        if (show) {
            passwordEdit.setTransformationMethod(null);
            showButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_crossed));
        } else {
            passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
            showButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
        }
        show = !show;
    }

    /**
     * Make vibration effect, set BarFragment to bar_fragment component and open QR scanner for scanning Securely QR
     */
    private void clickScan() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment();
        openScanner();
    }

    /**
     * Open QR scanner for scanning Securely QR
     */
    private void openScanner() {
        new IntentIntegrator(getActivity())
                .setBarcodeImageEnabled(false)
                .setPrompt(getString(R.string.find_and_scan))
                .setCameraId(0)
                .setCaptureActivity(ScannerActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setBeepEnabled(false)
                .setOrientationLocked(true)
                .initiateScan();
    }
}
