package com.merive.securely.utils;

import android.annotation.SuppressLint;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

    private static SecretKeySpec secretKey;
    private byte[] key;

    public Crypt(String password) {
        MessageDigest sha = null;
        try {
            key = Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(password.getBytes(StandardCharsets.UTF_8)), 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException ignore) {
        }
    }

    /**
     * @param text Text, that will be encrypted
     * @return Return encrypted text string
     */
    public String encrypt(String text) {
        try {
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return new String(Base64.getEncoder().encode(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * @param text Encrypted text, that will be decrypted
     * @return Return decrypted text string
     */
    public String decrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (Exception ignored) {
        }
        return null;
    }
}
