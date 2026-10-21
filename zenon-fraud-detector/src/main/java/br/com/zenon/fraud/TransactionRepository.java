package br.com.zenon.fraud;

import java.util.Optional;

public interface TransactionRepository {

    /**
     * Salva uma transação no repositório.
     *
     * @param transaction Transação a ser salva
     */
    void save(Transaction transaction);

    /**
     * Busca uma transação pelo nome do cliente de origem.
     *
     * @param originName Nome do cliente de origem
     * @return Optional contendo a transação se encontrada, ou Optional.empty() caso contrário
     */
    default Optional<Transaction> findByOriginName(String originName) {
        return findByCustomerName(originName);
    }

    /**
     * Busca uma transação pelo nome do cliente de origem.
     *
     * @param customerName Nome do cliente de origem
     * @return Optional contendo a transação se encontrada, ou Optional.empty() caso contrário
     */
    Optional<Transaction> findByCustomerName(String customerName);

    /**
     * Busca uma transação pelo nome do cliente de origem.
     *
     * @param customerName Nome do cliente de origem
     * @return Optional contendo a transação se encontrada, ou Optional.empty() caso contrário
     */
    default Optional<Transaction> findByOriginCustomerName(String customerName) {
        return findByCustomerName(customerName);
    }
}
