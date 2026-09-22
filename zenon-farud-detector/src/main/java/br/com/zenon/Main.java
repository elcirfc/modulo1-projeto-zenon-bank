package br.com.zenon;

import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;

import java.util.List;

public class Main {
    void main() {
        TransactionIngestor ingestor = new TransactionIngestor();
        String fileName = "data/paysim_with_bad_data.csv";
        List<Transaction> transactions = ingestor.read(fileName);

        System.out.println("Total de transações lidas: " + transactions.size());
        System.out.println("\n--- Transações Importadas ---");
        transactions.forEach(System.out::println);
    }
}
