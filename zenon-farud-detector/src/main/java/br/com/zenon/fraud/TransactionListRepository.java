package br.com.zenon.fraud;

import java.util.List;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : List.of();
    }

    /**
     * Busca uma transação pelo nome do cliente de origem.
     *
     * @param customerName Nome do cliente de origem
     * @return Optional contendo a transação se encontrada, ou Optional.empty() caso contrário
     */
    @Override
    public Optional<Transaction> findByCustomerName(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            return Optional.empty();
        }
        return transactions.stream()
                .filter(t -> customerName.equals(t.origin().name()))
                .findFirst();
    }

}
