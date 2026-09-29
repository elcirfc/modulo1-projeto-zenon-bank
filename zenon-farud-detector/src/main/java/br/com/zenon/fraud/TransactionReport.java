package br.com.zenon.fraud;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionReport {

    public record SummaryReport(
            long totalLines,
            long totalFrauds,
            BigDecimal totalAmount
    ) {}

    private final Path filePath;

    public TransactionReport(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Executa uma operação lazy sobre o Stream de linhas lidas pelo Files.lines(Path).
     * O Stream é fechado automaticamente ao término da função.
     */
    public <T> T processLines(Function<Stream<String>, T> processor) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return processor.apply(lines);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + filePath, e);
        }
    }

    /**
     * Executa uma operação lazy sobre o Stream de Transactions válidas.
     * O Stream gerado por Files.lines é fechado automaticamente ao término da função.
     */
    public <T> T processTransactions(Function<Stream<Transaction>, T> processor) {
        return processLines(lines -> processor.apply(
                lines.skip(1)
                     .map(this::parseTransaction)
                     .filter(Optional::isPresent)
                     .map(Optional::get)
        ));
    }

    /**
     * Processa o arquivo inteiro em uma única passagem lazy, calculando:
     * - Número total de linhas (excluindo cabeçalho)
     * - Número total de fraudes
     * - Valor total transacionado
     * 
     * Mantém consumo mínimo e constante de memória RAM O(1).
     */
    public SummaryReport generateSummaryReport() {
        return processLines(lines -> {
            var iterator = lines.skip(1).iterator();
            long totalLines = 0;
            long totalFrauds = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;

            while (iterator.hasNext()) {
                String line = iterator.next();
                totalLines++;

                Optional<Transaction> transactionOpt = parseTransaction(line);
                if (transactionOpt.isPresent()) {
                    Transaction transaction = transactionOpt.get();
                    if (transaction.isFraud()) {
                        totalFrauds++;
                    }
                    totalAmount = totalAmount.add(transaction.amount());
                }
            }

            return new SummaryReport(totalLines, totalFrauds, totalAmount);
        });
    }

    /**
     * Conta a quantidade total de linhas no arquivo (excluindo o cabeçalho).
     */
    public long countTotalTransactions() {
        return processLines(lines -> lines.skip(1).count());
    }

    /**
     * Conta o total de fraudes no arquivo de forma lazy.
     */
    public long countFrauds() {
        return processTransactions(stream -> stream.filter(Transaction::isFraud).count());
    }

    /**
     * Calcula o prejuízo total causado por fraudes de forma lazy.
     */
    public BigDecimal calculateTotalFraudLoss() {
        return processTransactions(stream ->
                stream.filter(Transaction::isFraud)
                      .map(Transaction::amount)
                      .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    /**
     * Retorna a contagem de fraudes agrupadas por tipo de transação de forma lazy.
     */
    public Map<TransactionType, Long> countFraudsByType() {
        return processTransactions(stream ->
                stream.filter(Transaction::isFraud)
                      .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()))
        );
    }

    /**
     * Retorna as N maiores fraudes por valor (amount).
     */
    public List<Transaction> getTopFraudsByAmount(int limit) {
        return processTransactions(stream ->
                stream.filter(Transaction::isFraud)
                      .sorted(Comparator.comparing(Transaction::amount).reversed())
                      .limit(limit)
                      .toList()
        );
    }

    private Optional<Transaction> parseTransaction(String line) {
        try {
            String[] fields = line.split(",", -1);
            if (fields.length != 11) {
                return Optional.empty();
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

            return Optional.of(new Transaction(step, type, amount, origin, destination, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean parseBoolean(String value) {
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }
}
