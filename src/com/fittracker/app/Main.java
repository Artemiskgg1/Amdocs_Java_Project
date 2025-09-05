package com.fittracker.app;

import com.fittracker.dao.*;
import com.fittracker.model.*;
import com.fittracker.util.ConsoleUtils;

import java.sql.Date;
import java.util.List;

public class Main {

    private static final UserDAO userDAO = new UserDAO();
    private static final ExerciseDAO exerciseDAO = new ExerciseDAO();
    private static final WorkoutDAO workoutDAO = new WorkoutDAO();
    private static final NutritionDAO nutritionDAO = new NutritionDAO();

    public static void main(String[] args) {
        System.out.println("=== Fitness Tracker (Console, Oracle) ===");
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1) Create User");
            System.out.println("2) List Users");
            System.out.println("3) Update User email");
            System.out.println("4) Delete User");
            System.out.println("5) Add Exercise");
            System.out.println("6) List Exercises");
            System.out.println("7) Create Workout Session");
            System.out.println("8) Add Set to Session");
            System.out.println("9) List Sessions for User");
            System.out.println("10) Add Nutrition Log");
            System.out.println("11) List Nutrition for User+Date");
            System.out.println("0) Exit");
            int ch = ConsoleUtils.promptInt("Choose");
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
                    case 0 -> { System.out.println("Bye!"); return; }
                    default -> System.out.println("Invalid.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace(System.out);
            }
        }
    }

    private static void createUser() throws Exception {
        User u = new User();
        u.fullName = ConsoleUtils.prompt("Full name");
        u.email    = ConsoleUtils.prompt("Email");
        u.gender   = ConsoleUtils.prompt("Gender (M/F/O)");
        String dob = ConsoleUtils.prompt("DOB (yyyy-mm-dd or blank)");
        u.dob      = dob.isEmpty() ? null : Date.valueOf(dob);
        String h   = ConsoleUtils.prompt("Height cm (blank to skip)");
        u.heightCm = h.isEmpty() ? null : Double.valueOf(h);
        long id = userDAO.create(u);
        System.out.println("Created USER_ID = " + id);
    }

    private static void listUsers() throws Exception {
        List<User> users = userDAO.listAll();
        for (User u : users) {
            System.out.printf("[%d] %s | %s | %s | DOB=%s | H=%.2fcm%n",
                u.userId, u.fullName, u.email, u.gender,
                String.valueOf(u.dob), u.heightCm == null ? 0 : u.heightCm);
        }
    }

    private static void updateUserEmail() throws Exception {
        long id = ConsoleUtils.promptInt("User ID");
        String email = ConsoleUtils.prompt("New email");
        User u = userDAO.findById(id);
        if (u == null) { System.out.println("Not found"); return; }
        u.email = email;
        if (userDAO.update(u)) System.out.println("Updated.");
    }

    private static void deleteUser() throws Exception {
        long id = ConsoleUtils.promptInt("User ID");
        if (userDAO.delete(id)) System.out.println("Deleted (cascade removes sessions/logs).");
    }

    private static void addExercise() throws Exception {
        Exercise e = new Exercise();
        e.name     = ConsoleUtils.prompt("Exercise name");
        e.category = ConsoleUtils.prompt("Category (Strength/Cardio/...)");
        e.unit     = ConsoleUtils.prompt("Unit (reps/minutes/km/kg/...)");
        String m   = ConsoleUtils.prompt("MET value (blank to skip)");
        e.metValue = m.isEmpty() ? null : Double.valueOf(m);
        long id = exerciseDAO.create(e);
        System.out.println("Created EXERCISE_ID = " + id);
    }

    private static void listExercises() throws Exception {
        for (Exercise e : exerciseDAO.listAll()) {
            System.out.printf("[%d] %s | %s | %s | MET=%s%n",
                e.exerciseId, e.name, e.category, e.unit,
                e.metValue == null ? "null" : e.metValue.toString());
        }
    }

    private static void createSession() throws Exception {
        WorkoutSession s = new WorkoutSession();
        s.userId      = (long) ConsoleUtils.promptInt("User ID");
        String d      = ConsoleUtils.prompt("Session date (yyyy-mm-dd)");
        s.sessionDate = Date.valueOf(d);
        String dur    = ConsoleUtils.prompt("Duration minutes (blank to skip)");
        s.durationMin = dur.isEmpty() ? null : Integer.valueOf(dur);
        s.notes       = ConsoleUtils.prompt("Notes (blank allowed)");
        long id = workoutDAO.createSession(s);
        System.out.println("Created SESSION_ID = " + id);
    }

    private static void addSet() throws Exception {
        WorkoutSet w = new WorkoutSet();
        w.sessionId  = (long) ConsoleUtils.promptInt("Session ID");
        w.exerciseId = (long) ConsoleUtils.promptInt("Exercise ID");
        w.setNo      = ConsoleUtils.promptInt("Set no");
        String reps  = ConsoleUtils.prompt("Reps (blank if NA)");
        w.reps       = reps.isEmpty() ? null : Integer.valueOf(reps);
        String kg    = ConsoleUtils.prompt("Weight kg (blank if NA)");
        w.weightKg   = kg.isEmpty() ? null : Double.valueOf(kg);
        String dm    = ConsoleUtils.prompt("Duration min (blank if NA)");
        w.durationMin= dm.isEmpty() ? null : Integer.valueOf(dm);
        String dk    = ConsoleUtils.prompt("Distance km (blank if NA)");
        w.distKm     = dk.isEmpty() ? null : Double.valueOf(dk);
        workoutDAO.addSet(w);
        System.out.println("Set added.");
    }

    private static void listSessions() throws Exception {
        long userId = ConsoleUtils.promptInt("User ID");
        for (WorkoutSession s : workoutDAO.listSessionsForUser(userId)) {
            System.out.printf("Session [%d] %s dur=%s notes=%s%n",
                s.sessionId, s.sessionDate.toString(),
                s.durationMin == null ? "null" : s.durationMin.toString(), s.notes);
        }
    }

    private static void addNutrition() throws Exception {
        NutritionLog n = new NutritionLog();
        n.userId   = (long) ConsoleUtils.promptInt("User ID");
        n.logDate  = Date.valueOf(ConsoleUtils.prompt("Date (yyyy-mm-dd)"));
        n.mealType = ConsoleUtils.prompt("Meal type");
        n.item     = ConsoleUtils.prompt("Item");
        String cal = ConsoleUtils.prompt("Calories (blank to skip)");
        n.calories = cal.isEmpty() ? null : Integer.valueOf(cal);
        String p = ConsoleUtils.prompt("Protein g (blank)");
        n.proteinG = p.isEmpty() ? null : Double.valueOf(p);
        String c = ConsoleUtils.prompt("Carbs g (blank)");
        n.carbsG = c.isEmpty() ? null : Double.valueOf(c);
        String f = ConsoleUtils.prompt("Fat g (blank)");
        n.fatG = f.isEmpty() ? null : Double.valueOf(f);
        long id = nutritionDAO.create(n);
        System.out.println("Created LOG_ID = " + id);
    }

    private static void listNutrition() throws Exception {
        long userId = ConsoleUtils.promptInt("User ID");
        java.sql.Date d = Date.valueOf(ConsoleUtils.prompt("Date (yyyy-mm-dd)"));
        List<NutritionLog> list = nutritionDAO.listForUserOnDate(userId, d);
        for (NutritionLog n : list) {
            System.out.printf("[%d] %s %s %s cal=%s P=%.2f C=%.2f F=%.2f%n",
                n.logId, n.logDate.toString(), n.mealType, n.item,
                n.calories == null ? "null" : n.calories.toString(),
                n.proteinG == null ? 0 : n.proteinG,
                n.carbsG == null ? 0 : n.carbsG,
                n.fatG == null ? 0 : n.fatG);
        }
    }
}
