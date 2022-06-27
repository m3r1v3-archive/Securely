package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.merive.securely.R;
import com.merive.securely.activities.LoginActivity;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.elements.TypingTextView;

public class ConfirmFragment extends Fragment {

    TypingTextView title;
    ImageView cancel, confirm;

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
        args.putString("name", null);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables(view);
        setTitle();

        cancel.setOnClickListener(v -> clickCancel());
        confirm.setOnClickListener(v -> clickConfirm());
    }

    private void initVariables(View view) {
        title = view.findViewById(R.id.confirm_title_text);
        cancel = view.findViewById(R.id.confirm_cancel_button);
        confirm = view.findViewById(R.id.confirm_confirm_button);
    }

    private void setTitle() {
        if (getArguments().getBoolean("changeKey", false))
            typingAnimation(title, getResources().getString(R.string.change_key));
        else if (getArguments().getString("name") == null)
            typingAnimation(title, getResources().getString(R.string.delete_all_passwords));
        else typingAnimation(title, getResources().getString(R.string.delete_password));
    }

    private void clickCancel() {
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    private void clickConfirm() {
        ((MainActivity) getActivity()).makeVibration();
        if (getArguments().getBoolean("changeKey")) changeKey();
        else {
            if (getArguments().getString("name") == null)
                ((MainActivity) getActivity()).deleteAllPasswords();
            else ((MainActivity) getActivity())
                    .deletePasswordByName(getArguments().getString("name"));
        }
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    private void changeKey() {
        ((MainActivity) getActivity()).updateEncrypt(false);
        startActivity(new Intent(getActivity(), LoginActivity.class)
                .putExtra("changeKey", true));
    }
}
