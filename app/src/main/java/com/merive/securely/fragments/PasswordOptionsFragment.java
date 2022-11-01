package com.merive.securely.fragments;

import static com.merive.securely.components.TypingTextView.typingAnimation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.merive.securely.R;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.components.TypingTextView;
import com.merive.securely.utils.VibrationManager;

public class PasswordOptionsFragment extends Fragment {

    private TypingTextView titleTypingText;
    private ImageView QRCodeImage, copyButton, cancelButton, deleteButton;
    private MainActivity mainActivity;

    /**
     * Create a new instance of PasswordOptionsFragment, initialized to share password by 'name' argument
     *
     * @param name Password name that will be deleted
     * @return ConfirmFragment object
     */
    public static PasswordOptionsFragment newInstance(String name) {
        PasswordOptionsFragment frag = new PasswordOptionsFragment();
        Bundle args = new Bundle();
        args.putString("name_value", name);
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
        return inflater.inflate(R.layout.fragment_password_options, parent, false);
    }

    /**
     * Called immediately after onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) has returned, but before any saved state has been restored in to the view
     *
     * @param view               The View returned by onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();
        setTitle();
        setListeners();
        setQRCode();
    }

    /**
     * Initialize PasswordOptionsFragment components
     */
    private void initComponents() {
        titleTypingText = getView().findViewById(R.id.password_options_title);
        copyButton = getView().findViewById(R.id.password_options_copy_button);
        deleteButton = getView().findViewById(R.id.password_options_delete_button);
        cancelButton = getView().findViewById(R.id.password_options_cancel_button);
        QRCodeImage = getView().findViewById(R.id.password_options_qr_code);
        mainActivity = (MainActivity) getActivity();
    }

    /**
     * Set title text to titleTypingText
     */
    private void setTitle() {
        typingAnimation(titleTypingText, getArguments().getString("name_value"));
    }

    /**
     * Set onClick listeners for components
     */
    private void setListeners() {
        copyButton.setOnClickListener(v -> clickCopy());
        cancelButton.setOnClickListener(v -> clickCancel());
        deleteButton.setOnClickListener(v -> clickDeletePassword());
    }

    /**
     * Set QR Code image to QRCodeImage component
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setQRCode() {
        QRCodeImage.setImageBitmap(makeQRCode(((MainActivity) getActivity()).getEncryptPasswordValues(getArguments().getString("name_value"))));
    }

    /**
     * Make vibration effect, add password to clipboard by 'name_value' argument and set BarFragment to bar_fragment component
     */
    private void clickCopy() {
        VibrationManager.makeVibration(getContext());
        mainActivity.addToClipboard(getArguments().getString("name_value"));
        mainActivity.setBarFragment();
    }

    /**
     * Makes vibration effect and set ConfirmFragment to bar_fragment component for confirm password delete
     *
     * @see ConfirmFragment
     */
    private void clickDeletePassword() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment(ConfirmFragment.newInstance(getArguments().getString("name_value")));
    }

    /**
     * Make vibration effect and set BarFragment to bar_fragment component
     */
    private void clickCancel() {
        VibrationManager.makeVibration(getContext());
        mainActivity.setBarFragment();
    }

    /**
     * @param value Value for future QR Code
     * @return Return QR Code image with encrypted value in it
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Bitmap makeQRCode(String value) {
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF1D201D : Color.TRANSPARENT;
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, width, 0, 0, width, height);
            return bmp;
        } catch (WriterException ignored) {
            return null;
        }
    }
}
