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

    private TypingTextView titleTypingText, labelTypingText, hintTypingText;

    /**
     * Called to have the fragment instantiate its user interface view
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param parent             If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, parent, false);
    }

    /**
     * Called immediately after onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) has returned, but before any saved state has been restored in to the view
     *
     * @param view               The View returned by onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initComponents();
        setTexts();
    }

    /**
     * Initialize EmptyFragment components
     */
    private void initComponents() {
        titleTypingText = getView().findViewById(R.id.empty_title_text);
        labelTypingText = getView().findViewById(R.id.empty_label_text);
        hintTypingText = getView().findViewById(R.id.empty_hint_text);
    }

    /**
     * Set texts to TypingText components
     */
    private void setTexts() {
        typingAnimation(titleTypingText, getResources().getString(R.string.app_name));
        typingAnimation(labelTypingText, getResources().getString(R.string.list_is_empty));
        typingAnimation(hintTypingText, getResources().getString(R.string.empty_hint));
    }
}
