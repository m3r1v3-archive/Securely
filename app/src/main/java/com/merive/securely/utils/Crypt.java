package com.merive.securely.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crypt {

    final String shuffledPack;
    final int seed;
    private final String pack = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!#$%&'@()*+^`-_./:;<>=?,\\[]{}|~";

    public Crypt(int seed) {
        shuffledPack = shuffle(pack);
        this.seed = seed;
    }

    private String shuffle(String string) {
        Random random = new Random(seed);
        List<Character> characters = new ArrayList<>();
        for (char c : string.toCharArray()) characters.add(c);
        StringBuilder output = new StringBuilder(string.length());
        while (characters.size() != 0) {
            int randPicker = (random.nextInt(characters.size()));
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    public String encrypt(String text) {
        StringBuilder encryptedText = new StringBuilder();
        String[] shuffledAlf = shuffle(pack).split("");
        for (String s : text.split("")) encryptedText.append(findIndex(shuffledAlf, s)).append(".");
        return encryptedText.toString();
    }

    public String decrypt(String text) {
        StringBuilder decryptedText = new StringBuilder();
        String[] shuffledAlf = shuffle(pack).split("");
        for (String s : text.split("\\.")) decryptedText.append(shuffledAlf[Integer.parseInt(s)]);
        return decryptedText.toString();
    }

    private int findIndex(String[] arr, String symbol) {
        if (arr == null) return -1;
        int len = arr.length;
        int i = 0;
        while (i < len) {
            if (arr[i].equals(symbol)) return i;
            else i = i + 1;
        }
        return -1;
    }
}
