package com.artmark.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() {
        try {
            Properties prop = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                throw new RuntimeException("Fisierul db.properties nu a fost gasit in resources.");
            }
            prop.load(is);
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.username");
            String pass = prop.getProperty("db.password");
            String driver = prop.getProperty("db.driver");
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexiune la baza de date stabilita.");
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Eroare la conectare la baza de date: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
