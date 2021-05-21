package com.merive.securepass.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crypt {

    String alf = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!#$%&'()*+`-./:;<>=?,{}|~";
    String shuffledAlf;
    int seed;

    public Crypt(int seed) {
        shuffledAlf = shuffle(alf);
        this.seed = seed;
    }

    public String shuffle(String string) {
        /* Shuffle symbols in string */
        Random random = new Random(seed);
        List<Character> characters = new ArrayList<>();
        for (char c : string.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(string.length());
        while (characters.size() != 0) {
            int randPicker = (random.nextInt(characters.size()));
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    public String encrypt(String text) {
        /* Encrypt string */
        StringBuilder encryptedText = new StringBuilder();
        String[] shuffledAlf = shuffle(alf).split("");
        for (String s : text.split("")) {
            encryptedText.append(findIndex(shuffledAlf, s)).append(".");
        }
        return encryptedText.toString();
    }

    public String decrypt(String text) {
        /* Decrypt string */
        StringBuilder decryptedText = new StringBuilder();
        String[] shuffledAlf = shuffle(alf).split("");
        for (String s : text.split("\\.")) {
            decryptedText.append(shuffledAlf[Integer.parseInt(s)]);
        }
        return decryptedText.toString();
    }

    public static int findIndex(String[] arr, String c) {
        /* Fin dindex of symbol */
        if (arr == null)
            return -1;

        int len = arr.length;
        int i = 0;

        while (i < len) {
            if (arr[i].equals(c))
                return i;
             else
                i = i + 1;

        }
        return -1;
    }
}
