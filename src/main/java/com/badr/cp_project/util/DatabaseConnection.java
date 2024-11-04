package com.badr.cp_project.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/klientenverwaltung?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = System.getenv("DB_USER"); // Benutzername aus Umgebungsvariable
    private static final String PASSWORD = System.getenv("DB_PASSWORD"); // Passwort aus Umgebungsvariable

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    public Connection connect() {
        if (USER == null || PASSWORD == null) {
            LOGGER.log(Level.SEVERE, "Datenbank-Benutzername oder Passwort sind nicht gesetzt.");
            return null;
        }

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.log(Level.INFO, "Verbindung zur Datenbank erfolgreich!");
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler bei der Verbindung zur Datenbank:", e);
            return null;
        }
    }
}
