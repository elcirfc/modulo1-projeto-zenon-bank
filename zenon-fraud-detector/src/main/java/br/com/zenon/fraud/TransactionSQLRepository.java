package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {

    private final String url;
    private final String user;
    private final String password;

    public TransactionSQLRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public TransactionSQLRepository() {
        this("jdbc:mysql://localhost:3306/zenon_bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", "root", "root");
    }
//
//    private Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(url, user, password);
//    }

    @Override
    public void save(Transaction transaction) {
        if (transaction == null) {
            return;
        }

        String sql = """
                INSERT INTO TRANSACTIONS (
                    step, type, amount,
                    nameOrig, oldbalanceOrg, newbalanceOrig,
                    nameDest, oldbalanceDest, newbalanceDest,
                    isFraud, isFlaggedFraud
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());
            ps.setString(4, transaction.origin().name());
            ps.setBigDecimal(5, transaction.origin().oldBalance());
            ps.setBigDecimal(6, transaction.origin().newBalance());
            ps.setString(7, transaction.destination().name());
            ps.setBigDecimal(8, transaction.destination().oldBalance());
            ps.setBigDecimal(9, transaction.destination().newBalance());
            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar transação no banco de dados", e);
        }
    }

    public void saveAll(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO zenon_frauds.TRANSACTIONS (
                    step, `type`, amount, 
                    name_origin, old_balance_origin, new_balance_origin,
                    name_recipient, old_balance_recipient, new_balance_recipient, 
                    is_fraud, is_flagged_fraud
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (Transaction transaction : transactions) {
                ps.setInt(1, transaction.step());
                ps.setString(2, transaction.type().name());
                ps.setBigDecimal(3, transaction.amount());
                ps.setString(4, transaction.origin().name());
                ps.setBigDecimal(5, transaction.origin().oldBalance());
                ps.setBigDecimal(6, transaction.origin().newBalance());
                ps.setString(7, transaction.destination().name());
                ps.setBigDecimal(8, transaction.destination().oldBalance());
                ps.setBigDecimal(9, transaction.destination().newBalance());
                ps.setBoolean(10, transaction.isFraud());
                ps.setBoolean(11, transaction.isFlaggedFraud());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar lote de transações no banco de dados", e);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        if (originName == null || originName.isBlank()) {
            return Optional.empty();
        }

        String sql = """
                SELECT id, step, `type`, amount, 
                       name_origin, old_balance_origin, new_balance_origin,
                       name_recipient, old_balance_recipient, new_balance_recipient, 
                       is_fraud, is_flagged_fraud
                FROM zenon_frauds.TRANSACTIONS
                WHERE name_origin = ?
                LIMIT 1
                """;

        try (Connection conn = ConnectFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, originName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nameOrig = rs.getString("name_origin");
                    int step = rs.getInt("step");
                    TransactionType type = TransactionType.valueOf(rs.getString("type"));
                    BigDecimal amount = rs.getBigDecimal("amount");

                    String nameOrig = rs.getString("name_origin");
                    BigDecimal oldbalanceOrg = rs.getBigDecimal("old_balance_origin");
                    BigDecimal newbalanceOrig = rs.getBigDecimal("new_balance_origin");
                    Customer origin = new Customer(nameOrig, oldbalanceOrg, newbalanceOrig);

                    String nameDest = rs.getString("name_recipient");
                    BigDecimal oldbalanceDest = rs.getBigDecimal("old_balance_recipient");
                    BigDecimal newbalanceDest = rs.getBigDecimal("new_balance_recipient");
                    Customer destination = new Customer(nameDest, oldbalanceDest, newbalanceDest);

                    boolean isFraud = rs.getBoolean("is_fraud");
                    boolean isFlaggedFraud = rs.getBoolean("is_flagged_fraud");

                    return Optional.of(new Transaction(step, type, amount, origin, destination, isFraud, isFlaggedFraud));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transação por originName: " + originName, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Transaction> findByCustomerName(String customerName) {
        return findByOriginName(customerName);
    }
}
