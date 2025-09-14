import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import org.json.*;

/**
 * Servlet for retrieving items from the database
 */
@WebServlet("/GetItemsServlet")
public class GetItemsServlet extends HttpServlet {
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/student_hub";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Aditya@123"; // Use your actual database password
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Add CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // Set content type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JSONArray items = new JSONArray();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Get database connection directly
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Query to get all items with user information
            String query = 
                "SELECT i.id, i.item_name, i.item_type, i.price, i.image_url, " +
                "i.contact_info, i.payment_method, i.upi_id, i.account_number, i.bank_name, " +
                "u.email " +
                "FROM uploaded_items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +  // Proper join using user_id
                "ORDER BY i.id DESC";

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("id", rs.getInt("id"));
                item.put("name", rs.getString("item_name"));
                item.put("itemType", rs.getString("item_type"));
                item.put("price", rs.getDouble("price"));
                item.put("imageUrl", rs.getString("image_url"));
                item.put("contact", rs.getString("contact_info"));
                item.put("paymentMethod", rs.getString("payment_method"));
                
                // Add payment details based on payment method
                String paymentMethod = rs.getString("payment_method");
                if ("upi".equals(paymentMethod)) {
                    item.put("upiId", rs.getString("upi_id"));
                } else if ("bank_transfer".equals(paymentMethod)) {
                    item.put("accountNumber", rs.getString("account_number"));
                    item.put("bankName", rs.getString("bank_name"));
                }
                
                // Get email from the users table
                String email = rs.getString("email");
                if (email != null) {
                    item.put("username", email);
                } else {
                    item.put("username", "Unknown Seller");
                }
                
                items.put(item);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            getServletContext().log("Database error in GetItemsServlet: " + e.getMessage(), e);
            
            // Send error in JSON format
            JSONObject errorObj = new JSONObject();
            errorObj.put("error", "Database error: " + e.getMessage());
            out.write(errorObj.toString());
            return;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                getServletContext().log("Error closing resources: " + e.getMessage(), e);
            }
        }

        // Write response
        out.write(items.toString());
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}