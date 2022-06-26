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

    public static PasswordFragment newInstance(int length, boolean show) {
        PasswordFragment frag = new PasswordFragment();
        Bundle args = new Bundle();
        args.putInt("length", length);
        args.putBoolean("show", show);

        frag.setArguments(args);
        return frag;
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password, parent, false);
    }

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

    private void setEdit() {
        edit = getArguments().getBoolean("edit", false);
    }

    private void setTitle() {
        String titleText = edit ? "Edit" : "Add";
        typingAnimation(title, titleText + " password");
    }

    private void setEditMode() {
        if (edit) {
            delete.setVisibility(View.VISIBLE);
            scan.setVisibility(View.INVISIBLE);
            setEditsData();
        }
    }

    private void setEditsData() {
        nameEdit.setText(getArguments().getString("name"));
        loginEdit.setText(getArguments().getString("login"));
        passwordEdit.setText(getArguments().getString("password"));
        descriptionEdit.setText(getArguments().getString("description"));
    }

    private void setShow() {
        show = getArguments().getBoolean("show");
        if (show) passwordEdit.setTransformationMethod(null);
    }

    private void clickCancel(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    private void clickDeletePassword(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
        ((MainActivity) getActivity()).openConfirmPasswordDelete(getArguments().getString("name"));
    }

    private void clickSave(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        if (edit) saveEditPassword();
        else saveNewPassword();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    private void saveEditPassword() {
        if (checkEditsOnEmpty())
            ((MainActivity) getActivity()).editPassword(putEditedDataInBundle());
        else ((MainActivity) getActivity()).makeToast("You have empty fields");
    }

    private boolean checkEditsOnEmpty() {
        return !nameEdit.getText().toString().isEmpty() &&
                !loginEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty();
    }

    private Bundle putEditedDataInBundle() {
        Bundle data = new Bundle();

        data.putString("name_before", getArguments().getString("name"));
        data.putString("edited_name", nameEdit.getText().toString());
        data.putString("edited_login", loginEdit.getText().toString());
        data.putString("edited_password", passwordEdit.getText().toString());
        data.putString("edited_description", descriptionEdit.getText().toString());

        return data;
    }

    private void saveNewPassword() {
        if (checkEditsOnEmpty())
            ((MainActivity) getActivity()).addNewPassword(putNewDataInBundle());
        else ((MainActivity) getActivity()).makeToast("You have empty fields");
    }

    private Bundle putNewDataInBundle() {
        Bundle data = new Bundle();

        data.putString("name", nameEdit.getText().toString());
        data.putString("login", loginEdit.getText().toString());
        data.putString("password", passwordEdit.getText().toString());
        data.putString("description", descriptionEdit.getText().toString());

        return data;
    }

    private void clickGeneratePassword() {
        passwordEdit.setText(new PasswordGenerator(getArguments().getInt("length", 16)).generatePassword());
        ((MainActivity) getActivity()).makeVibration();
    }

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

    private void clickScan(View view) {
        ((MainActivity) getActivity()).makeVibration();
        openScanner();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

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
