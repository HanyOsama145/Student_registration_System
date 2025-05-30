package com.example.project_phase_two;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private List<Course> coursesTeaching;

    public Instructor(String name, String ID) {
        super(name, ID);
        this.coursesTeaching = new ArrayList<>();
    }

    public void assignCourse(Course course) {
        coursesTeaching.add(course);
        System.out.println(name + " is now assigned to teach: " + course.getCourseName());
    }

    public void viewCourses() {
        System.out.println("Courses taught by " + name + ":");
        for (Course course : coursesTeaching) {
            course.displayInfo();
        }
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Instructor");
    }
}
