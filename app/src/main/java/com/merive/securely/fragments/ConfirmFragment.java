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
import com.merive.securely.utils.VibrationManager;

public class ConfirmFragment extends Fragment {

    private TypingTextView titleTypingText;
    private ImageView cancelButton, confirmButton;
    private MainActivity mainActivity;

    /**
     * Create a new instance of ConfirmFragment, initialized to delete password by 'name'
     *
     * @param name Password name that will be deleted
     * @return ConfirmFragment object
     */
    public static ConfirmFragment newInstance(String name) {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Create a new instance of ConfirmFragment, initialized to edit key
     *
     * @return ConfirmFragment object
     */
    public static ConfirmFragment newInstance() {
        ConfirmFragment frag = new ConfirmFragment();
        Bundle args = new Bundle();
        args.putBoolean("key_edit", true);
        frag.setArguments(args);
        return frag;
    }

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
        return inflater.inflate(R.layout.fragment_confirm, parent, false);
    }

    /**
     * Called immediately after onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) has returned, but before any saved state has been restored in to the view
     *
     * @param view               The View returned by onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();
        setTitle();
        setListeners();
    }

    /**
     * Initialize ConfirmFragment components
     */
    private void initComponents() {
        titleTypingText = getView().findViewById(R.id.confirm_title_text);
        cancelButton = getView().findViewById(R.id.confirm_cancel_button);
        confirmButton = getView().findViewById(R.id.confirm_confirm_button);
        mainActivity = ((MainActivity) getActivity());
    }

    /**
     * Set onClick listeners for components
     */
    private void setListeners() {
        cancelButton.setOnClickListener(v -> clickCancel());
        confirmButton.setOnClickListener(v -> clickConfirm());
    }

    /**
     * Set title text to titleTypingText
     */
    private void setTitle() {
        if (getArguments().getBoolean("key_edit", false))
            typingAnimation(titleTypingText, getResources().getString(R.string.change_key));
        else if (getArguments().getString("name", null) == null)
            typingAnimation(titleTypingText, getResources().getString(R.string.delete_all_passwords));
        else typingAnimation(titleTypingText, getResources().getString(R.string.delete_password));
    }

    /**
     * Make vibration effect and set BarFragment to bar_fragment
     */
    private void clickCancel() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment();
    }

    /**
     * Make vibration effect and check if 'key_edit' argument is true, start changing key.
     * Else if 'name' isn't null delete password by 'name'.
     * Else delete all passwords
     */
    private void clickConfirm() {
        VibrationManager.makeVibration(getContext());
        if (getArguments().getBoolean("key_edit")) changeKey();
        else if (getArguments().getString("name") == null) mainActivity.deleteAllPasswords();
        else mainActivity.deletePasswordByName(getArguments().getString("name"));
        mainActivity.setBarFragment();
    }

    /**
     * Disable date encrypt and start changing key in LoginActivity
     *
     * @see LoginActivity
     */
    private void changeKey() {
        mainActivity.updateEncrypt(false);
        startActivity(new Intent(getActivity(), LoginActivity.class)
                .putExtra("key_edit", true));
    }
}
