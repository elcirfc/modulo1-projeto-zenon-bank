package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionIngestor {

    public List<Transaction> ingest(String fileName) {
        return ingest(fileName, 1000);
    }

    public List<Transaction> ingest(String fileName, int limit) {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine(); // Lê o cabeçalho (header)
            if (line == null) {
                return transactions;
            }

            int count = 0;
            while ((line = reader.readLine()) != null && count < limit) {
                if (line.isBlank()) {
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 11) {
                    continue;
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

                Transaction transaction = new Transaction(
                        step,
                        type,
                        amount,
                        origin,
                        destination,
                        isFraud,
                        isFlaggedFraud
                );

                transactions.add(transaction);
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo: " + fileName, e);
        }

        return transactions;
    }

    private boolean parseBoolean(String value) {
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }
}
