package com.example.project_phase_two;

public class LabCourse extends Course {
    private String labEquipment;
    private int labHours;

    public LabCourse(String courseCode, String courseName, String instructorName, int credits, String labEquipment, int labHours) {
        super(courseCode, courseName, instructorName, credits);
        this.labEquipment = labEquipment;
        this.labHours = labHours;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Lab Equipment: " + labEquipment + ", Lab Hours: " + labHours);
    }
}
