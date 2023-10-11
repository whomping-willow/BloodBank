package com.example.bloodbank.Activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.example.bloodbank.R.id.choose_text;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //Create object of database reference class to access firebase's real time database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bloodbank-46964-default-rtdb.firebaseio.com/");

    private EditText nameEt,deptEt,batchEt,rollEt,distEt,passwordEt,mobileEt,regEt,emailEt,dob;

    private Spinner bloodGroupEt;
    TextView chooseImageText;
    private Button submitButton;
    TextView logInText;
    Uri imageUri;
    private Bitmap bitmap;
    ImageView postImage;
    private String PhotoUrl;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private StorageReference mStorageref;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private DatePickerDialog picker;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        logInText = findViewById(R.id.log_in_text);
        nameEt=findViewById(R.id.name);
        deptEt=findViewById(R.id.department);
        batchEt=findViewById(R.id.batch);
        rollEt=findViewById(R.id.roll);
        regEt=findViewById(R.id.reg);
        distEt=findViewById(R.id.district);
        passwordEt=findViewById(R.id.password);
        mobileEt=findViewById(R.id.number);
        emailEt=findViewById(R.id.email);
        bloodGroupEt=findViewById(R.id.blood_group);
        dob=findViewById(R.id.dob);

        submitButton=findViewById(R.id.submit_button);
        chooseImageText=findViewById(choose_text);
        postImage=findViewById(R.id.post_image);


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar= Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                picker=new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dob.setText(dayOfMonth+"/"+(month+1)+"/"+year);


                    }
                },year,month,day);
                picker.show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bloodGroupEt.setAutofillHints("Blood Group");
        }

        firestore= FirebaseFirestore.getInstance();
        storage= FirebaseStorage.getInstance();
        mStorageref=storage.getReference();
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();






        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

            }
        });

        chooseImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to pick image
                permission();

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name,dept,batch,roll,district,bloodGroup,password,mobile,reg,email,sdob;
                name=nameEt.getText().toString();
                dept=deptEt.getText().toString();
                batch=batchEt.getText().toString();
                roll=rollEt.getText().toString();
                reg=regEt.getText().toString();
                district=distEt.getText().toString();
                bloodGroup=bloodGroupEt.getSelectedItem().toString();
                password=passwordEt.getText().toString();
                mobile=mobileEt.getText().toString();
                email=emailEt.getText().toString();
                sdob=dob.getText().toString();
                uploadImage(mobile);

                    if (isValid(name, dept,batch,roll,reg,district, bloodGroup,email, password, mobile,sdob)) {
                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //check if the user (reg) is not registered before
                                if (snapshot.hasChild(mobile)){
                                    Toast.makeText(RegisterActivity.this,"This user is already registered",Toast.LENGTH_SHORT).show();

                                }
                                else {
                                    //sending data to firebase realtime database
                                    //using mobile as unique identfier
                                    //so all other details of user comes under registration number
    //                                if(!PhotoUrl.isEmpty())
    //                                {
    //                                    databaseReference.child("users").child(mobile).child("photo").setValue(PhotoUrl);
    //                                }
                                    databaseReference.child("users").child(mobile).child("name").setValue(name);
                                    databaseReference.child("users").child(mobile).child("department").setValue(dept);
                                    databaseReference.child("users").child(mobile).child("batch").setValue(batch);
                                    databaseReference.child("users").child(mobile).child("roll").setValue(roll);
                                    databaseReference.child("users").child(mobile).child("reg").setValue(reg);
                                    databaseReference.child("users").child(mobile).child("district").setValue(district);
                                    databaseReference.child("users").child(mobile).child("dob").setValue(sdob);
                                    databaseReference.child("users").child(mobile).child("number").setValue(mobile);
                                    databaseReference.child("users").child(mobile).child("email").setValue(email);
                                    databaseReference.child("users").child(mobile).child("blood_group").setValue(bloodGroup);
                                    databaseReference.child("users").child(mobile).child("password").setValue(password);

                                    Toast.makeText(RegisterActivity.this,"User registered successfully",Toast.LENGTH_SHORT).show();


                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();

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


    //--------------------------------------uploading image here-----------------------------------
    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launcher.launch(intent);

    }

    ActivityResultLauncher<Intent> launcher
            =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()== Activity.RESULT_OK){
                    Intent data=result.getData();

                    if(data!=null && data.getData()!=null){
                        imageUri=data.getData();

                        try{
                            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);

                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }

                    }

                    if (imageUri!=null){
                        postImage.setImageBitmap(bitmap);
                    }
                }
            }
    );


    private void permission() {
        if (PermissionChecker.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED) {
            //asking for permission
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 401);
        } else {
            //permission is already there
            pickImage();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 401) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                //permission was granted
                pickImage();
            } else {
                //permission not granted
                showMessage("Permission Declined");
            }
        }
    }

    private void uploadImage(String mobile){
        if(imageUri!=null){
            //create storage instance
            final StorageReference myRef=mStorageref.child("photo/"+imageUri.getLastPathSegment());
            myRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //getdownloadurl to store in string
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri!=null){
                                PhotoUrl=uri.toString();

                                databaseReference.child("users").child(mobile).child("photo").setValue(PhotoUrl);


                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage(e.getMessage());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage(e.getMessage());

                }
            });
        }
        else {
            showMessage("No image selected");
        }


    }

//------------------------------------------------------------------------------------------------------------------
    private boolean isValid(String name, String dept,String batch,String roll,String reg,String district, String blood_group,String email, String password,
                            String mobile,String sdob) {
        List<String> valid_blood_groups = new ArrayList<>();
        valid_blood_groups.add("A+");
        valid_blood_groups.add("A-");
        valid_blood_groups.add("B+");
        valid_blood_groups.add("B-");
        valid_blood_groups.add("AB+");
        valid_blood_groups.add("AB-");
        valid_blood_groups.add("O+");
        valid_blood_groups.add("O-");
        String[] dateParts = sdob.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try{
            Date dateOfBirth = sdf.parse(sdob);
            Date currentDate = new Date();

            // Calculate age by comparing the current date with the date of birth
            long ageInMillis = currentDate.getTime() - dateOfBirth.getTime();
            long ageInYears = ageInMillis / (1000L * 60 * 60 * 24 * 365);

            if (ageInYears < 18 || ageInYears>65) {
                showMessage("You have to be between 18 to 65 years old to register as donor.");
                return false;
            }

        }catch (ParseException e){
            showMessage("Invalid date of birth format. Please use the format dd/MM/yyyy.");
            return false;
        }
        if (name.isEmpty()) {
            showMessage("Name is empty");
            return false;
        }
        if (dept.isEmpty()) {
            showMessage("Department name is required");
            return false;
        }
        if (batch.isEmpty()) {
            showMessage("Batch is required");
            return false;
        }
        if (roll.isEmpty()) {
            showMessage("Roll is required");
            return false;
        }
        if(reg.isEmpty()){
            showMessage("Registration No. is required");
            return false;
        }
       if(email.isEmpty()){
            showMessage("Email is required");
            return false;
        }
        if (district.isEmpty()) {
            showMessage("District name is required");
            return false;
        }
        if (!valid_blood_groups.contains(blood_group)) {
            showMessage("Blood group invalid choose from " + valid_blood_groups);
            return false;
        } if (mobile.length() != 11) {
            showMessage("Invalid mobile number, number should be of 11 digits");
            return false;
        }


        return true;
    }
    private void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}