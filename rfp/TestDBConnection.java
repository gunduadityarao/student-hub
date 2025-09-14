import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/student_hub", "postgres", "Aditya@123"
            );
            System.out.println("✅ Connection successful!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Connection failed:");
            e.printStackTrace();
        }
    }
}
