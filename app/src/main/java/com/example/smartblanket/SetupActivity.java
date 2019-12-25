package com.example.smartblanket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.smartblanket.Classes.Department;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class SetupActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static List<com.example.smartblanket.Classes.Department> Departments = new ArrayList<>();
    private static String Department;
    private static String BedsNumber = "0";
    private static String RoomsNumber = "0";
    private static String ID = "0";
    private static String SuperKey;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private DocumentReference setDataDocRef;
    private String stageZero;
    private static ProgressDialog dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Bundle extras = getIntent().getExtras();
        SuperKey = extras.getString("SuperID");

        final TextInputLayout etBed = findViewById(R.id.text_input_bed);
        final TextInputLayout etRoom = findViewById(R.id.text_input_room);

        radioGroup = findViewById(R.id.radioGroup);
        BedsNumber = etBed.getEditText().getText().toString();
        RoomsNumber = etRoom.getEditText().getText().toString();


        db.collection("Departments").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Departments.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Department department = documentSnapshot.toObject(Department.class);
                    Departments.add(department);
                }
                for( int i = 0; i < radioGroup.getChildCount(); i++){
                    radioButton = (RadioButton) radioGroup.getChildAt(i);
                    int id=-1;
                    try {
                        if (Departments.get(i).isActive())
                            id = Integer.parseInt(Departments.get(i).getID());
                            if(id!=-1) {
                                radioButton = (RadioButton) radioGroup.getChildAt(id);
                                radioButton.setEnabled(false);
                            }
                    }catch (IndexOutOfBoundsException event){
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                }
            }
        });

        Button Finish = findViewById(R.id.Finish);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        Finish.setAnimation(animation);
    }



    public void createDept(View view) {
        for( int i = 0; i < radioGroup.getChildCount(); i++){
            radioButton = (RadioButton) radioGroup.getChildAt(i);
            if(radioButton.isChecked()) {
                Department = radioButton.getText().toString();
                ID = Integer.toString(i);
            }
        }
        if (TextUtils.isEmpty(Department))
            return;
        else {

            dia = new ProgressDialog(this);
            dia.setMessage("Please wait ...");
            dia.setCancelable(false);
            dia.setTitle("Loading");
            dia.show();
            assignDept();



            for (int r = 1; r <= Integer.parseInt(RoomsNumber); r++) {
                final Map<String, Object> Room = createHashMap("RoomNumber", Integer.toString(r));
                setData(Room, "set", "Departments",Department, "Room", Integer.toString(r));

                for (int b = 1; b <= Integer.parseInt(BedsNumber); b++) {

                    final Map<String, Object> Bed = createHashMap("Active", String.valueOf(false), "BedNumber", Integer.toString(b),"PatientId", "0");
                    setData(Bed, "set", "Departments",Department, "Room", Integer.toString(r), "Bed", Integer.toString(b));


                    Map<String, Object> Sensor = createHashMap("ActiveState", "false", "Type", "T&H");
                    setData(Sensor, "set", "Departments",Department, "Room", Integer.toString(r), "Bed", Integer.toString(b), "Sensors", "1");


                    Sensor = createHashMap("ActiveState", "false", "Type", "S");
                    setData(Sensor, "set", "Departments",Department, "Room", Integer.toString(r), "Bed", Integer.toString(b), "Sensors", "2");
                }
            }
            dia.cancel();
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("SuperSession", SuperKey);
            i.putExtra("DepartmentName", Department);
            startActivity(i);
        }

    }
    private void setData( Map<String, Object> hashMap,String operation, String ... args){
        int stage=0;
        boolean first = true;
        for(String arg : args){
            switch (stage){
                case 0:
                    stageZero=arg;
                    stage=1;
                    break;
                case 1:
                    if(first){
                        setDataDocRef = db.collection(stageZero).document(arg);
                        first = false;
                    }else {
                        setDataDocRef = setDataDocRef.collection(stageZero).document(arg);
                    }
                    stage=0;
                    break;
            }
        }
        if(operation.equals("set")){
            setDataDocRef.set(hashMap);
        }else if(operation.equals("update")){
            setDataDocRef.update(hashMap);
        }
    }
    public Map createHashMap(String ... args){
        Map<String, Object> hashMap = new HashMap<>();
        int stage=0;
        for(String arg : args){
            switch (stage){
                case 0:
                    stageZero=arg;
                    stage=1;
                    break;
                case 1:
                    if(arg.equals("false") || arg.equals("true")) {
                        boolean isBoolean = Boolean.valueOf(arg);
                        hashMap.put(stageZero, isBoolean);
                    }else {
                        hashMap.put(stageZero, arg);
                    }
                    stage=0;
                    break;
            }

        }
        return hashMap;
    }

    private void assignDept() {
        Map<String, Object> newDepartment = createHashMap("NumberOfRooms", RoomsNumber, "NumberOfBeds", BedsNumber, "Active", "true","ID", ID);
        setData(newDepartment, "set", "Departments",Department);

        setData(createHashMap("Department", Department),"update","Supervisor",SuperKey);
    }
}
