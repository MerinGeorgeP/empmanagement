package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static final String URL = "jdbc:mysql://localhost:3306/payroll_db";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC driver not found. Put mysql-connector-j.jar in lib/.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
