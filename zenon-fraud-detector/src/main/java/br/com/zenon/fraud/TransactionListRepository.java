package br.com.zenon.fraud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository() {
        this.transactions = new ArrayList<>();
    }

    public TransactionListRepository(List<Transaction> transactions) {
        this.transactions = transactions != null ? new ArrayList<>(transactions) : new ArrayList<>();
    }

    @Override
    public void save(Transaction transaction) {
        if (transaction != null) {
            this.transactions.add(transaction);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        return findByCustomerName(originName);
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
