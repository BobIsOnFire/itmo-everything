package com.bobisonfire;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SeriesFunction s = SeriesFunction.arctan();

        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);
        while (sc.hasNextDouble()) {
            System.out.println(s.get(sc.nextDouble(), 0.00001));
        }
    }
}
