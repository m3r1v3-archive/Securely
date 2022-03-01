package com.merive.securepass.fragments;

import static com.merive.securepass.elements.TypingTextView.typingAnimation;

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

import com.merive.securepass.MainActivity;
import com.merive.securepass.R;
import com.merive.securepass.elements.TypingTextView;

public class UpdateFragment extends DialogFragment {

    TypingTextView title, version;
    ImageView download;

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
     * @param oldVersion Using SecurePass' Version
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
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_update, parent);
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
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        initVariables(view);
        setTitle();
        setVersion();

        download.setOnClickListener(v -> clickDownload());
    }

    /**
     * This method is initializing layout variables.
     *
     * @param view Needs for finding elements on Layout.
     * @see View
     */
    private void initVariables(View view) {
        title = view.findViewById(R.id.update_title);
        version = view.findViewById(R.id.update_version_title);
        download = view.findViewById(R.id.download_update_button);
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
     * This method is open SecurePass Download Page in Browser.
     */
    private void clickDownload() {
        dismiss();
        ((MainActivity) getActivity()).makeVibration();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.website)));
        startActivity(browserIntent);
    }
}
