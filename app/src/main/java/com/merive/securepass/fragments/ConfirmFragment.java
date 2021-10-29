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
    }

    public static ConfirmFragment newInstance(String name) {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    public static ConfirmFragment newInstance() {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString("name", "all");
        frag.setArguments(args);
        return frag;
    }

    public static ConfirmFragment newInstance(boolean changeKey) {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putBoolean("changeKey", changeKey);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.confirm_fragment, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        initVariables(view);
        checkKeyChange();

        cancel.setOnClickListener(this::clickCancel);
        confirm.setOnClickListener(this::clickConfirm);

    }

    public void initVariables(View view) {
        title = view.findViewById(R.id.confirm_title);
        cancel = view.findViewById(R.id.confirm_cancel_button);
        confirm = view.findViewById(R.id.confirm_okay_button);
    }

    public void clickCancel(View view) {
        view.clearFocus();
        ((MainActivity) getActivity()).makeVibration();
        dismiss();
    }

    public void clickConfirm(View view) {
        ((MainActivity) getActivity()).makeVibration();
        if (getArguments().getBoolean("changeKey")) changeKey();
        else {
            if (getArguments().getString("name").equals("all"))
                ((MainActivity) getActivity()).deleteAllPasswords();
            else ((MainActivity) getActivity())
                    .deletePasswordByName(getArguments().getString("name"));
        } dismiss();
    }

    public void checkKeyChange() {
        if (getArguments().getBoolean("changeKey", false))
            typingAnimation(title, getResources().getString(R.string.change_key));
        else if (getArguments().getString("name").equals("all"))
            typingAnimation(title, getResources().getString(R.string.delete_all_passwords));
        else typingAnimation(title, getResources().getString(R.string.delete_a_password));
    }

    public void changeKey() {
        ((MainActivity) getActivity()).updateEncrypting(false);
        startActivity(new Intent(getActivity(), CheckKeyActivity.class)
                .putExtra("changeKey", true));
    }
}
