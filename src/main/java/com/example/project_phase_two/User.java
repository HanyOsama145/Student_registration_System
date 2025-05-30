package com.example.project_phase_two;

public abstract class User {
    protected String name;
    protected String ID;

    public User(String name, String ID) {
        this.name = name;
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public abstract void displayRole();
}