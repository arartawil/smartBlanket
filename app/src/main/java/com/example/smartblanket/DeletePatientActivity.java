package com.example.smartblanket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.smartblanket.Adapters.AdapterDeletePatient;
import com.example.smartblanket.Classes.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class DeletePatientActivity extends AppCompatPreferenceActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Patient> PatientInfo = new ArrayList<>();
    private AdapterDeletePatient ADP;
    private String activeValueDelete;
    private String current;
    private RecyclerView DeleteRecycler;
    private String SuperKey;
    private String DepartmentName;
    private int keyBed, keyRoom;
    final DocumentReference docRef = db.collection("lastid").document("check");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_patient);

        Bundle extras = getIntent().getExtras();
        SuperKey = extras.getString("SuperSession");
        DepartmentName = extras.getString("DepartmentName");

        /////////////////////////////////////////////////////////////////////////////
        // get how many bed(s) in each room in the database and return them       //
        ///////////////////////////////////////////////////////////////////////////
        db.collection("Departments").document(DepartmentName).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.getId().equals(DepartmentName)){
                    keyBed = Integer.parseInt(documentSnapshot.getString("NumberOfBeds"));
                    keyRoom = Integer.parseInt(documentSnapshot.getString("NumberOfRooms"));
                }
            }
        });

        //// .Clear the list of patient to make sure no duplicate data
        PatientInfo.clear();
        /////////////////////////////////////////////////////////////////////////////
        // Create the Recycle view that show ever single patient alone in one card//
        ///////////////////////////////////////////////////////////////////////////
        DeleteRecycler=findViewById(R.id.DeletePatient);
        DeleteRecycler.setLayoutManager(new LinearLayoutManager(this));
        DeleteRecycler.setHasFixedSize(true);
        db.collection("Supervisor").document(SuperKey).collection("Patient")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            return;
                        }
                        //// .Clear the list of patient to make sure no duplicate data
                        PatientInfo.clear();
                        //////////////////////////////////////////////////////////////////////////////
                        // for loop that work on getting the data from the data base each by one   //
                        // in each loop we got one object we stored it in the patient list        //
                        ///////////////////////////////////////////////////////////////////////////
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Patient onePatientInfo = documentSnapshot.toObject(Patient.class);
                            if(onePatientInfo.InBed) {
                                activeValueDelete = documentSnapshot.getId();
                                onePatientInfo.getId(activeValueDelete);
                                PatientInfo.add(onePatientInfo);
                            }
                        }
                        ///////////////////////////////////////////////////////////////////////////////
                        // call the adapter that we declare in the on create function               //
                        /////////////////////////////////////////////////////////////////////////////
                        ADP=new AdapterDeletePatient(PatientInfo, getApplicationContext(), DeletePatientActivity.this, DepartmentName, SuperKey);
                        DeleteRecycler.setAdapter(ADP);
                        DeleteRecycler.setItemAnimator(new DefaultItemAnimator());

                    }


                });
        ///////////////////////////////////////////////////////////////////////////////
        // return of the total patient                                              //
        /////////////////////////////////////////////////////////////////////////////
        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                TextView total = findViewById(R.id.total);
                DocumentSnapshot document = task.getResult();

                current = document.get("count").toString();
                total.setText("Total : " + current);}
        });

    }
    //////////////////////////////////////////////////////////////////////////////////////////
    // Back button to the main with sending some information of the current activity       //
    ////////////////////////////////////////////////////////////////////////////////////////
    public void BackButton(View view){
        Intent i = new Intent(DeletePatientActivity.this, MainActivity.class);
        i.putExtra("SuperSession", SuperKey);
        i.putExtra("DepartmentName", DepartmentName);
        startActivity(i);
    }
}
