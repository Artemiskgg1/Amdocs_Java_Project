package com.fittracker.dao;

import com.fittracker.db.Database;
import com.fittracker.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
@Override
    public long create(User u) throws SQLException {
        String sql = "INSERT INTO USERS (FULL_NAME, EMAIL, GENDER, DOB, HEIGHT_CM) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"USER_ID"})) {
            ps.setString(1, u.fullName);
            ps.setString(2, u.email);
            ps.setString(3, u.gender);
            if (u.dob != null) ps.setDate(4, u.dob); else ps.setNull(4, Types.DATE);
            if (u.heightCm != null) ps.setDouble(5, u.heightCm); else ps.setNull(5, Types.NUMERIC);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    @Override
    public User findById(long id) throws SQLException {
        String sql = "SELECT USER_ID, FULL_NAME, EMAIL, GENDER, DOB, HEIGHT_CM FROM USERS WHERE USER_ID = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.userId   = rs.getLong("USER_ID");
                    u.fullName = rs.getString("FULL_NAME");
                    u.email    = rs.getString("EMAIL");
                    u.gender   = rs.getString("GENDER");
                    u.dob      = rs.getDate("DOB");
                    double h   = rs.getDouble("HEIGHT_CM");
                    u.heightCm = rs.wasNull() ? null : h;
                    return u;
                }
                return null;
            }
        }
    }

    @Override
    public List<User> listAll() throws SQLException {
        String sql = "SELECT USER_ID, FULL_NAME, EMAIL, GENDER, DOB, HEIGHT_CM FROM USERS ORDER BY USER_ID";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> list = new ArrayList<>();
            while (rs.next()) {
                User u = new User();
                u.userId   = rs.getLong("USER_ID");
                u.fullName = rs.getString("FULL_NAME");
                u.email    = rs.getString("EMAIL");
                u.gender   = rs.getString("GENDER");
                u.dob      = rs.getDate("DOB");
                double h   = rs.getDouble("HEIGHT_CM");
                u.heightCm = rs.wasNull() ? null : h;
                list.add(u);
            }
            return list;
        }
    }

    @Override
    public boolean update(User u) throws SQLException {
        String sql = "UPDATE USERS SET FULL_NAME=?, EMAIL=?, GENDER=?, DOB=?, HEIGHT_CM=? WHERE USER_ID=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.fullName);
            ps.setString(2, u.email);
            ps.setString(3, u.gender);
            if (u.dob != null) ps.setDate(4, u.dob); else ps.setNull(4, Types.DATE);
            if (u.heightCm != null) ps.setDouble(5, u.heightCm); else ps.setNull(5, Types.NUMERIC);
            ps.setLong(6, u.userId);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM USERS WHERE USER_ID=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        }
    }
}
