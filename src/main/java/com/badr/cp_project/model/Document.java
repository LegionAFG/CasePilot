package com.badr.cp_project.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class Document {
    private final SimpleObjectProperty<LocalDate> documentDate;
    private final SimpleObjectProperty<LocalTime> documentTime;
    private final SimpleStringProperty documentDescription;
    private final SimpleStringProperty documentClientIfaNumber;
    private final SimpleStringProperty dokuTitel;
    private int documentId;

    public Document(int dokumentationsId, LocalDate dokuDatum, LocalTime dokuUhrzeit, String dokuBeschreibung, String dokuKlientenIfaNummer, String dokuTitel) {
        this.documentId = dokumentationsId;
        this.documentDate = new SimpleObjectProperty<>(dokuDatum);
        this.documentTime = new SimpleObjectProperty<>(dokuUhrzeit);
        this.documentDescription = new SimpleStringProperty(dokuBeschreibung);
        this.documentClientIfaNumber = new SimpleStringProperty(dokuKlientenIfaNummer);
        this.dokuTitel = new SimpleStringProperty(dokuTitel);
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public LocalDate getDocumentDate() {
        return documentDate.get();
    }

    public SimpleObjectProperty<LocalDate> documentDateProperty() {
        return documentDate;
    }

    public LocalTime getDocumentTime() {
        return documentTime.get();
    }

    public SimpleObjectProperty<LocalTime> documentTimeProperty() {
        return documentTime;
    }

    public String getDocumentDescription() {
        return documentDescription.get();
    }

    public SimpleStringProperty documentDescriptionProperty() {
        return documentDescription;
    }

    public String getDocumentClientIfaNumber() {
        return documentClientIfaNumber.get();
    }

    public String getDokuTitel() {
        return dokuTitel.get();
    }

    public SimpleStringProperty dokuTitelProperty() {
        return dokuTitel;
    }
}
