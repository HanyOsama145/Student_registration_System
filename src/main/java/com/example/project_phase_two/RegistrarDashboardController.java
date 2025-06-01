package com.example.project_phase_two;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrarDashboardController {

    @FXML private ComboBox<String> department_combo_box;
    @FXML private ComboBox<String> course_to_remove_combo_box;
    @FXML private Button remove_course_button;
    @FXML private Label status_label;

    private ObservableList<Department> department_object_list = FXCollections.observableArrayList();
    private ObservableList<Course> courses_in_selected_department_list = FXCollections.observableArrayList();

    public void initialize() {
        load_departments();

        department_combo_box.getSelectionModel().selectedItemProperty().addListener((obs, old_val, new_val) -> {
            if (new_val != null) {
                Department selected_dept = get_selected_department_by_name(new_val);
                if (selected_dept != null) {
                    load_courses_for_department(selected_dept.get_department_id());
                    course_to_remove_combo_box.setDisable(false);
                }
            } else {
                course_to_remove_combo_box.getItems().clear();
                course_to_remove_combo_box.setDisable(true);
                remove_course_button.setDisable(true);
            }
        });

        course_to_remove_combo_box.getSelectionModel().selectedItemProperty().addListener((obs, old_val, new_val) -> {
            remove_course_button.setDisable(new_val == null);
        });

        course_to_remove_combo_box.setDisable(true);
        remove_course_button.setDisable(true);
        status_label.setText("");
    }

    private void load_departments() {
        department_object_list.clear();
        ObservableList<String> department_display_names = FXCollections.observableArrayList();
        String sql = "SELECT db_id, departmentName FROM departments ORDER BY departmentName";

        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("db_id");
                String name = rs.getString("departmentName");
                Department dept = new Department(id, name);
                department_object_list.add(dept);
                department_display_names.add(name);
            }
            department_combo_box.setItems(department_display_names);

        } catch (SQLException e) {
            e.printStackTrace();
            show_alert(Alert.AlertType.ERROR, "Database Error", "Failed to load departments: " + e.getMessage());
        }
    }

    private Department get_selected_department_by_name(String name) {
        for (Department dept : department_object_list) {
            if (dept.get_department_name().equals(name)) {
                return dept;
            }
        }
        return null;
    }

    private void load_courses_for_department(int department_id) {
        courses_in_selected_department_list.clear();
        ObservableList<String> course_display_names = FXCollections.observableArrayList();
        status_label.setText("");

        String sql = "SELECT c.courseCode, c.courseName, c.credit_hours " +
                "FROM courses c " +
                "JOIN department_courses dc ON c.courseCode = dc.courseCode " +
                "WHERE dc.department_id = ? " +
                "ORDER BY c.courseName";

        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, department_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourseCode(rs.getString("courseCode"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credit_hours"));

                courses_in_selected_department_list.add(course);
                course_display_names.add(course.getCourseName() + " (" + course.getCourseCode() + ")");
            }
            course_to_remove_combo_box.setItems(course_display_names);

            if (course_display_names.isEmpty()) {
                status_label.setText("No courses found for this department.");
                course_to_remove_combo_box.setDisable(true);
                remove_course_button.setDisable(true);
            } else {
                course_to_remove_combo_box.setDisable(false);
                remove_course_button.setDisable(course_to_remove_combo_box.getSelectionModel().isEmpty());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            show_alert(Alert.AlertType.ERROR, "Database Error", "Failed to load courses for department: " + e.getMessage());
        }
    }

    private Course get_selected_course_to_remove_by_display_name(String display_name) {
        if (display_name == null || !display_name.contains(" (") || !display_name.endsWith(")")) {
            return null;
        }
        String course_code = display_name.substring(display_name.lastIndexOf(" (") + 2, display_name.length() - 1);
        for (Course course : courses_in_selected_department_list) {
            if (course.getCourseCode().equals(course_code)) {
                return course;
            }
        }
        return null;
    }

    @FXML
    void handleRemoveCourseAction(ActionEvent event) {
        String selected_department_name = department_combo_box.getSelectionModel().getSelectedItem();
        String selected_course_display_info = course_to_remove_combo_box.getSelectionModel().getSelectedItem();

        if (selected_department_name == null || selected_course_display_info == null) {
            show_alert(Alert.AlertType.WARNING, "Selection Required", "Please select both a department and a course to remove.");
            return;
        }

        Department department = get_selected_department_by_name(selected_department_name);
        Course course = get_selected_course_to_remove_by_display_name(selected_course_display_info);

        if (department == null || course == null) {
            show_alert(Alert.AlertType.ERROR, "Selection Error", "Could not identify selected department or course. Please try again.");
            return;
        }

        Alert confirm_alert = new Alert(Alert.AlertType.CONFIRMATION);
        confirm_alert.setTitle("Confirm Removal");
        confirm_alert.setHeaderText("Remove '" + course.getCourseName() + "' from department '" + department.get_department_name() + "'?");
        confirm_alert.setContentText("Are you sure you want to proceed with this removal?");

        if (confirm_alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            perform_course_removal_from_department_db(department.get_department_id(), course.getCourseCode());
        }
    }

    private void perform_course_removal_from_department_db(int department_id, String course_code) {
        String sql = "DELETE FROM department_courses WHERE department_id = ? AND courseCode = ?";

        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, department_id);
            stmt.setString(2, course_code);

            int rows_affected = stmt.executeUpdate();

            if (rows_affected > 0) {
                show_alert(Alert.AlertType.INFORMATION, "Success", "Course successfully removed from the department.");
                status_label.setText("Course '" + course_code + "' removed from department ID " + department_id + ".");
                load_courses_for_department(department_id);
                course_to_remove_combo_box.getSelectionModel().clearSelection();
                remove_course_button.setDisable(true);
            } else {
                show_alert(Alert.AlertType.WARNING, "Failed", "Could not remove the course. It might have already been removed or was not found linked to this department.");
                status_label.setText("Failed to remove course or course not found in department.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            show_alert(Alert.AlertType.ERROR, "Database Error", "Error removing course: " + e.getMessage());
            status_label.setText("Error removing course: " + e.getMessage());
        }
    }

    private void show_alert(Alert.AlertType alert_type, String title, String message) {
        Alert alert = new Alert(alert_type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class Department {
        private int department_id_internal;
        private String department_name_internal;

        public Department(int id, String name) {
            this.department_id_internal = id;
            this.department_name_internal = name;
        }
        public int get_department_id() { return department_id_internal; }
        public String get_department_name() { return department_name_internal; }
    }
}