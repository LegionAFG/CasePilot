package com.badr.cp_project.run;

import com.badr.cp_project.service.NavigationService;
import com.badr.cp_project.service.WindowsNaviService;
import com.badr.cp_project.util.DatabaseConnection;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());
    private  Connection connection;

    @Override
    public void start(Stage stage) {
        NavigationService navigationService = new NavigationService();
        try {
            // Starte die Anwendung und navigiere zur Home-Seite
            navigationService.navigateTo(stage, WindowsNaviService.Page.HOME);
            LOGGER.info("Anwendung erfolgreich gestartet und zur Home-Seite navigiert.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Starten der Anwendung", e);
        }
    }


    @Override
    public void stop() {
        // Schließe die Datenbankverbindung, falls diese aktiv ist
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Datenbankverbindung geschlossen.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Fehler beim Schließen der Datenbankverbindung", e);
            }
        }
    }

    public static void main(String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.initializeDatabaseConnection();
        launch();
    }

    private void initializeDatabaseConnection() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        connection = dbConnection.connect();

        if (connection != null) {
            LOGGER.info("Datenbankverbindung ist aktiv.");
        } else {
            LOGGER.severe("Datenbankverbindung fehlgeschlagen.");
        }
    }
}
