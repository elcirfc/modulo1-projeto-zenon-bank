package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record Customer(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance
) {
    public Customer {
        Objects.requireNonNull(name, "name should not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name should not be empty");
        }

        Objects.requireNonNull(oldBalance, "oldBalance should not be null");
        Objects.requireNonNull(newBalance, "newBalance should not be null");

        if (oldBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("oldBalance should be positive: " + oldBalance);
        }

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("newBalance should be positive: " + newBalance);
        }
    }
}