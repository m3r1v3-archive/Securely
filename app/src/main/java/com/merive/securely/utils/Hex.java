package com.merive.securely.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class Hex {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String value) {
        String[] encryptedSymbols = new String[value.length()];
        for (int i = 0; i < value.length(); i++)
            encryptedSymbols[i] = Integer.toHexString((int) value.toCharArray()[i]);
        return String.join(" ", encryptedSymbols);
    }

    public static String decrypt(String value) {
        StringBuilder decryptedString = new StringBuilder();
        for (String s : value.split(" "))
            decryptedString.append((char) Integer.parseInt(s, 16));
        return decryptedString.toString();
    }
}
