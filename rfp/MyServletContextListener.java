import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@WebListener
public class MyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Optional: initialize resources if needed
        System.out.println(">>> contextInitialized() CALLED <<<");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> contextDestroyed() CALLED <<<");

        // Get all registered JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                // Deregister the driver
                DriverManager.deregisterDriver(driver);
                System.out.println("Deregistered JDBC driver: " + driver);
            } catch (SQLException e) {
                System.err.println("Error deregistering driver: " + driver);
                e.printStackTrace();
            }
        }
    }
}
