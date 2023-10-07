package com.example.bloodbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.bloodbank.Adapters.RequestAdapter;
import com.example.bloodbank.DataModels.RequestModel;
import com.example.bloodbank.R;
import com.example.bloodbank.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private List<RequestModel> requestModels;
    private RequestAdapter requestAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseFirestore firestore;
    RequestModel requestModel;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//         Hooks

         drawerLayout = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
         toolbar = findViewById(R.id.toolbar);
        //---------------------Tool Bar-------------------
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        //Navigation drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        TextView make_request_button=findViewById(R.id.make_request_button);
        make_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MakeRequestActivity.class));
            }
        });

        //------------------------------------


//        Toolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.search_button) {
                    //open search
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                }
                return false;
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id=item.getItemId();
                if(id==R.id.logout){

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                if(id==R.id.blood_storage){
                    Intent intent = new Intent(MainActivity.this, FetchDonorActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        firestore=FirebaseFirestore.getInstance();
        requestModels = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        requestAdapter = new RequestAdapter(requestModels, this);
        recyclerView.setAdapter(requestAdapter);
        populateHomePage();
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    public void populateHomePage(){
        firestore.collection("RequestInfo").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    ArrayList<DocumentSnapshot> list= (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d:list){
                        requestModel=d.toObject(RequestModel.class);
                        requestModels.add(requestModel);
                        requestAdapter.notifyDataSetChanged();

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
//        RequestModel requestModel = new RequestModel("01759958460","Delta Hospital","Dhaka","O+","Emergency Patient","https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg");
//        requestModels.add(requestModel);
//        requestModels.add(requestModel);
//        requestModels.add(requestModel);
//        requestModels.add(requestModel);
//        requestModels.add(requestModel);


    }
}