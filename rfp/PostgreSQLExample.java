import java.sql.*;

public class PostgreSQLExample {
    public static void main(String[] args) {
        try {
            // Connect to the database
            String url = "jdbc:postgresql://localhost:5432/student_hub";
            String user = "postgres";
            String password = "Aditya@123";
            Connection conn = DriverManager.getConnection(url, user, password);
            
            // Create a statement object
            Statement stmt = conn.createStatement();
            
            // Inserting sample data
            String insertQuery = "INSERT INTO users (email, password) VALUES ('user@example.com', 'password789')";
            stmt.executeUpdate(insertQuery);
            
            // Retrieving data
            String selectQuery = "SELECT * FROM users;";
            ResultSet rs = stmt.executeQuery(selectQuery);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String pass = rs.getString("password");
                
                System.out.println("ID: " + id + ", Email: " + email + ", Password: " + pass);
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
