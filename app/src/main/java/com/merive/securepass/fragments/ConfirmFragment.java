package com.merive.securepass.fragments;

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

import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.confirm_fragment, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.confirmTitle);
        typingAnimation(title, "Are you sure?");

        /* OnClick Cancel */
        cancel = view.findViewById(R.id.cancelConfirm);
        cancel.setOnClickListener(v -> {
            view.clearFocus();
            dismiss();
        });

        /* OnClick OkayConfirm */
        confirm = view.findViewById(R.id.okayConfirm);
        confirm.setOnClickListener(v -> {
            if (getArguments().getString("name").equals("all")) {
                ((MainActivity) getActivity()).deleteAllPasswords();
            } else {
                ((MainActivity) getActivity())
                        .deletePasswordByName(getArguments().getString("name"));
            }
            dismiss();
        });

    }

    /* Elements methods */
    public void typingAnimation(TypingTextView view, String text) {
        /* Typing animation for TextViews */
        view.setText("");
        view.setCharacterDelay(125);
        view.animateText(text);
    }
}
