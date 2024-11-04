package com.badr.cp_project.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.IntegerProperty;

import java.time.LocalDate;

public class File {
    private final IntegerProperty fileId;
    private final ObjectProperty<LocalDate> fileUploadDate;
    private final StringProperty fileTyp;
    private final StringProperty fileName;
    private final StringProperty filePath;
    private final StringProperty fileClientIfaNumber;

    // Vollständiger Konstruktor mit allen Feldern
    public File(int dokumentId, LocalDate dateiHochladedatum, String dateiTyp, String dateiName, String dateiPfad, String dateiKlientenIfaNummer) {
        this.fileId = new SimpleIntegerProperty(dokumentId);
        this.fileUploadDate = new SimpleObjectProperty<>(dateiHochladedatum);
        this.fileTyp = new SimpleStringProperty(dateiTyp);
        this.fileName = new SimpleStringProperty(dateiName);
        this.filePath = new SimpleStringProperty(dateiPfad);
        this.fileClientIfaNumber = new SimpleStringProperty(dateiKlientenIfaNummer);
    }

    // Einfacherer Konstruktor für neue Dateien, bei denen die ID später gesetzt wird
    public File(LocalDate dateiHochladedatum, String dateiTyp, String dateiName, String dateiPfad, String dateiKlientenIfaNummer) {
        this(0, dateiHochladedatum, dateiTyp, dateiName, dateiPfad, dateiKlientenIfaNummer);
    }



    // Getter und Setter für die Properties
    public int getFileId() {
        return fileId.get();
    }

    public void setFileId(int fileId) {
        this.fileId.set(fileId);
    }

    public LocalDate getFileUploadDate() {
        return fileUploadDate.get();
    }

    public ObjectProperty<LocalDate> fileUploadDateProperty() {
        return fileUploadDate;
    }

    public String getFileTyp() {
        return fileTyp.get();
    }

    public StringProperty fileTypProperty() {
        return fileTyp;
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public String getFilePath() {
        return filePath.get();
    }

    public String getFileClientIfaNumber() {
        return fileClientIfaNumber.get();
    }

}
