package com.merive.securely.utils;

import android.content.Context;
import android.os.Vibrator;

public class VibrationManager {

    /**
     * This method is making vibration.
     *
     * @param context Needs for getting System Service.
     * @see Context
     */
    public static void makeVibration(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }
}
