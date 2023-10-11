package com.example.bloodbank.Activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bloodbank.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class UserProfile extends AppCompatActivity {
    String currentUserMobile;
    TextView nameTextView;
    TextView deptTextView;
    TextView batchTextView;
    TextView rollTextView;
    TextView distTextView;
    TextView passwordTextView;
    TextView mobileTextView;
    TextView regTextView;
    TextView emailTextView;
    TextView dobTextView;
    TextView bloodTextView;
    DatabaseReference reference;
    ImageView profileImageView;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        currentUserMobile = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("currentUserMobile", null);

        // Initialize TextView elements
        nameTextView = findViewById(R.id.name);
        deptTextView = findViewById(R.id.department);
        batchTextView = findViewById(R.id.batch);
        rollTextView = findViewById(R.id.roll);
        regTextView = findViewById(R.id.reg);
        distTextView = findViewById(R.id.district);
        passwordTextView = findViewById(R.id.password);
        mobileTextView = findViewById(R.id.mobile);
        emailTextView = findViewById(R.id.email);
        bloodTextView = findViewById(R.id.blood);
        dobTextView = findViewById(R.id.dob);
        profileImageView = findViewById(R.id.profile_image);



        // Initialize Firebase Database reference
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserMobile);

        // Add a ValueEventListener to fetch and display user data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data from the database
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userDept = dataSnapshot.child("department").getValue(String.class);
                    String userBatch = dataSnapshot.child("batch").getValue(String.class);
                    String userRoll = dataSnapshot.child("roll").getValue(String.class);
                    String userReg = dataSnapshot.child("reg").getValue(String.class);
                    String userDist = dataSnapshot.child("district").getValue(String.class);
                    String userPassword = dataSnapshot.child("password").getValue(String.class);
                    String userMobile = dataSnapshot.child("number").getValue(String.class);
                    String userEmail = dataSnapshot.child("email").getValue(String.class);
                    String userBlood = dataSnapshot.child("blood_group").getValue(String.class);
                    String userDob = dataSnapshot.child("dob").getValue(String.class);
                    String imgUrl=dataSnapshot.child("photo").getValue(String.class);
                    url=imgUrl;


                    // Update TextView elements with user data
                    nameTextView.setText(userName);
                    deptTextView.setText(userDept);
                    batchTextView.setText(userBatch);
                    rollTextView.setText(userRoll);
                    regTextView.setText(userReg);
                    distTextView.setText(userDist);
                    passwordTextView.setText(userPassword);
                    mobileTextView.setText(userMobile);
                    emailTextView.setText(userEmail);
                    bloodTextView.setText(userBlood);
                    dobTextView.setText(userDob);
                    // Initialize Firebase Storage
                    Glide.with(UserProfile.this)
                            .load(url)
                            .placeholder(R.drawable.blood_drop) // Set a placeholder image
                            .error(R.drawable.blood_drop) // Set an error image
                            .into(profileImageView);


// Load the user's photo into the ImageView


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
                // For example, you can display an error message
                // or take appropriate action.
            }
        });

    }
}
