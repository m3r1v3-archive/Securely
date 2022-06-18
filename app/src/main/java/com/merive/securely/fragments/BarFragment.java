package com.merive.securely.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;

public class BarFragment extends Fragment {

    ImageView settings, add, lock;

    /**
     * This method is creating BarFragment.
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
        return inflater.inflate(R.layout.fragment_bar, parent, false);
    }

    /**
     * This method is executing after Fragment View was created.
     * In this method will be initializing layout variables and will be setting click listeners for them.
     *
     * @param view               Fragment View Value.
     * @param savedInstanceState Save Fragment Values.
     * @see View
     * @see Bundle
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initVariables(view);

        setSettings();
        setAdd();
        setLock();
    }

    /**
     * This method is initializing layout variables.
     *
     * @param view Needs for finding elements on Layout.
     * @see View
     */
    private void initVariables(View view) {
        settings = view.findViewById(R.id.settings_button);
        add = view.findViewById(R.id.add_button);
        lock = view.findViewById(R.id.lock_button);
    }

    /**
     * This method is setting onClickListener for Settings Button.
     *
     * @see MainActivity
     * @see View
     * @see ImageView
     */
    private void setSettings() {
        settings.setOnClickListener(v -> ((MainActivity) getActivity()).clickSettings());
    }

    /**
     * This method is setting onClickListener for Add Button.
     *
     * @see MainActivity
     * @see View
     * @see ImageView
     */
    private void setAdd() {
        add.setOnClickListener(v -> ((MainActivity) getActivity()).clickAdd());
    }

    /**
     * This method is setting onClickListener and onLongClickListener for Lock Button.
     *
     * @see MainActivity
     * @see View
     * @see ImageView
     */
    private void setLock() {
        lock.setOnClickListener(v -> ((MainActivity) getActivity()).clickLock());
        lock.setOnLongClickListener(v -> {
            ((MainActivity) getActivity()).longClickLock();
            return true;
        });
    }
}
