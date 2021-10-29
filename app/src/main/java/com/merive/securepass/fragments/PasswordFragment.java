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
    boolean edit, show;


    public PasswordFragment() {
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.password_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        initVariables(view);
        setEdit();
        setTitle(edit);
        setDeleteVisibility();
        setShow();

        cancel.setOnClickListener(this::clickCancel);
        generate.setOnClickListener(v -> clickGeneratePassword());
        save.setOnClickListener(this::clickSave);
        delete.setOnClickListener(this::clickDeletePassword);
        passwordEdit.setOnLongClickListener(v -> {
            clickPasswordEdit();
            return false;
        });
    }

    public void initVariables(View view) {
        title = view.findViewById(R.id.password_title);
        nameEdit = view.findViewById(R.id.name_edit);
        loginEdit = view.findViewById(R.id.login_edit);
        passwordEdit = view.findViewById(R.id.password_edit);
        descriptionEdit = view.findViewById(R.id.description_edit);
        delete = view.findViewById(R.id.password_delete_button);
        cancel = view.findViewById(R.id.password_cancel_button);
        generate = view.findViewById(R.id.generate_password_button);
        save = view.findViewById(R.id.password_save_button);
    }

    public void setTitle(boolean edit) {
        String titleText = edit ? "Edit" : "Add";
        typingAnimation(title, titleText + " password");
    }

    public void setDeleteVisibility() {
        if (edit) {
            delete.setVisibility(View.VISIBLE);
            setEditsData();
        }
    }

    public void setEditsData() {
        nameEdit.setText(getArguments().getString("name"));
        loginEdit.setText(getArguments().getString("login"));
        passwordEdit.setText(getArguments().getString("password"));
        descriptionEdit.setText(getArguments().getString("description"));
    }

    public void setEdit() {
        edit = getArguments().getBoolean("edit", false);
    }

    public void setShow() {
        show = getArguments().getBoolean("show");
        if (show) passwordEdit.setTransformationMethod(null);
    }

    public void clickCancel(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        dismiss();
    }

    public void clickSave(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        if (edit) saveEditPassword();
        else saveNewPassword();
        dismiss();
    }

    public void clickDeletePassword(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).openConfirmPasswordDelete(getArguments().getString("name"));
        dismiss();
    }

    public void clickGeneratePassword() {
        ((MainActivity) getActivity()).makeVibration();
        passwordEdit.setText(new PasswordGenerator(getArguments().getInt("length", 16)).generatePassword());
    }

    public void clickPasswordEdit() {
        ((MainActivity) getActivity()).makeVibration();
        if (!show) {
            show = true;
            passwordEdit.setTransformationMethod(null);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
            }, 5000);
            show = false;
        }
    }

    public void saveNewPassword() {
        if (checkEditsOnEmpty()) {
            if (checkEditsOnEmpty())
                ((MainActivity) getActivity()).addNewPassword(putNewDataInBundle());
        } else ((MainActivity) getActivity()).makeToast("You have empty fields");
    }

    public void saveEditPassword() {
        if (checkEditsOnEmpty())
            ((MainActivity) getActivity()).editPassword(putEditedDataInBundle());
        else ((MainActivity) getActivity()).makeToast("You have empty fields");
    }

    public Bundle putNewDataInBundle() {
        Bundle data = new Bundle();

        data.putString("name", nameEdit.getText().toString());
        data.putString("login", loginEdit.getText().toString());
        data.putString("password", passwordEdit.getText().toString());
        data.putString("description", descriptionEdit.getText().toString());

        return data;
    }

    public Bundle putEditedDataInBundle() {
        Bundle data = new Bundle();

        data.putString("name_before", getArguments().getString("name"));
        data.putString("edited_name", nameEdit.getText().toString());
        data.putString("edited_login", loginEdit.getText().toString());
        data.putString("edited_password", passwordEdit.getText().toString());
        data.putString("edited_description", descriptionEdit.getText().toString());

        return data;
    }

    public boolean checkEditsOnEmpty() {
        return !nameEdit.getText().toString().isEmpty() &&
                !loginEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty();
    }
}
