package com.fittracker.dao;

import com.fittracker.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    long create(User u) throws SQLException;
    User findById(long id) throws SQLException;
    List<User> listAll() throws SQLException;
    boolean update(User u) throws SQLException;
    boolean delete(long id) throws SQLException;
}
