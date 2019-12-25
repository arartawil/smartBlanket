package com.example.smartblanket;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatPreferenceActivity {
    private static String KeySuper;
    private static String DeparmentName;
    private TextInputLayout textUser;
    private TextInputLayout textPassowrd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Animation from_bottom;
    private Animation from_top;
    private Button reg, log;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textUser = findViewById(R.id.text_input_user);
        textPassowrd = findViewById(R.id.text_input_password);
        from_top = AnimationUtils.loadAnimation(this, R.anim.from_top);
        from_bottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        reg = findViewById(R.id.new_user);
        log = findViewById(R.id.login_user);
        imageView = findViewById(R.id.Logo);
        reg.setAnimation(from_bottom);
        log.setAnimation(from_bottom);
        imageView.setAnimation(from_top);

    }

    private boolean validateUser() {
        String userInput = textUser.getEditText().getText().toString().trim();

        if (userInput.isEmpty()) {
            textUser.setError("Field can't be empty");
            return false;
        } else {
            textUser.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textPassowrd.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textPassowrd.setError("Field can't be empty");
            return false;
        } else {
            textPassowrd.setError(null);
            return true;
        }
    }

    public void registerBtn(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    private void matchLogin() {
        db.collection("Supervisor").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (@android.support.annotation.Nullable QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.getString("Username").equals(textUser.getEditText().getText().toString().trim())) {
                        if (documentSnapshot.getString("Password").equals(textPassowrd.getEditText().getText().toString().trim())) {
                            KeySuper = documentSnapshot.getId();
                            DeparmentName=documentSnapshot.getString("Department");
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("SuperSession", KeySuper);
                            i.putExtra("DepartmentName", documentSnapshot.getString("Department"));
                            startActivity(i);
                            break;
                        } else {
                            textUser.setError(" ");
                            textPassowrd.setError("User/Password incorrect");
                            break;
                        }
                    } else {
                        textUser.setError(" ");
                        textPassowrd.setError("User/Password incorrect");
                        break;
                    }
                }
            }
        });
    }

    public void Login(View view) {
        if (!validatePassword() | !validateUser()) {
            return;
        } else {
            matchLogin();
        }
    }

}
