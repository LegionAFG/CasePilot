package com.badr.cp_project.controller;

import com.badr.cp_project.dao.ClientDAO;
import com.badr.cp_project.dao.AppointmentDAO;
import com.badr.cp_project.model.Client;
import com.badr.cp_project.model.Appointment;
import com.badr.cp_project.service.DataLoadService;
import com.badr.cp_project.service.NavigationService;
import com.badr.cp_project.service.UtilityService;
import com.badr.cp_project.service.WindowsNaviService;
import com.badr.cp_project.util.DatabaseConnection;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HomeController {

    private final UtilityService utilityService = new UtilityService();
    private final NavigationService navigationService;
    private final DataLoadService dataLoadService;
    private final ClientDAO clientDAO;
    private final AppointmentDAO appointmentDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Client, String> clientIfaColumn, clientLastnameColumn, clientFirstnameColumn, clientGenderColumn, clientNationalityColumn, clientRelationshipStatusColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentIfaColumn, appointmentInstitutionColumn, appointmentPriorityColumn, appointmentStatusColumn, appointmentAddressColumn, appointmentClientLastnameColumn, appointmentClientFirstnameColumn;
    @FXML
    private TableColumn<Client, LocalDate> dateOfBirthColumn;
    @FXML
    private TableColumn<Appointment, LocalDate> appointmentDateColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> appointmentTimeColumn;

    @FXML
    private TextField searchField;

    public HomeController() {
        this.navigationService = new NavigationService();
        this.dataLoadService = new DataLoadService(utilityService);
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection conn = dbConnection.connect();
        this.clientDAO = new ClientDAO(conn);
        this.appointmentDAO = new AppointmentDAO(conn);
    }

    @FXML
    public void initialize() {
        initializeClientTable();
        initializeAppointmentTable();
        loadClientData();
        loadAppointmentData();
        addDoubleClickListeners();
    }

    private void initializeClientTable() {
        clientIfaColumn.setCellValueFactory(cellData -> cellData.getValue().clientIfaNumberProperty());
        clientLastnameColumn.setCellValueFactory(cellData -> cellData.getValue().clientLastnameProperty());
        clientFirstnameColumn.setCellValueFactory(cellData -> cellData.getValue().clientFirstnameProperty());
        clientGenderColumn.setCellValueFactory(cellData -> cellData.getValue().clientGenderProperty());
        clientNationalityColumn.setCellValueFactory(cellData -> cellData.getValue().clientNationalityProperty());
        clientRelationshipStatusColumn.setCellValueFactory(cellData -> cellData.getValue().clientRelationshipStatusProperty());
        dateOfBirthColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
        dateOfBirthColumn.setCellValueFactory(cellData -> cellData.getValue().clientDateOfBirthProperty());

    }

    private void initializeAppointmentTable() {
        appointmentIfaColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentClientIfaNumberProperty());
        appointmentStatusColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentStatusProperty());
        appointmentPriorityColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentPriorityProperty());
        appointmentInstitutionColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentInstitutionProperty());
        appointmentAddressColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentAddressProperty());
        appointmentClientLastnameColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentClientLastnameProperty());
        appointmentClientFirstnameColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentClientFirstnameProperty());
        appointmentDateColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());
        appointmentDateColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
        appointmentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentTimeProperty());
        appointmentTimeColumn.setCellFactory(utilityService.timeCellFactory(timeFormatter));
    }

    private void addDoubleClickListeners() {
        utilityService.addDoubleClickListener(clientTable, this::navigateToClientDetail);
        utilityService.addDoubleClickListener(appointmentTable, this::navigateToAppointmentDetail);
    }

    private void navigateToClientDetail(Client klient) {
        if (klient != null) {
            Stage stage = (Stage) clientTable.getScene().getWindow();
            navigationService.navigateToClientDetail(stage, klient);
        }
    }

    private void navigateToAppointmentDetail(Appointment appointment) {
        if (appointment != null) {
            Stage stage = (Stage) appointmentTable.getScene().getWindow();
            navigationService.navigateTo(stage, WindowsNaviService.Page.APPOINTMENT, controller -> {
                if (controller instanceof AppointmentController terminController) {
                    terminController.setClientData(
                            appointment.getAppointmentClientIfaNumber(),
                            appointment.getAppointmentClientLastname(),
                            appointment.getAppointmentClientFirstname()
                    );
                }
            });
        }
    }

    private void loadClientData() {
        ObservableList<Client> clients = dataLoadService.loadClientData(clientDAO);

        FilteredList<Client> filteredClientList = new FilteredList<>(clients, ignore -> true);

        utilityService.addSearchFilter(searchField, clientTable, filteredClientList, (client, lowerCaseFilter) ->
                client.getClientLastname().toLowerCase().contains(lowerCaseFilter)
                        || client.getClientFirstname().toLowerCase().contains(lowerCaseFilter)
                        || client.getClientIfaNumber().toLowerCase().contains(lowerCaseFilter)
                        || client.getClientGender().toLowerCase().contains(lowerCaseFilter)
                        || client.getClientNationality().toLowerCase().contains(lowerCaseFilter)
                        || client.getClientRelationshipStatus().toLowerCase().contains(lowerCaseFilter)
                        || client.getClientDateOfBirth().toString().contains(lowerCaseFilter)
        );
    }

    private void loadAppointmentData() {
        ObservableList<Appointment> appointments = dataLoadService.loadOffeneAppointment(appointmentDAO);
        appointmentTable.setItems(appointments);
    }

    @FXML
    protected void onClientButtonClick(ActionEvent event) {
        navigateToClientPage(event);
    }

    private void navigateToClientPage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateTo(stage, WindowsNaviService.Page.CLIENT);
    }
}
