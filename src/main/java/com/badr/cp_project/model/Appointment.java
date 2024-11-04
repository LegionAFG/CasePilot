package com.badr.cp_project.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private final ObjectProperty<LocalDate> appointmentDate;
    private final ObjectProperty<LocalTime> appointmentTime;
    private final StringProperty appointmentAddress;
    private final StringProperty appointmentInstitution;
    private final StringProperty appointmentPriority;
    private final StringProperty appointmentStatus;
    private final StringProperty clientIfaNumber;
    private final StringProperty clientLastname;
    private final StringProperty clientFirstname;
    private int appointmentId;

    // Konstruktor ohne ID (für neue Termine)
    public Appointment(String clientIfaNumber, String address, String institution,
                       LocalDate date, String priority, String status,
                       LocalTime time, String lastname, String firstname) {
        this.clientIfaNumber = new SimpleStringProperty(clientIfaNumber);
        this.appointmentAddress = new SimpleStringProperty(address);
        this.appointmentInstitution = new SimpleStringProperty(institution);
        this.appointmentDate = new SimpleObjectProperty<>(date);
        this.appointmentPriority = new SimpleStringProperty(priority);
        this.appointmentStatus = new SimpleStringProperty(status);
        this.appointmentTime = new SimpleObjectProperty<>(time);
        this.clientLastname = new SimpleStringProperty(lastname);
        this.clientFirstname = new SimpleStringProperty(firstname);
    }

    // Konstruktor mit ID (für bestehende Termine)
    public Appointment(int id, String clientIfaNumber, String address, String institution,
                       LocalDate date, String priority, String status,
                       LocalTime time, String lastname, String firstname) {
        this(clientIfaNumber, address, institution, date, priority, status, time, lastname, firstname);
        this.appointmentId = id;
    }

    // Getter für Properties (für TableView Bindungen)
    public ObjectProperty<LocalDate> appointmentDateProperty() {
        return appointmentDate;
    }

    public ObjectProperty<LocalTime> appointmentTimeProperty() {
        return appointmentTime;
    }

    public StringProperty appointmentAddressProperty() {
        return appointmentAddress;
    }

    public StringProperty appointmentInstitutionProperty() {
        return appointmentInstitution;
    }

    public StringProperty appointmentPriorityProperty() {
        return appointmentPriority;
    }

    public StringProperty appointmentStatusProperty() {
        return appointmentStatus;
    }

    public StringProperty appointmentClientIfaNumberProperty() {
        return clientIfaNumber;
    }

    public StringProperty appointmentClientLastnameProperty() {
        return clientLastname;
    }

    public StringProperty appointmentClientFirstnameProperty() {
        return clientFirstname;
    }

    // Getter und Setter für normale Verwendung
    public int getAppointmentId() {
        return appointmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate.get();
    }

    public void setAppointmentDate(LocalDate date) {
        this.appointmentDate.set(date);
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime.get();
    }

    public void setAppointmentTime(LocalTime time) {
        this.appointmentTime.set(time);
    }

    public String getAppointmentAddress() {
        return appointmentAddress.get();
    }

    public void setAppointmentAddress(String address) {
        this.appointmentAddress.set(address);
    }

    public String getAppointmentInstitution() {
        return appointmentInstitution.get();
    }

    public void setAppointmentInstitution(String institution) {
        this.appointmentInstitution.set(institution);
    }

    public String getAppointmentPriority() {
        return appointmentPriority.get();
    }

    public void setAppointmentPriority(String priority) {
        this.appointmentPriority.set(priority);
    }

    public String getAppointmentStatus() {
        return appointmentStatus.get();
    }

    public void setAppointmentStatus(String status) {
        this.appointmentStatus.set(status);
    }

    public String getAppointmentClientIfaNumber() {
        return clientIfaNumber.get();
    }

    public String getAppointmentClientLastname() {
        return clientLastname.get();
    }

    public String getAppointmentClientFirstname() {
        return clientFirstname.get();
    }
}
