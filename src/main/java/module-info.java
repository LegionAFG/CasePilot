module com.badr.cp_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.badr.cp_project to javafx.fxml;
    opens com.badr.cp_project.run to javafx.fxml;
    opens com.badr.cp_project.controller to javafx.fxml;
    opens com.badr.cp_project.model to javafx.base; // Diese Zeile hinzufügen

    exports com.badr.cp_project.run;
    exports com.badr.cp_project.controller;
    exports com.badr.cp_project.model;
}
