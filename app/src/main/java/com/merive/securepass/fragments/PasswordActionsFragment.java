package com.merive.securepass.fragments;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

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

public class PasswordActionsFragment extends DialogFragment {

    TypingTextView title;
    ImageView copy, delete, share;

    /**
     * PasswordActionsFragment Constructor.
     * Using for creating DialogFragment in MainActivity.
     *
     * @see DialogFragment
     * @see MainActivity
     */
    public PasswordActionsFragment() {
    }

    /**
     * This method is setting PasswordActionsFragment Arguments.
     *
     * @param name Password Name.
     * @return PasswordActionsFragment with necessary arguments.
     */
    public static PasswordActionsFragment newInstance(String name) {
        PasswordActionsFragment frag = new PasswordActionsFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is creating PasswordActionsFragment.
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
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_password_actions, parent);
    }

    /**
     * This method is executing after Fragment View was created.
     * In this method will be setting DialogAnimation, layout variables will be initializing.
     *
     * @param view               Fragment View Value.
     * @param savedInstanceState Saving Fragment Values.
     * @see View
     * @see Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        initVariables();
        setTitle();

        copy.setOnClickListener(v -> clickCopy());
        delete.setOnClickListener(v -> clickDelete());
    }

    /**
     * This method is initializing layout variables.
     */
    private void initVariables() {
        title = getView().findViewById(R.id.password_actions_title);
        copy = getView().findViewById(R.id.password_actions_copy_button);
        delete = getView().findViewById(R.id.password_actions_delete_button);
        share = getView().findViewById(R.id.password_actions_share_button);
    }

    /**
     * This method is setting title.
     */
    private void setTitle() {
        typingAnimation(title, getResources().getString(R.string.password_actions));
    }

    /**
     * This method add password value to clipboard.
     */
    private void clickCopy() {
        ((MainActivity) getActivity()).addToClipboard(getArguments().getString("name"));
        ((MainActivity) getActivity()).makeVibration();
        dismiss();
    }

    /**
     * This method opens ConfirmFragment for deleting password.
     */
    private void clickDelete() {
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).openConfirmPasswordDelete(getArguments().getString("name"));
        dismiss();
    }
}
