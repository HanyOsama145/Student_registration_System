package com.example.project_phase_two;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecondStudentController {

    //<editor-fold desc="FXML Declarations - Grouped Together">
    @FXML public Button One_course_button;
    @FXML public Button view_button;
    @FXML public Button more_course_button; // Main menu button to enter multi-add mode
    @FXML public Button Drop_course_button;
    @FXML public ImageView Back_button;
    @FXML public Button Multiple_course; // This button CONFIRMS adding multiple selected courses
    @FXML private Label welcome_label;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> codeCol;
    @FXML private TableColumn<Course, String> nameCol;
    @FXML private TableColumn<Course, Integer> creditsCol;

    @FXML private ComboBox<String> courseComboBox;
    @FXML private ComboBox<String> courseComboBox2;
    @FXML private ComboBox<String> courseComboBox3;
    @FXML private Button addCourseButton; // Confirms single add
    // @FXML private Button addCoursesButton; // REMOVED - Replaced by Multiple_course for this action

    @FXML private ComboBox<String> dropCourseComboBox;
    @FXML private Button confirmDropButton;
    //</editor-fold>

    private int currentStudentId;
    private ObservableList<String> allAvailableCourseNamesMasterList;
    private ObservableList<String> enrolledCourseNamesForDrop;
    private ObservableList<Course> currentTableCourses;

    private ChangeListener<String> courseCombo1Listener;
    private ChangeListener<String> courseCombo2Listener;
    private ChangeListener<String> courseCombo3Listener;
    private boolean isUpdatingComboBoxesProgrammatically = false;

    public void initialize() {
        allAvailableCourseNamesMasterList = FXCollections.observableArrayList();
        enrolledCourseNamesForDrop = FXCollections.observableArrayList();
        currentTableCourses = FXCollections.observableArrayList();

        if (courseTable != null) {
            courseTable.setItems(currentTableCourses);
            if (codeCol != null) codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
            if (nameCol != null) nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
            if (creditsCol != null) creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        }

        setupBackButtonHoverEffect();
        loadAllCourseNamesForRegistrationMasterList();
        setupMultiAddComboBoxListeners();
        resetToInitialView(); // This will hide Multiple_course initially
    }

    private void setupBackButtonHoverEffect() {
        if (Back_button != null) {
            DropShadow glow = new DropShadow();
            glow.setColor(Color.rgb(0, 150, 255, 0.7));
            glow.setRadius(20);
            glow.setSpread(0.3);
            Back_button.setOnMouseEntered(event -> {
                Back_button.setEffect(glow);
                Back_button.setOpacity(0.85);
            });
            Back_button.setOnMouseExited(event -> {
                Back_button.setEffect(null);
                Back_button.setOpacity(1.0);
            });
        }
    }

    public void setStudentName(String name) {
        if (welcome_label != null) welcome_label.setText("Welcome " + name);
    }

    public void setCurrentStudentId(int studentId) {
        this.currentStudentId = studentId;
    }

    private void hideAllSecondaryUIElements() {
        if (courseTable != null) courseTable.setVisible(false);
        if (courseComboBox != null) courseComboBox.setVisible(false);
        if (courseComboBox2 != null) courseComboBox2.setVisible(false);
        if (courseComboBox3 != null) courseComboBox3.setVisible(false);
        if (addCourseButton != null) addCourseButton.setVisible(false);
        if (Multiple_course != null) Multiple_course.setVisible(false); // Hide this one too
        if (dropCourseComboBox != null) dropCourseComboBox.setVisible(false);
        if (confirmDropButton != null) confirmDropButton.setVisible(false);
    }

    private void hideMainMenu() {
        if (One_course_button != null) One_course_button.setVisible(false);
        if (view_button != null) view_button.setVisible(false);
        if (more_course_button != null) more_course_button.setVisible(false); // This is the main menu button
        if (Drop_course_button != null) Drop_course_button.setVisible(false);
        if (welcome_label != null) welcome_label.setVisible(false);
    }

    private void resetToInitialView() {
        hideMainMenu();
        hideAllSecondaryUIElements(); // Ensures Multiple_course is hidden

        if (One_course_button != null) One_course_button.setVisible(true);
        if (view_button != null) view_button.setVisible(true);
        if (more_course_button != null) more_course_button.setVisible(true);
        if (Drop_course_button != null) Drop_course_button.setVisible(true);
        if (welcome_label != null) welcome_label.setVisible(true);
        if (Back_button != null) Back_button.setVisible(false);

        isUpdatingComboBoxesProgrammatically = true;
        try {
            if (courseComboBox != null) {
                courseComboBox.getSelectionModel().clearSelection();
                courseComboBox.setItems(allAvailableCourseNamesMasterList);
            }
            if (courseComboBox2 != null) {
                courseComboBox2.getSelectionModel().clearSelection();
                courseComboBox2.setItems(allAvailableCourseNamesMasterList);
            }
            if (courseComboBox3 != null) {
                courseComboBox3.getSelectionModel().clearSelection();
                courseComboBox3.setItems(allAvailableCourseNamesMasterList);
            }
        } finally {
            isUpdatingComboBoxesProgrammatically = false;
        }
    }

    @FXML
    void handleBackButtonClick(MouseEvent event) {
        resetToInitialView();
    }

    @FXML
    void now_one_button(ActionEvent event) {
        hideMainMenu();
        hideAllSecondaryUIElements();
        if (courseTable != null) courseTable.setVisible(true);
        if (courseComboBox != null) {
            courseComboBox.setVisible(true);
            isUpdatingComboBoxesProgrammatically = true;
            try {
                courseComboBox.setItems(allAvailableCourseNamesMasterList);
                courseComboBox.getSelectionModel().clearSelection();
            } finally {
                isUpdatingComboBoxesProgrammatically = false;
            }
        }
        if (addCourseButton != null) addCourseButton.setVisible(true);
        if (Back_button != null) Back_button.setVisible(true);
        populateTableWithAllCourses();
    }

    @FXML
    void now_view_button(ActionEvent event) {
        hideMainMenu();
        hideAllSecondaryUIElements();
        if (courseTable != null) courseTable.setVisible(true);
        if (Back_button != null) Back_button.setVisible(true);
        loadEnrolledCoursesIntoTable();
    }

    // This method is called by your 'more_course_button' (or similar) from the FXML
    @FXML
    void now_alot_button(ActionEvent event) {
        hideMainMenu();
        hideAllSecondaryUIElements();
        if (courseTable != null) courseTable.setVisible(true);
        if (courseComboBox != null) courseComboBox.setVisible(true);
        if (courseComboBox2 != null) courseComboBox2.setVisible(true);
        if (courseComboBox3 != null) courseComboBox3.setVisible(true);
        if (Multiple_course != null) Multiple_course.setVisible(true); // ⭐ Show your 'Multiple_course' button
        if (Back_button != null) Back_button.setVisible(true);

        isUpdatingComboBoxesProgrammatically = true;
        try {
            if (courseComboBox != null) courseComboBox.getSelectionModel().clearSelection();
            if (courseComboBox2 != null) courseComboBox2.getSelectionModel().clearSelection();
            if (courseComboBox3 != null) courseComboBox3.getSelectionModel().clearSelection();
        } finally {
            isUpdatingComboBoxesProgrammatically = false;
        }
        updateMultiAddComboBoxItems();
        populateTableWithAllCourses();
    }


    @FXML
    void now_drop_button(ActionEvent event) {
        hideMainMenu();
        hideAllSecondaryUIElements();
        if (courseTable != null) courseTable.setVisible(true);
        if (dropCourseComboBox != null) dropCourseComboBox.setVisible(true);
        if (confirmDropButton != null) confirmDropButton.setVisible(true);
        if (Back_button != null) Back_button.setVisible(true);
        loadEnrolledCoursesForDrop();
    }

    private void loadAllCourseNamesForRegistrationMasterList() {
        allAvailableCourseNamesMasterList.clear();
        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT courseName FROM courses ORDER BY courseName");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allAvailableCourseNamesMasterList.add(rs.getString("courseName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load master course list: " + e.getMessage());
        }
    }

    private void setupMultiAddComboBoxListeners() {
        courseCombo1Listener = (obs, oldVal, newVal) -> {
            if (!isUpdatingComboBoxesProgrammatically) updateMultiAddComboBoxItems();
        };
        courseCombo2Listener = (obs, oldVal, newVal) -> {
            if (!isUpdatingComboBoxesProgrammatically) updateMultiAddComboBoxItems();
        };
        courseCombo3Listener = (obs, oldVal, newVal) -> {
            if (!isUpdatingComboBoxesProgrammatically) updateMultiAddComboBoxItems();
        };

        if (courseComboBox != null) courseComboBox.getSelectionModel().selectedItemProperty().addListener(courseCombo1Listener);
        if (courseComboBox2 != null) courseComboBox2.getSelectionModel().selectedItemProperty().addListener(courseCombo2Listener);
        if (courseComboBox3 != null) courseComboBox3.getSelectionModel().selectedItemProperty().addListener(courseCombo3Listener);
    }

    private void updateMultiAddComboBoxItems() {
        if (isUpdatingComboBoxesProgrammatically) return;
        isUpdatingComboBoxesProgrammatically = true;

        try {
            String s1 = (courseComboBox != null) ? courseComboBox.getSelectionModel().getSelectedItem() : null;
            String s2 = (courseComboBox2 != null) ? courseComboBox2.getSelectionModel().getSelectedItem() : null;
            String s3 = (courseComboBox3 != null) ? courseComboBox3.getSelectionModel().getSelectedItem() : null;

            if (courseComboBox != null) {
                ObservableList<String> items1 = FXCollections.observableArrayList(allAvailableCourseNamesMasterList);
                if (s2 != null) items1.remove(s2);
                if (s3 != null) items1.remove(s3);
                courseComboBox.setItems(items1);
                if (s1 != null && items1.contains(s1)) courseComboBox.setValue(s1);
                else if (s1 != null) courseComboBox.getSelectionModel().clearSelection();
            }

            if (courseComboBox2 != null) {
                ObservableList<String> items2 = FXCollections.observableArrayList(allAvailableCourseNamesMasterList);
                if (s1 != null) items2.remove(s1);
                if (s3 != null) items2.remove(s3);
                courseComboBox2.setItems(items2);
                if (s2 != null && items2.contains(s2)) courseComboBox2.setValue(s2);
                else if (s2 != null) courseComboBox2.getSelectionModel().clearSelection();
            }

            if (courseComboBox3 != null) {
                ObservableList<String> items3 = FXCollections.observableArrayList(allAvailableCourseNamesMasterList);
                if (s1 != null) items3.remove(s1);
                if (s2 != null) items3.remove(s2);
                courseComboBox3.setItems(items3);
                if (s3 != null && items3.contains(s3)) courseComboBox3.setValue(s3);
                else if (s3 != null) courseComboBox3.getSelectionModel().clearSelection();
            }
        } finally {
            isUpdatingComboBoxesProgrammatically = false;
        }
    }


    private void populateTableWithAllCourses() {
        currentTableCourses.clear();
        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT courseCode, courseName, credit_hours FROM courses");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course course = new Course();
                course.setCourseCode(rs.getString("courseCode"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credit_hours"));
                currentTableCourses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load all courses into table: " + e.getMessage());
        }
    }

    private void loadEnrolledCoursesIntoTable() {
        currentTableCourses.clear();
        if (this.currentStudentId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Student Error", "Student not identified. Cannot load schedule.");
            return;
        }
        String sql = "SELECT c.courseCode, c.courseName, c.credit_hours " +
                "FROM courses c JOIN schedules s ON c.courseCode = s.courseCode " +
                "WHERE s.student_ID = ?";
        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.currentStudentId);
            ResultSet rs = stmt.executeQuery();
            boolean hasCourses = false;
            while (rs.next()) {
                hasCourses = true;
                Course course = new Course();
                course.setCourseCode(rs.getString("courseCode"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credit_hours"));
                currentTableCourses.add(course);
            }
            if (!hasCourses) {
                showAlert(Alert.AlertType.INFORMATION, "Schedule Empty", "You are not currently enrolled in any courses.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load your enrolled courses: " + e.getMessage());
        }
    }

    private void loadEnrolledCoursesForDrop() {
        currentTableCourses.clear();
        enrolledCourseNamesForDrop.clear();
        if (this.currentStudentId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Student Error", "Student not identified. Cannot load courses to drop.");
            if (dropCourseComboBox != null) dropCourseComboBox.setDisable(true);
            if (confirmDropButton != null) confirmDropButton.setDisable(true);
            return;
        }
        String sql = "SELECT c.courseCode, c.courseName, c.credit_hours " +
                "FROM courses c JOIN schedules s ON c.courseCode = s.courseCode " +
                "WHERE s.student_ID = ?";
        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.currentStudentId);
            ResultSet rs = stmt.executeQuery();
            boolean hasCourses = false;
            while (rs.next()) {
                hasCourses = true;
                Course course = new Course();
                course.setCourseCode(rs.getString("courseCode"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credit_hours"));
                currentTableCourses.add(course);
                enrolledCourseNamesForDrop.add(course.getCourseName());
            }
            if (dropCourseComboBox != null) {
                dropCourseComboBox.setItems(enrolledCourseNamesForDrop);
                dropCourseComboBox.setDisable(!hasCourses);
            }
            if (confirmDropButton != null) {
                confirmDropButton.setDisable(!hasCourses);
            }
            if (!hasCourses) {
                showAlert(Alert.AlertType.INFORMATION, "No Courses to Drop", "You are not enrolled in any courses to drop.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load your courses for dropping: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddCourse(ActionEvent event) { // Handles single course add
        String selectedCourseName = courseComboBox.getSelectionModel().getSelectedItem();
        if (selectedCourseName != null) {
            addSingleCourseToScheduleDB(selectedCourseName, "Course '" + selectedCourseName + "' added successfully.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select a course to add.");
        }
    }

    // This method should be the onAction for your 'Multiple_course' Button in FXML
    @FXML
    private void handleAddMultipleCourses(ActionEvent event) {
        Set<String> selectedCourseNames = new HashSet<>();
        String c1 = (courseComboBox != null) ? courseComboBox.getSelectionModel().getSelectedItem() : null;
        String c2 = (courseComboBox2 != null) ? courseComboBox2.getSelectionModel().getSelectedItem() : null;
        String c3 = (courseComboBox3 != null) ? courseComboBox3.getSelectionModel().getSelectedItem() : null;

        if (c1 != null) selectedCourseNames.add(c1);
        if (c2 != null) selectedCourseNames.add(c2);
        if (c3 != null) selectedCourseNames.add(c3);

        int nonNullSelectionsCount = 0;
        if (c1 != null) nonNullSelectionsCount++;
        if (c2 != null) nonNullSelectionsCount++;
        if (c3 != null) nonNullSelectionsCount++;

        if (nonNullSelectionsCount > 0 && selectedCourseNames.size() < nonNullSelectionsCount) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Selection", "The same course cannot be selected in multiple slots. Please select different courses.");
            return;
        }
        if (selectedCourseNames.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select at least one course.");
            return;
        }

        int successCount = 0;
        List<String> added = new ArrayList<>(), failed = new ArrayList<>();
        for (String name : selectedCourseNames) {
            if (addSingleCourseToScheduleDB(name, null)) {
                successCount++;
                added.add(name);
            } else {
                failed.add(name);
            }
        }
        StringBuilder summary = new StringBuilder();
        if (!added.isEmpty()) summary.append("Successfully added: ").append(String.join(", ", added)).append(".\n");
        if (!failed.isEmpty()) summary.append("Failed or already enrolled: ").append(String.join(", ", failed)).append(".");
        if (summary.length() == 0) summary.append("No new courses were added.");
        showAlert(Alert.AlertType.INFORMATION, "Registration Summary", summary.toString());

        if (successCount > 0 || nonNullSelectionsCount > 0) {
            isUpdatingComboBoxesProgrammatically = true;
            try {
                if (courseComboBox != null) courseComboBox.getSelectionModel().clearSelection();
                if (courseComboBox2 != null) courseComboBox2.getSelectionModel().clearSelection();
                if (courseComboBox3 != null) courseComboBox3.getSelectionModel().clearSelection();
            } finally {
                isUpdatingComboBoxesProgrammatically = false;
            }
            updateMultiAddComboBoxItems();
        }
    }

    private boolean addSingleCourseToScheduleDB(String courseName, String successMsgOverride) {
        if (this.currentStudentId <= 0) {
            if (successMsgOverride != null) showAlert(Alert.AlertType.ERROR, "Authentication Error", "Student ID not found.");
            return false;
        }
        String courseCode = getCourseCodeByName(courseName);
        if (courseCode == null) {
            if (successMsgOverride != null) showAlert(Alert.AlertType.ERROR, "Course Error", "Details for '" + courseName + "' not found.");
            return false;
        }
        try (Connection conn = Database_connection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM schedules WHERE student_ID = ? AND courseCode = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, this.currentStudentId);
                checkStmt.setString(2, courseCode);
                ResultSet rsCheck = checkStmt.executeQuery();
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    if (successMsgOverride != null) showAlert(Alert.AlertType.INFORMATION, "Already Enrolled", "Enrolled in '" + courseName + "'.");
                    return false;
                }
            }
            String insertSql = "INSERT INTO schedules (student_ID, courseCode) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, this.currentStudentId);
                insertStmt.setString(2, courseCode);
                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    if (successMsgOverride != null) showAlert(Alert.AlertType.INFORMATION, "Success", successMsgOverride);
                    return true;
                } else {
                    if (successMsgOverride != null) showAlert(Alert.AlertType.ERROR, "Failed", "Failed to add '" + courseName + "'.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (successMsgOverride != null) showAlert(Alert.AlertType.ERROR, "Database Error", "Could not add '" + courseName + "': " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void handleConfirmDropCourse(ActionEvent event) {
        String selectedCourseNameToDrop = dropCourseComboBox.getSelectionModel().getSelectedItem();
        if (selectedCourseNameToDrop == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select a course to drop.");
            return;
        }
        if (this.currentStudentId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "Student ID not found.");
            return;
        }
        String courseCodeToDrop = getCourseCodeByName(selectedCourseNameToDrop);
        if (courseCodeToDrop == null) {
            showAlert(Alert.AlertType.ERROR, "Course Error", "Details for '" + selectedCourseNameToDrop + "' not found.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Drop " + selectedCourseNameToDrop + "?", ButtonType.OK, ButtonType.CANCEL);
        confirmationAlert.setTitle("Confirm Drop");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Database_connection.getConnection()) {
                    String deleteSql = "DELETE FROM schedules WHERE student_ID = ? AND courseCode = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                        stmt.setInt(1, this.currentStudentId);
                        stmt.setString(2, courseCodeToDrop);
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "'" + selectedCourseNameToDrop + "' dropped.");
                            loadEnrolledCoursesForDrop();
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Not Dropped", "'" + selectedCourseNameToDrop + "' not found or not dropped.");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Error dropping course: " + e.getMessage());
                }
            }
        });
    }

    private String getCourseCodeByName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) return null;
        String sql = "SELECT courseCode FROM courses WHERE courseName = ?";
        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("courseCode");
            } else {
                System.err.println("No course code found for course name: " + courseName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not retrieve code for: " + courseName);
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}