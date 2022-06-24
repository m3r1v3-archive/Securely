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

    /**
     * This method is setting ConfirmFragment Arguments for deleting one password.
     *
     * @param name Deleting Password Name.
     * @return ConfirmFragment with necessary arguments.
     */
    public static ConfirmFragment newInstance(String name) {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is setting ConfirmFragment Arguments for deleting all passwords.
     *
     * @return ConfirmFragment with necessary arguments.
     */
    public static ConfirmFragment newInstance() {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString("name", null);
        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is setting ConfirmFragment Arguments for changing key.
     *
     * @return ConfirmFragment.
     */
    public static ConfirmFragment newInstance(boolean changeKey) {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putBoolean("changeKey", changeKey);
        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is creating ConfirmFragment.
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
        return inflater.inflate(R.layout.fragment_confirm, parent, false);
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

        initVariables(view);
        setTitle();

        cancel.setOnClickListener(v -> clickCancel());
        confirm.setOnClickListener(v -> clickConfirm());
    }

    /**
     * This method is initializing layout variables.
     *
     * @param view Needs for finding elements on Layout.
     * @see View
     */
    private void initVariables(View view) {
        title = view.findViewById(R.id.confirm_title);
        cancel = view.findViewById(R.id.confirm_cancel_button);
        confirm = view.findViewById(R.id.confirm_okay_button);
    }

    /**
     * This method is setting special title.
     * If changeKey argument is true, will set title for key changing.
     * Else if name argument is null, will set title for deleting all passwords.
     * Else will set title for deleting password with name "name".
     */
    private void setTitle() {
        if (getArguments().getBoolean("changeKey", false))
            typingAnimation(title, getResources().getString(R.string.change_key));
        else if (getArguments().getString("name") == null)
            typingAnimation(title, getResources().getString(R.string.delete_all_passwords));
        else typingAnimation(title, getResources().getString(R.string.delete_password));
    }

    /**
     * This method is executing after clicking on Cancel Button.
     *
     * @see MainActivity
     * @see View
     * @see ImageView
     */
    private void clickCancel() {
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    /**
     * This method is executing after clicking on Confirm Button.
     * If changeKey in Fragment Arguments is True, will start changeKey() method.
     * Else if name in Fragment Arguments is null, will start deleteAllPassword() method.
     * Else will start deletePasswordByName() method.
     *
     * @see MainActivity
     * @see View
     * @see ImageView
     */
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

    /**
     * This method is disable encrypting (to prevent errors)
     * and starting LoginActivity for key changing.
     */
    private void changeKey() {
        ((MainActivity) getActivity()).updateEncrypting(false);
        startActivity(new Intent(getActivity(), LoginActivity.class)
                .putExtra("changeKey", true));
    }
}
