package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.merive.securely.R;
import com.merive.securely.elements.TypingTextView;

public class EmptyFragment extends Fragment {

    TypingTextView title, message, hint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initLayoutVariables();
        setTexts();
    }

    private void initLayoutVariables() {
        title = getView().findViewById(R.id.empty_title_text);
        message = getView().findViewById(R.id.empty_label_text);
        hint = getView().findViewById(R.id.empty_hint_text);
    }

    private void setTexts() {
        typingAnimation(title, getResources().getString(R.string.app_name));
        typingAnimation(message, getResources().getString(R.string.list_is_empty));
        typingAnimation(hint, getResources().getString(R.string.empty_hint));
    }
}
