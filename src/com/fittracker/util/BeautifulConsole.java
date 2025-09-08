package com.fittracker.util;

public class BeautifulConsole {
    
    // ANSI Color codes
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String PURPLE = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";
    public static final String BRIGHT_GREEN = "\033[92m";
    public static final String BRIGHT_BLUE = "\033[94m";
    public static final String BRIGHT_YELLOW = "\033[93m";
    public static final String BRIGHT_RED = "\033[91m";
    
    // Background colors
    public static final String BG_BLACK = "\033[40m";
    public static final String BG_RED = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_YELLOW = "\033[43m";
    public static final String BG_BLUE = "\033[44m";
    public static final String BG_PURPLE = "\033[45m";
    public static final String BG_CYAN = "\033[46m";
    
    // Text styles
    public static final String BOLD = "\033[1m";
    public static final String UNDERLINE = "\033[4m";
    public static final String BLINK = "\033[5m";
    
    
    private static final String BRIGHT_CYAN = "\033[96m";

    // Clear screen
    public static void clearScreen() {
        System.out.print("\033[2J\033[H");
    }
    
    // Print header with styling
    public static void printHeader(String title) {
        String border = "═".repeat(50);
        System.out.println(BRIGHT_BLUE + border + RESET);
        System.out.println(BRIGHT_BLUE + "║" + BOLD + BRIGHT_YELLOW + 
            String.format("%" + ((48 + title.length()) / 2) + "s", title) + 
            String.format("%" + (48 - ((48 + title.length()) / 2)) + "s", "") + 
            RESET + BRIGHT_BLUE + "║" + RESET);
        System.out.println(BRIGHT_BLUE + border + RESET);
    }
    
    // Print beautiful menu
    public static void printMenu() {
        clearScreen();
        printHeader(" FITNESS TRACKER ");
        System.out.println();
        
        System.out.println(BRIGHT_CYAN + "┌─ USER MANAGEMENT" + RESET);
        System.out.println(CYAN + "│  1)" + RESET + " Create User " );
        System.out.println(CYAN + "│  2)" + RESET + " List Users " );
        System.out.println(CYAN + "│  3)" + RESET + " Update User Email " );
        System.out.println(CYAN + "│  4)" + RESET + " Delete User " );
        
        System.out.println();
        System.out.println(BRIGHT_GREEN + "┌─ EXERCISE & WORKOUTS" + RESET);
        System.out.println(GREEN + "│  5)" + RESET + " Add Exercise " );
        System.out.println(GREEN + "│  6)" + RESET + " List Exercises " );
        System.out.println(GREEN + "│  7)" + RESET + " Create Workout Session " );
        System.out.println(GREEN + "│  8)" + RESET + " Add Set to Session " );
        System.out.println(GREEN + "│  9)" + RESET + " List Sessions for User " );

        System.out.println();
        System.out.println(BRIGHT_YELLOW + "┌─ NUTRITION & WELLNESS" + RESET);
        System.out.println(YELLOW + "│  10)" + RESET + " Add Nutrition Log " );
        System.out.println(YELLOW + "│  11)" + RESET + " List Nutrition for User+Date " );

        System.out.println();
        System.out.println(BRIGHT_RED + "│  0)" + RESET + " Exit " );
        System.out.println();
        
        System.out.print(BOLD + BRIGHT_BLUE + "Choose an option: " + RESET);
    }
    
    // Success message
    public static void printSuccess(String message) {
        System.out.println(BRIGHT_GREEN + " " + message + RESET);
    }
    
    // Error message
    public static void printError(String message) {
        System.out.println(BRIGHT_RED + " Error: " + message + RESET);
    }
    
    // Info message
    public static void printInfo(String message) {
        System.out.println(BRIGHT_BLUE + message + RESET);
    }
    
    // Warning message
    public static void printWarning(String message) {
        System.out.println(BRIGHT_YELLOW + message + RESET);
    }
    
    // Print user table beautifully
    public static void printUserTable(java.util.List<com.fittracker.model.User> users) {
        if (users.isEmpty()) {
            printWarning("No users found!");
            return;
        }
        
        System.out.println(BOLD + BRIGHT_CYAN + "\n" +" USERS LIST " + RESET);
        System.out.println(CYAN + "┌─────┬──────────────────┬─────────────────────┬────────┬────────────┬─────────┐" + RESET);
        System.out.println(CYAN + "│ ID  │ Name             │ Email               │ Gender │ DOB        │ Height  │" + RESET);
        System.out.println(CYAN + "├─────┼──────────────────┼─────────────────────┼────────┼────────────┼─────────┤" + RESET);
        
        for (com.fittracker.model.User u : users) {
            System.out.printf(CYAN + "│" + RESET + " %-3d " + CYAN + "│" + RESET + " %-16s " + 
                CYAN + "│" + RESET + " %-19s " + CYAN + "│" + RESET + " %-6s " + 
                CYAN + "│" + RESET + " %-10s " + CYAN + "│" + RESET + " %6.1fcm " + 
                CYAN + "│" + RESET + "%n",
                u.userId, 
                truncate(u.fullName, 16), 
                truncate(u.email, 19),
                u.gender, 
                u.dob != null ? u.dob.toString() : "N/A",
                u.heightCm != null ? u.heightCm : 0.0);
        }
        System.out.println(CYAN + "└─────┴──────────────────┴─────────────────────┴────────┴────────────┴─────────┘" + RESET);
    }
    
    // Print exercise table
    public static void printExerciseTable(java.util.List<com.fittracker.model.Exercise> exercises) {
        if (exercises.isEmpty()) {
            printWarning("No exercises found!");
            return;
        }
        
        System.out.println(BOLD + BRIGHT_GREEN + "\n" +" EXERCISES LIST " +RESET);
        System.out.println(GREEN + "┌─────┬─────────────────────┬─────────────┬───────────┬─────────┐" + RESET);
        System.out.println(GREEN + "│ ID  │ Exercise Name       │ Category    │ Unit      │ MET     │" + RESET);
        System.out.println(GREEN + "├─────┼─────────────────────┼─────────────┼───────────┼─────────┤" + RESET);
        
        for (com.fittracker.model.Exercise e : exercises) {
            System.out.printf(GREEN + "│" + RESET + " %-3d " + GREEN + "│" + RESET + " %-19s " +
                GREEN + "│" + RESET + " %-11s " + GREEN + "│" + RESET + " %-9s " +
                GREEN + "│" + RESET + " %7s " + GREEN + "│" + RESET + "%n",
                e.exerciseId,
                truncate(e.name, 19),
                truncate(e.category, 11),
                truncate(e.unit, 9),
                e.metValue != null ? String.format("%.1f", e.metValue) : "N/A");
        }
        System.out.println(GREEN + "└─────┴─────────────────────┴─────────────┴───────────┴─────────┘" + RESET);
    }
    
    // Print step calculation results beautifully
    public static void printStepCalculations(com.fittracker.model.StepMetrics metrics) {
        System.out.println(BOLD + BRIGHT_YELLOW + "\n" +" AUTO-CALCULATED METRICS "+ RESET);
        System.out.println(YELLOW + "┌──────────────────────┬──────────────┐" + RESET);
        System.out.printf(YELLOW + "│" + RESET + " Distance             " + YELLOW + "│" + RESET + " %10.2f km " + YELLOW + "│" + RESET + "%n", metrics.distanceKm);
        System.out.printf(YELLOW + "│" + RESET + " Calories Burned      " + YELLOW + "│" + RESET + " %10.0f cal" + YELLOW + "│" + RESET + "%n", metrics.caloriesBurned);
        System.out.printf(YELLOW + "│" + RESET + " Average Speed        " + YELLOW + "│" + RESET + " %10.2f km/h" + YELLOW + "│" + RESET + "%n", metrics.avgSpeedKmh);
        System.out.printf(YELLOW + "│" + RESET + " MET Value Used       " + YELLOW + "│" + RESET + " %12.1f " + YELLOW + "│" + RESET + "%n", metrics.metValue);
        System.out.println(YELLOW + "└──────────────────────┴──────────────┘" + RESET);
    }
    
    // Print workout sessions
    public static void printWorkoutSessions(java.util.List<com.fittracker.model.WorkoutSession> sessions) {
        if (sessions.isEmpty()) {
            printWarning("No workout sessions found!");
            return;
        }
        
        System.out.println(BOLD + BRIGHT_GREEN + "\n" +" WORKOUT SESSIONS " + RESET);
        System.out.println(GREEN + "┌─────┬────────────┬──────────┬─────────────────────────────┐" + RESET);
        System.out.println(GREEN + "│ ID  │ Date       │ Duration │ Notes                       │" + RESET);
        System.out.println(GREEN + "├─────┼────────────┼──────────┼─────────────────────────────┤" + RESET);
        
        for (com.fittracker.model.WorkoutSession s : sessions) {
            System.out.printf(GREEN + "│" + RESET + " %-3d " + GREEN + "│" + RESET + " %-10s " +
                GREEN + "│" + RESET + " %6s min " + GREEN + "│" + RESET + " %-27s " +
                GREEN + "│" + RESET + "%n",
                s.sessionId,
                s.sessionDate.toString(),
                s.durationMin != null ? s.durationMin.toString() : "N/A",
                truncate(s.notes != null ? s.notes : "", 27));
        }
        System.out.println(GREEN + "└─────┴────────────┴──────────┴─────────────────────────────┘" + RESET);
    }
    
    // Helper method to truncate strings
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
    
    // Print a separator line
    public static void printSeparator() {
        System.out.println(BRIGHT_BLUE + "─".repeat(50) + RESET);
    }
    
    // Print loading animation (just for fun!)
    public static void showLoading(String message) {
        System.out.print(BRIGHT_CYAN + message + " ");
        String[] spinner = {"|", "/", "-", "\\"};
        try {
            for (int i = 0; i < 12; i++) {
                System.out.print("\r" + BRIGHT_CYAN + message + " " + BRIGHT_YELLOW + spinner[i % 4] + RESET);
                Thread.sleep(150);
            }
            System.out.print("\r" + BRIGHT_GREEN + message + " " + RESET + "\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}