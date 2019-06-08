package com.bobisonfire.foodshell.client.exchange;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class Password {
    private static final int LENGTH = 20;
    private static final double NUMBER_PROB = 0.25;
    private static final double THREE_LETTER_SYLLABLE_PROB = 0.4;

    private static final String CONSONANTS = "bcdfghkmnprstvz";
    private static final String VOWELS = "aeiouy";

    private String value;

    public Password() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        while (sb.length() < LENGTH) {
            sb.append( CONSONANTS.toUpperCase().charAt( random.nextInt(CONSONANTS.length()) ) );
            sb.append( VOWELS.charAt( random.nextInt(VOWELS.length()) ) );

            if (random.nextDouble() <= THREE_LETTER_SYLLABLE_PROB)
                sb.append( CONSONANTS.charAt( random.nextInt(CONSONANTS.length()) ) );

            if (random.nextDouble() <= NUMBER_PROB)
                sb.append(random.nextInt(10));
        }

        value = sb.toString();
    }

    public Password(String value) {
        this.value = value != null ? value : "";
    }

    public String get() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getHashCode() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD2");
            byte[] bytes = digest.digest(value.getBytes());
            String hash = new BigInteger(1, bytes).toString(16);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (32 - hash.length()); i++) sb.append('0');
            return sb.toString() + hash;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Password)
            return getHashCode().equals( ((Password) obj).getHashCode() );
        return false;
    }
}
