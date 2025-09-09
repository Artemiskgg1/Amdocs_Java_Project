package com.fittracker.dao;

import com.fittracker.model.NutritionLog;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public interface NutritionDAO {

    long create(NutritionLog n) throws SQLException;

    List<NutritionLog> listForUserOnDate(long userId, Date date) throws SQLException;

    boolean updateCalories(long logId, int calories) throws SQLException;

    boolean delete(long logId) throws SQLException;
}
