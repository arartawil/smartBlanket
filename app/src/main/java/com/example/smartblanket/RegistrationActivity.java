package com.example.smartblanket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartblanket.Classes.Supervisor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputLayout fullName;
    private TextInputLayout userName;
    private TextInputLayout password;
    private TextInputLayout rePassword;
    private Button loginBack;
    private Button next;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final DocumentReference docRef = db.collection("lastid").document("check");
    private static boolean userMatch = false;
    public String current = "0";
    private int newSeq;
    private List<Supervisor> supervisorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        fullName = findViewById(R.id.text_input_name);
        userName = findViewById(R.id.text_input_user);
        password = findViewById(R.id.text_input_password);
        rePassword = findViewById(R.id.text_input_re_password);
        loginBack = findViewById(R.id.regLoginBtn);
        next = findViewById(R.id.regNextBtn);
        userCheckDB();
    }

    private boolean validateName() {
        String nameInput = fullName.getEditText().getText().toString().trim();

        if (nameInput.isEmpty()) {
            fullName.setError("Field cannot be empty");
            return false;
        } else {
            fullName.setError(null);
            return true;
        }
    }

    private boolean validateUserName() {
        final String userNameInput = userName.getEditText().getText().toString().trim();
        EditText username = findViewById(R.id.regUserName);
        if (userNameInput.isEmpty()) {
            userName.setError("Field cannot be empty");
            return false;
        } else {
            username.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            for(int i = 0 ; i<supervisorList.size(); i++){
                if(supervisorList.get(i).getUsername().equals(userNameInput)){
                    userMatch = true;
                }else{
                    userMatch = false;
                }
            }
            if (userMatch) {
                userName.setError("Username in use!");
                return false;
            } else {
                userName.setError(null);
                return true;
            }
        }
    }
    private void userCheckDB(){
        db.collection("Supervisor").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Supervisor supervisor = documentSnapshot.toObject(Supervisor.class);
                    supervisorList.add(supervisor);
                }
            }
        });
    }
    private boolean validatePassword() {
        String passwordInput = password.getEditText().getText().toString().trim();
        String rePasswordInput = rePassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty() | rePasswordInput.isEmpty()) {
            password.setError("Field cannot be empty");
            rePassword.setError("Field cannot be empty");
            return false;
        } else {
            if (passwordInput.equals(rePasswordInput)) {
                password.setError(null);
                rePassword.setError(null);
                return true;
            } else {
                rePassword.setError("Passwords doesn't match");
                return false;
            }

        }
    }

    public void BackButton(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

    public void onClickRegister(View view) {
        if (!validateName() | !validateUserName() | !validatePassword()) {
            return;
        } else {
            final Map<String, Object> user = new HashMap<>();
            user.put("Name", fullName.getEditText().getText().toString().trim());
            user.put("Password", password.getEditText().getText().toString().trim());
            user.put("Username", userName.getEditText().getText().toString().trim());

            Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();

                    current = document.get("superValue").toString();
                    newSeq = Integer.parseInt(current) + 1;
                    current = Integer.toString(newSeq);

                    Task<Void> voidTask = db.collection("Supervisor").document(current)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void avoid) {
                                    db.collection("lastid").document("check").update("superValue", current);
                                    //Intent i = new Intent(RegistrationActivity.this, SetupActivity.class);
                                   // i.putExtra("SuperID", current);
                                   // startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            });
        }
    }
}
