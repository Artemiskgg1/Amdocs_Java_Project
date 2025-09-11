package com.fittracker.model;

public class ExerciseMetrics {

    public String exerciseName;
    public String category;
    public String unit;
    public double caloriesBurned;
    public double volume; // For strength: weight × reps
    public String volumeUnit;
    public Double oneRepMax; // Estimated 1RM for strength
    public Double distance; // For cardio
    public String distanceUnit;
    public Double avgSpeed; // For cardio
    public String speedUnit;
    public Double pace; // For cardio
    public String paceUnit;
    public Double estimatedAvgHeartRate; // For cardio
    public String intensity; // Low/Moderate/High/Maximum
    public String additionalInfo; // Extra calculated info

    public ExerciseMetrics() {
        // Initialize with default values
        this.caloriesBurned = 0;
        this.volume = 0;
        this.volumeUnit = "";
        this.distanceUnit = "";
        this.speedUnit = "";
        this.paceUnit = "";
        this.intensity = "";
        this.additionalInfo = "";
    }

    @Override
    public String toString() {
        return String.format("ExerciseMetrics{exercise='%s', category='%s', calories=%.1f, intensity='%s'}",
                exerciseName, category, caloriesBurned, intensity);
    }
}
