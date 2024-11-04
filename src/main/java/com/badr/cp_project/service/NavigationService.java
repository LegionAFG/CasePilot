package com.badr.cp_project.service;

import com.badr.cp_project.controller.FileController;
import com.badr.cp_project.controller.DocumentController;
import com.badr.cp_project.controller.ClientController;
import com.badr.cp_project.controller.AppointmentController;
import com.badr.cp_project.model.Document;
import com.badr.cp_project.model.Client;
import com.badr.cp_project.model.Appointment;
import javafx.stage.Stage;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NavigationService {

    // Logger für konsistente Protokollierung
    private static final Logger LOGGER = Logger.getLogger(NavigationService.class.getName());
    private final WindowsNaviService windowsNaviService;

    public NavigationService() {
        this.windowsNaviService = new WindowsNaviService();
    }

    /**
     * Navigiert zu einer bestimmten Seite ohne Controller-Konfiguration.
     * @param stage das aktuelle Stage-Objekt.
     * @param page der Page-Enum-Wert, der die gewünschte Seite repräsentiert.
     */
    public void navigateTo(Stage stage, WindowsNaviService.Page page) {
        try {
            windowsNaviService.navigateTo(stage, page, null);
            LOGGER.log(Level.INFO, "Erfolgreich zu Seite: {0} navigiert.", page.name());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Navigieren zu Seite: " + page.name(), e);
        }
    }

    /**
     * Navigiert zu einer bestimmten Seite und konfiguriert den Controller.
     * @param stage das aktuelle Stage-Objekt.
     * @param page der Page-Enum-Wert, der die gewünschte Seite repräsentiert.
     * @param controllerConsumer ein Consumer zur Konfiguration des Controllers.
     * @param <T> der Typ des Controllers.
     */
    public <T> void navigateTo(Stage stage, WindowsNaviService.Page page, Consumer<T> controllerConsumer) {
        try {
            windowsNaviService.navigateTo(stage, page, controllerConsumer);
            LOGGER.log(Level.INFO, "Erfolgreich zu Seite: {0} navigiert.", page.name());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Navigieren zu Seite: " + page.name(), e);
        }
    }

    /**
     * Zeigt einen Bestätigungsdialog für das Löschen an.
     * @return true, wenn der Benutzer das Löschen bestätigt; false andernfalls.
     */
    public boolean showConfirmDeleteDialog() {
        LOGGER.log(Level.INFO, "Zeige Bestätigungsdialog für das Löschen an.");
        return windowsNaviService.showConfirmDeleteDialog();
    }

    /**
     * Navigiert zur Detailansicht eines Klienten.
     * @param stage das aktuelle Stage-Objekt.
     * @param client das Klient-Objekt, dessen Details angezeigt werden sollen.
     */
    public void navigateToClientDetail(Stage stage, Client client) {
        LOGGER.log(Level.INFO, "Navigiere zu Klient-Detailansicht für: {0} {1}", new Object[]{client.getClientLastname(), client.getClientFirstname()});
        navigateTo(stage, WindowsNaviService.Page.CLIENT, controller -> {
            if (controller instanceof ClientController clientController) {
                clientController.setClientData(
                        client.getClientIfaNumber(),
                        client.getClientLastname(),
                        client.getClientFirstname(),
                        client.getClientDateOfBirth(),
                        client.getClientNationality(),
                        client.getClientGender(),
                        client.getClientRelationshipStatus()
                );
            }
        });
    }

    /**
     * Navigiert zur Detailansicht eines Dokuments basierend auf der IFA-Nummer.
     * @param stage das aktuelle Stage-Objekt.
     * @param ifaNumber die Ifa-Nummer des Klienten, dessen Dokumente angezeigt werden sollen.
     */
    public void navigateToDocumentDetail(Stage stage, String ifaNumber) {
        LOGGER.log(Level.INFO, "Navigiere zu Dokument-Detailansicht für IFA-Nummer: {0}", ifaNumber);
        navigateTo(stage, WindowsNaviService.Page.DOCUMENT, controller -> {
            if (controller instanceof DocumentController documentController) {
                documentController.setDocumentClientIfaNumber(ifaNumber);
            }
        });
    }

    /**
     * Navigiert zur Detailansicht eines Dokuments.
     * @param stage das aktuelle Stage-Objekt.
     * @param document das Dokument-Objekt, dessen Details angezeigt werden sollen.
     */
    public void navigateToDocumentDetail(Stage stage, Document document) {
        LOGGER.log(Level.INFO, "Navigiere zu Dokument-Detailansicht für: {0}", document.getDokuTitel());
        navigateTo(stage, WindowsNaviService.Page.DOCUMENT, controller -> {
            if (controller instanceof DocumentController documentController) {
                documentController.setDocumentClientIfaNumber(document.getDocumentClientIfaNumber());
            }
        });
    }

    /**
     * Navigiert zur Detailansicht eines Termins basierend auf einem Termin-Objekt.
     * @param stage das aktuelle Stage-Objekt.
     * @param appointment das Termin-Objekt, dessen Details angezeigt werden sollen.
     */
    public void navigateToAppointmentDetail(Stage stage, Appointment appointment) {
        LOGGER.log(Level.INFO, "Navigiere zu Termin-Detailansicht für: {0} {1}", new Object[]{appointment.getAppointmentClientLastname(), appointment.getAppointmentClientFirstname()});
        navigateTo(stage, WindowsNaviService.Page.APPOINTMENT, controller -> {
            if (controller instanceof AppointmentController appointmentController) {
                appointmentController.setClientData(
                        appointment.getAppointmentClientIfaNumber(),
                        appointment.getAppointmentClientLastname(),
                        appointment.getAppointmentClientFirstname()
                );
            }
        });
    }

    /**
     * Navigiert zur Detailansicht eines Termins basierend auf einzelnen String-Parametern.
     * @param stage das aktuelle Stage-Objekt.
     * @param ifaNumber die Ifa-Nummer des Klienten.
     * @param lastname der Name des Klienten.
     * @param firstname der Vorname des Klienten.
     */
    public void navigateToAppointmentDetail(Stage stage, String ifaNumber, String lastname, String firstname) {
        LOGGER.log(Level.INFO, "Navigiere zu Termin-Detailansicht für: {0} {1}", new Object[]{lastname, firstname});
        navigateTo(stage, WindowsNaviService.Page.APPOINTMENT, controller -> {
            if (controller instanceof AppointmentController appointmentController) {
                appointmentController.setClientData(ifaNumber, lastname, firstname);
            }
        });
    }

    /**
     * Navigiert zur Detailansicht einer Datei basierend auf der IFA-Nummer.
     * @param stage das aktuelle Stage-Objekt.
     * @param ifaNumber die Ifa-Nummer des Klienten.
     */
    public void navigateToFileDetail(Stage stage, String ifaNumber) {
        LOGGER.log(Level.INFO, "Navigiere zu Datei-Detailansicht für IFA-Nummer: {0}", ifaNumber);
        navigateTo(stage, WindowsNaviService.Page.FILE, controller -> {
            if (controller instanceof FileController fileController) {
                fileController.setFileClientIfaNumber(ifaNumber);
            }
        });
    }
}
