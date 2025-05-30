package com.example.project_phase_two;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Registrar extends User {
    private List<String> permissions;
    private String password;

    public Registrar(String name, String ID) {
        super(name, ID);
        this.password = password;
        this.permissions = new ArrayList<>();
        permissions.add("ADD_COURSE");
        permissions.add("REMOVE_COURSE");
    }

    public void addCourse(Course course, Department dept) {
        if (permissions.contains("ADD_COURSE")) {
            dept.addCourseToDepartment(course);
            System.out.println("Course " + course.getCourseName() + " has been added to the " + dept.getDepartmentName() + " department.");
        } else {
            System.out.println("Permission denied: cannot add course.");
        }
    }

    public void removeCourse(Course course, Department dept) {
        if (permissions.contains("REMOVE_COURSE")) {
            dept.removeCourse(course);
            System.out.println("Course " + course.getCourseName() + " has been removed from the " + dept.getDepartmentName() + " department.");
        } else {
            System.out.println("Permission denied: cannot remove course.");
        }
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Registrar");
    }

    // ✅ Save registrar to database
    public void saveToDatabase() {
        String sql = "INSERT INTO registrars (name, ID, password) VALUES (?, ?, ?)";

        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.name);
            stmt.setString(2, this.ID);
            stmt.setString(3, this.password);
            stmt.executeUpdate();

            System.out.println("✅ Registrar saved to database successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Error saving registrar: " + e.getMessage());
        }
    }

    // ✅ Static method for authentication
    public static Registrar authenticate(String ID, String password) {
        String sql = "SELECT name FROM registrars WHERE ID = ? AND password = ?";

        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ID);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                return new Registrar(name, ID); // return authenticated Registrar
            }

        } catch (SQLException e) {
            System.out.println("❌ Error during authentication: " + e.getMessage());
        }

        return null; // failed login
    }
}
