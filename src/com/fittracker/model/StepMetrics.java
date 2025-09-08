package com.fittracker.model;

public class StepMetrics {
    public int stepsCount;
    public double distanceKm;
    public double caloriesBurned;
    public double metValue;
    public double avgSpeedKmh;
    
    public StepMetrics() {}
    
    public StepMetrics(int stepsCount, double distanceKm, double caloriesBurned, 
                      double metValue, double avgSpeedKmh) {
        this.stepsCount = stepsCount;
        this.distanceKm = distanceKm;
        this.caloriesBurned = caloriesBurned;
        this.metValue = metValue;
        this.avgSpeedKmh = avgSpeedKmh;
    }
}