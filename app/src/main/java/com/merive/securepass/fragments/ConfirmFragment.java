package com.merive.securepass.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.merive.securepass.CheckKeyActivity;
import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

public class ConfirmFragment extends DialogFragment {

    TypingTextView title;
    ImageView cancel, confirm;

    public ConfirmFragment() {
        /* Empty constructor (Needs) */
    }

    public static ConfirmFragment newInstance(String name) {
        /* newInstance method for confirm deleting password by name */
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    public static ConfirmFragment newInstance() {
        /* newInstance method for confirm deleting all passwords */
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString("name", "all");
        frag.setArguments(args);
        return frag;
    }

    public static ConfirmFragment newInstance(boolean changeKey) {
        /* newInstance method for confirm changing key */
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putBoolean("changeKey", changeKey);
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
        return inflater.inflate(R.layout.confirm_fragment, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables(view);
        checkKeyChange();

        cancel.setOnClickListener(this::clickCancel);
        confirm.setOnClickListener(this::clickConfirm);

    }

    /* ************ */
    /* Init methods */
    /* ************ */

    public void initVariables(View view) {
        /* Init main variables */
        title = view.findViewById(R.id.confirmTitle);
        cancel = view.findViewById(R.id.cancelConfirm);
        confirm = view.findViewById(R.id.okayConfirm);
    }

    /* ************* */
    /* Click methods */
    /* ************* */

    public void clickCancel(View view) {
        /* Click Cancel Button */
        view.clearFocus();
        dismiss();
    }

    public void clickConfirm(View view) {
        /* Click Confirm Button */
        if (getArguments().getBoolean("changeKey")) changeKey();
        else {
            if (getArguments().getString("name").equals("all"))
                ((MainActivity) getActivity()).deleteAllPasswords();
            else ((MainActivity) getActivity())
                    .deletePasswordByName(getArguments().getString("name"));
        } dismiss();
    }

    /* *************** */
    /* Another methods */
    /* *************** */

    public void checkKeyChange() {
        if (getArguments().getBoolean("changeKey", false))
            typingAnimation(title, "Do you want change key?");
        else typingAnimation(title, "Are you sure?");
    }

    public void changeKey() {
        /* Start change key */
        ((MainActivity) getActivity()).updateEncrypting(false);
        startActivity(new Intent(getActivity(), CheckKeyActivity.class)
                .putExtra("changeKey", true));
    }
}
