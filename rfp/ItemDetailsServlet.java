import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;
import org.json.*;

@WebServlet("/ItemDetailsServlet")
public class ItemDetailsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("id");
        
        if (itemId == null || itemId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item ID is required");
            return;
        }
        
        JSONObject itemDetails = new JSONObject();
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/student_hub", "postgres", "Aditya@123");
            
            String sql = "SELECT * FROM uploaded_items WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(itemId));
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                itemDetails.put("id", rs.getInt("id"));
                itemDetails.put("name", rs.getString("item_name"));
                itemDetails.put("type", rs.getString("item_type"));
                itemDetails.put("price", rs.getDouble("price"));
                itemDetails.put("imageUrl", rs.getString("image_url"));
                itemDetails.put("contact", rs.getString("contact_info"));
                itemDetails.put("paymentMethod", rs.getString("payment_method"));
                // Add other fields as needed
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
            return;
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(itemDetails.toString());
    }
}