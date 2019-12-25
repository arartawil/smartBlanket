package com.example.smartblanket;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.smartblanket.Adapters.AdapterHistory;
import com.example.smartblanket.Adapters.AdapterMainInterface;
import com.example.smartblanket.Classes.Bed;
import com.example.smartblanket.Classes.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class HistoryActivity extends AppCompatPreferenceActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Patient> PatientInfo = new ArrayList<>();
    private RecyclerView HistoryRecycler;
    private AdapterHistory ADH;
    private String activeValue;
    private String SuperKey;
    private String DepartmentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Bundle extras = getIntent().getExtras();
        SuperKey = extras.getString("SuperSession");
        DepartmentName = extras.getString("DepartmentName");

        HistoryRecycler = findViewById(R.id.HistoryPatient);
        HistoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        HistoryRecycler.setHasFixedSize(true);

        db.collection("Supervisor").document(SuperKey).collection("Patient").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Patient onePatientInfo = documentSnapshot.toObject(Patient.class);
                    activeValue = documentSnapshot.getId();
                    onePatientInfo.getId(activeValue);
                    PatientInfo.add(onePatientInfo);
                }
                System.out.println(PatientInfo.size());
                ADH = new AdapterHistory(PatientInfo, getApplicationContext(), DepartmentName, SuperKey);
                HistoryRecycler.setAdapter(ADH);
                HistoryRecycler.setItemAnimator(new DefaultItemAnimator());

            }

        });
    }
}
