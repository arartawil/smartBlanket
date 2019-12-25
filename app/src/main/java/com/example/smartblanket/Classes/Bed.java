package com.example.smartblanket.Classes;

import java.io.Serializable;

public class Bed implements Serializable{
    public String id;
    public Boolean Active;
    public String BedNumber;
    public String PatientId;
    public com.example.smartblanket.Classes.Sensors Sensors;

    public Bed(){}

    public Bed(Boolean active, String bedNumber, Sensors sensors, String patientId) {
        this.Active = active;
        this.BedNumber = bedNumber;
        this.Sensors = sensors;
        this.PatientId = patientId;
    }


   public void getId(String id){
        this.id=id;
   }
}
