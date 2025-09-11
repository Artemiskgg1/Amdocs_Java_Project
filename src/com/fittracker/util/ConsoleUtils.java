package com.fittracker.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleUtils {

    private static final Scanner SC = new Scanner(System.in);

    public static class GoBackException extends Exception {

        public GoBackException() {
            super("User chose to go back to main menu");
        }
    }

    public static String promptName(String label) throws GoBackException {
        int attempts = 0;
        final int MAX_ATTEMPTS = 5;

        while (true) {
            try {
                System.out.print(label + " (or 'q' to go back): ");
                String input = SC.nextLine().trim();

                if ("q".equalsIgnoreCase(input)) {
                    throw new GoBackException();
                }

                if (!input.matches("^[A-Za-z][A-Za-z0-9 ]*$")) {
                    attempts++;
                    BeautifulConsole.printError("Invalid input. Please enter a valid name.");
                    if (attempts >= MAX_ATTEMPTS) {
                        BeautifulConsole.printError("Too many invalid attempts! Returning to main menu...");
                        try {
                            Thread.sleep(2000); // ⏳ wait 2 seconds before returning
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        throw new GoBackException();
                    }
                    continue;
                }

                return input; // ✅ valid name entered
            } catch (GoBackException e) {
                throw e;
            } catch (Exception e) {
                attempts++;
                BeautifulConsole.printError("An unexpected error occurred. Please try again.");
                if (attempts >= MAX_ATTEMPTS) {
                    BeautifulConsole.printError("Too many invalid attempts! Returning to main menu...");
                    try {
                        Thread.sleep(2000); // ⏳ wait 2 seconds
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    throw new GoBackException();
                }
            }
        }
    }

    public static String promptGender(String label) throws GoBackException {
        while (true) {
            System.out.print(label + " (or 'q' to go back): ");
            String input = SC.nextLine().trim();

            if ("q".equalsIgnoreCase(input)) {
                throw new GoBackException();
            }

            if (input.length() == 1) {
                char c = Character.toUpperCase(input.charAt(0));
                if (c == 'M' || c == 'F' || c == 'O') {
                    return String.valueOf(c);
                }
            }
            BeautifulConsole.printError("Invalid input. Enter M/F/O only. Try again.");
        }
    }

    public static String prompt(String label) throws GoBackException {
        System.out.print(label + " (or 'q' to go back): ");
        String str = SC.nextLine().trim();

        if ("q".equalsIgnoreCase(str)) {
            throw new GoBackException();
        }

        return str;
    }

    public static Double promptHeightCm(String label, boolean allowBlank) throws GoBackException {
        while (true) {
            System.out.print(label + " (or 'q' to go back): ");
            String input = SC.nextLine().trim();

            if ("q".equalsIgnoreCase(input)) {
                throw new GoBackException();
            }
            if (allowBlank && input.isEmpty()) {
                return null;
            }

            try {
                double height = Double.parseDouble(input);

                // Validation: height must be realistic
                if (height < 50 || height > 300) {
                    BeautifulConsole.printError("Enter Valid Height.");
                    continue;
                }

                return height;
            } catch (NumberFormatException e) {
                BeautifulConsole.printError("Enter a valid number for height.");
            }
        }
    }

    public static int promptInt(String label) throws GoBackException {
        while (true) {
            try {
                String input = prompt(label);
                return Integer.parseInt(input);
            } catch (GoBackException e) {
                throw e;
            } catch (NumberFormatException e) {
                BeautifulConsole.printError("Enter a valid integer.");
            }
        }
    }

    public static double promptDouble(String label) throws GoBackException {
        while (true) {
            try {
                String input = prompt(label);
                return Double.parseDouble(input);
            } catch (GoBackException e) {
                throw e;
            } catch (NumberFormatException e) {
                BeautifulConsole.printError("Enter a valid number.");
            }
        }
    }

    public static String promptDateString(String label, boolean allowBlank) throws GoBackException {
        while (true) {
            System.out.print(label + " (or 'q' to go back): ");
            String input = SC.nextLine().trim();

            if ("q".equalsIgnoreCase(input)) {
                throw new GoBackException();
            }
            if (allowBlank && input.isEmpty()) {
                return "";
            }

            try {
                LocalDate date = LocalDate.parse(input);
                LocalDate today = LocalDate.now();
                if (date.isAfter(today)) {
                    BeautifulConsole.printError("Date in Invalid.");
                    continue;
                }

                int age = Period.between(date, today).getYears();
                if (age > 100) {
                    BeautifulConsole.printError("Date is Invalid");
                    continue;
                }

                return input;
            } catch (DateTimeParseException e) {
                BeautifulConsole.printError(
                        "Enter date as yyyy-mm-dd (e.g., 1990-07-15)" + (allowBlank ? " or leave blank." : ".")
                );
            }
        }
    }

    public static String promptEmail(String label, boolean allowBlank) throws GoBackException {
        while (true) {
            System.out.print(label + " (or 'q' to go back): ");
            String input = SC.nextLine().trim();

            if ("q".equalsIgnoreCase(input)) {
                throw new GoBackException();
            }

            if (allowBlank && input.isEmpty()) {
                return "";
            }
            if (isBasicEmailValid(input)) {
                return input;
            }
            BeautifulConsole.printError("Enter a valid email address!"
                    + (allowBlank ? " or leave blank." : "."));
        }
    }

    public static int promptMenuChoice(String label) {
        while (true) {
            try {
                System.out.print(label);
                String input = SC.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                BeautifulConsole.printError("Enter a valid integer.");
            }
        }
    }

    private static boolean isBasicEmailValid(String email) {
        int atPos = email.indexOf('@');
        int dotPos = email.lastIndexOf('.');
        return atPos > 0 && dotPos > atPos + 1 && dotPos < email.length() - 1;
    }

    public static Date promptSessionDate(String label) throws GoBackException {

        while (true) {
            System.out.print(label + " (or 'q' to go back): ");
            String input = SC.nextLine().trim();

            if ("q".equalsIgnoreCase(input)) {
                throw new GoBackException();
            }

            try {
                LocalDate entered = LocalDate.parse(input);
                LocalDate today = LocalDate.now();

                // No future dates
                if (entered.isAfter(today)) {
                    BeautifulConsole.printError("Session date cannot be in the future.");
                    continue;
                }

                // No more than 7 days in the past
                if (entered.isBefore(today.minusDays(7))) {
                    BeautifulConsole.printError("Session date cannot be more than 7 days old.");
                    continue;
                }

                return Date.valueOf(entered);

            } catch (DateTimeParseException e) {
                BeautifulConsole.printError("Invalid date format. Use yyyy-mm-dd (e.g., 2025-09-10).");
            }
        }
    }

    public static Integer promptDuration(String label, boolean allowBlank) throws GoBackException {
        while (true) {
            System.out.print(label + " (or 'q' to go back): ");
            String input = SC.nextLine().trim();

            if ("q".equalsIgnoreCase(input)) {
                throw new GoBackException();
            }

            if (allowBlank && input.isEmpty()) {
                return null;
            }

            try {
                int value = Integer.parseInt(input);

                if (value <= 0) {
                    BeautifulConsole.printError("Duration must be a positive number.");
                    continue;
                }

                if (value > 1440) {
                    BeautifulConsole.printError("Duration cannot exceed 1440 minutes (24 hours).");
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                BeautifulConsole.printError("Enter a valid number for duration.");
            }
        }
    }

}
