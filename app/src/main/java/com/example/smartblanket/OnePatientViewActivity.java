package com.example.smartblanket;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.smartblanket.Classes.Bed;
import com.example.smartblanket.Classes.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.time.LocalDate;

public class OnePatientViewActivity extends AppCompatPreferenceActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Patient patient;
    private Bed bed;
    private Animation animation;
    private SlidrInterface slider;
    private String SuperKey;
    private String DeparmentName;

    private TextView FLN;
    private TextView BN;
    private TextView RN;
    private TextView ST;
    private TextView AG;
    private TextView CO;
    private TextView Min;
    private TextView Max;
    private Button L1;
    private Button L2;
    private Button L3;
    private Button L4;
    private RadioButton Temp;
    private RadioButton Humi;
    private RadioGroup radioGroup;
    private DatabaseReference sensData = FirebaseDatabase.getInstance().getReference("Sensors");
    private String temp;
    private String humi;
    private String tempf;
    private static float max_t=0, min_t=0;
    private static float max_h=0, min_h=0;

    private static double normalize(double min, double max, double value) {
        return (value - min) / (max - min);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_one_patient);

        Bundle extras = getIntent().getExtras();
        SuperKey = extras.getString("SuperSession");
        DeparmentName = extras.getString("DepartmentName");
        patient = (Patient) extras.getSerializable("patient");
        bed = (Bed) extras.getSerializable("bed");
        slider = Slidr.attach(this);
        slider.unlock();

        animation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(2000);

        FLN = findViewById(R.id.name_card);
        BN = findViewById(R.id.bed_card);
        RN = findViewById(R.id.room_card);
        ST = findViewById(R.id.status_card);
        AG = findViewById(R.id.age_card);
        CO = findViewById(R.id.con_card);
        Min = findViewById(R.id.Min);
        Max = findViewById(R.id.Max);
        L1 = findViewById(R.id.layout1);
        L2 = findViewById(R.id.layout2);
        L3 = findViewById(R.id.layout3);
        L4 = findViewById(R.id.layout4);
        Temp = findViewById(R.id.radioTemp);
        Humi = findViewById(R.id.radioHum);
        radioGroup = findViewById(R.id.radioGroup);

        FLN.setText(patient.FirstName + " " + patient.MiddleName + " " + patient.SureName);
        BN.setText(patient.BedNo);
        RN.setText(patient.RoomNo);
        ST.setText(patient.Statue);
        LocalDate date = LocalDate.now();
        int age = date.getYear() - Integer.parseInt(patient.BirthDate.substring(patient.BirthDate.length() - 4));
        AG.setText(Integer.toString(age));
        Min.setText("0");
        Max.setText("0");
        if(bed.Active) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    sensData.child("T&H").child(patient.BedNo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (Temp.isChecked()) {
                                temp = dataSnapshot.child("Temp").getValue(String.class);
                                Float tempNo = Float.parseFloat(temp);
                                L1.setBackgroundColor(getTrafficlightColor(tempNo, 15, 40));
                                L2.setBackgroundColor(getTrafficlightColor(tempNo, 15, 40));
                                L3.setBackgroundColor(getTrafficlightColor(tempNo, 15, 40));
                                L4.setBackgroundColor(getTrafficlightColor(tempNo, 15, 40));




                                Min.setText(Float.toString(15.0f));
                                Max.setText(Float.toString(40.0f));
                            } else if (Humi.isChecked()) {
                                humi = dataSnapshot.child("Humi").getValue(String.class);
                                Float humiNo = Float.parseFloat(humi);
                                L1.setBackgroundColor(getTrafficlightColor(humiNo, 25, 90));
                                L2.setBackgroundColor(getTrafficlightColor(humiNo, 25, 90));
                                L3.setBackgroundColor(getTrafficlightColor(humiNo, 25, 90));
                                L4.setBackgroundColor(getTrafficlightColor(humiNo, 25, 90));



                                Min.setText(Float.toString(25.0f));
                                Max.setText(Float.toString(90.0f));
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                }
            });
        }
    }

    private int getTrafficlightColor(double value, double min, double max) {
        return android.graphics.Color.HSVToColor(new float[]{(float) normalize(max, min, value) * 120f, 1f, 1f});
    }
}
