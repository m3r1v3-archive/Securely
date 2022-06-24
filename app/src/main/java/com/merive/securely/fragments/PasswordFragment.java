package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.os.Bundle;
import android.os.Handler;
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

public class PasswordFragment extends Fragment {

    TypingTextView title;
    EditText nameEdit, loginEdit, passwordEdit, descriptionEdit;
    ImageView save, cancel, scan, delete, generate;
    boolean edit, show;

    /**
     * This method is setting PasswordFragment Arguments for adding new password.
     *
     * @param length Password Generator Length.
     * @param show   Always show password.
     * @return PasswordFragment with necessary arguments.
     */
    public static PasswordFragment newInstance(int length, boolean show) {
        PasswordFragment frag = new PasswordFragment();
        Bundle args = new Bundle();
        args.putInt("length", length);
        args.putBoolean("show", show);

        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is setting PasswordFragment Arguments for editing password.
     *
     * @param name        Name of Password in Database.
     * @param login       Login Value in Database.
     * @param password    Password Value in Database.
     * @param description Description Value in Database.
     * @param length      Password Generator Length.
     * @param show        Always show password.
     * @return PasswordFragment with necessary arguments.
     */
    public static PasswordFragment newInstance
    (String name, String login, String password, String description, int length, boolean show) {
        PasswordFragment frag = new PasswordFragment();
        Bundle args = new Bundle();

        args.putString("name", name);
        args.putString("login", login);
        args.putString("password", password);
        args.putString("description", description);
        args.putInt("length", length);
        args.putBoolean("edit", true);
        args.putBoolean("show", show);

        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is creating PasswordFragment.
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
        return inflater.inflate(R.layout.fragment_password, parent, false);
    }

    /**
     * This method is executing after Fragment View was created.
     * In this method will be setting DialogAnimation, layout variables will be initializing,
     * will be setting values to edits if it necessary,
     * will be setting title, will be setting delete button visibility is PasswordFragment opened in edit mode
     * and will be setting password show in password field if show argument is true.
     * Also will be setting click listeners for buttons.
     *
     * @param view               Fragment View Value.
     * @param savedInstanceState Saving Fragment Values.
     * @see View
     * @see Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        setEdit();
        setTitle();
        setEditMode();
        setShow();

        cancel.setOnClickListener(this::clickCancel);
        delete.setOnClickListener(this::clickDeletePassword);
        save.setOnClickListener(this::clickSave);
        scan.setOnClickListener(this::clickScan);

        generate.setOnClickListener(v -> clickGeneratePassword());
        passwordEdit.setOnLongClickListener(v -> {
            longClickPasswordEdit();
            return false;
        });
    }

    /**
     * This method is initializing layout variables.
     */
    private void initVariables() {
        title = getView().findViewById(R.id.password_title_text);

        nameEdit = getView().findViewById(R.id.password_name_edit);
        loginEdit = getView().findViewById(R.id.password_login_edit);
        passwordEdit = getView().findViewById(R.id.password_password_edit);
        descriptionEdit = getView().findViewById(R.id.password_description_edit);

        delete = getView().findViewById(R.id.password_delete_button);
        scan = getView().findViewById(R.id.password_scan_button);
        cancel = getView().findViewById(R.id.password_cancel_button);
        save = getView().findViewById(R.id.password_save_button);

        generate = getView().findViewById(R.id.password_generate_button);
    }

    /**
     * This method is setting edit value from arguments to class variable.
     */
    private void setEdit() {
        edit = getArguments().getBoolean("edit", false);
    }

    /**
     * This method is setting edit or add title.
     */
    private void setTitle() {
        String titleText = edit ? "Edit" : "Add";
        typingAnimation(title, titleText + " password");
    }

    /**
     * This method is setting edit mode.
     * If PasswordFragment opened in Edit Mode,
     * Delete Button will be visible and will be setting values to edits.
     */
    private void setEditMode() {
        if (edit) {
            delete.setVisibility(View.VISIBLE);
            scan.setVisibility(View.INVISIBLE);
            setEditsData();
        }
    }

    /**
     * This method is setting values from Arguments to Edit Fields.
     */
    private void setEditsData() {
        nameEdit.setText(getArguments().getString("name"));
        loginEdit.setText(getArguments().getString("login"));
        passwordEdit.setText(getArguments().getString("password"));
        descriptionEdit.setText(getArguments().getString("description"));
    }

    /**
     * This method is setting Show Password for Password Field.
     *
     * @see SettingsFragment
     */
    private void setShow() {
        show = getArguments().getBoolean("show");
        if (show) passwordEdit.setTransformationMethod(null);
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
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    /**
     * This method is executing after clicking on Delete Password Button.
     * The method is making vibration and start confirming password deleting.
     *
     * @param view Needs for clear focus from Fragment.
     * @see ConfirmFragment
     * @see MainActivity
     */
    private void clickDeletePassword(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
        ((MainActivity) getActivity()).openConfirmPasswordDelete(getArguments().getString("name"));
    }

    /**
     * This method is saving added/edited password values to database.
     * The method is making vibration and saving added/edited values to database.
     *
     * @param view Needs for clear focus from Fragment.
     * @see MainActivity
     * @see com.merive.securely.database.PasswordDB
     */
    private void clickSave(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        if (edit) saveEditPassword();
        else saveNewPassword();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    /**
     * This method is saving edited values to database and make Toast message.
     *
     * @see com.merive.securely.database.PasswordDB
     */
    private void saveEditPassword() {
        if (checkEditsOnEmpty())
            ((MainActivity) getActivity()).editPassword(putEditedDataInBundle());
        else ((MainActivity) getActivity()).makeToast("You have empty fields");
    }

    /**
     * This method is checking what fields aren't empty.
     *
     * @return True if no one field is empty (Description is optional (Not checking)).
     */
    private boolean checkEditsOnEmpty() {
        return !nameEdit.getText().toString().isEmpty() &&
                !loginEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty();
    }

    /**
     * This method is putting edited values to Bundle for MainActivity.
     *
     * @return Bundle with edited values.
     * @see MainActivity
     */
    private Bundle putEditedDataInBundle() {
        Bundle data = new Bundle();

        data.putString("name_before", getArguments().getString("name"));
        data.putString("edited_name", nameEdit.getText().toString());
        data.putString("edited_login", loginEdit.getText().toString());
        data.putString("edited_password", passwordEdit.getText().toString());
        data.putString("edited_description", descriptionEdit.getText().toString());

        return data;
    }

    /**
     * This method is saving added values to database and make Toast message.
     *
     * @see com.merive.securely.database.PasswordDB
     */
    private void saveNewPassword() {
        if (checkEditsOnEmpty())
            ((MainActivity) getActivity()).addNewPassword(putNewDataInBundle());
        else ((MainActivity) getActivity()).makeToast("You have empty fields");
    }

    /**
     * This method is putting added values to Bundle for MainActivity.
     *
     * @return Bundle with added values.
     * @see MainActivity
     */
    private Bundle putNewDataInBundle() {
        Bundle data = new Bundle();

        data.putString("name", nameEdit.getText().toString());
        data.putString("login", loginEdit.getText().toString());
        data.putString("password", passwordEdit.getText().toString());
        data.putString("description", descriptionEdit.getText().toString());

        return data;
    }

    /**
     * This method is executing after clicking Generate Password Button.
     * The method is generating and putting password to Password Edit and make vibration.
     */
    private void clickGeneratePassword() {
        passwordEdit.setText(new PasswordGenerator(getArguments().getInt("length", 16)).generatePassword());
        ((MainActivity) getActivity()).makeVibration();
    }

    /**
     * This method is showing entered password in Password Edit after long clicking on Password Edit field.
     */
    private void longClickPasswordEdit() {
        ((MainActivity) getActivity()).makeVibration();
        if (!show) {
            show = true;
            passwordEdit.setTransformationMethod(null);
            new Handler().postDelayed(() -> {
                passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
            }, 5000);
            show = false;
        }
    }

    /**
     * This method executes when clicking Scan Button.
     */
    private void clickScan(View view) {
        ((MainActivity) getActivity()).makeVibration();
        openScanner();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    /**
     * This method opens QR scanner.
     */
    private void openScanner() {
        new IntentIntegrator(getActivity())
                .setBarcodeImageEnabled(false)
                .setPrompt("Find and Scan SecurelyQR")
                .setCameraId(0)
                .setCaptureActivity(ScannerActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setBeepEnabled(false)
                .setOrientationLocked(true)
                .initiateScan();
    }
}
