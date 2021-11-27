package com.merive.securepass.fragments;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;

public class ToastFragment extends Fragment {

    TypingTextView text;

    /**
     * This method is creating ToastFragment.
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
        return inflater.inflate(R.layout.toast, parent, false);
    }

    /**
     * This method is executing after Fragment View was created.
     * In this method will be initializing layout variables,
     * setting message to TypingTextView.
     * Fragment will replace after 5.75 seconds to BarFragment.
     *
     * @param view               Fragment View Value.
     * @param savedInstanceState Saving Fragment Values.
     * @see View
     * @see Bundle
     * @see TypingTextView
     * @see BarFragment
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initVariables(view);
        showToast();
    }

    /**
     * This method is initializing layout variables.
     *
     * @param view Needs for finding elements on Layout.
     * @see View
     */
    private void initVariables(View view) {
        text = view.findViewById(R.id.toast_text);
    }

    /**
     * This method is setting message value to TypingTextView.
     *
     * @param message Toast message value.
     */
    private void setText(String message) {
        typingAnimation(text, message);
    }

    private void showToast() {
        try {
            if (MainActivity.toastMessages.isEmpty()) {
                ((MainActivity) getActivity()).openBarFragment();
            } else {
                setText(MainActivity.toastMessages.getFirst());
                MainActivity.toastMessages.removeFirst();
                new Handler().postDelayed(() -> showToast(), 4250);
            }
        } catch (NullPointerException ignored) {}
    }
}

