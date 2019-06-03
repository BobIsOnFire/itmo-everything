package com.bobisonfire.foodshell;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Класс, использующийся для генерации, хранения и хэширования паролей.
 */
public class Password {
    private static final int LENGTH = 20;
    private static final double NUMBER_PROB = 0.25;
    private static final double THREE_LETTER_SYLLABLE_PROB = 0.4;

    private static final String CONSONANTS = "bcdfghkmnprstvz";
    private static final String VOWELS = "aeiouy";

    private String value;

    /**
     * Стандартный конструктор, генерирующий новый пароль.<br>
     * Пароль состоит из 20-23 символов и разделяется на слоги, состоящие из заглавной согласной буквы и
     * строчной гласной. С некоторым шансом (THREE_LETTER_SYLLABLE_PROB = 0.4) в слоге после гласной следует
     * строчная согласная, закрывающая слог. Также с некоторым шансом (NUMBER_PROB = 0.25) слоги разделены
     * случайной цифрой.
     */
    Password() {
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

    /**
     * Конструктор, создающий пароль из заданного значения, а не генерирующий его.
     * @param value пароль
     */
    Password(String value) {
        this.value = value;
    }

    String get() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Метод, возвращающий 32-символьный хэш пароля, полученный с помощью алгоритма
     * <a href="https://ru.wikipedia.org/wiki/MD2">MD2</a> (по заданию).<br>
     * Массив из 16 байт в результате выполнения алгоритма преобразуется в хэш таким
     * образом: каждый байт записывается как двузначное шестнадцатеричное число и все
     * 16 чисел записываются друг за другом.
     * @return хэш-код пароля длиной в 32 шестнадцатеричных символа.
     */
    String getHashCode() {
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

    /**
     * Метод, сравнивающий значение хэшей паролей для определения их соответствия.
     * @return true, если пароли совпадают
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Password)
            return getHashCode().equals( ((Password) obj).getHashCode() );
        return false;
    }
}
