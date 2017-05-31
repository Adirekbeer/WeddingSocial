package com.example.android.weddingsocial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by Adirek on 8/11/2559.
 */

public class Abount_activity extends AppCompatActivity {
    String key_id, user_id;
    Button btn_give,btn_question;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abount);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("กิจกรรมภายในงาน" +"  "+ TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        key_id = getIntent().getExtras().getString("key_id");
        user_id = getIntent().getExtras().getString("user_id");



        btn_give = (Button) findViewById(R.id.btn_give);
        btn_give.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), RandomUser.class);
                it.putExtra("key_id", key_id);
                it.putExtra("user_id", user_id);
                startActivity(it);
            }
        });

        btn_question = (Button) findViewById(R.id.btn_question);
        btn_question.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), Schedule.class);
                it.putExtra("key_id", key_id);
                startActivity(it);
            }
        });
    }

}
