import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = "student";  // Default role is "student"

        // Optionally, if you want to set the role based on some condition, you can retrieve it from the request:
        // String role = request.getParameter("role");

        try {
            Connection conn = DBConnection.initializeDatabase();

            // Check if email already exists
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                response.getWriter().println("❌ Registration failed: Email already exists.");
            } else {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users (email, password, role) VALUES (?, ?, ?)");
                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, role);  // Set the role here
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    response.sendRedirect("login.html");
                } else {
                    response.getWriter().println("❌ Registration failed. Please try again.");
                }

                ps.close();
            }

            checkStmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("❌ Registration failed: " + e.getMessage());
        }
    }
}
