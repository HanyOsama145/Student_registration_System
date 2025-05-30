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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Platform;

public class HelloController {
    @FXML
    public Label welcome_label;
    @FXML
    private TextField ID_field;
    @FXML
    private TextField Password_Field;
    @FXML
    private Button Login_button;



    public void initialize() {
        //Welcome_student.setVisible(false);

        Password_Field.setDisable(true);

        // Enable password field when ID is typed
        ID_field.textProperty().addListener((observable, oldValue, newValue) -> {
            Password_Field.setDisable(newValue.trim().isEmpty());
        });

        // Use Platform.runLater to wait for the scene to be ready
        Platform.runLater(() -> {
            Scene scene = ID_field.getScene();
            if (scene != null) {
                // INSERT = Add Student
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.INSERT) {
                        openAddStudentWindow();
                    }
                });

                // Ctrl+R = Registrar login
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
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Registrar Login");

        TextField idField = new TextField();
        idField.setPromptText("Enter Registrar ID");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");

        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> {
            String enteredId = idField.getText();
            String enteredPassword = passwordField.getText();

            if (enteredId.isEmpty() || enteredPassword.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields.").show();
                return;
            }

            Registrar registrar = Registrar.authenticate(enteredId, enteredPassword);
            if (registrar != null) {
                new Alert(Alert.AlertType.INFORMATION, "Welcome " + registrar.getName() + "!").show();
                popup.close();



            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid ID or password.").show();
            }
        });

        VBox layout = new VBox(10, idField, passwordField, loginButton);
        layout.setStyle("-fx-padding: 20;");
        popup.setScene(new Scene(layout, 300, 180));
        popup.showAndWait();
    }




    public void Log_Action(ActionEvent actionEvent) {
        String id = ID_field.getText();
        String password = Password_Field.getText();

        try (Connection conn = Database_connection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM students WHERE ID = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, id);
                    stmt.setString(2, password);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String studentName = rs.getString("name");

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project_phase_two/second_student.fxml"));
                            Parent root = loader.load();

// ✅ Pass student name to second controller
                            SecondStudentController controller = loader.getController();
                            controller.setStudentName(studentName);

                            Stage stage = (Stage) Login_button.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Student Page");
                            stage.show();


                        } catch (IOException e) {
                            e.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Error loading scene: " + e.getMessage());
                        }


                    } else {
                        showAlert(Alert.AlertType.ERROR, "Invalid ID or password.");
                    }
                }
            }
        } catch (SQLException sqlException) {
            showAlert(Alert.AlertType.ERROR, "Database error: " + sqlException.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.showAndWait();
    }



    private void openAddStudentWindow() {
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
            String id = idField.getText();
            String password = passwordField.getText();

            if (name.isEmpty() || id.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields.").show();
                return;
            }

            // ✅ Create Student object and save using the object method
            Student student = new Student(name, id, password);
            try {
                student.saveToDatabase();
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
