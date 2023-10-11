package com.example.bloodbank.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.bloodbank.R;

public class SplashScreen extends AppCompatActivity {

    TextView jubloodbook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        jubloodbook=findViewById(R.id.textView1);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(SplashScreen.this,LoginActivity.class));
               finish();
            }
        },3000);
        Animation myanimation = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.animation1);
        jubloodbook.startAnimation(myanimation);
    }
}