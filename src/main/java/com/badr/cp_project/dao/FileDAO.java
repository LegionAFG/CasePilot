package com.badr.cp_project.dao;

import com.badr.cp_project.model.File;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDAO {

    private static final String INSERT_FILE =
            "INSERT INTO document (fileName, fileType, uploadDate, filePath, clientIfaNumber) VALUES (?, ?, ?, ?, ?)";

    private static final String DELETE_FILE_BY_ID =
            "DELETE FROM document WHERE documentId = ?";

    private static final String SELECT_FILE_BY_IFA_NUMBER =
            "SELECT * FROM document WHERE clientIfaNumber = ?";


    private final Connection conn;

    public FileDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Speichert eine neue Datei in der Datenbank.
     * @param datei die zu speichernde Datei.
     */
    public void save(File datei) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_FILE, Statement.RETURN_GENERATED_KEYS)) {
            setDateiInsertParameters(stmt, datei);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    datei.setFileId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Datei erfolgreich gespeichert.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Speichern der Datei: " + e.getMessage());
        }
    }

    /**
     * Löscht eine Datei anhand ihrer ID aus der Datenbank.
     *
     * @param dokumentId die ID der zu löschenden Datei.
     */
    public boolean delete(int dokumentId) {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_FILE_BY_ID)) {
            stmt.setInt(1, dokumentId);
            stmt.executeUpdate();
            System.out.println("Datei erfolgreich gelöscht.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen der Datei: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ruft alle Dateien für eine bestimmte Ifa-Nummer ab.
     * @param ifaNumber die Ifa-Nummer des Klienten.
     * @return eine Liste der zugehörigen Dateien.
     */
    public List<File> getFileByIfa(String ifaNumber) {
        List<File> dateien = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_FILE_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dateien.add(mapResultSetToFile(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Laden der Dateien: " + e.getMessage());
        }
        return dateien;
    }

    /**
     * Mappt ein ResultSet auf ein Datei-Objekt (File-Objekt).
     * @param rs das ResultSet.
     * @return das gemappte File-Objekt.
     * @throws SQLException wenn ein Fehler beim Zugriff auf die Daten auftritt.
     */
    private File mapResultSetToFile(ResultSet rs) throws SQLException {
        int documentId = rs.getInt("documentId"); // Statt "dokumentId"
        String fileName = rs.getString("fileName"); // Statt "dateiname"
        String fileType = rs.getString("fileType"); // Statt "dateityp"
        Date uploadDate = rs.getDate("uploadDate"); // Statt "hochladedatum"
        String filePath = rs.getString("filePath"); // Statt "dateipfad"
        String clientIfaNumber = rs.getString("clientIfaNumber"); // Statt "klientIfaNummer"

        return new File(documentId, uploadDate.toLocalDate(), fileType, fileName, filePath, clientIfaNumber);
    }


    /**
     * Setzt die Parameter für eine Insert-Anweisung.
     * @param stmt das PreparedStatement.
     * @param file die Datei, deren Daten eingefügt werden.
     * @throws SQLException wenn ein Fehler beim Setzen der Parameter auftritt.
     */
    private void setDateiInsertParameters(PreparedStatement stmt, File file) throws SQLException {
        stmt.setString(1, file.getFileName());
        stmt.setString(2, file.getFileTyp());
        stmt.setDate(3, java.sql.Date.valueOf(file.getFileUploadDate()));
        stmt.setString(4, file.getFilePath());
        stmt.setString(5, file.getFileClientIfaNumber());
    }
}
