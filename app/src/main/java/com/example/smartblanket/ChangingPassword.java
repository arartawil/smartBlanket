package com.example.smartblanket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangingPassword extends AppCompatPreferenceActivity {
    private String SuperKey;
    private String DepartmentName;
    private TextInputLayout current;
    private TextInputLayout newPassword;
    private TextInputLayout rePassword;
    private Button cancel;
    private Button confirm;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bundle extra = getIntent().getExtras();
        SuperKey = extra.getString("SuperSession");
        DepartmentName = extra.getString("DepartmentName");
        System.out.println("Chanaging " + SuperKey + " " + DepartmentName);
        current = findViewById(R.id.current);
        newPassword = findViewById(R.id.new_password);
        rePassword = findViewById(R.id.retype);

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this.confirmListener);

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this.cancelListener);

    }

    View.OnClickListener confirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String currentPass = current.getEditText().getText().toString();
            System.out.println(currentPass);
            System.out.println("Chanaging creatae" + SuperKey + " " + DepartmentName);

            db.collection("Supervisor").document(SuperKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    System.out.println(task.getResult().getString("Password") + " drom");
                    if (!currentPass.isEmpty() && currentPass.equals(task.getResult().getString("Password"))) {
                        if (validatePassword()) {
                            db.collection("Supervisor").document(SuperKey).update("Password", newPassword.getEditText().getText().toString());
                            Intent i = new Intent(ChangingPassword.this, MainActivity.class);
                            i.putExtra("SuperSession", SuperKey);
                            System.out.println(DepartmentName + "fonr");
                            i.putExtra("DepartmentName", DepartmentName);
                            System.out.println("done");
                            startActivity(i);
                        }
                    } else if (currentPass.isEmpty()) {
                        current.setError("Field cannot be empty");
                    } else {
                        current.setError("Entered password does not match the current password");
                    }
                }
            });
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ChangingPassword.this, MainActivity.class);
            i.putExtra("SuperSession", SuperKey);
            System.out.println(DepartmentName + " cancel");
            i.putExtra("DepartmentName", DepartmentName);
            System.out.println("done");
            startActivity(i);
        }
    };

    private boolean validatePassword() {
        String passwordInput = newPassword.getEditText().getText().toString().trim();
        String rePasswordInput = rePassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty() | rePasswordInput.isEmpty()) {
            newPassword.setError("Field cannot be empty");
            rePassword.setError("Field cannot be empty");
            return false;
        } else {
            if (passwordInput.equals(rePasswordInput)) {
                newPassword.setError(null);
                rePassword.setError(null);
                return true;
            } else {
                rePassword.setError("Passwords doesn't match");
                return false;
            }

        }
    }
}


