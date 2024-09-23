import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class demonstrates a simple Java application for checking
 * a connection to a DB2 database using JDBC (Java Database Connectivity).
 * 
 * The class:
 * - Loads the DB2 JDBC driver.
 * - Attempts to establish a connection to the DB2 database using
 * the provided URL, username, and password.
 * - Verifies if the connection is successful and outputs the result.
 * - Handles exceptions related to driver loading and SQL issues.
 * - Ensures that the database connection is closed after the operation.
 */

public class Db2JdbcTest {

    
    // JDBC URL for connecting to the DB2 database
    private static final String JDBC_URL = "jdbc:db2://localhost:50000/testdb";
    // Database username
    private static final String USERNAME = "db2inst1";
    // Database password
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Load the DB2 JDBC driver
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // Establishing a connection to DB2
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            // Check if the connection is valid
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection to DB2 database is successful.");
            } else {
                System.out.println("Failed to connect to DB2 database.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("DB2 JDBC Driver not found. Include the driver in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL exception occurred while trying to connect to the DB2 database.");
            e.printStackTrace();
        } finally {
            // Close the connection
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Failed to close the connection.");
                e.printStackTrace();
            }
        }

    }
}