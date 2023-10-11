package com.example.bloodbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bloodbank.R;
import com.example.bloodbank.Utils.Endpoints;
import com.example.bloodbank.Utils.VolleySingleton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bloodbank-46964-default-rtdb.firebaseio.com/");

    EditText numberEt, passwordEt;
    Button submit_button;
    TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        numberEt = findViewById(R.id.number);
        passwordEt = findViewById(R.id.password);
        submit_button = findViewById(R.id.submit_button);
        signUpText = findViewById(R.id.sign_up_text);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberEt.setError(null);
                passwordEt.setError(null);
                String number = numberEt.getText().toString();
                String password = passwordEt.getText().toString();
                if (isValid(number, password)) {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(number)){
                                final String password=snapshot.child(number).child("password").getValue(String.class);
                                if(password.equals(password)){
                                    showMessage("Successfully logged in");
                                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                                            .putString("currentUserMobile", number)
                                            .apply();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                }
                                else {
                                    showMessage("Wrong Password");
                                }


                            }
                            else{
                                showMessage("Wrong Number");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    private boolean isValid(String number, String password) {
        if (number.isEmpty()) {
            showMessage("Empty Mobile Number");
            numberEt.setError("Empty Mobile Number");
            return false;
        } else if (password.isEmpty()) {
            showMessage("Empty Password");
            passwordEt.setError("Empty Password");
            return false;
        }
        return true;
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
