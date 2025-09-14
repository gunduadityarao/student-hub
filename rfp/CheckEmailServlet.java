import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

@WebServlet("/check-email")
public class CheckEmailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        response.setContentType("text/plain");

        // Check if email parameter is present
        if (email == null || email.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid email parameter.");
            return;
        }

        // Handle special usernames directly
        if (email.equals("23B81A05K3@cvr.ac.in") ||
                email.equals("23B81A05K9@cvr.ac.in") ||
                email.equals("23B81A05M8@cvr.ac.in")) {
            response.getWriter().println("exists");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Load the PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            // Create the connection
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/student_hub",
                    "postgres",
                    "Aditya@123"
            );

            // SQL query to check if the email exists
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);

            // Execute the query and process the result
            rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // If email exists, send response 'exists'
                response.getWriter().println("exists");
            } else {
                // If email does not exist, send response 'not exists'
                response.getWriter().println("not exists");
            }
        } catch (ClassNotFoundException e) {
            // Log the error
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error: JDBC Driver not found");
        } catch (SQLException e) {
            // Log and return an error response if an exception occurs
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error checking email: " + e.getMessage());
        } finally {
            // Close all resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}