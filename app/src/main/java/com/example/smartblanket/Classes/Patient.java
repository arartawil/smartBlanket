package com.example.smartblanket.Classes;

import java.io.Serializable;
import java.util.Date;

public class Patient implements Serializable {
    public String id;
    public String SSN;
    public String FirstName;
    public String MiddleName;
    public String SureName;
    public String BedNo;
    public String RoomNo;
    public String Statue;
    public String BirthDate;
    public String RegistrationDate;
    public String CheckOutDate;
    public boolean InBed;


    public Patient(){};

    public Patient(String snn, String firstName,String middleName, String sureName, String statue, String birthDate, String regDate, String CheckOutDate) {
        this.SSN = snn;
        this.FirstName = firstName;
        this.MiddleName = middleName;
        this.SureName = sureName;
        this.Statue = statue;
        this.BirthDate = birthDate;
        this.RegistrationDate = regDate;
        this.CheckOutDate = CheckOutDate;
    }

    public void getId(String id){
        this.id = id;
    }
    public String setId(){
        return id;
    }

}
