package com.badr.cp_project.dao;

import com.badr.cp_project.model.Document;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentDAO {


    private static final String SELECT_ALL_DOCUMENT =
            "SELECT documentationId, date, time, description, title, clientIfaNumber FROM documentation";

    private static final String INSERT_DOCUMENT =
            "INSERT INTO documentation (date, time, description, title, clientIfaNumber) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_DOCUMENT =
            "UPDATE documentation SET date = ?, time = ?, description = ?, title = ? WHERE documentationId = ?";

    private static final String DELETE_DOCUMENT_BY_ID =
            "DELETE FROM documentation WHERE documentationId = ?";

    private static final String SELECT_DOCUMENT_BY_IFA_NUMBER =
            "SELECT *" +
                    "FROM documentation WHERE clientIfaNumber = ?";



    private final Connection conn;
    Logger logger = Logger.getLogger(getClass().getName());

    public DocumentDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Ruft alle Dokumentationen aus der Datenbank ab.
     * @return eine Liste aller Dokumentationen.
     */
    public List<Document> getAllDocuments() {
        List<Document> dokuList = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_DOCUMENT)) {

            while (rs.next()) {
                dokuList.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Abrufen der Dokumentationen: ", e);
        }
        return dokuList;
    }

    /**
     * Speichert eine neue Dokumentation in der Datenbank.
     * @param document die zu speichernde Dokumentation.
     */
    public void save(Document document) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_DOCUMENT)) {
            setDocumentInsertParameters(stmt, document);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Dokumentation erfolgreich gespeichert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Speichern der Dokumentation:: ", e);
        }
    }

    /**
     * Aktualisiert eine bestehende Dokumentation in der Datenbank.
     * @param document die zu aktualisierende Dokumentation.
     */
    public void update(Document document) {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_DOCUMENT)) {
            setDocumentUpdateParameters(stmt, document);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Dokumentation erfolgreich aktualisiert.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Aktualisieren der Dokumentation: ", e);
        }
    }

    /**
     * Löscht eine Dokumentation anhand ihrer ID aus der Datenbank.
     * @param documentId die ID der zu löschenden Dokumentation.
     */
    public void delete(int documentId) {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_DOCUMENT_BY_ID)) {
            stmt.setInt(1, documentId);
            stmt.executeUpdate();
            logger.log(Level.INFO,"Dokumentation erfolgreich gelöscht.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Löschen der Dokumentation: ", e);
        }
    }

    /**
     * Ruft alle Dokumentationen für eine bestimmte Ifa-Nummer ab.
     * @param ifaNumber die Ifa-Nummer des Klienten.
     * @return eine Liste der zugehörigen Dokumentationen.
     */
    public List<Document> getDocumentByIfa(String ifaNumber) {
        List<Document> dokus = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_DOCUMENT_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dokus.add(mapResultSetToDocument(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Fehler beim Laden der Dokumentationen: ", e);
        }
        return dokus;
    }

    /**
     * Mappt ein ResultSet auf ein Document-Objekt.
     * @param rs das ResultSet.
     * @return das gemappte Document-Objekt.
     * @throws SQLException wenn ein Fehler beim Zugriff auf die Daten auftritt.
     */
    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        int documentId = rs.getInt("documentationId");
        LocalDate documentDate = rs.getDate("date").toLocalDate();
        LocalTime documentTime = rs.getTime("time").toLocalTime();
        String documentDescription = rs.getString("description");
        String documentClientIfaNumber = rs.getString("clientIfaNumber");
        String documentTitle = rs.getString("title");

        return new Document(documentId, documentDate, documentTime, documentDescription, documentClientIfaNumber, documentTitle);
    }


    /**
     * Setzt die Parameter für eine Insert-Anweisung.
     * @param stmt das PreparedStatement.
     * @param document die Dokumentation, deren Daten eingefügt werden.
     * @throws SQLException wenn ein Fehler beim Setzen der Parameter auftritt.
     */
    private void setDocumentInsertParameters(PreparedStatement stmt, Document document) throws SQLException {
        stmt.setDate(1, java.sql.Date.valueOf(document.getDocumentDate()));
        stmt.setTime(2, java.sql.Time.valueOf(document.getDocumentTime()));
        stmt.setString(3, document.getDocumentDescription());
        stmt.setString(4, document.getDokuTitel());
        stmt.setString(5, document.getDocumentClientIfaNumber());
    }

    /**
     * Setzt die Parameter für eine Update-Anweisung.
     * @param stmt das PreparedStatement.
     * @param document die Dokumentation, deren Daten aktualisiert werden.
     * @throws SQLException wenn ein Fehler beim Setzen der Parameter auftritt.
     */
    private void setDocumentUpdateParameters(PreparedStatement stmt, Document document) throws SQLException {
        stmt.setDate(1, java.sql.Date.valueOf(document.getDocumentDate()));
        stmt.setTime(2, java.sql.Time.valueOf(document.getDocumentTime()));
        stmt.setString(3, document.getDocumentDescription());
        stmt.setString(4, document.getDokuTitel());
        stmt.setInt(5, document.getDocumentId());
    }
}
