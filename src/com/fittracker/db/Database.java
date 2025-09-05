package com.fittracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL  = "jdbc:oracle:thin:@localhost:1521/xepdb1";
    private static final String USER = "FITTRACK";
    private static final String PASS = "Fittrack#123";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle JDBC driver not found in classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
