package com.badr.cp_project.controller;

import com.badr.cp_project.dao.DocumentDAO;
import com.badr.cp_project.model.Document;
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
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DocumentController {

    private final NavigationService navigationService;
    private final UtilityService utilityService;
    private final DataLoadService dataLoadService;

    private final DocumentDAO documentDAO;
    private Document selectedDocument;

    @FXML
    private TextArea documentDescriptionTextArea;
    @FXML
    private DatePicker documentDatePicker;
    @FXML
    private TextField documentTimeField;
    @FXML
    private TextField documentClientIfaNumberField;
    @FXML
    private TextField documentTitelField;

    @FXML
    private TableView<Document> documentTableView;
    @FXML
    private TableColumn<Document, LocalDate> documentDateColumn;
    @FXML
    private TableColumn<Document, LocalTime> documentTimeColumn;
    @FXML
    private TableColumn<Document, String> documentTitelColumn;
    @FXML
    private TableColumn<Document, String> documentDescriptionColumn;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public DocumentController() {
        this.utilityService = new UtilityService();
        this.navigationService = new NavigationService();
        this.dataLoadService = new DataLoadService(utilityService);
        Connection connection = new DatabaseConnection().connect();
        this.documentDAO = new DocumentDAO(connection);
    }

    @FXML
    private void initialize() {
        documentDatePicker.setValue(LocalDate.now());
        documentTimeField.setText(LocalTime.now().format(timeFormatter));


        setCellFactories();
        addDoubleClickListener();
        loadDocumentData();
    }

    private void setCellFactories() {
        documentDateColumn.setCellValueFactory(cellData -> cellData.getValue().documentDateProperty());
        documentDateColumn.setCellFactory(utilityService.dateCellFactory(dateFormatter));
        documentTimeColumn.setCellValueFactory(cellData -> cellData.getValue().documentTimeProperty());
        documentTimeColumn.setCellFactory(utilityService.timeCellFactory(timeFormatter));
        documentTitelColumn.setCellValueFactory(cellData -> cellData.getValue().dokuTitelProperty());
        documentDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().documentDescriptionProperty());
    }

    private void addDoubleClickListener() {
        utilityService.addDoubleClickListener(documentTableView, this::loadDocumentIntoFields);
    }

    private void loadDocumentData() {
        ObservableList<Document> dokuList = dataLoadService.loadDocuments(documentDAO);
        documentTableView.setItems(dokuList);
    }

    private void loadDocumentIntoFields(Document document) {
        this.selectedDocument = document;
        documentClientIfaNumberField.setText(document.getDocumentClientIfaNumber());
        documentDescriptionTextArea.setText(document.getDocumentDescription());
        documentDatePicker.setValue(document.getDocumentDate());
        documentTimeField.setText(document.getDocumentTime().toString());
        documentTitelField.setText(document.getDokuTitel());
    }

    public void setDocumentClientIfaNumber(String ifaNumber) {
        documentClientIfaNumberField.setText(ifaNumber);
        documentClientIfaNumberField.setDisable(true);

    }

    @FXML
    protected void onSaveButtonClick(ActionEvent ignoredEvent) {
        String documentDeskription = documentDescriptionTextArea.getText();
        LocalDate documentDate = documentDatePicker.getValue();
        LocalTime documentTime;
        try {
            documentTime = LocalTime.parse(documentTimeField.getText());
        } catch (DateTimeParseException e) {
            showAlert("Bitte geben Sie eine gültige Uhrzeit im Format HH:mm ein.");
            return;
        }
        String documentClientNumberings = documentClientIfaNumberField.getText();
        String documentTitle = documentTitelField.getText();

        if (!isDocumentValid(documentDeskription, documentClientNumberings, documentTitle, documentDate, documentTime)) {
            showAlert("Bitte füllen Sie alle erforderlichen Felder aus.");
            return;
        }

        Document doku = createDocumentObject(documentDeskription, documentDate, documentTime, documentClientNumberings, documentTitle);
        if (selectedDocument != null) {
            doku.setDocumentId(selectedDocument.getDocumentId());
            documentDAO.update(doku);
        } else {
            documentDAO.save(doku);
        }

        loadDocumentData();
        clearForm();
    }


    private Document createDocumentObject(String documentDeskription, LocalDate documentDate, LocalTime documentTime, String documentClientIfaNumber, String documentTitel) {
        return new Document(0, documentDate, documentTime, documentDeskription, documentClientIfaNumber, documentTitel);
    }

    private boolean isDocumentValid(String documentDeskription, String documentClientIfaNumber, String documentTitle, LocalDate documentDate, LocalTime documentTime) {
        return !documentDeskription.isEmpty() && documentClientIfaNumber != null && !documentClientIfaNumber.isEmpty()
                && documentTitle != null && !documentTitle.isEmpty() && documentDate != null && documentTime != null;
    }

    @FXML
    protected void onHomeButtonClick(ActionEvent event) {
        navigateToHome(event);
    }

    private void navigateToHome(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationService.navigateTo(stage, WindowsNaviService.Page.HOME);
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent ignoredEvent) {
        Document selectedDoku = documentTableView.getSelectionModel().getSelectedItem();
        if (selectedDoku == null) {
            showAlert("Bitte wählen Sie eine Dokumentation aus, die Sie löschen möchten.");
            return;
        }

        boolean confirm = showConfirmationDialog();
        if (!confirm) {
            return;
        }

        documentDAO.delete(selectedDoku.getDocumentId());
        loadDocumentData();
    }

    private boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText(null);
        alert.setContentText("Sind Sie sicher, dass Sie diese Dokumentation löschen möchten?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void clearForm() {
        documentDescriptionTextArea.clear();
        documentDatePicker.setValue(null);
        documentTimeField.clear();
        documentTitelField.clear();
        selectedDocument = null;
    }

}
