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
    
    // Getter and Setter methods (optional, for better encapsulation)
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(double caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    
    public String getVolumeUnit() { return volumeUnit; }
    public void setVolumeUnit(String volumeUnit) { this.volumeUnit = volumeUnit; }
    
    public Double getOneRepMax() { return oneRepMax; }
    public void setOneRepMax(Double oneRepMax) { this.oneRepMax = oneRepMax; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public String getDistanceUnit() { return distanceUnit; }
    public void setDistanceUnit(String distanceUnit) { this.distanceUnit = distanceUnit; }
    
    public Double getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(Double avgSpeed) { this.avgSpeed = avgSpeed; }
    
    public String getSpeedUnit() { return speedUnit; }
    public void setSpeedUnit(String speedUnit) { this.speedUnit = speedUnit; }
    
    public Double getPace() { return pace; }
    public void setPace(Double pace) { this.pace = pace; }
    
    public String getPaceUnit() { return paceUnit; }
    public void setPaceUnit(String paceUnit) { this.paceUnit = paceUnit; }
    
    public Double getEstimatedAvgHeartRate() { return estimatedAvgHeartRate; }
    public void setEstimatedAvgHeartRate(Double estimatedAvgHeartRate) { this.estimatedAvgHeartRate = estimatedAvgHeartRate; }
    
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    
    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    
    @Override
    public String toString() {
        return String.format("ExerciseMetrics{exercise='%s', category='%s', calories=%.1f, intensity='%s'}", 
            exerciseName, category, caloriesBurned, intensity);
    }
}