package com.example.smartblanket.Classes;

public class Room {
    private String RoomNumber;
    Room(){};

    public void setRoomNumber(String roomNumber) {
        RoomNumber = roomNumber;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public Room(String roomNumber) {
        RoomNumber = roomNumber;
    }
}
