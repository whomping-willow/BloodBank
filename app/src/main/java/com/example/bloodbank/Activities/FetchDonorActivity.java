package com.example.bloodbank.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbank.Adapters.DonorAdapter;
import com.example.bloodbank.Adapters.RequestAdapter;
import com.example.bloodbank.DataModels.DonorModel;
import com.example.bloodbank.DataModels.RequestModel;
import com.example.bloodbank.R;
import com.example.bloodbank.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FetchDonorActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private List<DonorModel> donorModels;
    private DonorAdapter donorAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseFirestore firestore;
    DatabaseReference reference;
    DonorModel donorModel;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_donor);

         //Hooks

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


        Spinner spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Blood_Group, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(adapter);





        //------------------------------------


//        Toolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.search_button) {
                    //open search
                    startActivity(new Intent(FetchDonorActivity.this, SearchActivity.class));
                }
                return false;
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id=item.getItemId();
                if(id==R.id.logout){

                    Intent intent = new Intent(FetchDonorActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else if (id==R.id.home) {
                    Intent intent = new Intent(FetchDonorActivity.this, MainActivity.class);
                    startActivity(intent);

                }else if(id==R.id.userprofile){
                    Intent intent = new Intent(FetchDonorActivity.this, UserProfile.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("users");
        donorModels = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        donorAdapter = new DonorAdapter(donorModels, this);
        recyclerView.setAdapter(donorAdapter);
        populateHomePage();


        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBloodGroup = parentView.getItemAtPosition(position).toString();

                // Call the method to populate the RecyclerView with donors of the selected blood group
                if ("Select Blood Group".equals(selectedBloodGroup))
                {
                    populateHomePage();
                }
                else
                 populateHomePage2(selectedBloodGroup);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });






    }
    public void populateHomePage(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donorModels.clear();
                for (DataSnapshot d:snapshot.getChildren()){
                    DonorModel donor=d.getValue(DonorModel.class);
                    donorModels.add(donor);

                }
                donorAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void populateHomePage2(final String bloodGroup) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donorModels.clear(); // Clear the previous data

                for (DataSnapshot d : snapshot.getChildren()) {
                    DonorModel donor = d.getValue(DonorModel.class);

                    // Check if the donor's blood group matches the desired blood group
                    if (donor != null && donor.getBlood_group().equalsIgnoreCase(bloodGroup)) {
                        donorModels.add(donor);
                    }
                }

                donorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }


}
