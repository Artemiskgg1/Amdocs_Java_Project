package com.fittracker.util;

import java.util.Scanner;

public class ConsoleUtils {
    private static final Scanner SC = new Scanner(System.in);

    public static String prompt(String label) {
        System.out.print(label + ": ");
        return SC.nextLine().trim();
    }

    public static int promptInt(String label) {
        while (true) {
            try {
                return Integer.parseInt(prompt(label));
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    public static double promptDouble(String label) {
        while (true) {
            try {
                return Double.parseDouble(prompt(label));
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}
