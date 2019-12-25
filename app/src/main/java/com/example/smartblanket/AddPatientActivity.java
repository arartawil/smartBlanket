package com.example.smartblanket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.smartblanket.Classes.Bed;
import com.example.smartblanket.Classes.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddPatientActivity extends AppCompatActivity {

    public String current = "0";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final DocumentReference docRef = db.collection("lastid").document("check");
    private EditText SSN;
    private EditText FirstName;
    private EditText MiddleName;
    private EditText SureName;
    private EditText State;
    private TextInputLayout layout_ssn;
    private TextInputLayout layout_first;
    private TextInputLayout layout_middle;
    private TextInputLayout layout_sure;
    private TextInputLayout layout_state;
    private DatePicker BirthDate;
    private String ssn;
    private String firstName;
    private String middleName;
    private String sureName;
    private String state;
    private String birthDate;
    private String keyBed, keyRoom;
    private String activeValue;
    private int newSeq;
    private Spinner spinnerRoomOption;
    private Spinner spinnerBedOption;
    private List<String> spinnerBedNumber = new ArrayList<>();
    private List<String> spinnerRoomNumber = new ArrayList<>();
    private int selectedItem = -1;
    private String regDate;
    private Button ConfirmBtn;
    private Button CancelBtn;
    private Animation from_bottom;
    private Animation from_top;
    private String SuperKey;
    private String DepartmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Bundle extras = getIntent().getExtras();
        SuperKey = extras.getString("SuperSession");
        DepartmentName = extras.getString("DepartmentName");

        db.collection("Departments").document(DepartmentName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.getId().equals(DepartmentName)) {
                    keyBed = documentSnapshot.getString("NumberOfBeds");
                    keyRoom = documentSnapshot.getString("NumberOfRooms");
                }
            }
        });

        ConfirmBtn = findViewById(R.id.ConfirmBtn);
        CancelBtn = findViewById(R.id.CancelBtn);
        from_top = AnimationUtils.loadAnimation(this, R.anim.from_top);
        from_bottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        ConfirmBtn.setAnimation(from_bottom);
        CancelBtn.setAnimation(from_bottom);

        layout_ssn = findViewById(R.id.layout_ssn);
        layout_first = findViewById(R.id.layout_first);
        layout_middle = findViewById(R.id.layout_middle);
        layout_sure = findViewById(R.id.layout_sure);
        layout_state = findViewById(R.id.layout_state);

        getBedRoomNumber();

    }

    //Validate(s) Function://

    /////////////////////////////////////////////////////////////////////////////////////////
    // First one we check if the SSN is empty if yes raise error if not complete process  //
    ///////////////////////////////////////////////////////////////////////////////////////
    private boolean validateSSN() {
        String ssnInput = layout_ssn.getEditText().getText().toString().trim();

        if (ssnInput.isEmpty()) {
            layout_ssn.setError("Most necessary: e.g. 996100xxxx");
            return false;
        } else {
            layout_ssn.setError(null);
            return true;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // Second one we check if the Name is empty if yes raise error if not complete process //
    ////////////////////////////////////////////////////////////////////////////////////////
    private boolean validateName() {
        String firstNameInput = layout_first.getEditText().getText().toString().trim();
        String middleNameInput = layout_middle.getEditText().getText().toString().trim();
        String sureNameInput = layout_sure.getEditText().getText().toString().trim();

        if (firstNameInput.isEmpty() | middleNameInput.isEmpty() | sureNameInput.isEmpty()) {
            layout_first.setError("Make sure you put Full Name");
            layout_middle.setError("Make sure you put Full Name");
            layout_sure.setError("Make sure you put Full Name");
            return false;
        } else {
            layout_first.setError(null);
            layout_middle.setError(null);
            layout_sure.setError(null);
            return true;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    // Third one we check if the State is empty if yes raise error if not complete process   //
    //////////////////////////////////////////////////////////////////////////////////////////
    private boolean validateState() {
        String stateInput = layout_state.getEditText().getText().toString().trim();

        if (stateInput.isEmpty()) {
            layout_state.setError("(e.g. Stable, Danger)");
            return false;
        } else {
            layout_state.setError(null);
            return true;
        }
    }

    //Add Patient Function://
    ///////////////////////////////////////////////////////////////////////////////////////////
    // Get data from the interface, Put the data in HashMap, set the data in the database   //
    // Update the flag in the database every time patient added                            //
    // Change the the state of the bed and sensor classes to start receive data           //
    ///////////////////////////////////////////////////////////////////////////////////////
    public void onClick(View view) {
        SSN = findViewById(R.id.SSN);
        FirstName = findViewById(R.id.FirstName);
        MiddleName = findViewById(R.id.MiddleName);
        SureName = findViewById(R.id.SureName);
        State = findViewById(R.id.State);
        BirthDate = findViewById(R.id.DateOfBirth);
        firstName = FirstName.getText().toString();
        middleName = MiddleName.getText().toString();
        sureName = SureName.getText().toString();
        ssn = SSN.getText().toString();
        state = State.getText().toString();
        int day = BirthDate.getDayOfMonth();
        int month = BirthDate.getMonth();
        int year = BirthDate.getYear();
        birthDate = day + "/" + (month + 1) + "/" + year;
        regDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        if (!validateSSN() | !validateName() | !validateState()) {
            return;
        } else {
            // Create a new user with a first and last name


            final Map<String, Object> user = new HashMap<>();
            user.put("SSN", ssn);
            user.put("FirstName", firstName);
            user.put("MiddleName", middleName);
            user.put("SureName", sureName);
            user.put("Statue", state);
            user.put("BirthDate", birthDate);
            user.put("BedNo", keyBed);
            user.put("RoomNo", keyRoom);
            user.put("RegistrationDate", regDate);
            user.put("CheckOutDate", "null");
            user.put("InBed", true);
            // Add a new document with a generated ID
            Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();

                    current = document.get("value").toString();
                    newSeq = Integer.parseInt(current) + 1;
                    current = Integer.toString(newSeq);


                    Task<Void> voidTask = db.collection("Supervisor").document(SuperKey)
                            .collection("Patient").document(current)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void avoid) {
                                    db.collection("lastid").document("check").update("value", current);
                                    db.collection("Departments").document(DepartmentName).collection("Room").document(keyRoom).collection("Bed").document(keyBed)
                                            .collection("Sensors").document("1")
                                            .update("ActiveState", true);
                                    db.collection("Departments").document(DepartmentName).collection("Room").document(keyRoom).collection("Bed").document(keyBed)
                                            .collection("Sensors").document("2")
                                            .update("ActiveState", true);
                                    db.collection("Departments").document(DepartmentName).collection("Room").document(keyRoom).collection("Bed")
                                            .document(keyBed).update("Active", true);
                                    db.collection("Departments").document(DepartmentName).collection("Room").document(keyRoom).collection("Bed")
                                            .document(keyBed).update("PatientId", current);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }

            });
                    Intent i = new Intent(AddPatientActivity.this, MainActivity.class);
                    i.putExtra("SuperSession", SuperKey);
                    i.putExtra("DepartmentName", DepartmentName);
                    startActivity(i);
        }

    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // Back button to the main with sending some information of the current activity       //
    ////////////////////////////////////////////////////////////////////////////////////////
    public void BackButton(View view) {
        Intent i = new Intent(AddPatientActivity.this, MainActivity.class);
        i.putExtra("SuperSession", SuperKey);
        i.putExtra("DepartmentName", DepartmentName);
        startActivity(i);
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // Return the Room number are picked in the spinner in the interface                   //
    ////////////////////////////////////////////////////////////////////////////////////////
    public void getBedRoomNumber() {
        spinnerRoomNumber.clear();
        db.collection("Departments").document(DepartmentName).collection("Room").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Room room = documentSnapshot.toObject(Room.class);
                    spinnerRoomNumber.add(room.getRoomNumber());
                }
                spinnerRoomOption = findViewById(R.id.RoomNoSpinner);

                ArrayAdapter<String> adapterManageOption = new ArrayAdapter<String>(AddPatientActivity.this,
                        android.R.layout.simple_spinner_item, spinnerRoomNumber);
                adapterManageOption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRoomOption.setAdapter(adapterManageOption);
                spinnerRoomOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        keyRoom = String.valueOf(spinnerRoomOption.getSelectedItem());
                        getBedNumber(keyRoom);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        });
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // Return the Bed number are picked in the spinner in the interface                    //
    ////////////////////////////////////////////////////////////////////////////////////////
    private void getBedNumber(String rooms) {
        db.collection("Departments").document(DepartmentName).collection("Room").document(rooms).collection("Bed")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        spinnerBedNumber.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Bed bed = documentSnapshot.toObject(Bed.class);
                            if (bed.Active == false) {
                                activeValue = documentSnapshot.getId();
                                bed.getId(activeValue);
                                spinnerBedNumber.add(bed.BedNumber);

                            }


                        }
                        if (spinnerBedNumber.isEmpty()) {
                            spinnerBedNumber.add("Full");
                        }
                        spinnerBedOption = findViewById(R.id.BedNoSpinner);

                        ArrayAdapter<String> adapterManageOption = new ArrayAdapter<String>(AddPatientActivity.this,
                                android.R.layout.simple_spinner_item, spinnerBedNumber);
                        adapterManageOption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerBedOption.setAdapter(adapterManageOption);
                        spinnerBedOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                                keyBed = String.valueOf(spinnerBedOption.getSelectedItem());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
    }

}
