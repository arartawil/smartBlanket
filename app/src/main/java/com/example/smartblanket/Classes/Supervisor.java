package com.example.smartblanket.Classes;

import com.example.smartblanket.Classes.Patient;

public class Supervisor {
    private String Id;
    private String Name;
    private String Password;
    private String Username;
    private Patient Patient;
    private String Department;

    public Supervisor(String name, String password, String username, com.example.smartblanket.Classes.Patient patient, String department) {
        Name = name;
        Password = password;
        Username = username;
        Patient = patient;
        Department = department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getDepartment() {
        return Department;
    }

    public void setPatient(Patient patient) {
        this.Patient = patient;
    }

    public Patient getPatient() {
        return Patient;
    }

    Supervisor() {
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setUsername(String username) {
        Username = username;
    }


    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public String getId() {
        return Id;
    }

    public String getPassword() {
        return Password;
    }

    public String getUsername() {
        return Username;
    }

}
