package com.example.project_phase_two;

public class Course {
    protected String courseCode;
    protected String courseName;
    protected String instructorName;
    protected int credits;

    // Default constructor (required by TableView)
    public Course() {}

    // Full constructor
    public Course(String courseCode, String courseName, String instructorName, int credits) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.credits = credits;
    }

    // ✅ These must match the exact property names used in PropertyValueFactory
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void displayInfo() {
        System.out.println(courseCode + ": " + courseName + " taught by " + instructorName + " (" + credits + " credits)");
    }
}
