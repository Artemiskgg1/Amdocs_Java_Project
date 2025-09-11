package com.fittracker.dao;

import com.fittracker.model.WorkoutSession;
import com.fittracker.model.WorkoutSet;
import java.sql.SQLException;
import java.util.List;

public interface WorkoutDAO {

    long createSession(WorkoutSession s) throws SQLException;

    void addSet(WorkoutSet w) throws SQLException;

    List<WorkoutSession> listSessionsForUser(long userId) throws SQLException;

    boolean updateSessionDuration(long sessionId, int duration) throws SQLException;

}
