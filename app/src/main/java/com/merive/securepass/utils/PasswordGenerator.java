package com.merive.securepass.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {

    int lowerLength, upperLength, numberLength, symbolsLength;

    String[] lowerAlf = "abcdefghijklmnopqrstuvwxyz".split("");
    String[] upperAlf = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    String[] numberAlf = "0123456789".split("");
    String[] symbolsAlf = "!#$%&'()*+`-/:;<>=?,{}|~".split("");

    public PasswordGenerator(int length) {
        /* Set count of symbols */
        if (length % 2 == 0) {
            lowerLength = (int) Math.round(0.4 * length);
            upperLength = (int) Math.round(0.4 * length);
            numberLength = (int) Math.round(0.1 * length);
            symbolsLength = (int) Math.round(0.1 * length);
        } else {
            length -= 1;
            lowerLength = (int) Math.round(0.4 * length);
            upperLength = (int) Math.round(0.4 * length);
            numberLength = (int) Math.round(0.1 * length);
            symbolsLength = (int) Math.round(0.1 * length) + 1;
        }
    }

    public String generatePassword() {
        return shuffle(generateAlfsString());
    }

    public String generateAlfString(String[] alf, int length) {
        /* Generate alphabet */
        StringBuilder string = new StringBuilder();
        while (string.length() != length)
            string.append(alf[new Random().nextInt(alf.length)]);

        return string.toString();
    }

    public String generateAlfsString() {
        return generateAlfString(lowerAlf, lowerLength)
                + generateAlfString(upperAlf, upperLength)
                + generateAlfString(numberAlf, numberLength)
                + generateAlfString(symbolsAlf, symbolsLength);
    }

    public String shuffle(String string) {
        /* Shuffle symbols in string */
        List<Character> characters = new ArrayList<>();
        for (char c : string.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(string.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }
}
