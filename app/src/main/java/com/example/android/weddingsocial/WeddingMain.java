package com.example.android.weddingsocial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class WeddingMain extends AppCompatActivity {
    TextView rege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//หน้ารีโหลด
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_wedding);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent it = new Intent(getApplicationContext(), login.class);
                startActivity(it);
            }
        }, 2000);
    }

}