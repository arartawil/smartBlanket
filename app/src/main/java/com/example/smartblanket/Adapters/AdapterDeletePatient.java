package com.example.smartblanket.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.smartblanket.Classes.Patient;
import com.example.smartblanket.DeletePatientActivity;
import com.example.smartblanket.R;
import com.google.firebase.firestore.DocumentReference;
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

public class AdapterDeletePatient extends RecyclerView.Adapter<AdapterDeletePatient.ViewHolder> {
    private List<Patient> PatientClass = new ArrayList<>();
    private Context context;
    private Activity activity;
    private int count = 0;
    private String SuperKey;
    private String DepartmentName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference setDataDocRef;
    private String stageZero;

    @NonNull
    @Override
    public AdapterDeletePatient.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.remove_card, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    public AdapterDeletePatient(List<Patient> patientClass, Context context, Activity activity, String Department, String SuperKey) {
        this.PatientClass = patientClass;
        this.context = context;
        this.activity = activity;
        this.DepartmentName = Department;
        this.SuperKey = SuperKey;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final Patient PC = PatientClass.get(i);
        if(!PatientClass.isEmpty())
            count += 1;
        viewHolder.No.setText(Integer.toString(count));
        viewHolder.FLN.setText(PC.FirstName + " " + PC.MiddleName + " " + PC.SureName);
        viewHolder.BN.setText("Bed No. " + PC.BedNo);
        viewHolder.RN.setText("Room No. " + PC.RoomNo);
        setData(createHashMap("count", Integer.toString(count)), "update", "lastid", "check");


        viewHolder.DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogCustom);
                builder.setCancelable(true);
                builder.setTitle("Confirm Delete ?");
                builder.setMessage("For this name : " + PC.FirstName + " " + PC.SureName + "\nIn Room No. : " + PC.RoomNo + ", Bed No. : " + PC.BedNo);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setData(createHashMap("CheckOutDate", getCurrentTime()), "update", "Supervisor", SuperKey, "Patient", PC.id);
                        setData(createHashMap("InBed", "false"), "update", "Supervisor", SuperKey, "Patient", PC.id);

                        setData(createHashMap("Active", "false"), "update", "Departments", DepartmentName, "Room", PC.RoomNo, "Bed", PC.BedNo);
                        setData(createHashMap("PatientId", "0"), "update", "Departments", DepartmentName, "Room", PC.RoomNo, "Bed", PC.BedNo);

                        setData(createHashMap("ActiveState", "false"), "update", "Departments", DepartmentName, "Room", PC.RoomNo, "Bed", PC.BedNo, "Sensors", "1");
                        setData(createHashMap("ActiveState", "false"), "update", "Departments", DepartmentName, "Room", PC.RoomNo, "Bed", PC.BedNo, "Sensors", "2");

                        count -= 1;
                        setData(createHashMap("count", Integer.toString(count)), "update", "lastid", "check");
                    }
                });
                builder.show();
            }
        });
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
    }

    private void setData(Map<String, Object> hashMap, String operation, String... args) {
        int stage = 0;
        boolean first = true;
        for (String arg : args) {
            switch (stage) {
                case 0:
                    stageZero = arg;
                    stage = 1;
                    break;
                case 1:
                    if (first) {
                        setDataDocRef = db.collection(stageZero).document(arg);
                        first = false;
                    } else {
                        setDataDocRef = setDataDocRef.collection(stageZero).document(arg);
                    }
                    stage = 0;
                    break;
            }
        }
        if (operation.equals("set")) {
            setDataDocRef.set(hashMap);
        } else if (operation.equals("update")) {
            setDataDocRef.update(hashMap);
        }
    }

    public Map createHashMap(String... args) {
        Map<String, Object> hashMap = new HashMap<>();
        int stage = 0;
        for (String arg : args) {
            switch (stage) {
                case 0:
                    stageZero = arg;
                    stage = 1;
                    break;
                case 1:
                    if (arg.equals("false") || arg.equals("true")) {
                        boolean isBoolean = Boolean.valueOf(arg);
                        hashMap.put(stageZero, isBoolean);
                    } else {
                        hashMap.put(stageZero, arg);
                    }
                    stage = 0;
                    break;
            }

        }
        return hashMap;
    }

    @Override
    public int getItemCount() {
        return PatientClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView FLN;
        public TextView BN;
        public TextView RN;
        public TextView No;
        public Button DeleteBtn;
        public TextView TOT;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            No = itemView.findViewById(R.id.No);
            FLN = itemView.findViewById(R.id.PatientName);
            BN = itemView.findViewById(R.id.BedNumber);
            RN = itemView.findViewById(R.id.RoomNumber);
            DeleteBtn = itemView.findViewById(R.id.DeleteButton);
            TOT = itemView.findViewById(R.id.total);
        }
    }
}