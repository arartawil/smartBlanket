package com.example.smartblanket.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartblanket.Classes.Patient;
import com.example.smartblanket.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {
    private List<Patient> PatientClass = new ArrayList<>();
    private Context context;
    private String SuperKey;
    private String Department;
    public AdapterHistory(List<Patient> patientClass, Context context, String superKey, String department){
        this.PatientClass = patientClass;
        this.context = context;
        this.SuperKey = superKey;
        this.Department = department;
    }
    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_history, viewGroup, false);
        AdapterHistory.ViewHolder viewHolder = new AdapterHistory.ViewHolder(itemLayoutView);
        System.out.println("adapter "+PatientClass);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Patient PC = PatientClass.get(i);

        viewHolder.SSN.setText(PC.SSN);
        viewHolder.FLN.setText(PC.FirstName + " " + PC.MiddleName + " " + PC.SureName);
        viewHolder.CI.setText(PC.RegistrationDate);
        if(PC.CheckOutDate.equals("null"))
            viewHolder.CO.setText("Still in");
        else
            viewHolder.CO.setText(PC.CheckOutDate);

        try {
            writeToFile(PC.FirstName + " " + PC.MiddleName + " " + PC.SureName + " " + PC.RegistrationDate + " " +PC.CheckOutDate, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void writeToFile(String data,Context context) throws IOException {
        File path = context.getFilesDir();

        File file = new File(path, "historyLog.txt");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            stream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
            System.out.println("Done");
        }
    }
    @Override
    public int getItemCount() {
        return PatientClass.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView FLN;
        public TextView CI;
        public TextView CO;
        public TextView SSN;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            FLN = itemView.findViewById(R.id.full_name_h);
            SSN = itemView.findViewById(R.id.ssn_h);
            CI = itemView.findViewById(R.id.checkin_h);
            CO = itemView.findViewById(R.id.checkout_h);

        }
    }
    public AdapterHistory(){}
}
