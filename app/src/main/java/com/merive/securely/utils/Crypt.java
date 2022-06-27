package com.merive.securely.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crypt {

    final String shuffledPack;
    final int seed;
    private final String pack = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!#$%&'@()*+^`-_./:;<>=?,\\[]{}|~";

    public Crypt(int seed) {
        shuffledPack = shuffle();
        this.seed = seed;
    }

    /**
     * @return Return shuffled pack string, shuffled by seed
     */
    private String shuffle() {
        List<Character> characters = new ArrayList<>();
        for (char c : pack.toCharArray()) characters.add(c);
        StringBuilder output = new StringBuilder(pack.length());
        while (characters.size() != 0)
            output.append(characters.remove((new Random(seed).nextInt(characters.size()))));
        return output.toString();
    }

    /**
     * @param text Text, that will be encrypted
     * @return Return encrypted text string
     */
    public String encrypt(String text) {
        StringBuilder encryptedText = new StringBuilder();
        for (String s : text.split(""))
            encryptedText.append(findIndex(shuffle().split(""), s)).append(".");
        return encryptedText.toString();
    }

    /**
     * @param text Encrypted text, that will be decrypted
     * @return Return decrypted text string
     */
    public String decrypt(String text) {
        StringBuilder decryptedText = new StringBuilder();
        for (String s : text.split("\\."))
            decryptedText.append(shuffle().split("")[Integer.parseInt(s)]);
        return decryptedText.toString();
    }

    /**
     * @return Return index of symbol in arr
     */
    private int findIndex(String[] arr, String symbol) {
        if (arr == null) return -1;
        int i = 0;
        while (i < arr.length)
            if (arr[i].equals(symbol)) return i;
            else i = i + 1;
        return -1;
    }
}
