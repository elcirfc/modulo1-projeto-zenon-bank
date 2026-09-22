package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionIngestor {

    public List<Transaction> read(String fileName) {
        return read(fileName, Integer.MAX_VALUE);
    }

    public List<Transaction> read(String fileName, int limit) {
        Objects.requireNonNull(fileName, "fileName should not be null");

        if (fileName.isBlank()) {
            throw new IllegalArgumentException("fileName should not be empty");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("limit should be positive: " + limit);
        }

        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();

            if (line == null) {
                return transactions;
            }

            while ((line = reader.readLine()) != null && transactions.size() < limit) {
                if (line.isBlank()) {
                    continue;
                }

                try {
                    Transaction transaction = parseTransaction(line);
                    transactions.add(transaction);
                } catch (RuntimeException e) {
                    System.err.println("Erro: " + line + " | " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo: " + fileName, e);
        }

        return transactions;
    }

    private Transaction parseTransaction(String line) {
        String[] fields = line.split(",", -1);

        if (fields.length != 11) {
            throw new IllegalArgumentException(
                    "invalid columns quantity. Expected: 11, found: " + fields.length
            );
        }

        int step = Integer.parseInt(fields[0].trim());
        TransactionType type = TransactionType.valueOf(fields[1].trim());
        BigDecimal amount = new BigDecimal(fields[2].trim());

        String nameOrig = fields[3].trim();
        BigDecimal oldBalanceOrig = new BigDecimal(fields[4].trim());
        BigDecimal newBalanceOrig = new BigDecimal(fields[5].trim());
        Customer origin = new Customer(nameOrig, oldBalanceOrig, newBalanceOrig);

        String nameDest = fields[6].trim();
        BigDecimal oldBalanceDest = new BigDecimal(fields[7].trim());
        BigDecimal newBalanceDest = new BigDecimal(fields[8].trim());
        Customer destination = new Customer(nameDest, oldBalanceDest, newBalanceDest);

        boolean isFraud = parseBoolean(fields[9].trim());
        boolean isFlaggedFraud = parseBoolean(fields[10].trim());

        return new Transaction(step, type, amount, origin, destination, isFraud, isFlaggedFraud);
    }

    private boolean parseBoolean(String value) {
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }
}