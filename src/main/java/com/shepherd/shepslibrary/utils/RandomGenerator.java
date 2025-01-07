package com.shepherd.shepslibrary.utils;


import org.apache.commons.text.RandomStringGenerator;

public final class RandomGenerator {
    public static String generateRandomString(int length) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .get();
        return generator.generate(length);
    }
    private RandomGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

}


