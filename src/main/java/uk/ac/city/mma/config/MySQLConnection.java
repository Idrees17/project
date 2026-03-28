package uk.ac.city.mma.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/mma_gym";

    private static final String USER = "mma_app";
    private static final String PASSWORD = "mma_password";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

