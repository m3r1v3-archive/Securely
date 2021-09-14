package com.merive.securepass.utils;

import android.content.Context;
import android.os.Vibrator;

public class VibrationManager {

    public static void makeVibration(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }
}
