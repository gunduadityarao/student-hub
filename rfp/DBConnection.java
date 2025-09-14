import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection initializeDatabase() throws SQLException {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Set up the database connection
String url = "jdbc:postgresql://localhost:5432/student_hub";
String user = "postgres";
String password = "Aditya@123";


            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new SQLException("Database connection failed", e);
        }
    }
}
