package com.fittracker.app;

import com.fittracker.dao.*;
import com.fittracker.model.*;
import com.fittracker.util.BeautifulConsole;
import com.fittracker.util.ConsoleUtils;
import com.fittracker.util.ExerciseMetricsCalculator;
import java.sql.Date;
import java.util.List;

public class Main {

    private static final UserDAO userDAO = new UserDAOImpl();
    private static final ExerciseDAO exerciseDAO = new ExerciseDAOImpl();
    private static final WorkoutDAO workoutDAO = new WorkoutDAOImpl();
    private static final NutritionDAO nutritionDAO = new NutritionDAOImpl();

    public static void main(String[] args) {
        while (true) {
            BeautifulConsole.printMenu();
            int ch = ConsoleUtils.promptInt("");
            try {
                switch (ch) {
                    case 1 -> createUser();
                    case 2 -> listUsers();
                    case 3 -> updateUserEmail();
                    case 4 -> deleteUser();
                    case 5 -> addExercise();
                    case 6 -> listExercises();
                    case 7 -> createSession();
                    case 8 -> addSet();
                    case 9 -> listSessions();
                    case 10 -> addNutrition();
                    case 11 -> listNutrition();
                    case 0 -> { 
                        BeautifulConsole.printInfo("Thanks for using Fitness Tracker! Stay fit!");
                        return; 
                    }
                    default -> BeautifulConsole.printError("Invalid choice. Please try again.");
                }
                
                // Pause before showing menu again
                System.out.print("\nPress Enter to continue...");
                System.in.read();
                
            } catch (Exception e) {
                BeautifulConsole.printError(e.getMessage());
                e.printStackTrace(System.out);
                try {
                    System.out.print("\nPress Enter to continue...");
                    System.in.read();
                } catch (Exception ignored) {}
            }
        }
    }

    private static void createUser() throws Exception {
        BeautifulConsole.printHeader("CREATE NEW USER");
        User u = new User();
        u.fullName = ConsoleUtils.prompt("Full name");
        u.email    = ConsoleUtils.prompt("Email");
        u.gender   = ConsoleUtils.prompt("Gender (M/F/O)");
        String dob = ConsoleUtils.prompt("DOB (yyyy-mm-dd or blank)");
        u.dob      = dob.isEmpty() ? null : Date.valueOf(dob);
        String h   = ConsoleUtils.prompt("Height cm (blank to skip)");
        u.heightCm = h.isEmpty() ? null : Double.valueOf(h);
        
        BeautifulConsole.showLoading("Creating user");
        long id = userDAO.create(u);
        BeautifulConsole.printSuccess("Created USER_ID = " + id);
    }

    private static void listUsers() throws Exception {
        BeautifulConsole.printHeader("ALL USERS");
        List<User> users = userDAO.listAll();
        BeautifulConsole.printUserTable(users);
    }

    private static void updateUserEmail() throws Exception {
        BeautifulConsole.printHeader("UPDATE USER EMAIL");
        long id = ConsoleUtils.promptInt("User ID");
        String email = ConsoleUtils.prompt("New email");
        User u = userDAO.findById(id);
        if (u == null) { 
            BeautifulConsole.printError("User not found"); 
            return; 
        }
        u.email = email;
        if (userDAO.update(u)) {
            BeautifulConsole.printSuccess("Email updated successfully!");
        }
    }

    private static void deleteUser() throws Exception {
        BeautifulConsole.printHeader("DELETE USER");
        long id = ConsoleUtils.promptInt("User ID");
        BeautifulConsole.printWarning("This will delete all user data including sessions and logs!");
        String confirm = ConsoleUtils.prompt("Type 'DELETE' to confirm");
        
        if ("DELETE".equals(confirm)) {
            if (userDAO.delete(id)) {
                BeautifulConsole.printSuccess("User deleted successfully!");
            } else {
                BeautifulConsole.printError("User not found or deletion failed");
            }
        } else {
            BeautifulConsole.printInfo("Deletion cancelled");
        }
    }

    private static void addExercise() throws Exception {
        BeautifulConsole.printHeader("ADD NEW EXERCISE");
        Exercise e = new Exercise();
        e.name     = ConsoleUtils.prompt("Exercise name");
        e.category = ConsoleUtils.prompt("Category (Strength/Cardio/Flexibility/Balance/Other)");
        e.unit     = ConsoleUtils.prompt("Unit (reps/minutes/km/kg/calories/meters/other)");
        String m   = ConsoleUtils.prompt("MET value (blank to skip)");
        e.metValue = m.isEmpty() ? null : Double.valueOf(m);
        
        BeautifulConsole.showLoading("Adding exercise");
        long id = exerciseDAO.create(e);
        BeautifulConsole.printSuccess("Created EXERCISE_ID = " + id);
    }

    private static void listExercises() throws Exception {
        BeautifulConsole.printHeader("ALL EXERCISES");
        List<Exercise> exercises = exerciseDAO.listAll();
        BeautifulConsole.printExerciseTable(exercises);
    }

    private static void createSession() throws Exception {
        BeautifulConsole.printHeader("CREATE WORKOUT SESSION");
        WorkoutSession s = new WorkoutSession();
        s.userId      = (long) ConsoleUtils.promptInt("User ID");
        String d      = ConsoleUtils.prompt("Session date (yyyy-mm-dd)");
        s.sessionDate = Date.valueOf(d);
        String dur    = ConsoleUtils.prompt("Duration minutes (blank to skip)");
        s.durationMin = dur.isEmpty() ? null : Integer.valueOf(dur);
        s.notes       = ConsoleUtils.prompt("Notes (blank allowed)");
        
        BeautifulConsole.showLoading("Creating workout session");
        long id = workoutDAO.createSession(s);
        BeautifulConsole.printSuccess("Created SESSION_ID = " + id);
    }

    private static void addSet() throws Exception {
    BeautifulConsole.printHeader("ADD SET TO SESSION");
    WorkoutSet w = new WorkoutSet();
    w.sessionId  = (long) ConsoleUtils.promptInt("Session ID");
    w.exerciseId = (long) ConsoleUtils.promptInt("Exercise ID");
    w.setNo      = ConsoleUtils.promptInt("Set number");
    
    // Get exercise details for smart input
    Exercise exercise = exerciseDAO.findById(w.exerciseId);
    if (exercise == null) {
        BeautifulConsole.printError("Exercise not found!");
        return;
    }
    
    BeautifulConsole.printInfo("🎯 Exercise: " + exercise.name + " (" + exercise.category + ")");
    BeautifulConsole.printSeparator();
    
    // Get user weight for calculations
    String weightInput = ConsoleUtils.prompt("Your body weight in kg (for calculations, default 70)");
    double userWeight = weightInput.isEmpty() ? 70.0 : Double.valueOf(weightInput);
    
    // Collect data based on exercise category
    switch (exercise.category.toUpperCase()) {
        case "STRENGTH" -> collectStrengthData(w, exercise);
        case "CARDIO" -> collectCardioData(w, exercise);
        case "FLEXIBILITY", "BALANCE" -> collectFlexibilityBalanceData(w, exercise);
        default -> collectGeneralData(w, exercise);
    }
    
    // Calculate comprehensive metrics
    BeautifulConsole.showLoading("Calculating comprehensive metrics");
    ExerciseMetrics metrics = ExerciseMetricsCalculator.calculateMetrics(exercise, w, userWeight);
    
    // Display beautiful metrics
    BeautifulConsole.printExerciseMetrics(metrics);
    
    // Ask for confirmation or modifications
    String confirm = ConsoleUtils.prompt("Save this set? (Y/n)");
    if (confirm.isEmpty() || confirm.toLowerCase().startsWith("y")) {
        BeautifulConsole.showLoading("Adding set to session");
        workoutDAO.addSet(w);
        BeautifulConsole.printSuccess("Set added with comprehensive metrics! 🎉");
    } else {
        BeautifulConsole.printInfo("Set cancelled");
    }
}

private static void collectStrengthData(WorkoutSet w, Exercise exercise) {
    BeautifulConsole.printInfo("💪 STRENGTH EXERCISE - Enter your performance data:");
    
    String reps = ConsoleUtils.prompt("Reps performed");
    w.reps = reps.isEmpty() ? null : Integer.valueOf(reps);
    
    String weight = ConsoleUtils.prompt("Weight used (kg)");
    w.weightKg = weight.isEmpty() ? null : Double.valueOf(weight);
    
    String duration = ConsoleUtils.prompt("Rest time between sets (min) - optional");
    w.durationMin = duration.isEmpty() ? null : Integer.valueOf(duration);
    
    // For bodyweight exercises
    if (w.weightKg == null) {
        BeautifulConsole.printInfo("💡 Tip: For bodyweight exercises, enter your body weight for accurate calculations");
    }
}

private static void collectCardioData(WorkoutSet w, Exercise exercise) {
    BeautifulConsole.printInfo("🏃‍♂️ CARDIO EXERCISE - Enter your activity data:");
    
    // Check if it's step-based
    if (isStepBasedExercise(exercise.name)) {
        String steps = ConsoleUtils.prompt("Steps count (if tracked)");
        w.steps = steps.isEmpty() ? null : Integer.valueOf(steps);
    }
    
    String distance = ConsoleUtils.prompt("Distance covered (km) - optional");
    w.distKm = distance.isEmpty() ? null : Double.valueOf(distance);
    
    String duration = ConsoleUtils.prompt("Duration (minutes)");
    w.durationMin = duration.isEmpty() ? null : Integer.valueOf(duration);
    
    // Additional cardio-specific data
    String reps = ConsoleUtils.prompt("Intervals/rounds (if applicable)");
    w.reps = reps.isEmpty() ? null : Integer.valueOf(reps);
}

private static void collectFlexibilityBalanceData(WorkoutSet w, Exercise exercise) {
    BeautifulConsole.printInfo("🧘‍♀️ FLEXIBILITY/BALANCE EXERCISE - Enter duration:");
    
    String duration = ConsoleUtils.prompt("Duration (minutes)");
    w.durationMin = duration.isEmpty() ? null : Integer.valueOf(duration);
    
    String reps = ConsoleUtils.prompt("Repetitions/Holds (if applicable)");
    w.reps = reps.isEmpty() ? null : Integer.valueOf(reps);
}

private static void collectGeneralData(WorkoutSet w, Exercise exercise) {
    BeautifulConsole.printInfo("🏋️ GENERAL EXERCISE - Enter available data:");
    
    String reps = ConsoleUtils.prompt("Reps/Count (if applicable)");
    w.reps = reps.isEmpty() ? null : Integer.valueOf(reps);
    
    String weight = ConsoleUtils.prompt("Weight/Resistance (kg) - if applicable");
    w.weightKg = weight.isEmpty() ? null : Double.valueOf(weight);
    
    String duration = ConsoleUtils.prompt("Duration (minutes)");
    w.durationMin = duration.isEmpty() ? null : Integer.valueOf(duration);
    
    String distance = ConsoleUtils.prompt("Distance (km) - if applicable");
    w.distKm = distance.isEmpty() ? null : Double.valueOf(distance);
}


    private static void listSessions() throws Exception {
        BeautifulConsole.printHeader("WORKOUT SESSIONS");
        long userId = ConsoleUtils.promptInt("User ID");
        List<WorkoutSession> sessions = workoutDAO.listSessionsForUser(userId);
        BeautifulConsole.printWorkoutSessions(sessions);
    }

    private static void addNutrition() throws Exception {
        BeautifulConsole.printHeader("ADD NUTRITION LOG");
        NutritionLog n = new NutritionLog();
        n.userId   = (long) ConsoleUtils.promptInt("User ID");
        n.logDate  = Date.valueOf(ConsoleUtils.prompt("Date (yyyy-mm-dd)"));
        n.mealType = ConsoleUtils.prompt("Meal type (Breakfast/Lunch/Dinner/Snack)");
        n.item     = ConsoleUtils.prompt("Food item");
        String cal = ConsoleUtils.prompt("Calories (blank to skip)");
        n.calories = cal.isEmpty() ? null : Integer.valueOf(cal);
        String p = ConsoleUtils.prompt("Protein g (blank)");
        n.proteinG = p.isEmpty() ? null : Double.valueOf(p);
        String c = ConsoleUtils.prompt("Carbs g (blank)");
        n.carbsG = c.isEmpty() ? null : Double.valueOf(c);
        String f = ConsoleUtils.prompt("Fat g (blank)");
        n.fatG = f.isEmpty() ? null : Double.valueOf(f);
        
        BeautifulConsole.showLoading("Adding nutrition log");
        long id = nutritionDAO.create(n);
        BeautifulConsole.printSuccess("Created LOG_ID = " + id);
    }

    private static void listNutrition() throws Exception {
        BeautifulConsole.printHeader("NUTRITION LOG");
        long userId = ConsoleUtils.promptInt("User ID");
        java.sql.Date d = Date.valueOf(ConsoleUtils.prompt("Date (yyyy-mm-dd)"));
        List<NutritionLog> list = nutritionDAO.listForUserOnDate(userId, d);
        
        if (list.isEmpty()) {
            BeautifulConsole.printWarning("No nutrition logs found for this date!");
            return;
        }
        
        System.out.println(BeautifulConsole.BOLD + BeautifulConsole.BRIGHT_YELLOW + "\n🍎 NUTRITION LOG FOR " + d + BeautifulConsole.RESET);
        System.out.println(BeautifulConsole.YELLOW + "┌─────┬────────────┬──────────────────────┬─────────┬─────────┬─────────┬─────────┐" + BeautifulConsole.RESET);
        System.out.println(BeautifulConsole.YELLOW + "│ ID  │ Meal Type  │ Item                 │ Cal     │ Protein │ Carbs   │ Fat     │" + BeautifulConsole.RESET);
        System.out.println(BeautifulConsole.YELLOW + "├─────┼────────────┼──────────────────────┼─────────┼─────────┼─────────┼─────────┤" + BeautifulConsole.RESET);
        
        for (NutritionLog n : list) {
            System.out.printf(BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %-3d " +
                BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %-10s " +
                BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %-20s " +
                BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7s " +
                BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7.1fg " +
                BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7.1fg " +
                BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7.1fg " +
                BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + "%n",
                n.logId,
                n.mealType,
                truncate(n.item, 20),
                n.calories == null ? "N/A" : n.calories.toString(),
                n.proteinG == null ? 0 : n.proteinG,
                n.carbsG == null ? 0 : n.carbsG,
                n.fatG == null ? 0 : n.fatG);
        }
        System.out.println(BeautifulConsole.YELLOW + "└─────┴────────────┴──────────────────────┴─────────┴─────────┴─────────┴─────────┘" + BeautifulConsole.RESET);
    }

    // Helper methods (add these to your Main class)
    private static boolean isStepBasedExercise(String exerciseName) {
        String name = exerciseName.toLowerCase();
        return name.contains("walk") || name.contains("run") || name.contains("jog") || 
               name.contains("step") || name.contains("treadmill");
    }

    private static String determineActivityType(String exerciseName) {
        String name = exerciseName.toLowerCase();
        if (name.contains("run")) return "RUNNING";
        if (name.contains("jog")) return "JOGGING";
        return "WALKING";
    }

    private static StepMetrics calculateStepMetrics(int steps, String activityType, double userWeight, Integer durationMin) {
        if (durationMin == null || durationMin <= 0) {
            BeautifulConsole.printWarning("Duration required for accurate calculations");
            return null;
        }
        

        
        try {
            return workoutDAO.calculateStepMetrics(steps, activityType, userWeight, durationMin);
        } catch (Exception e) {
            BeautifulConsole.printError("Error calculating metrics: " + e.getMessage());
            return null;
        }
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}