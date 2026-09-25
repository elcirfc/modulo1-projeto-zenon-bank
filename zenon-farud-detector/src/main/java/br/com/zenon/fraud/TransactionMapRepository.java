package br.com.zenon.fraud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionsByOriginCustomer;

    public TransactionMapRepository(List<Transaction> transactions) {
        this.transactionsByOriginCustomer = new HashMap<>();
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                if (transaction != null && transaction.origin() != null && transaction.origin().name() != null) {
                    this.transactionsByOriginCustomer.putIfAbsent(transaction.origin().name(), transaction);
                }
            }
        }

//        this.transactionsByOriginCustomer =
//                transactions.stream()
//                        .collect(Collectors.toMap(Transaction -> Transaction.origin().name(),
//                                Function.identity()));
//
//        this.transactionsByOriginCustomer =
//                transactions.stream()
//                        .collect(HashMap::new, (map,
//                                                transaction) -> map.put(transaction.origin().name(), transaction), HashMap::putAll);
    }

    public TransactionMapRepository(Map<String, Transaction> transactionsByOriginCustomer) {
        this.transactionsByOriginCustomer = transactionsByOriginCustomer != null
                ? new HashMap<>(transactionsByOriginCustomer)
                : new HashMap<>();
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
