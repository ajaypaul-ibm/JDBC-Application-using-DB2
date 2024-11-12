

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DemographicInfo {

    public ZonedDateTime collectedOn;
    public BigInteger id;
    public BigDecimal publicDebt;
    public BigDecimal intragovernmentalDebt;
    public BigInteger numFullTimeWorkers;

    // Static factory method for creating a new DemographicInfo instance
    public static DemographicInfo of(int year, int month, int day,
                                     long numFullTimeWorkers,
                                     double intragovernmentalDebt, double publicDebt) {
        DemographicInfo inst = new DemographicInfo();
        inst.collectedOn = ZonedDateTime.of(year, month, day, 12, 0, 0, 0, ZoneId.of("America/New_York"));
        inst.numFullTimeWorkers = BigInteger.valueOf(numFullTimeWorkers);
        inst.intragovernmentalDebt = BigDecimal.valueOf(intragovernmentalDebt);
        inst.publicDebt = BigDecimal.valueOf(publicDebt);
        return inst;
    }

    // Save instance to database
    public void saveToDatabase(Connection connection) throws SQLException {
        String sql = "INSERT INTO DemographicInfo (collectedOn, publicDebt, intragovernmentalDebt, numFullTimeWorkers) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.from(collectedOn.toInstant()));
            stmt.setBigDecimal(2, publicDebt);
            stmt.setBigDecimal(3, intragovernmentalDebt);
            stmt.setBigDecimal(4, new BigDecimal(numFullTimeWorkers));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.id = BigInteger.valueOf(generatedKeys.getLong(1));
                    }
                }
            }
        }
    }

    // Load instance from database by ID
    public static DemographicInfo loadFromDatabase(Connection connection, BigInteger id) throws SQLException {
        String sql = "SELECT collectedOn, publicDebt, intragovernmentalDebt, numFullTimeWorkers FROM DemographicInfo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, new BigDecimal(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DemographicInfo info = new DemographicInfo();
                    info.id = id;
                    info.collectedOn = rs.getTimestamp("collectedOn").toInstant().atZone(ZoneId.of("America/New_York"));
                    info.publicDebt = rs.getBigDecimal("publicDebt");
                    info.intragovernmentalDebt = rs.getBigDecimal("intragovernmentalDebt");
                    info.numFullTimeWorkers = rs.getBigDecimal("numFullTimeWorkers").toBigInteger();
                    return info;
                }
            }
        }
        return null;
    }

    // Getters and setters
    public ZonedDateTime getCollectedOn() {
        return collectedOn;
    }

    public void setCollectedOn(ZonedDateTime collectedOn) {
        this.collectedOn = collectedOn;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigDecimal getPublicDebt() {
        return publicDebt;
    }

    public void setPublicDebt(BigDecimal publicDebt) {
        this.publicDebt = publicDebt;
    }

    public BigDecimal getIntragovernmentalDebt() {
        return intragovernmentalDebt;
    }

    public void setIntragovernmentalDebt(BigDecimal intragovernmentalDebt) {
        this.intragovernmentalDebt = intragovernmentalDebt;
    }

    public BigInteger getNumFullTimeWorkers() {
        return numFullTimeWorkers;
    }

    public void setNumFullTimeWorkers(BigInteger numFullTimeWorkers) {
        this.numFullTimeWorkers = numFullTimeWorkers;
    }

    @Override
    public String toString() {
        return "DemographicInfo from " + collectedOn;
    }
}
