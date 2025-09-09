package com.fittracker.dao;

import com.fittracker.model.Exercise;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseDAO {
    long create(Exercise e) throws SQLException;
    Exercise findById(long id) throws SQLException;
    List<Exercise> listAll() throws SQLException;
    boolean update(Exercise e) throws SQLException;
    boolean delete(long id) throws SQLException;
}
