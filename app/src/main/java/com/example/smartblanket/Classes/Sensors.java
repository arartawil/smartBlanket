package com.example.smartblanket.Classes;

public class Sensors {
    public String id;
    public String Type;
    public Boolean ActiveState;

    public Sensors(){}

    public Sensors(String type, Boolean activeState) {
        Type = type;
        ActiveState = activeState;
    }

    public void getId(String id){
        this.id=id;
    }
}
