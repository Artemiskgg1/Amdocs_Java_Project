package com.fittracker.dao;

import com.fittracker.model.NutritionLog;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface NutritionDAO {

    long create(NutritionLog n) throws SQLException;

    List<NutritionLog> listForUserOnDate(long userId, Date date) throws SQLException;

}
