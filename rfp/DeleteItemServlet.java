import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/DeleteItemServlet")
public class DeleteItemServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("id");
        
        if (itemId == null || itemId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item ID is required");
            return;
        }
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/student_hub", "postgres", "Aditya@123");
            
            String sql = "DELETE FROM uploaded_items WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(itemId));
            
            int rowsAffected = stmt.executeUpdate();
            
            stmt.close();
            conn.close();
            
            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Item deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Item not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }
}