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

    public PasswordGenerator(int length) {
        if (length % 2 != 0) length--;
        lowerLength = (int) Math.round(0.4 * length);
        upperLength = (int) Math.round(0.4 * length);
        numberLength = (int) Math.round(0.1 * length);
        symbolsLength = (int) Math.round(0.1 * length) + ((length % 2 == 0) ? 0 : 1);
    }

    public String generatePassword() {
        return shuffle(generatePackString());
    }

    private String generateSetString(String[] set, int length) {
        StringBuilder string = new StringBuilder();
        while (string.length() != length)
            string.append(set[new Random().nextInt(set.length)]);

        return string.toString();
    }

    private String generatePackString() {
        return generateSetString(lowerAlf, lowerLength)
                + generateSetString(upperAlf, upperLength)
                + generateSetString(numberAlf, numberLength)
                + generateSetString(symbolsAlf, symbolsLength);
    }

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
