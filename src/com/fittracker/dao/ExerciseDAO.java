package com.fittracker.dao;

import com.fittracker.db.Database;
import com.fittracker.model.Exercise;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDAO {

    public long create(Exercise e) throws SQLException {
        String sql = "INSERT INTO EXERCISE(NAME, CATEGORY, UNIT, MET_VALUE) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"EXERCISE_ID"})) {
            ps.setString(1, e.name);
            ps.setString(2, e.category);
            ps.setString(3, e.unit);
            if (e.metValue != null) ps.setDouble(4, e.metValue); else ps.setNull(4, Types.NUMERIC);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next(); return rs.getLong(1);
            }
        }
    }

    public List<Exercise> listAll() throws SQLException {
        String sql = "SELECT EXERCISE_ID, NAME, CATEGORY, UNIT, MET_VALUE FROM EXERCISE ORDER BY NAME";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Exercise> list = new ArrayList<>();
            while (rs.next()) {
                Exercise e = new Exercise();
                e.exerciseId = rs.getLong(1);
                e.name       = rs.getString(2);
                e.category   = rs.getString(3);
                e.unit       = rs.getString(4);
                double m     = rs.getDouble(5);
                e.metValue   = rs.wasNull() ? null : m;
                list.add(e);
            }
            return list;
        }
    }

    public boolean update(Exercise e) throws SQLException {
        String sql = "UPDATE EXERCISE SET NAME=?, CATEGORY=?, UNIT=?, MET_VALUE=? WHERE EXERCISE_ID=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.name);
            ps.setString(2, e.category);
            ps.setString(3, e.unit);
            if (e.metValue != null) ps.setDouble(4, e.metValue); else ps.setNull(4, Types.NUMERIC);
            ps.setLong(5, e.exerciseId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM EXERCISE WHERE EXERCISE_ID=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // Add this method to your ExerciseDAO.java class

public Exercise findById(long id) throws SQLException {
    String sql = "SELECT EXERCISE_ID, NAME, CATEGORY, UNIT, MET_VALUE FROM EXERCISE WHERE EXERCISE_ID = ?";
    try (Connection c = Database.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setLong(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Exercise e = new Exercise();
                e.exerciseId = rs.getLong("EXERCISE_ID");
                e.name       = rs.getString("NAME");
                e.category   = rs.getString("CATEGORY");
                e.unit       = rs.getString("UNIT");
                double m     = rs.getDouble("MET_VALUE");
                e.metValue   = rs.wasNull() ? null : m;
                return e;
            }
            return null;
        }
    }
}
}
