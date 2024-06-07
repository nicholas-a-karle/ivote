package com.ivote;

public class Student {
    private String name;
    private String id;

    // Constructor
    public Student() {}

    public Student(String name, String id) {
        this.name = name;
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Override toString for better readability
    @Override
    public String toString() {
        return "Student{" +
               "name='" + name + '\'' +
               ", id='" + id + '\'' +
               '}';
    }
}
