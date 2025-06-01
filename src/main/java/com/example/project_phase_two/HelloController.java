package com.example.project_phase_two;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    @FXML
    private TextField ID_field;
    @FXML
    private TextField Password_Field;
    @FXML
    private Button Login_button;

    public void initialize() {
        Password_Field.setDisable(true);
        ID_field.textProperty().addListener((observable, oldValue, newValue) -> {
            Password_Field.setDisable(newValue.trim().isEmpty());
        });

        Platform.runLater(() -> {
            Scene scene = ID_field.getScene();
            if (scene != null) {
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.INSERT) {
                        openAddStudentWindow();
                    }
                });
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.R) {
                        openRegistrarLoginWindow();
                        event.consume();
                    }
                });
            }
        });
    }

    private void openRegistrarLoginWindow() {
        Stage loginPopup = new Stage(); // Renamed for clarity
        loginPopup.initModality(Modality.APPLICATION_MODAL);
        // loginPopup.initOwner(Login_button.getScene().getWindow()); // Optional: set owner
        loginPopup.setTitle("Registrar Login");

        TextField idField = new TextField();
        idField.setPromptText("Enter Registrar ID");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        Button doLoginButton = new Button("Login"); // Renamed for clarity

        Label errorLabel = new Label(); // For displaying login errors
        errorLabel.setStyle("-fx-text-fill: red;");

        doLoginButton.setOnAction(e -> {
            errorLabel.setText(""); // Clear previous errors
            String enteredId = idField.getText();
            String enteredPassword = passwordField.getText();

            if (enteredId.isEmpty() || enteredPassword.isEmpty()) {
                errorLabel.setText("Please fill all fields.");
                return;
            }

            Registrar registrar = Registrar.authenticate(enteredId, enteredPassword); // Assuming this method exists
            if (registrar != null) {
                // showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + registrar.getName() + "!");
                loginPopup.close(); // Close the login popup

                // ⭐ Open the Registrar Dashboard
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project_phase_two/registrar_dashboard.fxml")); // Adjust path
                    Parent registrarDashboardRoot = loader.load();

                    // You can pass the registrar object to the dashboard controller if needed
                    // RegistrarDashboardController dashboardController = loader.getController();
                    // dashboardController.setRegistrar(registrar);

                    Stage dashboardStage = new Stage();
                    dashboardStage.setTitle("Registrar Dashboard - " + registrar.getName());
                    dashboardStage.setScene(new Scene(registrarDashboardRoot));
                    dashboardStage.show();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not open registrar dashboard.");
                }
            } else {
                errorLabel.setText("Invalid ID or password.");
            }
        });

        VBox layout = new VBox(10, new Label("Registrar ID:"), idField, new Label("Password:"), passwordField, doLoginButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center-left;");
        loginPopup.setScene(new Scene(layout, 350, 250));
        loginPopup.showAndWait();
    }

    public void Log_Action(ActionEvent actionEvent) {
        String id = ID_field.getText();
        String password = Password_Field.getText();

        if (id.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "ID and Password cannot be empty.");
            return;
        }

        try (Connection conn = Database_connection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM students WHERE ID = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, id);
                    stmt.setString(2, password);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String studentName = rs.getString("name");
                        int actualStudentId = Integer.parseInt(id); // Assuming ID from field is the correct integer ID

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project_phase_two/second_student.fxml"));
                            Parent root = loader.load();

                            SecondStudentController controller = loader.getController();
                            controller.setStudentName(studentName);
                            controller.setCurrentStudentId(actualStudentId);

                            Stage stage = (Stage) Login_button.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Student Page - " + studentName);
                            stage.show();

                        } catch (IOException | NumberFormatException ex) { // Catch NumberFormatException too
                            ex.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Application Error", "Error loading student page or processing ID: " + ex.getMessage());
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid ID or password.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database operation failed: " + sqlException.getMessage());
        } catch (Exception ex) { // General exception catch
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + ex.getMessage());
        }
    }
    // Overloaded showAlert for convenience
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Original showAlert if needed for just message
    private void showAlert(Alert.AlertType type, String message) {
        showAlert(type, type.toString(), message); // Use AlertType as title if not provided
    }

    private void openAddStudentWindow() {
        // ... (your existing openAddStudentWindow method)
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Student");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");
        TextField idField = new TextField();
        idField.setPromptText("Enter ID");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        Button saveButton = new Button("Save Student");
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String newStudentId = idField.getText();
            String newStudentPassword = passwordField.getText();
            if (name.isEmpty() || newStudentId.isEmpty() || newStudentPassword.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields.").show();
                return;
            }
            Student student = new Student(name, newStudentId, newStudentPassword); // Assuming constructor
            try {
                student.saveToDatabase(); // Assuming method exists
                new Alert(Alert.AlertType.INFORMATION, "Student added successfully!").show();
                popup.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error saving student: " + ex.getMessage()).show();
            }
        });
        VBox layout = new VBox(10, nameField, idField, passwordField, saveButton);
        layout.setStyle("-fx-padding: 20;");
        popup.setScene(new Scene(layout, 300, 200));
        popup.showAndWait();
    }
}