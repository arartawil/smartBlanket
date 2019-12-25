package com.example.smartblanket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;

import com.example.smartblanket.Adapters.AdapterMainInterface;
import com.example.smartblanket.Classes.Bed;
import com.example.smartblanket.Classes.Department;
import com.example.smartblanket.Classes.Patient;
import com.example.smartblanket.Classes.Supervisor;
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


public class MainActivity extends AppCompatPreferenceActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , SwipeRefreshLayout.OnRefreshListener{
    public SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Patient> PatientInfo = new ArrayList<>();
    private List<Bed> BedInfo = new ArrayList<>();
    private AdapterMainInterface ADP;
    private String activeValue;
    private Animation animation;
    private String SuperKey;
    private String DeparmentName;
    private int keyBed, keyRoom;
    private List<Department> departmentList = new ArrayList<>();
    private List<Supervisor> supervisorList = new ArrayList<>();
    private RecyclerView MainRecycler;
    private static String passwordReturn;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        SuperKey = extras.getString("SuperSession");
        DeparmentName = extras.getString("DepartmentName");

        System.out.println(DeparmentName + " main");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.greenState);

        animation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(2000);





        MainRecycler = findViewById(R.id.main_interface);
        MainRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        MainRecycler.setHasFixedSize(false);
        loadRecyclerViewData(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
//    public void onResume(){
//        super.onResume();
//        loadRecyclerViewData();
//    }
    @Override
    public void onRefresh() {
        loadRecyclerViewData(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog(final Intent i){
        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.password_confirm, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.confirmPasswordCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("Supervisor").document(SuperKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        TextInputLayout textInputLayout = deleteDialog.findViewById(R.id.text_input_password_confirm);
                        editText = deleteDialog.findViewById(R.id.edit_password_check);
                        passwordReturn = task.getResult().getString("Password");
                        if(editText.getText().toString().equals(passwordReturn)){
                            startActivity(i);
                        }else{
                            textInputLayout.setError("Password is wrong");
                        }
                    }
                });
            }
        });
        deleteDialogView.findViewById(R.id.cancelPasswordCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_logout){

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("SuperSession", "0");
            i.putExtra("DepartmentName", "0");
            startActivity(i);
        }
        else if (id == R.id.nav_add) {
            Intent i = new Intent(MainActivity.this, AddPatientActivity.class);
            i.putExtra("SuperSession", SuperKey);
            i.putExtra("DepartmentName", DeparmentName);
            showDialog(i);
        } else if (id == R.id.nav_remove) {
            finish();
            Intent i = new Intent(MainActivity.this, DeletePatientActivity.class);
            i.putExtra("SuperSession", SuperKey);
            i.putExtra("DepartmentName", DeparmentName);
            showDialog(i);
        } else if(id == R.id.nav_setting){
            Intent i = new Intent(MainActivity.this, ChangingPassword.class);
            i.putExtra("SuperSession", SuperKey);
            i.putExtra("DepartmentName", DeparmentName);
            startActivity(i);
        } else if(id == R.id.nav_history){
            Intent i = new Intent(MainActivity.this, HistoryActivity.class);
            i.putExtra("SuperSession", SuperKey);
            i.putExtra("DepartmentName", DeparmentName);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadRecyclerViewData(final Context context) {
        db.collection("Departments").document(DeparmentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                BedInfo.clear();
                PatientInfo.clear();
                if (task.getResult().getId().equals(DeparmentName)) {
                    keyBed = Integer.parseInt(task.getResult().getString("NumberOfBeds"));
                    keyRoom = Integer.parseInt(task.getResult().getString("NumberOfRooms"));
                    db.collection("Supervisor").document(SuperKey).collection("Patient").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Patient onePatientInfo = documentSnapshot.toObject(Patient.class);
                                if (onePatientInfo.InBed) {
                                    activeValue = documentSnapshot.getId();
                                    onePatientInfo.getId(activeValue);
                                    PatientInfo.add(onePatientInfo);
                                }
                            }
                            for (int keyR = 1; keyR <= keyRoom; keyR++) {
                                final int key = keyR;
                                db.collection("Departments").document(DeparmentName)
                                        .collection("Room").document(Integer.toString(keyR)).collection("Bed")
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    return;
                                                }

                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    Bed oneBedInfo = documentSnapshot.toObject(Bed.class);
                                                    activeValue = documentSnapshot.getId();
                                                    oneBedInfo.getId(activeValue);
                                                    BedInfo.add(oneBedInfo);
                                                }
                                                ADP = new AdapterMainInterface(BedInfo, PatientInfo, context, DeparmentName, keyBed, keyRoom, SuperKey, key);
                                                MainRecycler.setAdapter(ADP);
                                                MainRecycler.setItemAnimator(new DefaultItemAnimator());
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        });
                            }

                        }

                    });

                }
            }
        });

//        Toast.makeText(getApplicationContext(), String.valueOf(keyBed), Toast.LENGTH_LONG).show();


    }
}