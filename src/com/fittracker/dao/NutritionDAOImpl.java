package com.fittracker.dao;

import com.fittracker.db.Database;
import com.fittracker.model.NutritionLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NutritionDAOImpl implements NutritionDAO {

    @Override
    public long create(NutritionLog n) throws SQLException {
        String sql = "INSERT INTO NUTRITION_LOG (USER_ID, LOG_DATE, MEAL_TYPE, ITEM, CALORIES, PROTEIN_G, CARBS_G, FAT_G) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"LOG_ID"})) {
            ps.setLong(1, n.userId);
            ps.setDate(2, n.logDate);
            ps.setString(3, n.mealType);
            ps.setString(4, n.item);
            if (n.calories != null) ps.setInt(5, n.calories); else ps.setNull(5, Types.INTEGER);
            if (n.proteinG != null) ps.setDouble(6, n.proteinG); else ps.setNull(6, Types.NUMERIC);
            if (n.carbsG != null) ps.setDouble(7, n.carbsG); else ps.setNull(7, Types.NUMERIC);
            if (n.fatG != null) ps.setDouble(8, n.fatG); else ps.setNull(8, Types.NUMERIC);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    @Override
    public List<NutritionLog> listForUserOnDate(long userId, java.sql.Date date) throws SQLException {
        String sql = "SELECT LOG_ID, USER_ID, LOG_DATE, MEAL_TYPE, ITEM, CALORIES, PROTEIN_G, CARBS_G, FAT_G " +
                     "FROM NUTRITION_LOG WHERE USER_ID=? AND LOG_DATE=? ORDER BY LOG_ID";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                List<NutritionLog> list = new ArrayList<>();
                while (rs.next()) {
                    NutritionLog n = new NutritionLog();
                    n.logId    = rs.getLong(1);
                    n.userId   = rs.getLong(2);
                    n.logDate  = rs.getDate(3);
                    n.mealType = rs.getString(4);
                    n.item     = rs.getString(5);
                    int cal    = rs.getInt(6);
                    n.calories = rs.wasNull() ? null : cal;
                    n.proteinG = rs.getDouble(7); if (rs.wasNull()) n.proteinG = null;
                    n.carbsG   = rs.getDouble(8); if (rs.wasNull()) n.carbsG = null;
                    n.fatG     = rs.getDouble(9); if (rs.wasNull()) n.fatG = null;
                    list.add(n);
                }
                return list;
            }
        }
    }

    @Override
    public boolean updateCalories(long logId, int calories) throws SQLException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE NUTRITION_LOG SET CALORIES=? WHERE LOG_ID=?")) {
            ps.setInt(1, calories);
            ps.setLong(2, logId);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long logId) throws SQLException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM NUTRITION_LOG WHERE LOG_ID=?")) {
            ps.setLong(1, logId);
            return ps.executeUpdate() == 1;
        }
    }
}
