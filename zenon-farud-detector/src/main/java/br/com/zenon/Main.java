package br.com.zenon;

import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;

import java.util.List;

public class Main {
    void main() {
        TransactionIngestor ingestor = new TransactionIngestor();
        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        List<Transaction> transactions = ingestor.ingest(fileName);

        System.out.println("Total de transações lidas: " + transactions.size());
        System.out.println("\n--- 10 Primeiras Transações Importadas ---");
        transactions.stream()
                .limit(10)
                .forEach(System.out::println);
    }
}
