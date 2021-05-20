package com.merive.securepass;

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

import com.merive.securepass.elements.TypingTextView;

import java.util.Calendar;


public class SettingsFragment extends DialogFragment {

    TypingTextView title, info;
    EditText passwordLengthEdit, copyingMessageEdit;
    SwitchCompat deletingSwitch;
    ImageView cancel, deleteAll, save;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance(int length, String message, boolean deleting) {
        SettingsFragment frag = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt("length", length);
        args.putString("message", message);
        args.putBoolean("deleting", deleting);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.settings_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.confirmTitle);
        typingAnimation(title, getResources().getString(R.string.settings));

        info = view.findViewById(R.id.info);
        typingAnimation(info, "SecurePass " + BuildConfig.VERSION_NAME + ", " + Calendar.getInstance().get(Calendar.YEAR));

        passwordLengthEdit = view.findViewById(R.id.passwordLengthEdit);
        passwordLengthEdit.setText(String.valueOf(getArguments().getInt("length")));

        copyingMessageEdit = view.findViewById(R.id.copyingMessageEdit);
        copyingMessageEdit.setText(getArguments().getString("message"));

        deletingSwitch = view.findViewById(R.id.deletingSwitch);
        deletingSwitch.setChecked(getArguments().getBoolean("deleting"));

        /* OnClick Cancel */
        cancel = view.findViewById(R.id.cancelSettings);
        cancel.setOnClickListener(v -> {
            view.clearFocus();
            dismiss();
        });

        /* OnClick deleteAll */
        deleteAll = view.findViewById(R.id.deletePasswords);
        deleteAll.setOnClickListener(v -> {
            ((MainActivity) getActivity()).deleteAllFragment();
            view.clearFocus();
            dismiss();
        });

        /* OnClick save */
        save = view.findViewById(R.id.saveSettings);
        save.setOnClickListener(v -> {
            int length = passwordLengthEdit.getText().toString().isEmpty() ?
                    16 : Integer.parseInt(passwordLengthEdit.getText().toString());
            String copyingMessage = copyingMessageEdit.getText().toString().isEmpty() ?
                    "was copied." : copyingMessageEdit.getText().toString();
            ((MainActivity) getActivity()).saveSettings(
                    length,
                    copyingMessage,
                    deletingSwitch.isChecked());

            view.clearFocus();
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
