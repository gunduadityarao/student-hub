<%@ page import="java.sql.*, org.mindrot.bcrypt.BCrypt" %>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    String dbURL = "jdbc:mysql://localhost:3306/student_hub";  // Replace with your database name
    String dbUser = "root"; // Replace with your database username
    String dbPassword = "Aditya@123"; // Replace with your database password

    // Check if the form fields are filled
    if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isValidUser = false;

        try {
            // Connect to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            // Check if the user exists in the database
            String sql = "SELECT * FROM users WHERE email = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // User found, check password using BCrypt
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    // Password is correct, check role
                    String role = rs.getString("role");
                    isValidUser = true;
                    // Start session and redirect based on role
                    session.setAttribute("user", email);
                    session.setAttribute("role", role);

                    if ("admin".equals(role)) {
                        response.sendRedirect("admin.html");  // Redirect to admin page
                    } else {
                        response.sendRedirect("home.html");  // Redirect to student page
                    }
                } else {
                    out.println("Invalid login credentials.");  // Invalid password
                }
            } else {
                out.println("Invalid login credentials.");  // Invalid email
            }
        } catch (Exception e) {
            log("Error: " + e.getMessage());  // Log error details for debugging
            out.println("An error occurred, please try again later.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    } else {
        out.println("Please enter your email and password.");
    }
%>
