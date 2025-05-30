package com.example.project_phase_two;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecondStudentController {
    @FXML
    public Button One_course_button;
    @FXML
    public Button view_button;
    @FXML
    public Button more_course_button;
    @FXML
    public Button Drop_course_button;
    @FXML
    private Label welcome_label;
    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, String> codeCol;
    @FXML
    private TableColumn<Course, String> nameCol;
    @FXML
    private TableColumn<Course, Integer> creditsCol;


    public void initialize() {


    }

    public void setStudentName(String name) {
        if (welcome_label != null) {
            welcome_label.setText("Welcome " + name);
        }
    }

    public void now_one_button(ActionEvent actionEvent) {
        view_button.setVisible(false);
        more_course_button.setVisible(false);
        Drop_course_button.setVisible(false);
        One_course_button.setVisible(false);
        welcome_label.setVisible(false);
        courseTable.setVisible(true);
        loadCoursesFromDatabase();
    }

    public void now_view_button(ActionEvent actionEvent) {
        view_button.setVisible(false);
        more_course_button.setVisible(false);
        Drop_course_button.setVisible(false);
        One_course_button.setVisible(false);
        welcome_label.setVisible(false);
        courseTable.setVisible(true);
        loadCoursesFromDatabase();
    }

    public void now_alot_button(ActionEvent actionEvent) {
        view_button.setVisible(false);
        more_course_button.setVisible(false);
        Drop_course_button.setVisible(false);
        One_course_button.setVisible(false);
        welcome_label.setVisible(false);
        courseTable.setVisible(true);
        loadCoursesFromDatabase();
    }

    public void now_drop_button(ActionEvent actionEvent) {
        view_button.setVisible(false);
        more_course_button.setVisible(false);
        Drop_course_button.setVisible(false);
        One_course_button.setVisible(false);
        welcome_label.setVisible(false);
        courseTable.setVisible(true);
        loadCoursesFromDatabase();
    }
    public void loadCoursesFromDatabase() {
        ObservableList<Course> courses = FXCollections.observableArrayList();

        try (Connection conn = Database_connection.getConnection()) {
            String query = "SELECT courseCode, courseName, credit_hours FROM courses";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course("", "", "", 0);
                course.setCourseCode(rs.getString("courseCode"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credit_hours"));
                courses.add(course);
            }

            // Set up the columns
            codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
            creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));

            // Set table data
            courseTable.setItems(courses);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
