package br.com.zenon.fraud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionIngestor {

    // Código para ler grandes volume de linhas para grandes volumes de dados sem precisar ler tudo antecipadamente.
    // Lê todas as linhas do volume. Ainda guarda tudo na mémória final.
    public List<Transaction> read(String fileName) {
        return read(fileName, Integer.MAX_VALUE);
    }

    // Utiliza Stream com limitador de linhas a serem lidas.
    public List<Transaction> read(String fileName, int limit) {
        Path file = Paths.get(fileName);

        try (Stream<String> lines = Files.lines(file)) {
            return lines
                    .skip(1)
                    .limit(limit)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo: " + fileName, e);
        }
    }

    // Código para ler grandes volume de linhas para grandes volumes de dados.
    // Lê todas as todas as linhas do volume. Gera menos buffer de memória.
    public List<Transaction> readBuffer(String fileName) {
        return readBuffer(fileName, Integer.MAX_VALUE);
    }

    // Utiliza BufferedReader para ler grandes volumes de linhas para grandes volumes de dados sem gerar buffer de memória.
    // Utiliza limitador de linhas a serem lidas.
    public List<Transaction> readBuffer(String fileName, int limit) {
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

                Optional<Transaction> transaction = parseTransaction(line);

                transaction.ifPresent(transactions::add);

            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo: " + fileName, e);
        }

        return transactions;
    }

    private Optional <Transaction> parseTransaction(String line) {
        try {
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

            return  Optional.of(new Transaction(step, type, amount, origin, destination, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            System.err.println("Erro: " + line + " | " + e);
            return Optional.empty();
        }
    }

    private boolean parseBoolean (String value){
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

}