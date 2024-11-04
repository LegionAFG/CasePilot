package com.badr.cp_project.service;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilityService {

    // Logger für konsistente Protokollierung
    private static final Logger LOGGER = Logger.getLogger(UtilityService.class.getName());
    private final Random random = new Random();

    /**
     * Generiert eine zufällige 6-stellige IFA-Nummer als String.
     * @return eine zufällige 6-stellige Nummer als String.
     */
    public String generateRandomIfaNumber() {
        String ifaNummer = String.valueOf(100000 + random.nextInt(900000));
        LOGGER.log(Level.INFO, "Generierte zufällige IFA-Nummer: {0}", ifaNummer);
        return ifaNummer;
    }

    /**
     * Fügt einem TableView einen Doppelklick-Listener hinzu, der eine bestimmte Aktion ausführt.
     * @param tableView das TableView, dem der Listener hinzugefügt wird.
     * @param onItemDoubleClick die Aktion, die bei einem Doppelklick auf ein Element ausgeführt wird.
     * @param <T> der Typ der Elemente im TableView.
     */
    public <T> void addDoubleClickListener(TableView<T> tableView, Consumer<T> onItemDoubleClick) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                T selectedItem = tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    LOGGER.log(Level.INFO, "Element im TableView doppelt angeklickt: {0}", selectedItem);
                    onItemDoubleClick.accept(selectedItem);
                } else {
                    LOGGER.log(Level.WARNING, "Kein Element im TableView ausgewählt.");
                }
            }
        });
    }

    /**
     * Handhabt doppelte Klicks auf ein Element und führt eine spezifische Navigationslogik aus.
     * @param tableView das TableView, das überwacht wird.
     * @param itemType der Typ des Elements (für Logging-Zwecke).
     * @param detailInfo Detailinformationen über das Element (für Logging).
     * @param navigateFunction die Funktion, die die Navigation ausführt.
     * @param <T> der Typ der Elemente im TableView.
     */
    public <T> void handleDoubleClick(TableView<T> tableView, String itemType, String detailInfo, BiConsumer<Stage, T> navigateFunction) {
        addDoubleClickListener(tableView, item -> {
            LOGGER.info(String.format("Doppelklick auf %s: %s", itemType, detailInfo));
            Stage stage = (Stage) tableView.getScene().getWindow();
            navigateFunction.accept(stage, item);
        });
    }

    /**
     * Setzt die Elemente in einem TableView und behandelt dabei eventuelle Fehler.
     * @param tableView das TableView, in dem die Elemente gesetzt werden.
     * @param dataList die Daten, die im TableView angezeigt werden sollen.
     * @param errorMessage die Fehlermeldung, die im Falle einer Ausnahme ausgegeben wird.
     * @param <T> der Typ der Elemente im TableView.
     */
    public <T> void loadData(TableView<T> tableView, ObservableList<T> dataList, String errorMessage) {
        try {
            tableView.setItems(dataList);
            LOGGER.log(Level.INFO, "Daten erfolgreich in TableView geladen: {0} Einträge.", dataList.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, errorMessage, e);
        }
    }

    /**
     * Erstellt eine Zellfabrik zur Formatierung von LocalDate-Werten in einem TableView.
     * @param dateFormatter der DateTimeFormatter zur Formatierung der LocalDate.
     * @param <T> der Typ der Elemente im TableView.
     * @return ein Callback zur Formatierung von LocalDate-Zellen.
     */
    public <T> Callback<TableColumn<T, LocalDate>, TableCell<T, LocalDate>> dateCellFactory(DateTimeFormatter dateFormatter) {
        return ignore -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : dateFormatter.format(item));
            }
        };
    }

    /**
     * Erstellt eine Zellfabrik zur Formatierung von LocalTime-Werten in einem TableView.
     * @param timeFormatter der DateTimeFormatter zur Formatierung der LocalTime.
     * @param <T> der Typ der Elemente im TableView.
     * @return ein Callback zur Formatierung von LocalTime-Zellen.
     */
    public <T> Callback<TableColumn<T, LocalTime>, TableCell<T, LocalTime>> timeCellFactory(DateTimeFormatter timeFormatter) {
        return ignore -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : timeFormatter.format(item));
            }
        };
    }

    /**
     * Fügt ein Suchfeld hinzu, um die Daten einer Tabelle zu filtern.
     * @param searchField Das Suchfeld (TextField), in das der Benutzer eingibt.
     * @param tableView Das TableView, das gefiltert werden soll.
     * @param filteredList Die Liste der Daten, die gefiltert werden soll.
     * @param <T> Der Typ der Daten in der Tabelle.
     * @param filterFunction Die Funktion, die definiert, wie gefiltert werden soll.
     */
    public <T> void addSearchFilter(TextField searchField, TableView<T> tableView, FilteredList<T> filteredList, BiPredicate<T, String> filterFunction) {
        // Standardmäßig: zeige alle Daten an, wenn das Suchfeld leer ist
        filteredList.setPredicate(ignore -> true); // Zeigt zunächst alle Daten an

        searchField.textProperty().addListener((ignore, ignore2, newValue) -> {
            filteredList.setPredicate(item -> {
                // Wenn das Suchfeld leer ist, zeige alle Daten an
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Anwenden der benutzerdefinierten Filterlogik
                String lowerCaseFilter = newValue.toLowerCase();
                return filterFunction.test(item, lowerCaseFilter);
            });

            // Aktualisiere die Tabelle mit den gefilterten Daten
            SortedList<T> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedList);
        });

        // Setze die sortierte Liste direkt, um die anfänglichen Daten anzuzeigen
        SortedList<T> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }
}
