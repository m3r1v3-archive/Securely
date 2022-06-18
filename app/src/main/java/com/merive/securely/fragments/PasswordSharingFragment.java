package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.merive.securely.activities.MainActivity;
import com.merive.securely.R;
import com.merive.securely.elements.TypingTextView;

public class PasswordSharingFragment extends DialogFragment {

    TypingTextView title;
    ImageView QRCode, copy;

    /**
     * PasswordSharingFragment Constructor.
     * Using for creating DialogFragment in MainActivity.
     *
     * @see DialogFragment
     * @see MainActivity
     */
    public PasswordSharingFragment() {
    }

    /**
     * This method is setting PasswordSharingFragment Arguments.
     *
     * @param name Password Name.
     * @return PasswordSharingFragment with necessary arguments.
     */
    public static PasswordSharingFragment newInstance(String name) {
        PasswordSharingFragment frag = new PasswordSharingFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    /**
     * This method is creating PasswordSharingFragment.
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
        return inflater.inflate(R.layout.fragment_password_sharing, parent);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        initVariables();
        setTitle();

        QRCode.setImageBitmap(makeQRCode(((MainActivity) getActivity()).getEncryptedValues(getArguments().getString("name"))));
        copy.setOnClickListener(v -> clickCopy());
    }

    /**
     * This method is initializing layout variables.
     */
    private void initVariables() {
        title = getView().findViewById(R.id.password_sharing_title);
        copy = getView().findViewById(R.id.password_sharing_copy_button);
        QRCode = getView().findViewById(R.id.qr_code);
    }

    /**
     * This method is setting title.
     */
    private void setTitle() {
        typingAnimation(title, getArguments().getString("name"));
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
     * This method generates QR-Code.
     *
     * @return QR-Code Bitmap image.
     * @see Bitmap
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
