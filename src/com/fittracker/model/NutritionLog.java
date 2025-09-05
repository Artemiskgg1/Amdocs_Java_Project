package com.fittracker.model;
public class NutritionLog {
    public Long   logId;
    public Long   userId;
    public java.sql.Date logDate;
    public String mealType; // Breakfast/Lunch/Dinner/Snack
    public String item;
    public Integer calories;
    public Double proteinG, carbsG, fatG;
}