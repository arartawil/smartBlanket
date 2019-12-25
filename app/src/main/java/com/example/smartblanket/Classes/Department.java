package com.example.smartblanket.Classes;

public class Department {
    private String ID;
    private String NumberOfRooms;
    private String NumberOfBeds;
    private boolean Active;
    Department(){}
    public Department(String ID, String numberOfRooms, String numberOfBeds, boolean active) {
        this.ID = ID;
        NumberOfRooms = numberOfRooms;
        NumberOfBeds = numberOfBeds;
        Active = active;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setNumberOfRooms(String numberOfRooms) {
        NumberOfRooms = numberOfRooms;
    }

    public void setNumberOfBeds(String numberOfBeds) {
        NumberOfBeds = numberOfBeds;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    public String getID() {
        return ID;
    }

    public String getNumberOfRooms() {
        return NumberOfRooms;
    }

    public String getNumberOfBeds() {
        return NumberOfBeds;
    }

    public boolean isActive() {
        return Active;
    }
}
