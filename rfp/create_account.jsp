<%@ page import="java.sql.*" %>
<%@ page import="javax.servlet.http.*, javax.servlet.*" %>
<%
    String email = request.getParameter("email");
    String password = request.getParameter("new-password");
    String confirmPassword = request.getParameter("confirm-password");

    if (email == null || password == null || confirmPassword == null) {
        out.println("Missing form data.");
        return;
    }

    if (!email.endsWith("@cvr.ac.in")) {
        out.println("Only @cvr.ac.in emails are allowed.");
        return;
    }

    if (!password.equals(confirmPassword)) {
        out.println("Passwords do not match.");
        return;
    }

    if (password.length() < 8) {
        out.println("Password must be at least 8 characters.");
        return;
    }

    Connection conn = null;
    PreparedStatement stmt = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_hub", "root", ""); // Update password if set

        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        stmt.setString(2, password); // Note: You should hash passwords in real projects!

        int rows = stmt.executeUpdate();
        if (rows > 0) {
            response.sendRedirect("login.html");
        } else {
            out.println("Failed to create account. Try again.");
        }

    } catch (SQLIntegrityConstraintViolationException e) {
        out.println("This email is already registered.");
    } catch (Exception e) {
        e.printStackTrace(out);
    } finally {
        if (stmt != null) try { stmt.close(); } catch (Exception e) {}
        if (conn != null) try { conn.close(); } catch (Exception e) {}
    }
%>
