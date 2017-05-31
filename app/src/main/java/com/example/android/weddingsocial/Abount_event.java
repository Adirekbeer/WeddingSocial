package com.example.android.weddingsocial;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Abount_event extends AppCompatActivity{
    String key_id;
    Button btn_map, btn_schedule;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abount_event);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("กำหนดการณ์ภายในงาน" +"  "+ TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        key_id = getIntent().getExtras().getString("key_id");

        btn_map = (Button) findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), MapActivity.class);
                it.putExtra("key_id", key_id);
                startActivity(it);
            }
        });

        btn_schedule = (Button) findViewById(R.id.btn_schedule);
        btn_schedule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), Schedule.class);
                it.putExtra("key_id", key_id);
                startActivity(it);
            }
        });
    }

}
