package com.example.project_phase_two;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Student extends User implements Registrable {
    private Schedule schedule;
    private String password;

    public Student(String name, String ID, String password) {
        super(name, ID);
        this.password = password;
        this.schedule = new Schedule(ID); // Pass student ID to Schedule
    }

    @Override
    public void register(Course course) {
        schedule.addCourse(course);  // This now updates the database
        System.out.println(name + " has registered for the course: " + course.getCourseName());
    }

    @Override
    public void drop(Course course) {
        schedule.removeCourse(course); // This also updates the database
        System.out.println(name + " has dropped the course: " + course.getCourseName());
    }

    public void viewSchedule() {
        schedule.loadCoursesFromDatabase(); // Load from database
        System.out.println("Schedule for " + name + ":");
        schedule.viewCourses(); // Display current list
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Student");
    }

    // ✅ Save student to database
    public void saveToDatabase() {
        String sql = "INSERT INTO students (name, ID, password) VALUES (?, ?, ?)";

        try (Connection conn = Database_connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.name);
            stmt.setString(2, this.ID);
            stmt.setString(3, this.password);
            stmt.executeUpdate();

            System.out.println("✅ Student saved to database successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Error saving student: " + e.getMessage());
        }
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
