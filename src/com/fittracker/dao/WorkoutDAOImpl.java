package com.fittracker.dao;

import com.fittracker.db.Database;
import com.fittracker.model.WorkoutSession;
import com.fittracker.model.WorkoutSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDAOImpl implements WorkoutDAO {

    @Override
    public long createSession(WorkoutSession s) throws SQLException {
        try (Connection c = Database.getConnection(); CallableStatement cs = c.prepareCall("{ call CREATE_SESSION(?, ?, ?, ?, ?) }")) {
            cs.setLong(1, s.userId);
            cs.setDate(2, s.sessionDate);
            if (s.durationMin != null) {
                cs.setInt(3, s.durationMin);
            } else {
                cs.setNull(3, Types.INTEGER);
            }
            cs.setString(4, s.notes);
            cs.registerOutParameter(5, Types.NUMERIC);
            cs.execute();
            return cs.getLong(5);
        }
    }

    @Override
    public void addSet(WorkoutSet w) throws SQLException {
        try (Connection c = Database.getConnection(); CallableStatement cs = c.prepareCall("{ call ADD_SET(?, ?, ?, ?, ?, ?, ?, ?) }")) {
            cs.setLong(1, w.sessionId);
            cs.setLong(2, w.exerciseId);
            cs.setInt(3, w.setNo);
            if (w.reps != null) {
                cs.setInt(4, w.reps);
            } else {
                cs.setNull(4, Types.INTEGER);
            }
            if (w.weightKg != null) {
                cs.setDouble(5, w.weightKg);
            } else {
                cs.setNull(5, Types.NUMERIC);
            }
            if (w.durationMin != null) {
                cs.setInt(6, w.durationMin);
            } else {
                cs.setNull(6, Types.INTEGER);
            }
            if (w.distKm != null) {
                cs.setDouble(7, w.distKm);
            } else {
                cs.setNull(7, Types.NUMERIC);
            }
            if (w.steps != null) {
                cs.setInt(8, w.steps);
            } else {
                cs.setNull(8, Types.INTEGER);
            }
            cs.execute();
        }
    }

    @Override
    public List<WorkoutSession> listSessionsForUser(long userId) throws SQLException {
        String sql = "SELECT SESSION_ID, USER_ID, SESSION_DATE, DURATION_MIN, NOTES "
                + "FROM WORKOUT_SESSION WHERE USER_ID=? ORDER BY SESSION_DATE DESC, SESSION_ID DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<WorkoutSession> list = new ArrayList<>();
                while (rs.next()) {
                    WorkoutSession s = new WorkoutSession();
                    s.sessionId = rs.getLong(1);
                    s.userId = rs.getLong(2);
                    s.sessionDate = rs.getDate(3);
                    int d = rs.getInt(4);
                    s.durationMin = rs.wasNull() ? null : d;
                    s.notes = rs.getString(5);
                    list.add(s);
                }
                return list;
            }
        }
    }

    @Override
    public boolean updateSessionDuration(long sessionId, int duration) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE WORKOUT_SESSION SET DURATION_MIN=? WHERE SESSION_ID=?")) {
            ps.setInt(1, duration);
            ps.setLong(2, sessionId);
            return ps.executeUpdate() == 1;
        }
    }

}
