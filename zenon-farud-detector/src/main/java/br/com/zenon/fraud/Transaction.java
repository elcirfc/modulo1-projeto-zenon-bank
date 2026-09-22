package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(
        int step,
        TransactionType type,
        BigDecimal amount,
        Customer origin,
        Customer destination,
        boolean isFraud,
        boolean isFlaggedFraud
) {
    public Transaction {
        if (step < 1) {
            throw new IllegalArgumentException("step should be positive: " + step);
        }

        Objects.requireNonNull(type, "type should not be null");
        Objects.requireNonNull(amount, "amount should not be null");
        Objects.requireNonNull(origin, "origin should not be null");
        Objects.requireNonNull(destination, "destination should not be null");

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount should be positive: " + amount);
        }
    }
}