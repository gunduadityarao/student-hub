import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/ItemUploadServlet")
public class ItemUploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        // Get user info from session
        HttpSession session = request.getSession(false);
        String username = null;
        Integer userId = null;
        
        if (session != null) {
            username = (String) session.getAttribute("userEmail");
            userId = (Integer) session.getAttribute("userId");
            System.out.println("Session found. User email: " + username + ", User ID: " + userId);
        } else {
            System.out.println("No session found.");
        }
        
        // If no session, try to get from request parameter
        if (username == null || username.isEmpty()) {
            username = request.getParameter("username");
            System.out.println("Using username from request parameter: " + username);
        }
        
        String itemName = request.getParameter("itemName");
        String itemType = request.getParameter("itemType");
        String contactInfo = request.getParameter("contactInfo");
        String paymentMethod = request.getParameter("paymentMethod");
        String upiId = request.getParameter("upiId");
        String accountNumber = request.getParameter("accountNumber");
        String bankName = request.getParameter("bankName");
        String image_url = request.getParameter("image_url");
        
        if (image_url != null && !image_url.isEmpty()) {
            // Check if URL ends with an image extension
            if (!image_url.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)($|\\?.*)")) {
                // Not a direct image URL
                response.sendRedirect("uploaditem.html?error=invalid_image_url");
                return;
            }
        }
        
        double price = 0.0;
        try {
            price = Double.parseDouble(request.getParameter("price"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid price format");
            return;
        }

        // Check if any required fields are missing
        if (itemName == null || itemType == null || contactInfo == null || paymentMethod == null || image_url == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
            return;
        }
        
        // Require username to be present
        if (username == null || username.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required. Please log in before uploading.");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement userStmt = null;
        ResultSet rs = null;
        
        try {
            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            // Establish a connection to the database
            conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/student_hub", "postgres", "Aditya@123");
            
            // Use userId from session if available, otherwise look up by email
            int finalUserId = -1;
            
            if (userId != null && userId > 0) {
                finalUserId = userId;
                System.out.println("Using user ID from session: " + finalUserId);
            } else {
                // Find user ID based on the provided username (email)
                String userQuery = "SELECT id FROM users WHERE email = ?";
                userStmt = conn.prepareStatement(userQuery);
                userStmt.setString(1, username);
                rs = userStmt.executeQuery();
                
                if (rs.next()) {
                    finalUserId = rs.getInt("id");
                    System.out.println("Found user with ID: " + finalUserId + " for email: " + username);
                } else {
                    // User not found in database
                    System.out.println("ERROR: User with email " + username + " not found in database.");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                        "User with email " + username + " not found in database. Please log in with a valid account.");
                    return;
                }
            }

            // Prepare the SQL insert statement with user_id
            String sql = "INSERT INTO uploaded_items (item_name, item_type, price, image_url, contact_info, payment_method, upi_id, account_number, bank_name, user_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, itemName);
            stmt.setString(2, itemType);
            stmt.setDouble(3, price);
            stmt.setString(4, image_url);
            stmt.setString(5, contactInfo);
            stmt.setString(6, paymentMethod);
            stmt.setString(7, upiId != null ? upiId : null);
            stmt.setString(8, accountNumber != null ? accountNumber : null);
            stmt.setString(9, bankName != null ? bankName : null);
            stmt.setInt(10, finalUserId); // Set the user_id from session or database lookup

            // Execute the SQL insert statement
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Item inserted successfully. Rows affected: " + rowsAffected);

            // Send a successful response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Item uploaded successfully");
        } catch (Exception e) {
            // Log the exception to the server logs
            System.out.println("Error in ItemUploadServlet: " + e.getMessage());
            e.printStackTrace();
            
            // Send a detailed error message to the client
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        } finally {
            // Close all resources properly
            try {
                if (rs != null) rs.close();
                if (userStmt != null) userStmt.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}