package com.merive.securely.fragments;

import static com.merive.securely.components.TypingTextView.typingAnimation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.merive.securely.BuildConfig;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;
import com.merive.securely.components.TypingTextView;
import com.merive.securely.utils.VibrationManager;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateFragment extends Fragment {

    private TypingTextView titleTypingText;
    private EditText changelogText;
    private ImageView downloadButton, cancelButton;
    private MainActivity mainActivity;

    /**
     * Creates new instance of UpdateFragment that will be initialized with the given arguments
     *
     * @return New instance of UpdateFragment with necessary arguments
     */
    public static UpdateFragment newInstance(JSONObject jsonObject) throws JSONException {
        UpdateFragment frag = new UpdateFragment();

        Bundle args = new Bundle();
        args.putString("version", (String) jsonObject.get("version"));
        args.putString("changelog", (String) jsonObject.get("changelog"));
        args.putString("link", (String) jsonObject.get("link"));

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

        initVariables();
        setListeners();
        setTitle();
        setChangelog();
    }

    /**
     * Initialize UpdateFragment components
     */
    private void initVariables() {
        titleTypingText = getView().findViewById(R.id.update_title_text);
        changelogText = getView().findViewById(R.id.update_changelog);
        downloadButton = getView().findViewById(R.id.update_download_button);
        cancelButton = getView().findViewById(R.id.update_cancel_button);
        mainActivity = ((MainActivity) getActivity());
    }

    /**
     * Sets button click listeners
     */
    private void setListeners() {
        downloadButton.setOnClickListener(v -> clickDownload());
        cancelButton.setOnClickListener(v -> clickCancel());
    }

    /**
     * Set texts to titleTypingText
     */
    private void setTitle() {
        typingAnimation(titleTypingText, getResources().getString(R.string.update));
    }

    /**
     * Sets text to versionText TextView
     */
    private void setChangelog() {
        changelogText.setText(String.format("Changelog (%s)\n\n%s", getArguments().getString("version"),
                getArguments().getString("changelog").replace("\\n", "\n")));
    }

    /**
     * Executes when clicking on downloadButton
     * Makes vibration and opens P1MT web page in browser
     */
    private void clickDownload() {
        VibrationManager.makeVibration(getContext());
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getArguments().getString("link"))));
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
