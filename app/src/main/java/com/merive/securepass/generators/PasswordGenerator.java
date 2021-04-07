package com.merive.securepass.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {

    int lowerLength, upperLength, numberLength, symbolsLength;

    String[] lowerAlf = "abcdefghijklmnopqrstuvwxyz".split("");
    String[] upperAlf = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    String[] numberAlf = "0123456789".split("");
    String[] symbolsAlf = "!\"#$%&'()*+`-./:;<>=?,{}|~".split("");

    public PasswordGenerator(int length) {
        if (length % 2 == 0) {
            lowerLength = (int) Math.round(0.35 * length);
            upperLength = (int) Math.round(0.35 * length);
            numberLength = (int) (0.50 * (lowerLength + upperLength));
            symbolsLength = (int) (0.50 * (lowerLength + upperLength));
        } else {
            length -= 1;
            lowerLength = (int) Math.round(0.4 * length);
            upperLength = (int) Math.round(0.4 * length);
            numberLength = (int) (0.50 * (lowerLength + upperLength));
            symbolsLength = (int) (0.50 * (lowerLength + upperLength)) + 1;
        }
    }

    public String generatePassword() {
        return shuffle(generateAlfsString());
    }

    public String generateAlfString(String[] alf, int length) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < length; i++) {
            string.append(alf[new Random().nextInt((length) + 1)]);
        }
        return string.toString();
    }

    public String generateAlfsString() {
        return generateAlfString(lowerAlf, lowerLength)
                + generateAlfString(upperAlf, upperLength)
                + generateAlfString(numberAlf, numberLength)
                + generateAlfString(symbolsAlf, symbolsLength);
    }

    public String shuffle(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }
}
