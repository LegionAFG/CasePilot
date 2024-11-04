package com.badr.cp_project.dao;

import com.badr.cp_project.model.Client;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private static final String SELECT_ALL_CLIENT =
            "SELECT ifaNumber, lastName, firstName, birthDate, gender, nationality, relationshipStatus FROM client";


    private static final String INSERT_CLIENT =
            "INSERT INTO client (ifaNumber, lastName, firstName, birthDate, nationality, gender, relationshipStatus) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CLIENT =
            "UPDATE client SET lastName = ?, firstName = ?, birthDate = ?, nationality = ?, gender = ?, relationshipStatus = ? " +
                    "WHERE ifaNumber = ?";

    private static final String DELETE_CLIENT_BY_IFA_NUMBER =
            "DELETE FROM client WHERE ifaNumber = ?";

    private static final String EXISTS_BY_IFA_NUMBER =
            "SELECT COUNT(*) FROM client WHERE ifaNumber = ?";


    private final Connection conn;

    public ClientDAO(Connection conn) {
        this.conn = conn;
    }


    /**
     * Ruft alle Klienten aus der Datenbank ab.
     * @return eine Liste aller Klienten.
     */
    public List<Client> getAllClient() {
        List<Client> klientenListe = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_CLIENT)) {
            while (rs.next()) {
                klientenListe.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Abrufen der Klienten: " + e.getMessage());
        }
        return klientenListe;
    }

    /**
     * Speichert einen neuen Klienten in der Datenbank.
     * @param client der zu speichernde Klient.
     */
    public void save(Client client) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_CLIENT)) {
            setClientInsertStatement(stmt, client);
            stmt.executeUpdate();
            System.out.println("Klient erfolgreich gespeichert.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Speichern des Klienten: " + e.getMessage());
        }
    }

    /**
     * Aktualisiert einen bestehenden Klienten in der Datenbank.
     * @param client der zu aktualisierende Klient.
     */
    public void update(Client client) {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_CLIENT)) {
            setClientUpdateStatement(stmt, client);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Klient erfolgreich aktualisiert." : "Kein Klient zum Aktualisieren gefunden.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Aktualisieren des Klienten: " + e.getMessage());
        }
    }

    /**
     * Überprüft, ob ein Klient mit der gegebenen Ifa-Nummer existiert.
     * @param ifaNumber die zu überprüfende Ifa-Nummer.
     * @return true, wenn ein Klient mit der Ifa-Nummer existiert, andernfalls false.
     */
    public boolean existsByIfa(String ifaNumber) {
        try (PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Überprüfung der IfaNummer: " + e.getMessage());
        }
        return false;
    }

    /**
     * Löscht einen Klienten anhand seiner Ifa-Nummer.
     * @param ifaNumber die Ifa-Nummer des zu löschenden Klienten.
     */
    public void deleteByIfa(String ifaNumber) {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_CLIENT_BY_IFA_NUMBER)) {
            stmt.setString(1, ifaNumber);
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Klient erfolgreich gelöscht." : "Kein Klient zum Löschen gefunden.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen des Klienten: " + e.getMessage());
        }
    }

    /**
     * Mappt ein ResultSet auf ein Klient-Objekt.
     * @param rs das ResultSet.
     * @return das gemappte Klient-Objekt.
     * @throws SQLException wenn ein Fehler beim Zugriff auf die Daten auftritt.
     */
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        String ifaNumber = rs.getString("ifaNumber");
        String lastname = rs.getString("lastName");
        String firstname = rs.getString("firstName");
        LocalDate dateOfBirth = rs.getDate("birthDate").toLocalDate();
        String gender = rs.getString("gender");
        String nationality = rs.getString("nationality");
        String relationship = rs.getString("relationshipStatus");

        return new Client(ifaNumber, lastname, firstname, dateOfBirth, nationality, gender, relationship);

}

    /**
     * Setzt die Parameter für eine Insert-Anweisung.
     * @param stmt das PreparedStatement.
     * @param client der Klient, dessen Daten eingefügt werden.
     * @throws SQLException wenn ein Fehler beim Setzen der Parameter auftritt.
     */
    private void setClientInsertStatement(PreparedStatement stmt, Client client) throws SQLException {
        stmt.setString(1, client.getClientIfaNumber());
        stmt.setString(2, client.getClientLastname());
        stmt.setString(3, client.getClientFirstname());
        stmt.setDate(4, java.sql.Date.valueOf(client.getClientDateOfBirth()));
        stmt.setString(5, client.getClientNationality());
        stmt.setString(6, client.getClientGender());
        stmt.setString(7, client.getClientRelationshipStatus());
    }

    /**
     * Setzt die Parameter für eine Update-Anweisung.
     * @param stmt das PreparedStatement.
     * @param client der Klient, dessen Daten aktualisiert werden.
     * @throws SQLException wenn ein Fehler beim Setzen der Parameter auftritt.
     */
    private void setClientUpdateStatement(PreparedStatement stmt, Client client) throws SQLException {
        stmt.setString(1, client.getClientLastname());
        stmt.setString(2, client.getClientFirstname());
        stmt.setDate(3, java.sql.Date.valueOf(client.getClientDateOfBirth()));
        stmt.setString(4, client.getClientNationality());
        stmt.setString(5, client.getClientGender());
        stmt.setString(6, client.getClientRelationshipStatus());
        stmt.setString(7, client.getClientIfaNumber());
    }
}
