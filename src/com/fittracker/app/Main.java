package com.fittracker.app;

import com.fittracker.dao.*;
import com.fittracker.model.*;
import com.fittracker.util.BeautifulConsole;
import com.fittracker.util.ConsoleUtils;
import com.fittracker.util.ConsoleUtils.GoBackException;
import com.fittracker.util.ExerciseMetricsCalculator;
import java.io.IOException;
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
            int ch = ConsoleUtils.promptMenuChoice("");
            try {
                switch (ch) {
                    case 1 ->
                        createUser();
                    case 2 ->
                        listUsers();
                    case 3 ->
                        updateUserEmail();
                    case 4 ->
                        deleteUser();
                    case 5 ->
                        addExercise();
                    case 6 ->
                        listExercises();
                    case 7 ->
                        createSession();
                    case 8 ->
                        addSet();
                    case 9 ->
                        listSessions();
                    case 10 ->
                        addNutrition();
                    case 11 ->
                        listNutrition();
                    case 0 -> {
                        BeautifulConsole.printInfo("Thanks for using Fitness Tracker! Stay fit!");
                        return;
                    }
                    default ->
                        BeautifulConsole.printError("Invalid choice. Please try again.");
                }

                if (ch >= 1 && ch <= 11) {
                    System.out.print("\nPress Enter to continue...");
                    System.in.read();
                }

            } catch (GoBackException e) {
                BeautifulConsole.printInfo("Returning to main menu...");
                // Continue to next iteration of main loop
            } catch (Exception e) {
                BeautifulConsole.printError(e.getMessage());
                e.printStackTrace(System.out);
                try {
                    System.out.print("\nPress Enter to continue...");
                    System.in.read();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void createUser() throws Exception {
        BeautifulConsole.printHeader("CREATE NEW USER");
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        User u = new User();
        u.fullName = ConsoleUtils.promptName("Full name");
        u.email = ConsoleUtils.promptEmail("Email", false);
        u.gender = ConsoleUtils.promptGender("Gender (M/F/O)");
        String dob = ConsoleUtils.promptDateString("DOB (yyyy-mm-dd or blank)", true);
        u.dob = dob.isEmpty() ? null : java.sql.Date.valueOf(dob);
        u.heightCm = ConsoleUtils.promptHeightCm("Height cm (blank to skip)", true);
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
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        long id = ConsoleUtils.promptInt("User ID");

        User u = userDAO.findById(id);
        if (u == null) {
            BeautifulConsole.printError("User not found");
            return;
        }
        String email = ConsoleUtils.promptEmail("New email", false);
        u.email = email;

        if (userDAO.update(u)) {
            BeautifulConsole.printSuccess("Email updated successfully!");
        }
    }

    private static void deleteUser() throws Exception {
        BeautifulConsole.printHeader("DELETE USER");
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

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
        BeautifulConsole.printHeader("ADD EXERCISE");
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        String password = ConsoleUtils.prompt("Enter admin password to continue");
        if (!"12345".equals(password)) {
            BeautifulConsole.printError("Access denied. Only admin can add exercises.");
            return;
        }

        Exercise e = new Exercise();
        e.name = ConsoleUtils.prompt("Exercise name");
        e.category = ConsoleUtils.prompt("Category (Strength/Cardio/Flexibility/Balance/Other)");
        e.unit = ConsoleUtils.prompt("Unit (reps/minutes/km/kg/calories/meters/other)");
        String m = ConsoleUtils.prompt("MET value (blank to skip)");
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
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        WorkoutSession s = new WorkoutSession();

        while (true) {
            long enteredId = (long) ConsoleUtils.promptInt("User ID");
            if (!userDAO.existsById(enteredId)) {
                BeautifulConsole.printError("User ID " + enteredId + " does not exist. Try again.");
                continue;
            }
            s.userId = enteredId;
            break;
        }

        s.sessionDate = ConsoleUtils.promptSessionDate("Session date (yyyy-mm-dd)");

        s.durationMin = ConsoleUtils.promptDuration("Duration minutes (blank to skip)", true);

        s.notes = ConsoleUtils.prompt("Notes (blank allowed)");

        BeautifulConsole.showLoading("Creating workout session");
        long id = workoutDAO.createSession(s);
        BeautifulConsole.printSuccess("Created SESSION_ID = " + id);
    }

    private static void addSet() throws Exception {
        BeautifulConsole.printHeader("ADD SET TO SESSION");
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        WorkoutSet w = new WorkoutSet();
        w.sessionId = (long) ConsoleUtils.promptInt("Session ID");
        w.exerciseId = (long) ConsoleUtils.promptInt("Exercise ID");
        w.setNo = ConsoleUtils.promptInt("Set Number");

        Exercise exercise = exerciseDAO.findById(w.exerciseId);
        if (exercise == null) {
            BeautifulConsole.printError("Exercise not found!");
            return;
        }

        BeautifulConsole.printInfo("Exercise: " + exercise.name + " (" + exercise.category + ")");
        BeautifulConsole.printSeparator();

        String weightInput = ConsoleUtils.prompt("Your body weight in kg (for calculations, default 70)");
        double userWeight = weightInput.isEmpty() ? 70.0 : Double.valueOf(weightInput);

        switch (exercise.category.toUpperCase()) {
            case "STRENGTH" ->
                collectStrengthData(w);
            case "CARDIO" ->
                collectCardioData(w, exercise);
            case "FLEXIBILITY", "BALANCE" ->
                collectFlexibilityBalanceData(w);
            default ->
                collectGeneralData(w);
        }

        BeautifulConsole.showLoading("Calculating comprehensive metrics");
        ExerciseMetrics metrics = ExerciseMetricsCalculator.calculateMetrics(exercise, w, userWeight);

        BeautifulConsole.printExerciseMetrics(metrics);

        String confirm = ConsoleUtils.prompt("Save this set? (Y/n)");
        if (confirm.isEmpty() || confirm.toLowerCase().startsWith("y")) {
            BeautifulConsole.showLoading("Adding set to session");
            workoutDAO.addSet(w);
            BeautifulConsole.printSuccess("Set added with comprehensive metrics!");
        } else {
            BeautifulConsole.printInfo("Set cancelled");
        }
    }

    private static void collectStrengthData(WorkoutSet w) throws GoBackException {
        BeautifulConsole.printInfo("STRENGTH EXERCISE - Enter your performance data:");

        String reps = ConsoleUtils.prompt("Reps performed");
        w.reps = reps.isEmpty() ? null : Integer.valueOf(reps);

        String weight = ConsoleUtils.prompt("Weight used (kg)");
        w.weightKg = weight.isEmpty() ? null : Double.valueOf(weight);

        String duration = ConsoleUtils.prompt("Rest time between sets (min) - optional");
        w.durationMin = duration.isEmpty() ? null : Integer.valueOf(duration);

        if (w.weightKg == null) {
            BeautifulConsole.printInfo("Tip: For bodyweight exercises, enter your body weight for accurate calculations");
        }
    }

    private static void collectCardioData(WorkoutSet w, Exercise exercise) throws GoBackException {
        BeautifulConsole.printInfo("CARDIO EXERCISE - Enter your activity data:");

        if (isStepBasedExercise(exercise.name)) {
            String steps = ConsoleUtils.prompt("Steps count (if tracked)");
            w.steps = steps.isEmpty() ? null : Integer.valueOf(steps);
        }

        String distance = ConsoleUtils.prompt("Distance covered (km) - optional");
        w.distKm = distance.isEmpty() ? null : Double.valueOf(distance);

        String duration = ConsoleUtils.prompt("Duration (minutes)");
        w.durationMin = duration.isEmpty() ? null : Integer.valueOf(duration);

        String reps = ConsoleUtils.prompt("Intervals/rounds (if applicable)");
        w.reps = reps.isEmpty() ? null : Integer.valueOf(reps);
    }

    private static void collectFlexibilityBalanceData(WorkoutSet w) throws GoBackException {
        BeautifulConsole.printInfo("FLEXIBILITY/BALANCE EXERCISE - Enter duration:");

        String duration = ConsoleUtils.prompt("Duration (minutes)");
        w.durationMin = duration.isEmpty() ? null : Integer.valueOf(duration);

        String reps = ConsoleUtils.prompt("Repetitions/Holds (if applicable)");
        w.reps = reps.isEmpty() ? null : Integer.valueOf(reps);
    }

    private static void collectGeneralData(WorkoutSet w) throws GoBackException {
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
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        long userId = ConsoleUtils.promptInt("User ID");
        List<WorkoutSession> sessions = workoutDAO.listSessionsForUser(userId);
        BeautifulConsole.printWorkoutSessions(sessions);
    }

    private static void addNutrition() throws Exception {
        BeautifulConsole.printHeader("ADD NUTRITION LOG");
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        NutritionLog n = new NutritionLog();
        n.userId = (long) ConsoleUtils.promptInt("User ID");
        n.logDate = Date.valueOf(ConsoleUtils.prompt("Date (yyyy-mm-dd)"));
        n.mealType = ConsoleUtils.prompt("Meal type (Breakfast/Lunch/Dinner/Snack)");
        n.item = ConsoleUtils.prompt("Food item");
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
        BeautifulConsole.printInfo("Tip: Type 'q' at any prompt to return to main menu");
        BeautifulConsole.printSeparator();

        long userId = ConsoleUtils.promptInt("User ID");
        java.sql.Date d = Date.valueOf(ConsoleUtils.prompt("Date (yyyy-mm-dd)"));
        List<NutritionLog> list = nutritionDAO.listForUserOnDate(userId, d);

        if (list.isEmpty()) {
            BeautifulConsole.printWarning("No nutrition logs found for this date!");
            return;
        }

        System.out.println(BeautifulConsole.BOLD + BeautifulConsole.BRIGHT_YELLOW + "\nNUTRITION LOG FOR " + d + BeautifulConsole.RESET);
        System.out.println(BeautifulConsole.YELLOW + "┌─────┬────────────┬──────────────────────┬─────────┬─────────┬─────────┬─────────┐" + BeautifulConsole.RESET);
        System.out.println(BeautifulConsole.YELLOW + "│ ID  │ Meal Type  │ Item                 │ Cal     │ Protein │ Carbs   │ Fat     │" + BeautifulConsole.RESET);
        System.out.println(BeautifulConsole.YELLOW + "├─────┼────────────┼──────────────────────┼─────────┼─────────┼─────────┼─────────┤" + BeautifulConsole.RESET);

        for (NutritionLog n : list) {
            System.out.printf(BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %-3d "
                    + BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %-10s "
                    + BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %-20s "
                    + BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7s "
                    + BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7.1fg "
                    + BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7.1fg "
                    + BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + " %7.1fg "
                    + BeautifulConsole.YELLOW + "│" + BeautifulConsole.RESET + "%n",
                    n.logId,
                    n.mealType,
                    truncate(n.item, 20),
                    n.calories == null ? "N/A" : n.calories.toString(),
                    n.proteinG == null ? 0.0 : n.proteinG,
                    n.carbsG == null ? 0.0 : n.carbsG,
                    n.fatG == null ? 0.0 : n.fatG);
        }
        System.out.println(BeautifulConsole.YELLOW + "└─────┴────────────┴──────────────────────┴─────────┴─────────┴─────────┴─────────┘" + BeautifulConsole.RESET);
    }

    private static boolean isStepBasedExercise(String exerciseName) {
        String name = exerciseName.toLowerCase();
        return name.contains("walk") || name.contains("run") || name.contains("jog")
                || name.contains("step") || name.contains("treadmill");
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}
