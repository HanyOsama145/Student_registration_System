package com.example.project_phase_two;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<Course> courses;
    private String studentId;

    public Schedule(String studentId) {
        this.courses = new ArrayList<>();
        this.studentId = studentId;
    }

    public void addCourse(Course course) {
        try (Connection conn = Database_connection.getConnection()) {
            String sql = "INSERT INTO student_schedule (student_id, course_code) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId);
            stmt.setString(2, course.courseCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                courses.add(course);
                System.out.println("Course added to schedule: " + course.getCourseName());
            } else {
                System.out.println("Failed to add course to database.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    public void removeCourse(Course course) {
        try (Connection conn = Database_connection.getConnection()) {
            String sql = "DELETE FROM student_schedule WHERE student_id = ? AND course_code = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId);
            stmt.setString(2, course.courseCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                courses.remove(course);
                System.out.println("Course removed from schedule: " + course.getCourseName());
            } else {
                System.out.println("Course not found in schedule.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing course: " + e.getMessage());
        }
    }

    public void loadCoursesFromDatabase() {
        courses.clear();
        try (Connection conn = Database_connection.getConnection()) {
            String sql = "SELECT c.courseCode, c.courseName, i.name AS instructorName, c.credit_hours " +
                    "FROM student_schedule s " +
                    "JOIN courses c ON s.course_code = c.courseCode " +
                    "JOIN instructors i ON c.instructor_id = i.id " +
                    "WHERE s.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, studentId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        rs.getString("instructorName"),
                        rs.getInt("credit_hours")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error loading schedule: " + e.getMessage());
        }
    }

    public void viewCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses in schedule.");
        } else {
            for (Course course : courses) {
                course.displayInfo();
            }
        }
    }

    public List<Course> getCourses() {
        return courses;
    }
}
