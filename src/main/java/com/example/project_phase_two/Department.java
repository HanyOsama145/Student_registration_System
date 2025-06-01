package com.example.project_phase_two;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String departmentName;
    private List<Course> offeredCourses;

    public Department(String departmentName) {
        this.departmentName = departmentName;
        this.offeredCourses = new ArrayList<>();
    }

    public void addCourseToDepartment(Course course) {
        offeredCourses.add(course);
        System.out.println("Course added to department: " + course.getCourseName());
    }

    public void removeCourse(Course course) {
        offeredCourses.remove(course);
        System.out.println("Course removed from department: " + course.getCourseName());
    }

    public void viewAllCourses() {
        System.out.println("Courses offered in " + departmentName + " department:");
        if (offeredCourses.isEmpty()) {
            System.out.println("No courses currently offered.");
        } else {
            for (Course course : offeredCourses) {
                course.displayInfo();
            }
        }
    }

    public String getDepartmentName() {
        return departmentName;
    }
}
