package com.merive.securepass.fragments;

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
import androidx.fragment.app.DialogFragment;

import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;
import com.merive.securepass.utils.PasswordGenerator;

public class PasswordFragment extends DialogFragment {

    TypingTextView title;
    EditText nameEdit, loginEdit, passwordEdit, descriptionEdit;
    ImageView save, cancel, delete, generate;

    /* For AddPassword */
    public static PasswordFragment newInstance(int length) {
        PasswordFragment frag = new PasswordFragment();
        Bundle args = new Bundle();
        args.putInt("length", length);

        frag.setArguments(args);
        return frag;
    }

    /* For EditPassword */
    public static PasswordFragment newInstance
    (String name, String login, String password, String description, int length) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.password_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.viewTitle);

        nameEdit = view.findViewById(R.id.nameEdit);
        loginEdit = view.findViewById(R.id.loginEdit);
        passwordEdit = view.findViewById(R.id.passwordEdit);
        descriptionEdit = view.findViewById(R.id.descriptionEdit);

        delete = view.findViewById(R.id.deletePassword);

        boolean edit = getArguments().getBoolean("edit", false);
        setTitle(edit);

        if (edit) {
            delete.setVisibility(View.VISIBLE);
            setEditsData();
        }

        /* OnClick Cancel */
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            view.clearFocus();
            dismiss();
        });

        /* OnClick Generate */
        generate = view.findViewById(R.id.generate);
        generate.setOnClickListener(v -> generatePassword());

        /* OnClick Save */
        save = view.findViewById(R.id.save);
        save.setOnClickListener(v -> {
            view.clearFocus();
            if (edit) {
                if (checkEditsOnEmpty())
                    ((MainActivity) getActivity()).editPassword(putEditedDataInBundle());
                else ((MainActivity) getActivity()).makeToast("You have empty edits.");
            } else {
                if (checkEditsOnEmpty()) {
                    if (checkEditsOnEmpty())
                        ((MainActivity) getActivity()).addNewPassword(putNewDataInBundle());
                }
            }
            dismiss();
        });

        delete.setOnClickListener(v -> {
            view.clearFocus();
            ((MainActivity) getActivity()).openConfirmPasswordDelete(getArguments().getString("name"));
            dismiss();
        });
    }

    public void setTitle(boolean edit) {
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

    public void generatePassword() {
        /* Generate password and set to passwordEdit */
        passwordEdit.setText(new PasswordGenerator(getArguments().getInt("length", 16)).generatePassword());
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

    public Bundle putNewDataInBundle() {
        Bundle data = new Bundle();

        data.putString("name", nameEdit.getText().toString());
        data.putString("login", loginEdit.getText().toString());
        data.putString("password", passwordEdit.getText().toString());
        data.putString("description", descriptionEdit.getText().toString());

        return data;
    }

    public boolean checkEditsOnEmpty() {
        return !nameEdit.getText().toString().isEmpty() &&
                !loginEdit.getText().toString().isEmpty() &&
                !passwordEdit.getText().toString().isEmpty();
    }


    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        /* Typing animation for TextViews */
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }
}
