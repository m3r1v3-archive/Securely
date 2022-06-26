package com.merive.securely.utils;

import android.content.Context;
import android.os.Vibrator;

public class VibrationManager {

    public static void makeVibration(Context context) {
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(75);
    }
}
