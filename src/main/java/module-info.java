module com.example.project_phase_two {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;

    opens com.example.project_phase_two to javafx.fxml;
    exports com.example.project_phase_two;
}