import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ListDemographicInfo {
    private static final String DB_URL = "jdbc:db2://localhost:50000/testdb";
    private static final String USER = "db2inst1";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM DemographicInfo");
             PreparedStatement insertStmt = conn.prepareStatement(
                     "INSERT INTO DemographicInfo (collectedOn, id, numFullTimeWorkers, publicDebt, intragovernmentalDebt) VALUES (?, ?, ?, ?, ?)");
             PreparedStatement selectStmt = conn.prepareStatement(
                     "SELECT numFullTimeWorkers FROM DemographicInfo WHERE collectedOn = ?")) {

            Class.forName("com.ibm.db2.jcc.DB2Driver");
            System.out.println("DB2 JDBC Driver loaded successfully.");
            System.out.println("Connected to the database successfully.");

            // Step 1: Delete all entries in the DemographicInfo table
            System.out.println("Deleting all entries in the DemographicInfo table...");
            deleteStmt.executeUpdate();

            // Step 2: Insert entries into the DemographicInfo table
            System.out.println("Inserting new entries into the DemographicInfo table...");
            ZoneId ET = ZoneId.of("America/New_York");
            Instant when = ZonedDateTime.of(2022, 4, 29, 12, 0, 0, 0, ET).toInstant();
            insertDemographicInfo(insertStmt, when, 2022, 132250000, new BigDecimal("6526909395140.41"), new BigDecimal("23847245116757.60"));

            Instant anotherWhen = ZonedDateTime.of(2007, 4, 30, 12, 0, 0, 0, ET).toInstant();
            insertDemographicInfo(insertStmt, anotherWhen, 2007, 121090000, new BigDecimal("3833110332444.19"), new BigDecimal("5007058051986.64"));

            System.out.println("Inserted DemographicInfo entries successfully.");

            // Step 3: Execute the select query multiple times and verify results
            List<AssertionError> errors = new ArrayList<>();
            selectStmt.setTimestamp(1, Timestamp.from(when));

            for (int i = 0; i < 10; i++) {
                System.out.println("Executing SELECT query, iteration: " + i);

                List<BigInteger> results = new ArrayList<>();
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getBigDecimal("numFullTimeWorkers").toBigInteger());
                    }

                    if (results.isEmpty()) {
                        errors.add(new AssertionError("Query should not have returned an empty list after iteration " + i));
                    } else if (results.size() > 1) {
                        errors.add(new AssertionError("Query should not have returned more than one result after iteration " + i));
                    } else if (!results.get(0).equals(BigInteger.valueOf(132250000))) {
                        errors.add(new AssertionError("Expected numFullTimeWorkers of 132250000 but found " + results.get(0)));
                    }
                }
            }

            if (!errors.isEmpty()) {
                System.out.println("Test failed with " + errors.size() + " errors out of 10 executions.");
                throw new AssertionError("Executing the same query returned incorrect results " + errors.size() + " out of 10 executions", errors.get(0));
            } else {
                System.out.println("Test passed for all 10 iterations.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("DB2 JDBC Driver not found. Include the driver in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL exception occurred while connecting to or querying the DB2 database.");
            e.printStackTrace();
        }
    }

    private static void insertDemographicInfo(PreparedStatement stmt, Instant collectedOn, int id, int numFullTimeWorkers, BigDecimal publicDebt, BigDecimal intragovernmentalDebt) throws SQLException {
        stmt.setTimestamp(1, Timestamp.from(collectedOn));
        stmt.setInt(2, id);
        stmt.setBigDecimal(3, BigDecimal.valueOf(numFullTimeWorkers));
        stmt.setBigDecimal(4, publicDebt);
        stmt.setBigDecimal(5, intragovernmentalDebt);
        stmt.executeUpdate();
    }
}
