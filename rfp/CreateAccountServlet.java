package handler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/createAccount")
public class CreateAccountServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("new-password");
        String confirmPassword = request.getParameter("confirm-password");

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            response.getWriter().println("Passwords do not match.");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Establish database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studenthub", "root", "Aditya@123");

            // Prepare the SQL statement to insert the new user
            String query = "INSERT INTO users (email, password) VALUES (?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);  // In a production environment, you should hash the password

            // Execute the statement
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect("login.html");  // Redirect to login page after successful account creation
            } else {
                response.getWriter().println("Error creating account. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error creating account. Try again later.");
        } finally {
            // Close the resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
