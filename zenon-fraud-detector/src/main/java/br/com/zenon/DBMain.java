package br.com.zenon;

import br.com.zenon.fraud.ConnectFactory;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;
import br.com.zenon.fraud.TransactionSQLRepository;

import java.util.List;
import java.util.Optional;

public class DBMain {
    static void main() {

        ConnectFactory.getConnection();
        IO.println("Banco conectado com sucesso!");

        TransactionSQLRepository repository = new TransactionSQLRepository();
        repository.findByOriginName("C1000001");
        repository.findByOriginName("C1000002");

//        String fileName = "data/PS_20174392719_1491204439457_log.csv";
//        int limit = 10_000;
//
//        System.out.println("Iniciando a leitura do arquivo CSV (" + limit + " transações)...");
//        TransactionIngestor ingestor = new TransactionIngestor();
//        List<Transaction> transactions = ingestor.readBuffer(fileName, limit);
//        System.out.println("Total de transações lidas do CSV: " + transactions.size());
//
//        TransactionSQLRepository repository = new TransactionSQLRepository();

//
//        System.out.println("Inserindo transações no banco de dados MySQL...");
//        long startTime = System.currentTimeMillis();
//        repository.saveAll(transactions);
//        long endTime = System.currentTimeMillis();
//
//        long duration = endTime - startTime;
//        System.out.println("Tempo de inserção de " + transactions.size() + " transações: " + duration + " ms (" + (duration / 1000.0) + " segundos)");
//
//        System.out.println("\n--- Buscando transações ---");
//
//        String existingCustomer = "C1231006815";
//        System.out.println("Buscando pela transação com originName: " + existingCustomer);
//        Optional<Transaction> foundExisting = repository.findByOriginName(existingCustomer);
//        if (foundExisting.isPresent()) {
//            System.out.println("Encontrada: " + foundExisting.get());
//        } else {
//            System.out.println("Transação não encontrada para originName: " + existingCustomer);
//        }
//
//        String nonExistingCustomer = "C12345";
//        System.out.println("\nBuscando pela transação inexistente com originName: " + nonExistingCustomer);
//        Optional<Transaction> foundNonExisting = repository.findByOriginName(nonExistingCustomer);
//        if (foundNonExisting.isPresent()) {
//            System.out.println("Encontrada: " + foundNonExisting.get());
//        } else {
//            System.out.println("Transação não encontrada para originName: " + nonExistingCustomer);
//        }
    }
}
