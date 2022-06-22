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

    /**
     * This method is creating EmptyFragment.
     *
     * @param inflater           Needs for getting Fragment View.
     * @param parent             Argument of inflater.inflate().
     * @param savedInstanceState Saving Fragment Values.
     * @return Fragment View.
     * @see View
     * @see Bundle
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, parent, false);
    }

    /**
     * This method is executing after Fragment View was created.
     * In this method will be initializing layout variables and will be setting click listeners for them.
     *
     * @param view               Fragment View Value.
     * @param savedInstanceState Save Fragment Values.
     * @see View
     * @see Bundle
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initLayoutVariables();
        setTexts();
    }

    /**
     * This method is assigns main layout variables.
     */
    private void initLayoutVariables() {
        title = getView().findViewById(R.id.empty_list_title);
        message = getView().findViewById(R.id.empty_list_empty_message);
        hint = getView().findViewById(R.id.empty_list_hint);
    }

    private void setTexts() {
        typingAnimation(title, getResources().getString(R.string.app_name));
        typingAnimation(message, getResources().getString(R.string.list_is_empty));
        typingAnimation(hint, getResources().getString(R.string.empty_hint));
    }
}
