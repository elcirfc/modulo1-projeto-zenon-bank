package br.com.zenon.fraud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectFactory {

    private ConnectFactory() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/zenon_frauds?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", "root", "senha123");
        } catch (SQLException e) {
            throw new RuntimeException("Errro ao connectar com o BD", e);
        }
    }
}
