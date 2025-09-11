package com.fittracker.util;

import com.fittracker.model.*;

public class ExerciseMetricsCalculator {

    // Comprehensive metric calculation for ANY exercise type
    public static ExerciseMetrics calculateMetrics(Exercise exercise, WorkoutSet workoutSet, double userWeight) {
        ExerciseMetrics metrics = new ExerciseMetrics();

        metrics.exerciseName = exercise.name;
        metrics.category = exercise.category;
        metrics.unit = exercise.unit;

        switch (exercise.category.toUpperCase()) {
            case "STRENGTH" ->
                calculateStrengthMetrics(metrics, exercise, workoutSet, userWeight);
            case "CARDIO" ->
                calculateCardioMetrics(metrics, exercise, workoutSet, userWeight);
            case "FLEXIBILITY" ->
                calculateFlexibilityMetrics(metrics, exercise, workoutSet, userWeight);
            case "BALANCE" ->
                calculateBalanceMetrics(metrics, exercise, workoutSet, userWeight);
            default ->
                calculateGeneralMetrics(metrics, exercise, workoutSet, userWeight);
        }

        return metrics;
    }

    private static void calculateStrengthMetrics(ExerciseMetrics metrics, Exercise exercise,
            WorkoutSet set, double userWeight) {
        // Volume calculation (weight × reps)
        if (set.weightKg != null && set.reps != null) {
            metrics.volume = set.weightKg * set.reps;
            metrics.volumeUnit = "kg";
        }

        // Calorie estimation for strength training
        double metValue = exercise.metValue != null ? exercise.metValue : getDefaultStrengthMET(exercise.name);
        if (set.durationMin != null) {
            metrics.caloriesBurned = metValue * userWeight * (set.durationMin / 60.0);
        } else {
            // Estimate duration based on reps (assuming 2-3 seconds per rep + rest)
            int estimatedDuration = set.reps != null ? (set.reps * 3) / 60 : 5; // Convert to minutes
            metrics.caloriesBurned = metValue * userWeight * (estimatedDuration / 60.0);
        }

        // One Rep Max estimation using Epley formula
        if (set.weightKg != null && set.reps != null && set.reps <= 15) {
            metrics.oneRepMax = set.weightKg * (1 + (set.reps / 30.0));
        }

        // Intensity classification
        if (set.weightKg != null && metrics.oneRepMax != null) {
            double intensityPercentage = (set.weightKg / metrics.oneRepMax) * 100;
            if (intensityPercentage >= 90) {
                metrics.intensity = "Maximum (90%+ 1RM)";
            } else if (intensityPercentage >= 80) {
                metrics.intensity = "High (80-89% 1RM)";
            } else if (intensityPercentage >= 65) {
                metrics.intensity = "Moderate (65-79% 1RM)";
            } else {
                metrics.intensity = "Low (<65% 1RM)";
            }
        }

        metrics.additionalInfo = String.format("Volume: %.1f %s | Estimated 1RM: %.1f kg",
                metrics.volume, metrics.volumeUnit, metrics.oneRepMax != null ? metrics.oneRepMax : 0);
    }

    private static void calculateCardioMetrics(ExerciseMetrics metrics, Exercise exercise,
            WorkoutSet set, double userWeight) {
        double metValue = exercise.metValue != null ? exercise.metValue : getDefaultCardioMET(exercise.name);

        // Handle step-based exercises
        if (set.steps != null && set.durationMin != null) {
            String activityType = determineActivityType(exercise.name);
            double stepsPerKm = getStepsPerKm(activityType);

            metrics.distance = set.steps / stepsPerKm;
            metrics.distanceUnit = "km";
            metrics.avgSpeed = metrics.distance / (set.durationMin / 60.0);
            metrics.speedUnit = "km/h";
            metrics.pace = (set.durationMin / 60.0) / metrics.distance; // hours per km
            metrics.paceUnit = "hr/km";
        } // Handle distance-based exercises
        else if (set.distKm != null && set.durationMin != null) {
            metrics.distance = set.distKm;
            metrics.distanceUnit = "km";
            metrics.avgSpeed = set.distKm / (set.durationMin / 60.0);
            metrics.speedUnit = "km/h";
            metrics.pace = set.durationMin / set.distKm; // minutes per km
            metrics.paceUnit = "min/km";
        }

        // Calorie calculation
        if (set.durationMin != null) {
            metrics.caloriesBurned = metValue * userWeight * (set.durationMin / 60.0);
        }

        // Heart rate zones (estimated)
        double maxHR = 220 - 25; // Assuming average age of 25
        metrics.estimatedAvgHeartRate = (metValue / 3.5) * maxHR * 0.7; // Rough estimation

        // Classify cardio intensity
        if (metValue >= 9) {
            metrics.intensity = "Vigorous (>9 METs)";
        } else if (metValue >= 6) {
            metrics.intensity = "Moderate-Vigorous (6-9 METs)";
        } else if (metValue >= 3) {
            metrics.intensity = "Moderate (3-6 METs)";
        } else {
            metrics.intensity = "Light (<3 METs)";
        }

        metrics.additionalInfo = String.format("Pace: %.2f %s | Est. HR: %.0f bpm | Steps: %d",
                metrics.pace, metrics.paceUnit, metrics.estimatedAvgHeartRate,
                set.steps != null ? set.steps : 0);
    }

    private static void calculateFlexibilityMetrics(ExerciseMetrics metrics, Exercise exercise,
            WorkoutSet set, double userWeight) {
        double metValue = exercise.metValue != null ? exercise.metValue : 2.5; // Default for flexibility

        if (set.durationMin != null) {
            metrics.caloriesBurned = metValue * userWeight * (set.durationMin / 60.0);
        }

        metrics.intensity = "Low-Moderate";
        metrics.additionalInfo = "Focus on mobility and range of motion";
    }

    private static void calculateBalanceMetrics(ExerciseMetrics metrics, Exercise exercise,
            WorkoutSet set, double userWeight) {
        double metValue = exercise.metValue != null ? exercise.metValue : 2.0; // Default for balance

        if (set.durationMin != null) {
            metrics.caloriesBurned = metValue * userWeight * (set.durationMin / 60.0);
        }

        metrics.intensity = "Low";
        metrics.additionalInfo = "Stability and proprioception training";
    }

    private static void calculateGeneralMetrics(ExerciseMetrics metrics, Exercise exercise,
            WorkoutSet set, double userWeight) {
        double metValue = exercise.metValue != null ? exercise.metValue : 4.0; // Default general

        if (set.durationMin != null) {
            metrics.caloriesBurned = metValue * userWeight * (set.durationMin / 60.0);
        }

        metrics.intensity = "Moderate";
        metrics.additionalInfo = "General fitness activity";
    }

    // Helper methods for default MET values
    private static double getDefaultStrengthMET(String exerciseName) {
        String name = exerciseName.toLowerCase();
        if (name.contains("deadlift") || name.contains("squat")) {
            return 6.0;
        }
        if (name.contains("bench") || name.contains("press")) {
            return 5.0;
        }
        if (name.contains("curl") || name.contains("extension")) {
            return 4.5;
        }
        if (name.contains("pullup") || name.contains("chinup")) {
            return 8.0;
        }
        if (name.contains("pushup")) {
            return 3.8;
        }
        return 5.0; // Default strength training
    }

    private static double getDefaultCardioMET(String exerciseName) {
        String name = exerciseName.toLowerCase();
        if (name.contains("running") || name.contains("sprint")) {
            return 9.8;
        }
        if (name.contains("jogging")) {
            return 7.0;
        }
        if (name.contains("walking")) {
            return 3.5;
        }
        if (name.contains("cycling") || name.contains("bike")) {
            return 6.8;
        }
        if (name.contains("swimming")) {
            return 8.0;
        }
        if (name.contains("rowing")) {
            return 7.0;
        }
        if (name.contains("elliptical")) {
            return 5.0;
        }
        if (name.contains("stairs") || name.contains("climbing")) {
            return 8.5;
        }
        return 6.0; // Default cardio
    }

    private static String determineActivityType(String exerciseName) {
        String name = exerciseName.toLowerCase();
        if (name.contains("run") || name.contains("sprint")) {
            return "RUNNING";
        }
        if (name.contains("jog")) {
            return "JOGGING";
        }
        return "WALKING";
    }

    private static double getStepsPerKm(String activityType) {
        return switch (activityType) {
            case "RUNNING" ->
                1200.0;
            case "JOGGING" ->
                1250.0;
            case "WALKING" ->
                1312.0;
            default ->
                1300.0;
        };
    }
}
