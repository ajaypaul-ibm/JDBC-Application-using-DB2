import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListTallPackages {

    // JDBC URL for connecting to the DB2 database
    private static final String DB_URL = "jdbc:db2://localhost:50000/testdb";
    // Database username
    private static final String USER = "db2inst1";
    // Database password
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Load the DB2 JDBC driver
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            System.out.println("DB2 JDBC Driver loaded successfully.");

            // Establish a connection to the database
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully.");

            // 1. Delete all entries in the Package table
            System.out.println("Deleting all entries in the Package table...");
            String sqlDelete = "DELETE FROM Package";
            preparedStatement = connection.prepareStatement(sqlDelete);
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println(rowsDeleted + " rows deleted.");

            // 2. Insert specific entries into the Package table
            System.out.println("Inserting new entries into the Package table...");
            String sqlInsert = "INSERT INTO Package (id, height, length, width, description) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sqlInsert);

            // Insert data for each package
            addPackage(preparedStatement, 70071, 17.0f, 17.1f, 7.7f, "Should be Second");
            addPackage(preparedStatement, 70077, 77.0f, 17.7f, 7.7f, "Should be Third");
            addPackage(preparedStatement, 70007, 70.0f, 10.7f, 0.7f, "Should not be returned");
            addPackage(preparedStatement, 70073, 77.0f, 17.7f, 7.8f, "Should be First");
            addPackage(preparedStatement, 70076, 104.0f, 17.7f, 7.7f, "Should be Fourth");

            // Execute batch insert
            preparedStatement.executeBatch();

            // 3. List all packages
            System.out.println("\nListing all packages...");
            String sqlAll = "SELECT * FROM Package";
            preparedStatement = connection.prepareStatement(sqlAll);
            resultSet = preparedStatement.executeQuery();
            List<Package> allPackages = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                float height = resultSet.getFloat("height");
                float length = resultSet.getFloat("length");
                float width = resultSet.getFloat("width");
                String description = resultSet.getString("description");
                Package pkg = Package.of(id, height, length);
                pkg.setWidth(width);
                pkg.setDescription(description);
                allPackages.add(pkg);
            }

            // Print all packages
            System.out.println("ID | Height | Length | Width | Description");
            System.out.println("---|--------|--------|-------|-------------");
            for (Package pkg : allPackages) {
                System.out.println("id :" + pkg.getId()+" height :"+pkg.getHeight()+"description:"+pkg.getDescription());
            }
            
            

            // 4. Retrieve and print ordered packages
            // System.out.println("\nRetrieving ordered packages...");
            // String sqlOrdered = "SELECT * FROM Package WHERE height < ? ORDER BY height DESC, length";
            // preparedStatement = connection.prepareStatement(sqlOrdered);
            // preparedStatement.setDouble(1, 8.0); // Example parameter
            // preparedStatement.setMaxRows(4);
            // resultSet = preparedStatement.executeQuery();
            // List<Package> orderedPackages = new ArrayList<>();
            // while (resultSet.next()) {
            //     int id = resultSet.getInt("id");
            //     float height = resultSet.getFloat("height");
            //     float length = resultSet.getFloat("length");
            //     float width = resultSet.getFloat("width");
            //     String description = resultSet.getString("description");
            //     Package pkg = Package.of(id, height, length);
            //     pkg.setWidth(width);
            //     pkg.setDescription(description);
            //     orderedPackages.add(pkg);
            // }

            // // Print ordered packages
            // System.out.println("\nID | Height | Length | Width | Description");
            // System.out.println("---|--------|--------|-------|-------------");
            // for (Package pkg : orderedPackages) {
            //     System.out.println("id :" + pkg.getId()+" height :"+pkg.getHeight()+"description:"+pkg.getDescription());
            // }
            System.out.println("\nRetrieving ordered packages with row limits...");

// Modified SQL query with row limits and locks
String sqlComplexOrdered = 
    "SELECT * FROM ( " +
    "  SELECT * FROM ( " +
    "    SELECT EL_TEMP.*, ROWNUMBER() OVER() AS EL_ROWNM " +
    "    FROM ( " +
    "      SELECT ID AS a1, DESCRIPTION AS a2, HEIGHT AS a3, LENGTH AS a4, WIDTH AS a5 " +
    "      FROM PACKAGE " +
    "      WHERE HEIGHT < ? " +
    "      ORDER BY HEIGHT DESC, LENGTH " +
    "    ) AS EL_TEMP " +
    "  ) AS EL_TEMP2 WHERE EL_ROWNM <= ? " +
    ") AS EL_TEMP3 WHERE EL_ROWNM > ? " +
    "FOR READ ONLY WITH RS USE AND KEEP UPDATE LOCKS";

preparedStatement = connection.prepareStatement(sqlComplexOrdered);

// Set the bind parameters
preparedStatement.setDouble(1, 8.0); // Example parameter for HEIGHT < 8.0
preparedStatement.setInt(2, 2);      // Upper limit for ROWNUM (EL_ROWNM <= 2)
preparedStatement.setInt(3, 0);      // Lower limit for ROWNUM (EL_ROWNM > 0)

preparedStatement.setMaxRows(4); // Optional, limiting the total number of rows returned

resultSet = preparedStatement.executeQuery();

// Processing the result set
while (resultSet.next()) {
    int id = resultSet.getInt("a1");
    String description = resultSet.getString("a2");
    float height = resultSet.getFloat("a3");
    float length = resultSet.getFloat("a4");
    float width = resultSet.getFloat("a5");
    
    System.out.println("ID: " + id + ", Description: " + description + 
                       ", Height: " + height + ", Length: " + length + 
                       ", Width: " + width);
}

        } catch (ClassNotFoundException e) {
            System.err.println("DB2 JDBC Driver not found. Include the driver in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL exception occurred while connecting to or querying the DB2 database.");
            e.printStackTrace();
        } finally {
            // Ensure resources are closed properly
            try {
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                System.err.println("Failed to close the result set.");
                e.printStackTrace();
            }

            try {
                if (preparedStatement != null && !preparedStatement.isClosed()) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                System.err.println("Failed to close the prepared statement.");
                e.printStackTrace();
            }

            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Failed to close the database connection.");
                e.printStackTrace();
            }
        }
    }
    private static void addPackage(PreparedStatement preparedStatement, int id,float length,float width, float height,String description) throws SQLException {
        preparedStatement.setInt(1, id);
        preparedStatement.setFloat(2, height);
        preparedStatement.setFloat(3, length);
        preparedStatement.setFloat(4, width);
        preparedStatement.setString(5, description);
        preparedStatement.addBatch();
    }
}
