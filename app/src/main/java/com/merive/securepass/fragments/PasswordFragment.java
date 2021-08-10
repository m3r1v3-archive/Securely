package com.merive.securepass.fragments;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.fragment.app.DialogFragment;

import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;
import com.merive.securepass.utils.PasswordGenerator;

public class PasswordFragment extends DialogFragment {

    TypingTextView title;
    EditText nameEdit, loginEdit, passwordEdit, descriptionEdit;
    ImageView save, cancel, delete, generate;
    boolean edit;


    public PasswordFragment() {
        /* Empty constructor (Needs) */
    }

    public static PasswordFragment newInstance(int length) {
        /* newInstance method for add password */
        PasswordFragment frag = new PasswordFragment();
        Bundle args = new Bundle();
        args.putInt("length", length);

        frag.setArguments(args);
        return frag;
    }

    public static PasswordFragment newInstance
            (String name, String login, String password, String description, int length) {
        /* newInstance method for edit password */
        PasswordFragment frag = new PasswordFragment();
        Bundle args = new Bundle();

        args.putString("name", name);
        args.putString("login", login);
        args.putString("password", password);
        args.putString("description", description);
        args.putInt("length", length);
        args.putBoolean("edit", true);

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
        return inflater.inflate(R.layout.password_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables(view);
        setEdit();
        setTitle(edit);
        setDeleteVisibility();

        cancel.setOnClickListener(this::clickCancel);
        generate.setOnClickListener(v -> clickGeneratePassword());
        save.setOnClickListener(this::clickSave);
        delete.setOnClickListener(this::clickDeletePassword);
        passwordEdit.setOnClickListener(this::clickPasswordEdit);
    }

    /* ************ */
    /* Init methods */
    /* ************ */

    public void initVariables(View view) {
        /* Init main variables */
        title = view.findViewById(R.id.viewTitle);
        nameEdit = view.findViewById(R.id.nameEdit);
        loginEdit = view.findViewById(R.id.loginEdit);
        passwordEdit = view.findViewById(R.id.passwordEdit);
        descriptionEdit = view.findViewById(R.id.descriptionEdit);
        delete = view.findViewById(R.id.deletePassword);
        cancel = view.findViewById(R.id.cancel);
        generate = view.findViewById(R.id.generate);
        save = view.findViewById(R.id.save);
    }

    /* *********** */
    /* Set methods */
    /* *********** */

    public void setTitle(boolean edit) {
        /* Set Title Value */
        String titleText = edit ? "Edit" : "Add";
        typingAnimation(title, titleText + " password");
    }

    public void setEditsData() {
        /* Set data to EditTexts */
        nameEdit.setText(getArguments().getString("name"));
        loginEdit.setText(getArguments().getString("login"));
        passwordEdit.setText(getArguments().getString("password"));
        descriptionEdit.setText(getArguments().getString("description"));
    }

    public void setDeleteVisibility() {
        /* Set Delete Visibility */
        if (edit) {
            delete.setVisibility(View.VISIBLE);
            setEditsData();
        }
    }

    public void setEdit() {
        /* Set edit value from arguments */
        edit = getArguments().getBoolean("edit", false);
    }

    /* ************* */
    /* Click methods */
    /* ************* */

    public void clickCancel(View view) {
        /* Click Cancel Button */
        view.clearFocus();
        dismiss();
    }

    public void clickSave(View view) {
        /* Click Save Button */
        view.clearFocus();
        if (edit) saveEditPassword();
        else saveNewPassword();
        dismiss();
    }

    public void clickDeletePassword(View view) {
        /* Click DeletePassword Button */
        view.clearFocus();
        ((MainActivity) getActivity()).openConfirmPasswordDelete(getArguments().getString("name"));
        dismiss();
    }

    public void clickGeneratePassword() {
        /* Generate password and set to passwordEdit */
        passwordEdit.setText(new PasswordGenerator(getArguments().getInt("length", 16)).generatePassword());
    }

    public void clickPasswordEdit(View view) {
        /* View password 5 seconds and after hide */
        passwordEdit.setTransformationMethod(null);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
        }, 5000);
    }

    /* ************ */
    /* Save methods */
    /* ************ */

    public void saveNewPassword() {
        /* Save New Password Operation */
        if (checkEditsOnEmpty()) {
            if (checkEditsOnEmpty())
                ((MainActivity) getActivity()).addNewPassword(putNewDataInBundle());
        }
    }

    public void saveEditPassword() {
        /* Save Edited Password Operation */
        if (checkEditsOnEmpty())
            ((MainActivity) getActivity()).editPassword(putEditedDataInBundle());
        else ((MainActivity) getActivity()).makeToast("You have empty edits.");
    }

    /* *************** */
    /* Another methods */
    /* *************** */

    public Bundle putNewDataInBundle() {
        /* Put new data from edits to bundle */
        Bundle data = new Bundle();

        data.putString("name", nameEdit.getText().toString());
        data.putString("login", loginEdit.getText().toString());
        data.putString("password", passwordEdit.getText().toString());
        data.putString("description", descriptionEdit.getText().toString());

        return data;
    }

    public Bundle putEditedDataInBundle() {
        /* Put edited data from edits to bundle */
        Bundle data = new Bundle();

        data.putString("name_before", getArguments().getString("name"));
        data.putString("edited_name", nameEdit.getText().toString());
        data.putString("edited_login", loginEdit.getText().toString());
        data.putString("edited_password", passwordEdit.getText().toString());
        data.putString("edited_description", descriptionEdit.getText().toString());

        return data;
    }

    public boolean checkEditsOnEmpty() {
        /* Check Main Edits on Empty */
        return !nameEdit.getText().toString().isEmpty() &&
                !loginEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty();
    }
}
