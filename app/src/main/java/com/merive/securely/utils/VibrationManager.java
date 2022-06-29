package com.merive.securely.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;

public class VibrationManager {

    /**
     * @param context Context object
     */
    public static void makeVibration(Context context) {
        if ((((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode() > 0))
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(75);
    }
}
