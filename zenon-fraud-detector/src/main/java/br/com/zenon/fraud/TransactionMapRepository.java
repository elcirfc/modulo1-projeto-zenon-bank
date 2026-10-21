package br.com.zenon.fraud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionsByOriginCustomer;

    public TransactionMapRepository() {
        this.transactionsByOriginCustomer = new HashMap<>();
    }

    public TransactionMapRepository(List<Transaction> transactions) {
        this.transactionsByOriginCustomer = new HashMap<>();
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                if (transaction != null && transaction.origin() != null && transaction.origin().name() != null) {
                    this.transactionsByOriginCustomer.putIfAbsent(transaction.origin().name(), transaction);
                }
            }
        }
    }

    public TransactionMapRepository(Map<String, Transaction> transactionsByOriginCustomer) {
        this.transactionsByOriginCustomer = transactionsByOriginCustomer != null
                ? new HashMap<>(transactionsByOriginCustomer)
                : new HashMap<>();
    }

    @Override
    public void save(Transaction transaction) {
        if (transaction != null && transaction.origin() != null && transaction.origin().name() != null) {
            this.transactionsByOriginCustomer.putIfAbsent(transaction.origin().name(), transaction);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        return findByCustomerName(originName);
    }

    /**
     * Busca uma transação pelo nome do cliente de origem utilizando acesso O(1) do Map.
     *
     * @param customerName Nome do cliente de origem
     * @return Optional contendo a transação se encontrada, ou Optional.empty() caso contrário
     */
    @Override
    public Optional<Transaction> findByCustomerName(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(transactionsByOriginCustomer.get(customerName));
    }
}
