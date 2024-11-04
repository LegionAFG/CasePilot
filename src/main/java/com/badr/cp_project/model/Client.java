package com.badr.cp_project.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Client {
    private final StringProperty clientIfaNumber;
    private final StringProperty clientLastname;
    private final StringProperty clientFirstname;
    private final StringProperty clientGender;
    private final StringProperty clientNationality;
    private final StringProperty clientRelationshipStatus;
    private final ObjectProperty<LocalDate> clientDateOfBirth;

    public Client(String ifaNumber, String lastName, String firstName, LocalDate birthDate, String nationality, String gender, String relationshipStatus) {
        this.clientIfaNumber = new SimpleStringProperty(ifaNumber);
        this.clientLastname = new SimpleStringProperty(lastName);
        this.clientFirstname = new SimpleStringProperty(firstName);
        this.clientDateOfBirth = new SimpleObjectProperty<>(birthDate);
        this.clientNationality = new SimpleStringProperty(nationality);
        this.clientGender = new SimpleStringProperty(gender);
        this.clientRelationshipStatus = new SimpleStringProperty(relationshipStatus);
    }

    // Getter for Properties
    public StringProperty clientIfaNumberProperty() {
        return clientIfaNumber;
    }

    public StringProperty clientLastnameProperty() {
        return clientLastname;
    }

    public StringProperty clientFirstnameProperty() {
        return clientFirstname;
    }

    public StringProperty clientGenderProperty() {
        return clientGender;
    }

    public StringProperty clientNationalityProperty() {
        return clientNationality;
    }

    public StringProperty clientRelationshipStatusProperty() {
        return clientRelationshipStatus;
    }

    public ObjectProperty<LocalDate> clientDateOfBirthProperty() {
        return clientDateOfBirth;
    }

    // Getters for normal usage
    public String getClientIfaNumber() {
        return clientIfaNumber.get();
    }

    public String getClientLastname() {
        return clientLastname.get();
    }

    public String getClientFirstname() {
        return clientFirstname.get();
    }

    public String getClientGender() {
        return clientGender.get();
    }

    public String getClientNationality() {
        return clientNationality.get();
    }

    public String getClientRelationshipStatus() {
        return clientRelationshipStatus.get();
    }

    public LocalDate getClientDateOfBirth() {
        return clientDateOfBirth.get();
    }
}
