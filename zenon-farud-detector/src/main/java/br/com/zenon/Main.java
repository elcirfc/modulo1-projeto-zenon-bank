package br.com.zenon;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;

public class Main {
    void main() {

        Customer customerOrigin = new Customer(
                "C1231006815",
                new BigDecimal("170136.0"),
                new BigDecimal("160296.36")
        );

        Customer customerDestination = new Customer(
                "M1979787155",
                new BigDecimal("0.0"),
                new BigDecimal("0.0")
        );

        Transaction transaction1 = new Transaction(
                1,
                TransactionType.PAYMENT,
                new BigDecimal("9839.64"),
                customerOrigin,
                customerDestination,
                false,
                false
        );

        Customer customerOrigin2 = new Customer(
                "C1280323807",
                new BigDecimal("850002.52"),
                new BigDecimal("0.0")
        );
        Customer customerDestination2 = new Customer(
                "C873221189",
                new BigDecimal("6510099.11"),
                new BigDecimal("7360101.63")
        );
        Transaction transaction2 = new Transaction(
                743,
                TransactionType.CASH_OUT,
                new BigDecimal("850002.52"),
                customerOrigin2,
                customerDestination2,
                true,
                false
        );

        System.out.println(transaction1);
        System.out.println(transaction2);
    }
}
