import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        // Basic validation
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required");
            request.getRequestDispatcher("/login.html").forward(request, response);
            return;
        }

        email = email.trim();
        password = password.trim();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            // Establish a connection to the database
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/student_hub", "postgres", "Aditya@123");

            // Handle special usernames with role selection
            if (email.equals("23B81A05K3@cvr.ac.in") ||
                    email.equals("23B81A05K9@cvr.ac.in") ||
                    email.equals("23B81A05M8@cvr.ac.in")) {

                // Use a predefined password for special users
                if ("admin123".equals(password)) {
                    // Set session attributes
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", 0); // Special ID for admin
                    session.setAttribute("userEmail", email);
                    session.setAttribute("userRole", role != null ? role : "admin");

                    // Redirect based on role
                    if (role == null || "admin".equals(role)) {
                        response.sendRedirect(request.getContextPath() + "/admin.html");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/home.html");
                    }
                    return;
                } else {
                    request.setAttribute("errorMessage", "Invalid credentials for special user");
                    request.getRequestDispatcher("/login.html").forward(request, response);
                    return;
                }
            }

            ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String storedPassword = rs.getString("password");
                String userRole = rs.getString("role");

                // Check password
                boolean passwordMatches;
                if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
                    passwordMatches = BCrypt.checkpw(password, storedPassword);
                } else {
                    passwordMatches = password.equals(storedPassword);
                }

                if (passwordMatches) {
                    // Set session attributes
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", userId);
                    session.setAttribute("userEmail", email);
                    session.setAttribute("userRole", userRole);

                    // Debug log
                    System.out.println("Login successful for: " + email);

                    // Redirect based on role
                    if ("admin".equals(userRole)) {
                        response.sendRedirect(request.getContextPath() + "/admin.html");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/home.html");
                    }
                } else {
                    request.setAttribute("errorMessage", "Invalid email or password");
                    request.getRequestDispatcher("/login.html").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Email not found");
                request.getRequestDispatcher("/login.html").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Server error: " + e.getMessage());
            request.getRequestDispatcher("/login.html").forward(request, response);
        } finally {
            // Close all resources
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}