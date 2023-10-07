package com.example.bloodbank.Activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.bloodbank.DataModels.RequestModel;
import com.example.bloodbank.R;
import com.example.bloodbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONException;
import org.json.JSONObject;

public class MakeRequestActivity extends AppCompatActivity {
  private EditText edPhone,edLocation,edMsg;
  private Spinner edBlood,edDivision;
  EditText messageText;
  TextView chooseImageText;
  ImageView postImage;
  Button submit_button;
  Uri imageUri;
  private Bitmap bitmap;
  ImageView CarImageView;
  private FirebaseStorage storage;
  private FirebaseFirestore firestore;
  private StorageReference mStorageref;
  private String PhotoUrl;
  //private FirebaseAuth fireBaseAuth;
  //private String CurrentUserId;
  DrawerLayout drawerLayout;
  NavigationView navigationView;
  Toolbar toolbar;





  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_request);

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

    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.logout){

          Intent intent = new Intent(MakeRequestActivity.this, LoginActivity.class);
          startActivity(intent);
        }
        else if(id==R.id.home){
          Intent intent = new Intent(MakeRequestActivity.this, MainActivity.class);
          startActivity(intent);
        }
        return true;
      }
    });


//    AndroidNetworking.initialize(getApplicationContext());
    //messageText = findViewById(R.id.message);
    chooseImageText = findViewById(R.id.choose_text);
    postImage = findViewById(R.id.post_image);

    //create instances
    firestore=FirebaseFirestore.getInstance();
    storage=FirebaseStorage.getInstance();
    mStorageref=storage.getReference();
    //fireBaseAuth=FirebaseAuth.getInstance();
    //CurrentUserId=fireBaseAuth.getCurrentUser().getUid();

    edPhone=findViewById(R.id.getMobile);
    edLocation=findViewById(R.id.getLocation);
    edDivision=findViewById(R.id.SpinnerDivision);
    edBlood=findViewById(R.id.SpinnerBlood);
    edMsg=findViewById(R.id.message);


    submit_button = findViewById(R.id.submit_button);
    submit_button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        uploadImage();
      }
    });

    chooseImageText.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        //code to pick image
        permission();

      }
    });

  }

  public void onBackPressed() {

    if(drawerLayout.isDrawerOpen(GravityCompat.START)){
      drawerLayout.closeDrawer(GravityCompat.START);
    }
    else{
      super.onBackPressed();
    }

  }


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
                          bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);

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




  private void showMessage(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  private void uploadImage(){
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
                 UploadRequestInfo();


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

  private void UploadRequestInfo(){
    String mobile=edPhone.getText().toString().trim();
    String location=edLocation.getText().toString().trim();
    String division=edDivision.getSelectedItem().toString().trim();
    String blood=edBlood.getSelectedItem().toString().trim();
    String msg=edMsg.getText().toString().trim();

    if(TextUtils.isEmpty(mobile)||TextUtils.isEmpty(location)||TextUtils.isEmpty(division)||TextUtils.isEmpty(blood)||TextUtils.isEmpty(msg)){
      showMessage("Please Fill all the Fields");
    }

    else{
      DocumentReference documentReference=firestore.collection("RequestInfo").document();
      //create requestModel class
      //set all data into RequestModel class

      RequestModel requestModel=new RequestModel(mobile,location,division,blood,msg,PhotoUrl);
      documentReference.set(requestModel, SetOptions.merge()).addOnCompleteListener(
              new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()){
                    if(task.isSuccessful()){
                      documentReference.set(requestModel,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          showMessage("Upload Successful");
                        }
                      }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                          showMessage(e.getMessage());
                        }
                      });
                    }
                  }

                }
              }
      ).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          showMessage(e.getMessage());

        }
      });


    }

  }




}
