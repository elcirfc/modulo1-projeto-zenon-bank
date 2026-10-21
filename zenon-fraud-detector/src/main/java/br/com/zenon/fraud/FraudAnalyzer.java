package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FraudAnalyzer {

    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : List.of();
    }

    /**
     * Retorna a lista de transações fraudulentas (isFraud == true).
     */
    public List<Transaction> getFraudTransactions() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .toList();
    }

    /**
     * Retorna as N transações de fraude de maior valor (amount).
     */
    public List<Transaction> getTopFraudsByAmount(int limit) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(limit)
                .toList();
    }

    /**
     * Retorna as transações de fraude dos N maiores clientes de origem suspeitos sem repetições.
     */
    public List<Transaction> getTopSuspectOriginTransactions(int limit) {
        Set<String> seen = new HashSet<>();
        return transactions.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .filter(t -> seen.add(t.origin().name()))
                .limit(limit)
                .toList();
    }

    /**
     * Obtém os nomes dos clientes de origem (nameOrig) das fraudes sem repetições.
     */
    public List<String> getTopSuspectOriginCustomers(int limit) {
        return getTopSuspectOriginTransactions(limit).stream()
                .map(t -> t.origin().name())
                .toList();
    }

    /**
     * Calcula o prejuízo total causado pelas fraudes (soma dos amount).
     */
    public BigDecimal calculateTotalFraudLoss() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Conta quantas fraudes ocorreram por tipo de transação.
     */
    public Map<TransactionType, Long> countFraudsByType() {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }
}
