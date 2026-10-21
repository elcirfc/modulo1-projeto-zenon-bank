package br.com.zenon;

import br.com.zenon.fraud.FraudAnalyzer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;
import br.com.zenon.fraud.TransactionListRepository;
import br.com.zenon.fraud.TransactionMapRepository;
import br.com.zenon.fraud.TransactionRepository;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        TransactionIngestor ingestor = new TransactionIngestor();
        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        int limit = 100_000;

        List<Transaction> transactions = ingestor.read(fileName, limit);
//        List<Transaction> transactions = ingestor.read(fileName);

//        FraudAnalyzer analyzer = new FraudAnalyzer(transactions);
//
//        // 1. Apenas transações onde isFraud == true, imprima o tamanho da lista.
//        List<Transaction> frauds = analyzer.getFraudTransactions();
//        System.out.println("1. Total de Fraudes: " + frauds.size());
//
//        // 2. Imprima as 3 fraudes de maior valor (amount).
//        List<Transaction> top3Frauds = analyzer.getTopFraudsByAmount(3);
//        System.out.println("2. Top 3 Fraudes de Maior Valor:");
//        top3Frauds.forEach(t -> System.out.println(String.format(Locale.US, "%.2f", t.amount())));
//
//        // 3. Obter apenas os nomes dos clientes de origem (nameOrig) dessas fraudes e depois gere uma lista sem repetições (Set ou distinct) com os 5 maiores clientes suspeitos.
//        List<Transaction> top5Suspects = analyzer.getTopSuspectOriginTransactions(5);
//        System.out.println("3. Clientes Suspeitos:");
//        top5Suspects.forEach(t -> System.out.println(t.origin().name()));
//
//        // 4. Calcule o prejuízo total causado pelas fraudes (soma dos amount).
//        BigDecimal totalLoss = analyzer.calculateTotalFraudLoss();
//        System.out.println("4. Prejuízo Total: " + String.format(Locale.US, "%.2f", totalLoss));
//
//        // 5. Conte quantas fraudes ocorreram por tipo de transação (CASH_OUT, TRANSFER, etc...).
//        System.out.println("5. Fraudes por Tipo:");
//        Map<TransactionType, Long> fraudsByType = analyzer.countFraudsByType();
//        fraudsByType.forEach((type, count) -> System.out.println(" - " + type + ": " + count));

        // 6. Teste de busca no TransactionRepository (implementado por TransactionListRepository)
        System.out.println("\n6. Teste TransactionListRepository:");
        TransactionRepository repository = new TransactionListRepository(transactions);

        String existingCustomer = "C1231006815";
        Optional<Transaction> transactionFound = repository.findByOriginCustomerName(existingCustomer);
        transactionFound.ifPresentOrElse(
                t -> System.out.println(" - Cliente existente (" + existingCustomer + "): " + t),
                () -> System.out.println(" - Cliente existente (" + existingCustomer + ") não encontrado")
        );

        String nonExistingCustomer = "C12345";
        Optional<Transaction> transactionNotFound = repository.findByOriginCustomerName(nonExistingCustomer);
        transactionNotFound.ifPresentOrElse(
                t -> System.out.println(" - Cliente inexistente (" + nonExistingCustomer + "): " + t),
                () -> System.out.println(" - Cliente inexistente (" + nonExistingCustomer + ") não encontrado (Optional.empty)")
        );

        // 7. Busca no pior caso (última transação da lista) com medição de tempo no ListRepository
        String worstCaseCustomer = "C1868032458";
        long startTimeList = System.nanoTime();
        Optional<Transaction> worstCaseTransactionList = repository.findByOriginCustomerName(worstCaseCustomer);
        long endTimeList = System.nanoTime();
        long durationNsList = endTimeList - startTimeList;

        System.out.println("\n7. Medição de tempo no pior caso (ListRepository - " + worstCaseCustomer + "):");
        worstCaseTransactionList.ifPresentOrElse(
                IO::println,
                () -> IO.println(" - Transação não encontrada")
        );
        System.out.println(" - Tempo decorrido (List): " + durationNsList + " ns (" + String.format(Locale.US, "%.3f", durationNsList / 1_000_000.0) + " ms)");

        // 8. Teste e medição de tempo com TransactionMapRepository (O(1))
        TransactionRepository mapRepository = new TransactionMapRepository(transactions);

        long startTimeMap = System.nanoTime();
        Optional<Transaction> transactionFoundMap = mapRepository.findByOriginCustomerName(worstCaseCustomer);
        long endTimeMap = System.nanoTime();
        long durationNsMap = endTimeMap - startTimeMap;

        System.out.println("\n8. Medição de tempo no pior caso (MapRepository - " + worstCaseCustomer + "):");
        transactionFoundMap.ifPresentOrElse(
                t -> IO.println(" - Transação encontrada: " + t),
                () -> IO.println(" - Transação não encontrada")
        );
        System.out.println(" - Tempo decorrido (Map):  " + durationNsMap + " ns (" + String.format(Locale.US, "%.3f", durationNsMap / 1_000_000.0) + " ms)");
    }
}
