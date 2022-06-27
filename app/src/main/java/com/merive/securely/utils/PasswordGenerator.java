package com.merive.securely.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {

    final int lowerLength;
    final int upperLength;
    final int numberLength;
    final int symbolsLength;

    final String[] lowerAlf = "abcdefghijklmnopqrstuvwxyz".split("");
    final String[] upperAlf = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    final String[] numberAlf = "0123456789".split("");
    final String[] symbolsAlf = "!#$%&'()*+`-_@^/:;<>=?,[]\\{}|~.".split("");

    /**
     * PasswordGenerator constructor
     *
     * @param length Length of future generated password
     */
    public PasswordGenerator(int length) {
        if (length % 2 != 0) length--;
        lowerLength = (int) Math.round(0.4 * length);
        upperLength = (int) Math.round(0.4 * length);
        numberLength = (int) Math.round(0.1 * length);
        symbolsLength = (int) Math.round(0.1 * length) + ((length % 2 == 0) ? 0 : 1);
    }

    /**
     * @return Return generated password string
     */
    public String generatePassword() {
        return shuffle(generatePack());
    }

    /**
     * @param set    String array of symbols, that will be used for generating of symbol set
     * @param length Length of future symbol set
     * @return Return set of selected symbols for future password
     */
    private String generateSet(String[] set, int length) {
        StringBuilder string = new StringBuilder();
        while (string.length() != length) string.append(set[new Random().nextInt(set.length)]);
        return string.toString();
    }

    /**
     * @return Return symbols pack of future password
     */
    private String generatePack() {
        return generateSet(lowerAlf, lowerLength) +
                generateSet(upperAlf, upperLength) +
                generateSet(numberAlf, numberLength) +
                generateSet(symbolsAlf, symbolsLength);
    }

    /**
     * @param pack Symbols what using for generating password
     * @return Return password using shuffled pack symbols
     */
    private String shuffle(String pack) {
        List<Character> characters = new ArrayList<>();
        for (char c : pack.toCharArray()) characters.add(c);
        StringBuilder output = new StringBuilder(pack.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }
}
