package com.merive.securely.fragments;

import static com.merive.securely.components.TypingTextView.typingAnimation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.merive.securely.BuildConfig;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;
import com.merive.securely.components.TypingTextView;
import com.merive.securely.utils.VibrationManager;

public class UpdateFragment extends Fragment {

    private TypingTextView titleTypingText, versionTypingText;
    private ImageView downloadButton, cancelButton;

    /**
     * Create a new instance of UpdateFragment
     *
     * @return UpdateFragment object
     */
    public static UpdateFragment newInstance(String version) {
        UpdateFragment frag = new UpdateFragment();
        Bundle args = new Bundle();
        args.putString("version_value", version);
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
        return inflater.inflate(R.layout.fragment_update, parent, false);
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
        setListeners();
        setTypingTexts();
    }

    /**
     * Initialize UpdateFragment components
     */
    private void initComponents() {
        titleTypingText = getView().findViewById(R.id.update_title_text);
        versionTypingText = getView().findViewById(R.id.update_label_text);
        downloadButton = getView().findViewById(R.id.update_download_button);
        cancelButton = getView().findViewById(R.id.update_cancel_button);
    }

    /**
     * Set onClick listeners for components
     */
    private void setListeners() {
        downloadButton.setOnClickListener(v -> clickDownload());
        cancelButton.setOnClickListener(v -> clickCancel());
    }

    /**
     * Set texts to titleTypingText and versionTypingText
     */
    private void setTypingTexts() {
        typingAnimation(titleTypingText, getResources().getString(R.string.update));
        typingAnimation(versionTypingText, getResources().getString(R.string.version_current_and_new, BuildConfig.VERSION_NAME, getArguments().getString("version_value")));
    }

    /**
     * Make vibration effect, set BarFragment to bar_fragment component and open Securely download page in web browser
     *
     * @see BarFragment
     */
    private void clickDownload() {
        VibrationManager.makeVibration(getContext());
        ((MainActivity) getActivity()).setBarFragment();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.website))));
    }

    /**
     * Make vibration and set BarFragment to bar_fragment component
     *
     * @see BarFragment
     */
    private void clickCancel() {
        VibrationManager.makeVibration(getContext());
        ((MainActivity) getActivity()).setBarFragment();
    }
}
