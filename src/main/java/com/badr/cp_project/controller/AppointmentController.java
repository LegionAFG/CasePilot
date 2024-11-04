package com.badr.cp_project.controller;

import com.badr.cp_project.dao.AppointmentDAO;
import com.badr.cp_project.model.Appointment;
import com.badr.cp_project.service.DataLoadService;
import com.badr.cp_project.service.NavigationService;
import com.badr.cp_project.service.UtilityService;
import com.badr.cp_project.service.WindowsNaviService;
import com.badr.cp_project.util.DatabaseConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentController {

    private final NavigationService navigationService;
    private final DataLoadService dataLoadService;
    private final UtilityService utilityService = new UtilityService();
    private final AppointmentDAO terminDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Logger logger = Logger.getLogger(AppointmentController.class.getName());

    @FXML
    private TextField appointmantIfaTextField, appointmentLastNameField, appointmentFirstNameField, appointmantAdressTextField, appointmentInstitutionField, appointmentTimeField;
    @FXML
    private DatePicker appointmantDatePicker;
    @FXML
    private ChoiceBox<String> appointmantPriorityChoiceBox;
    @FXML
    private TableView<Appointment> appointmantTableView;
    @FXML
    private TableColumn<Appointment, String> addressColumn, institutionColumn, statusColumn, priorityColumn;
    @FXML
    private TableColumn<Appointment, LocalDate> dateColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> timeColumn;

    public AppointmentController() {
        this.navigationService = new NavigationService();
        this.dataLoadService = new DataLoadService(utilityService);
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection conn = dbConnection.connect();
        this.terminDAO = new AppointmentDAO(conn);
    }

    @FXML
    private void initialize() {
        appointmantPriorityChoiceBox.getItems().addAll("Niedrig", "Mittel", "Hoch");

        appointmantPriorityChoiceBox.setValue("Bitte auswählen");
        appointmantDatePicker.setValue(LocalDate.of(1900, 1, 1));

        initializeAppointmentTable();
        loadAllAppointmentIntoTable();

        appointmantTableView.getSelectionModel().selectedItemProperty().addListener((ignored , ignored2 , newAppointment) ->
                appointmentSelected(newAppointment)
        );
    }


    private void initializeAppointmentTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        dateColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        timeColumn.setCellFactory(utilityService.timeCellFactory(timeFormatter));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentAddress"));
        institutionColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentInstitution"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentPriority"));
    }


    private void loadAllAppointmentIntoTable() {
        ObservableList<Appointment> alleTermine = dataLoadService.loadAllAppointment(terminDAO);
        utilityService.loadData(appointmantTableView, alleTermine, "Fehler beim Laden der Termindaten");
    }

    public void loadAppointmentForClient(String klientIfaNummer) {
        ObservableList<Appointment> termine = dataLoadService.loadTermineByClientIfaNumber(terminDAO, klientIfaNummer);
        utilityService.loadData(appointmantTableView, termine, "Fehler beim Laden der Termine für den Klienten");
    }

    @FXML
    private void onCompletedButtonClick(ActionEvent ignoredEvent) {
        Appointment selectedTermin = appointmantTableView.getSelectionModel().getSelectedItem();
        if (selectedTermin == null) {
            showWarning("Bitte wählen Sie einen Termin aus, den Sie als erledigt markieren möchten.");
            return;
        }

        // Toggle zwischen "Erledigt" und "Offen"
        if ("Erledigt".equals(selectedTermin.getAppointmentStatus())) {
            selectedTermin.setAppointmentStatus("Offen");
        } else {
            selectedTermin.setAppointmentStatus("Erledigt");
        }

        dataLoadService.updateAppointment(selectedTermin, terminDAO);
        loadAllAppointmentIntoTable();
    }

    private void appointmentSelected(Appointment termin) {
        if (termin != null) {
            appointmantAdressTextField.setText(termin.getAppointmentAddress());
            appointmentInstitutionField.setText(termin.getAppointmentInstitution());
            appointmantDatePicker.setValue(termin.getAppointmentDate());
            appointmentTimeField.setText(termin.getAppointmentTime().toString());
            appointmantPriorityChoiceBox.setValue(termin.getAppointmentPriority());
        } else {
            clearFields();
        }
    }

    @FXML
    private void onSaveButtonClick(ActionEvent ignoredEvent) {
        if (!validateInput()) {
            showWarning("Bitte füllen Sie alle erforderlichen Felder korrekt aus.");
            return;
        }

        String ifaNummer = appointmantIfaTextField.getText();
        String name = appointmentLastNameField.getText();
        String vorname = appointmentFirstNameField.getText();
        LocalDate terminDatum = appointmantDatePicker.getValue();
        LocalTime terminUhrzeit = LocalTime.parse(appointmentTimeField.getText());
        String terminInstitution = appointmentInstitutionField.getText();
        String terminAdresse = appointmantAdressTextField.getText();
        String terminPrioritaet = appointmantPriorityChoiceBox.getValue();

        Appointment selectedTermin = appointmantTableView.getSelectionModel().getSelectedItem();

        if (selectedTermin != null) {
            selectedTermin.setAppointmentAddress(terminAdresse);
            selectedTermin.setAppointmentInstitution(terminInstitution);
            selectedTermin.setAppointmentDate(terminDatum);
            selectedTermin.setAppointmentTime(terminUhrzeit);
            selectedTermin.setAppointmentPriority(terminPrioritaet);

            dataLoadService.updateAppointment(selectedTermin, terminDAO);


            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, String.format("Termin erfolgreich aktualisiert für Klient: %s %s", name, vorname));
            }
        } else {
            Appointment neuerTermin = new Appointment(ifaNummer, terminAdresse, terminInstitution, terminDatum, terminPrioritaet, "Offen", terminUhrzeit, name, vorname);
            dataLoadService.saveAppointment(neuerTermin, terminDAO);


            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, String.format("Termin erfolgreich erstellt für Klient: %s %s", name, vorname));
            }
        }


        clearFields();
        loadAllAppointmentIntoTable();
    }

    private boolean validateInput() {
        return !appointmantIfaTextField.getText().isEmpty()
                && appointmantDatePicker.getValue() != null
                && !appointmentTimeField.getText().isEmpty()
                && appointmantPriorityChoiceBox.getValue() != null;
    }

    private void clearFields() {
        appointmantAdressTextField.clear();
        appointmantDatePicker.setValue(null);
        appointmentTimeField.clear();
        appointmentInstitutionField.clear();
        appointmantPriorityChoiceBox.setValue(null);
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onDeletButtonClick(ActionEvent ignoredEvent) {
        Appointment selectedTermin = appointmantTableView.getSelectionModel().getSelectedItem();
        if (selectedTermin == null) {
            showWarning("Bitte wählen Sie einen Termin aus, den Sie löschen möchten.");
            return;
        }

        if (navigationService.showConfirmDeleteDialog()) {
            int terminId = selectedTermin.getAppointmentId();
            dataLoadService.deleteAppointmentById(terminId, terminDAO);
            logger.log(Level.INFO,"Termin wurde erfolgreich gelöscht.");
            loadAllAppointmentIntoTable();
        }
    }

    public void setClientData(String ifaNummer, String name, String vorname) {
        appointmantIfaTextField.setText(ifaNummer);
        appointmentLastNameField.setText(name);
        appointmentFirstNameField.setText(vorname);
        appointmantIfaTextField.setDisable(true);
        appointmentLastNameField.setDisable(true);
        appointmentFirstNameField.setDisable(true);

        loadAppointmentForClient(ifaNummer);
    }

    @FXML
    protected void onHomeButtonClick(ActionEvent event) {
        navigateToHome(event);
    }

    private void navigateToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateTo(stage, WindowsNaviService.Page.HOME);

    }
}
