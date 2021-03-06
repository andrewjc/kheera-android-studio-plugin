package com.github.kheera.plugin.bdd.java.snippet;

public class CamelCaseConcatenator implements Concatenator {
    @Override
    public String concatenate(String[] words) {
        StringBuilder functionName = new StringBuilder();
        boolean firstWord = true;
        for (String word : words) {
            if (firstWord) {
                functionName.append(word.toLowerCase());
                firstWord = false;
            } else {
                functionName.append(capitalize(word));
            }
        }
        return functionName.toString();
    }

    public String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}