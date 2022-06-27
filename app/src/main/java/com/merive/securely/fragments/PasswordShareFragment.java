package com.merive.securely.fragments;

import static com.merive.securely.elements.TypingTextView.typingAnimation;

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
import com.merive.securely.elements.TypingTextView;

public class PasswordShareFragment extends Fragment {

    TypingTextView title;
    ImageView QRCode, copy, cancel;

    public static PasswordShareFragment newInstance(String name) {
        PasswordShareFragment frag = new PasswordShareFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_share, parent, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        setTitle();
        setListeners();

        QRCode.setImageBitmap(makeQRCode(((MainActivity) getActivity()).getEncryptPasswordValues(getArguments().getString("name"))));
    }

    private void initVariables() {
        title = getView().findViewById(R.id.password_share_title);
        copy = getView().findViewById(R.id.password_share_copy_button);
        cancel = getView().findViewById(R.id.password_share_cancel_button);
        QRCode = getView().findViewById(R.id.password_share_qr_code);
    }

    private void setListeners() {
        copy.setOnClickListener(v -> clickCopy());
        cancel.setOnClickListener(v -> clickCancel());
    }

    private void setTitle() {
        typingAnimation(title, getArguments().getString("name"));
    }

    private void clickCopy() {
        ((MainActivity) getActivity()).addToClipboard(getArguments().getString("name"));
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

    private void clickCancel() {
        ((MainActivity) getActivity()).makeVibration();
        ((MainActivity) getActivity()).setBarFragment(new BarFragment());
    }

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
