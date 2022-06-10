package com.merive.securely.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crypt {

    final String shuffledPack;
    final int seed;
    private final String pack = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!#$%&'@()*+^`-_./:;<>=?,\\[]{}|~";

    /**
     * Crypt Constructor.
     *
     * @param seed Crypter seed (Equals your key)
     */
    public Crypt(int seed) {
        shuffledPack = shuffle(pack);
        this.seed = seed;
    }

    /**
     * This method is shuffling symbol PACK.
     *
     * @param string Symbol PACK.
     * @return Shuffled symbol PACK.
     */
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

    /**
     * This method is encrypt text using generated symbol PACK.
     *
     * @param text Text what will be encrypting.
     * @return Encrypted text.
     */
    public String encrypt(String text) {
        StringBuilder encryptedText = new StringBuilder();
        String[] shuffledAlf = shuffle(pack).split("");
        for (String s : text.split("")) encryptedText.append(findIndex(shuffledAlf, s)).append(".");
        return encryptedText.toString();
    }

    /**
     * This method is decrypt text using generated symbol PACK.
     *
     * @param text Text what will be decrypting.
     * @return Decrypted text.
     */
    public String decrypt(String text) {
        StringBuilder decryptedText = new StringBuilder();
        String[] shuffledAlf = shuffle(pack).split("");
        for (String s : text.split("\\.")) decryptedText.append(shuffledAlf[Integer.parseInt(s)]);
        return decryptedText.toString();
    }

    /**
     * This method is finding symbol index in array.
     *
     * @param arr    Array with symbols.
     * @param symbol Finding symbol.
     * @return Symbol index.
     */
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
