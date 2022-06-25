package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;
import com.merive.securely.elements.TypingTextView;

public class UpdateFragment extends Fragment {

    TypingTextView title, version;
    ImageView download, cancel;

    /**
     * UpdateFragment Constructor.
     * Using for creating DialogFragment in MainActivity.
     *
     * @see DialogFragment
     * @see MainActivity
     */
    public UpdateFragment() {
    }

    /**
     * This method is setting UpdateFragment Arguments.
     *
     * @param oldVersion Using Securely' Version
     * @param newVersion Actual Version on Website
     * @return UpdateFragment with necessary arguments.
     */
    public static UpdateFragment newInstance(String oldVersion, String newVersion) {
        UpdateFragment frag = new UpdateFragment();
        Bundle args = new Bundle();
        args.putString("oldVersion", oldVersion);
        args.putString("newVersion", newVersion);
        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is creating UpdateFragment.
     *
     * @param inflater           Needs for getting Fragment View.
     * @param parent             Argument of inflater.inflate().
     * @param savedInstanceState Save Fragment Values.
     * @return Fragment View.
     * @see View
     * @see Bundle
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, parent, false);
    }

    /**
     * This method is executing after Fragment View was created.
     * In this method will be setting DialogAnimation, layout variables will be initializing,
     * will be setting UpdateFragment title and version text.
     * Also will be setting onClickListener for Download Button.
     *
     * @param view               Fragment View Value.
     * @param savedInstanceState Saving Fragment Values.
     * @see View
     * @see Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        setTitle();
        setVersion();
        setListeners();
    }

    /**
     * This method is initializing layout variables.
     */
    private void initVariables() {
        title = getView().findViewById(R.id.update_title_text);
        version = getView().findViewById(R.id.update_label_text);
        download = getView().findViewById(R.id.update_download_button);
        cancel = getView().findViewById(R.id.update_cancel_button);
    }

    private void setListeners() {
        download.setOnClickListener(v -> clickDownload());
        cancel.setOnClickListener(v -> clickCancel());
    }

    /**
     * This method is setting Title of UpdateFragment.
     */
    private void setTitle() {
        typingAnimation(title, getResources().getString(R.string.update_released));
    }

    /**
     * This method is setting text to version_text element.
     */
    private void setVersion() {
        typingAnimation(version, ("Old: " + getArguments().getString("oldVersion") + " / New: " + getArguments().getString("newVersion")));
    }

    /**
     * This method is open Securely Download Page in Browser.
     */
    private void clickDownload() {
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
        ((MainActivity) getActivity()).makeVibration();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.website))));
    }

    private void clickCancel() {
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }
}
