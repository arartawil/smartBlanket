package com.example.smartblanket.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartblanket.Classes.Bed;
import com.example.smartblanket.Classes.Patient;
import com.example.smartblanket.OnePatientViewActivity;
import com.example.smartblanket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterMainInterface extends RecyclerView.Adapter<AdapterMainInterface.ViewHolder> {
    private List<Patient> PatientClass = new ArrayList<>();
    private List<Bed> BedClass = new ArrayList<>();
    private Context context;
    private String SuperKey;
    private String DepartmentName;
    private int keyBed;
    private int keyRoom;
    private int count = 0;
    private String temp;
    private String humi;
    private String tempf;
    private Intent intent;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private DatabaseReference sensData = FirebaseDatabase.getInstance().getReference("Sensors");
    private int roomCount=1;
//    private Patient PC;

    public AdapterMainInterface(){}

    public AdapterMainInterface(List<Bed> bedClass, List<Patient> patientClass, Context context, String DepartmentName, int keyBed, int keyRoom, String SuperKey, int keyR) {
        this.context = context;
        this.PatientClass = patientClass;
        this.BedClass = bedClass;
        this.DepartmentName = DepartmentName;
        this.keyBed = keyBed;
        this.keyRoom = keyRoom;
        this.SuperKey = SuperKey;
    }

    @NonNull
    @Override
    public AdapterMainInterface.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bed_card, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        editor.putInt("PatientCount", PatientClass.size());
        editor.commit();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.No.setText(Integer.toString(i+1));
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.from_right);
        viewHolder.itemView.startAnimation(animation);
        final Bed BC = BedClass.get(i);
        if(BC.id.equals("1")) {
            viewHolder.roomNo.setText(Integer.toString(roomCount));
            roomCount++;
        }
        else
            viewHolder.roomNo.setVisibility(View.GONE);
        if (!BC.PatientId.equals("0")) {
            for (final Patient patient : PatientClass) {
                if (patient.setId().equals(BC.PatientId)) {
                    final Patient PC = patient;
                    viewHolder.FLN.setText(PC.FirstName + " " + PC.MiddleName + " " + PC.SureName);
                    viewHolder.BN.setText("Bed No. " + PC.BedNo);
                    viewHolder.RN.setText("Room No. " + PC.RoomNo);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent = new Intent(context, OnePatientViewActivity.class);
                            intent.putExtra("SuperSession", SuperKey);
                            intent.putExtra("DepartmentName", DepartmentName);
                            intent.putExtra("patient", patient);
                            intent.putExtra("bed", BC);
                            context.startActivity(intent);
                        }
                    });

                    sensData.child("T&H").child(String.valueOf(PC.BedNo)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            temp = dataSnapshot.child("Temp").getValue(String.class);
                            humi = dataSnapshot.child("Humi").getValue(String.class);
                            tempf = dataSnapshot.child("TempF").getValue(String.class);
                            Float humiNo = Float.parseFloat(humi);
                            Float tempNo = Float.parseFloat(temp);
                            Float tempfNo = Float.parseFloat(tempf);


                            if (PC.BedNo.equals(dataSnapshot.child("BedNo").getValue(String.class)) && BC.Active) {
                                Drawable dTemp = context.getResources().getDrawable(R.drawable.rounded_blue_btn);
                                dTemp.setColorFilter(getTrafficLightColor(tempNo, 15, 40), PorterDuff.Mode.SRC_ATOP);
                                viewHolder.T.setBackground(dTemp);
                                Drawable dHumi = context.getResources().getDrawable(R.drawable.rounded_green_btn);
                                dHumi.setColorFilter(getTrafficLightColor(humiNo, 25, 90), PorterDuff.Mode.SRC_ATOP);
                                viewHolder.H.setBackground(dHumi);
                                Drawable dScale = context.getResources().getDrawable(R.drawable.rounded_red_btn);
                                dScale.setColorFilter(getTrafficLightColor(tempfNo, 50, 110), PorterDuff.Mode.SRC_ATOP);
                                viewHolder.S.setBackground(dScale);
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }else{
            viewHolder.FLN.setText("Empty");
        }

    }

    private int getTrafficLightColor(double value, double min, double max) {
        return android.graphics.Color.HSVToColor(new float[]{(float) normalize(max, min, value) * 120f, 1f, 1f});
    }

    private static double normalize(double min, double max, double value) {
        return (value - min) / (max - min);
    }

    @Override
    public int getItemCount() {
        return BedClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView FLN;
        public TextView BN;
        public TextView RN;
        public TextView No;
        public TextView TOT;
        public Button T;
        public Button H;
        public Button S;
        public TextView roomNo;
        public CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            No = itemView.findViewById(R.id.No);
            FLN = itemView.findViewById(R.id.PatientName);
            BN = itemView.findViewById(R.id.BedNumber);
            RN = itemView.findViewById(R.id.RoomNumber);
            TOT = itemView.findViewById(R.id.total);
            T = itemView.findViewById(R.id.T);
            H = itemView.findViewById(R.id.H);
            S = itemView.findViewById(R.id.S);
            card = itemView.findViewById(R.id.card_view);
            roomNo = itemView.findViewById(R.id.roomNo);
        }
    }
}
