package com.fittracker.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleUtils {

    private static final Scanner SC = new Scanner(System.in);

    public static String promptName(String label) {
        while (true) {
            try {
                System.out.print(label + ": ");
                String input = SC.nextLine().trim();

                if (!input.matches("^[A-Za-z][A-Za-z0-9 ]*$")) {
                    throw new IllegalArgumentException("Input must start with a letter and can only contain letters, numbers, and spaces.");
                }

                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Please enter a valid name (letters, numbers, spaces, but not starting with a number).");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred. Please try again.");
            }
        }
    }

    public static String promptGender(String label) {
        while (true) {
            System.out.print(label + ": ");
            String input = SC.nextLine().trim();

            if (input.length() == 1) {
                char c = Character.toUpperCase(input.charAt(0));
                if (c == 'M' || c == 'F' || c == 'O') {
                    return String.valueOf(c);
                }
            }
            System.out.println("Invalid input. Enter M/F/O only. Try again.");
        }
    }

    public static String prompt(String label) {
        System.out.print(label + ':');
        String str = SC.nextLine().trim();
        return str;
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

    public static String promptDateString(String label, boolean allowBlank) {
        while (true) {
            System.out.print(label + ": ");
            String input = SC.nextLine().trim();

            if (allowBlank && input.isEmpty()) {
                return "";
            }
            try {
                LocalDate.parse(input);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Enter date as yyyy-mm-dd (e.g., 1990-07-15)"
                        + (allowBlank ? " or leave blank." : "."));
            }
        }
    }

    public static String promptEmail(String label, boolean allowBlank) {
        while (true) {
            System.out.print(label + ": ");
            String input = SC.nextLine().trim();

            if (allowBlank && input.isEmpty()) {
                return "";
            }
            if (isBasicEmailValid(input)) {
                return input;
            }
            System.out.println("Enter a valid email (must contain '@' and '.' after '@')"
                    + (allowBlank ? " or leave blank." : "."));
        }
    }

    private static boolean isBasicEmailValid(String email) {
        int atPos = email.indexOf('@');
        int dotPos = email.lastIndexOf('.');
        return atPos > 0 && dotPos > atPos + 1 && dotPos < email.length() - 1;
    }

}
