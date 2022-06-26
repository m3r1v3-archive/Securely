package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

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

import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;
import com.merive.securely.elements.TypingTextView;

public class UpdateFragment extends Fragment {

    TypingTextView title, version;
    ImageView download, cancel;

    public static UpdateFragment newInstance(String oldVersion, String newVersion) {
        UpdateFragment frag = new UpdateFragment();
        Bundle args = new Bundle();
        args.putString("oldVersion", oldVersion);
        args.putString("newVersion", newVersion);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        setTitle();
        setVersion();
        setListeners();
    }

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

    private void setTitle() {
        typingAnimation(title, getResources().getString(R.string.update));
    }

    private void setVersion() {
        typingAnimation(version, ("Old: " + getArguments().getString("oldVersion") + " / New: " + getArguments().getString("newVersion")));
    }

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
