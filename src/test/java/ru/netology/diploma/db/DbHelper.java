package ru.netology.diploma.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbHelper {
    private final String url;
    private final String user;
    private final String password;

    public DbHelper() {
        this.url = System.getProperty("db.url", "jdbc:mysql://localhost:13306/app");
        this.user = System.getProperty("db.user", "app");
        this.password = System.getProperty("db.password", "pass");
    }

    public Set<String> paymentIds() throws SQLException {
        return idsFrom("payment_entity");
    }

    public Set<String> creditIds() throws SQLException {
        return idsFrom("credit_request_entity");
    }

    public boolean hasCardDataColumns(String table) throws SQLException {
        try (Connection connection = connect()) {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet columns = metadata.getColumns(connection.getCatalog(), null, table, "%")) {
                while (columns.next()) {
                    String name = columns.getString("COLUMN_NAME").toLowerCase();
                    if (name.contains("card") || name.contains("cvc") || name.contains("cvv")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public PaymentRecord waitForNewPayment(Set<String> knownIds) throws SQLException, InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(12));
        while (Instant.now().isBefore(deadline)) {
            PaymentRecord record = findNewPayment(knownIds);
            if (record != null) {
                return record;
            }
            Thread.sleep(300);
        }
        return null;
    }

    public CreditRecord waitForNewCredit(Set<String> knownIds) throws SQLException, InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(12));
        while (Instant.now().isBefore(deadline)) {
            CreditRecord record = findNewCredit(knownIds);
            if (record != null) {
                return record;
            }
            Thread.sleep(300);
        }
        return null;
    }

    private Set<String> idsFrom(String table) throws SQLException {
        Set<String> ids = new HashSet<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id FROM " + table)) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("id"));
            }
        }
        return ids;
    }

    private PaymentRecord findNewPayment(Set<String> knownIds) throws SQLException {
        String query = "SELECT p.id, p.status, p.amount FROM payment_entity p ORDER BY p.created DESC";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                if (!knownIds.contains(id)) {
                    return new PaymentRecord(id, resultSet.getString("status"), resultSet.getInt("amount"));
                }
            }
        }
        return null;
    }

    private CreditRecord findNewCredit(Set<String> knownIds) throws SQLException {
        String query = "SELECT c.id, c.status FROM credit_request_entity c ORDER BY c.created DESC";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                if (!knownIds.contains(id)) {
                    return new CreditRecord(id, resultSet.getString("status"));
                }
            }
        }
        return null;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static class PaymentRecord {
        private final String id;
        private final String status;
        private final int amount;

        public PaymentRecord(String id, String status, int amount) {
            this.id = id;
            this.status = status;
            this.amount = amount;
        }

        public String getId() { return id; }
        public String getStatus() { return status; }
        public int getAmount() { return amount; }
    }

    public static class CreditRecord {
        private final String id;
        private final String status;

        public CreditRecord(String id, String status) {
            this.id = id;
            this.status = status;
        }

        public String getId() { return id; }
        public String getStatus() { return status; }
    }
}
