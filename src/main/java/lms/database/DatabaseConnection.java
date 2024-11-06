package lms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {

    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/your_database_name";
        String username = "root";  // replace with your MySQL username
        String password = "password";  // replace with your MySQL password
        return DriverManager.getConnection(url, username, password);
    }
}
