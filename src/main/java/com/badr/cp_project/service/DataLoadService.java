package com.badr.cp_project.service;

import com.badr.cp_project.dao.FileDAO;
import com.badr.cp_project.dao.DocumentDAO;
import com.badr.cp_project.dao.ClientDAO;
import com.badr.cp_project.dao.AppointmentDAO;
import com.badr.cp_project.model.File;
import com.badr.cp_project.model.Document;
import com.badr.cp_project.model.Client;
import com.badr.cp_project.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLoadService {

    private static final Logger LOGGER = Logger.getLogger(DataLoadService.class.getName());
    private final UtilityService utilityService;

    public DataLoadService(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    private <T> ObservableList<T> toObservableList(List<T> list) {
        return FXCollections.observableArrayList(list);
    }

    public ObservableList<Client> loadClientData(ClientDAO clientDAO) {
        return executeWithLogging(() -> toObservableList(clientDAO.getAllClient()), "Klientendaten");
    }

    public ObservableList<Appointment> loadAllAppointment(AppointmentDAO appointmentDAO) {
        return executeWithLogging(() -> toObservableList(appointmentDAO.getAllAppointment()), "Termindaten");
    }

    public ObservableList<Appointment> loadOffeneAppointment(AppointmentDAO appointmentDAO) {
        return executeWithLogging(() -> {
            List<Appointment> termine = appointmentDAO.getAllAppointment();
            List<Appointment> offeneTermine = List.copyOf(
                    termine.stream()
                            .filter(termin -> "Offen".equalsIgnoreCase(termin.getAppointmentStatus()))
                            .toList()
            );
            return toObservableList(offeneTermine);
        }, "offene Termine");
    }

    public ObservableList<Appointment> loadTermineByClientIfaNumber(AppointmentDAO appointmentDAO, String ifaNumber) {
        return executeWithLogging(() -> toObservableList(appointmentDAO.getAppointmentByClientIfa(ifaNumber)),
                "Termine für Klient mit Ifa-Nummer: " + ifaNumber);
    }

    public ObservableList<Document> loadDocuments(DocumentDAO documentDAO) {
        return executeWithLogging(() -> toObservableList(documentDAO.getAllDocuments()), "Dokumentationen");
    }

    public void saveOrUpdateClient(Client klient, ClientDAO klientDAO) {
        try {
            if (klientDAO.existsByIfa(klient.getClientIfaNumber())) {
                klientDAO.update(klient);
                LOGGER.info("Klient wurde erfolgreich aktualisiert.");
            } else {
                klientDAO.save(klient);
                LOGGER.info("Neuer Klient wurde erfolgreich gespeichert.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern oder Aktualisieren des Klienten", e);
        }
    }

    public void resetClientForm(TextField nameField, TextField vornameField, DatePicker geburtsdatumPicker,
                                TextField nationalitaetField, ChoiceBox<String> geschlechtField,
                                ChoiceBox<String> beziehungsstatusField, Label ifaNummerLabel) {
        nameField.clear();
        vornameField.clear();
        geburtsdatumPicker.setValue(null);
        nationalitaetField.clear();
        geschlechtField.setValue("Bitte auswählen...");
        beziehungsstatusField.setValue("Bitte auswählen...");
        ifaNummerLabel.setText(utilityService.generateRandomIfaNumber());
    }

    public void updateAppointment(Appointment termin, AppointmentDAO terminDAO) {
        try {
            terminDAO.update(termin);
            LOGGER.info("Termin wurde erfolgreich aktualisiert.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Termins", e);
        }
    }

    public void saveAppointment(Appointment termin, AppointmentDAO terminDAO) {
        try {
            terminDAO.save(termin);
            LOGGER.info("Neuer Termin wurde erfolgreich gespeichert.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Termins", e);
        }
    }

    public void deleteAppointmentById(int appointmentId, AppointmentDAO terminDAO) {
        try {
            terminDAO.deleteById(appointmentId);
            LOGGER.info("Termin wurde erfolgreich gelöscht.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Löschen des Termins mit ID: " + appointmentId, e);
        }
    }

    @FunctionalInterface
    private interface ActionWithResult<T> {
        T execute() throws Exception;
    }
    public ObservableList<File> loadFileByClientIfaNumber(FileDAO dateiDAO, String ifaNummer) {
        List<File> dateien = dateiDAO.getFileByIfa(ifaNummer);
        return FXCollections.observableArrayList(dateien);
    }

    private <T> T executeWithLogging(ActionWithResult<T> action, String contextMessage) {
        try {
            return action.execute();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der " + contextMessage, e);
            return getDefaultValue();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getDefaultValue() {
        if (ObservableList.class.isAssignableFrom(Object.class)) {
            return (T) FXCollections.observableArrayList();
        }
        return null;

    }
    }
