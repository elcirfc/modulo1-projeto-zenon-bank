package br.com.zenon;

import br.com.zenon.fraud.FraudAnalyzer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        TransactionIngestor ingestor = new TransactionIngestor();
        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        int limit = 50_000;

        List<Transaction> transactions = ingestor.read(fileName, limit);

        FraudAnalyzer analyzer = new FraudAnalyzer(transactions);

        // 1. Apenas transações onde isFraud == true, imprima o tamanho da lista.
        List<Transaction> frauds = analyzer.getFraudTransactions();
        System.out.println("1. Total de Fraudes: " + frauds.size());

        // 2. Imprima as 3 fraudes de maior valor (amount).
        List<Transaction> top3Frauds = analyzer.getTopFraudsByAmount(3);
        System.out.println("2. Top 3 Fraudes de Maior Valor:");
        top3Frauds.forEach(t -> System.out.println(String.format(Locale.US, "%.2f", t.amount())));

        // 3. Obter apenas os nomes dos clientes de origem (nameOrig) dessas fraudes e depois gere uma lista sem repetições (Set ou distinct) com os 5 maiores clientes suspeitos.
        List<Transaction> top5Suspects = analyzer.getTopSuspectOriginTransactions(5);
        System.out.println("3. Clientes Suspeitos:");
        top5Suspects.forEach(t -> System.out.println(t.origin().name()));

        // 4. Calcule o prejuízo total causado pelas fraudes (soma dos amount).
        BigDecimal totalLoss = analyzer.calculateTotalFraudLoss();
        System.out.println("4. Prejuízo Total: " + String.format(Locale.US, "%.2f", totalLoss));

        // 5. Conte quantas fraudes ocorreram por tipo de transação (CASH_OUT, TRANSFER, etc...).
        System.out.println("5. Fraudes por Tipo:");
        Map<TransactionType, Long> fraudsByType = analyzer.countFraudsByType();
        fraudsByType.forEach((type, count) -> System.out.println(" - " + type + ": " + count));
    }
}
