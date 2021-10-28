package com.merive.securepass.fragments;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;

public class ToastFragment extends Fragment {

    TypingTextView text;

    public static ToastFragment newInstance(String message) {
        /* newInstance method */
        ToastFragment frag = new ToastFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.toast, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initVariables(view);
        setText();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            ((MainActivity) getActivity()).setBar();
        }, 5750);
    }

    public void initVariables(View view) {
        /* Init main variables */
        text = view.findViewById(R.id.message);
    }

    public void setText() {
        typingAnimation(text, getArguments().getString("message"));
    }
}

