package com.example.android.weddingsocial;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Pc100 on 20/10/2559.
 */
public class Schedule extends AppCompatActivity {

    String key_id;
    TextView lbl_schedule;
    OkHttpClient okHttpClient = new OkHttpClient();
    String State, SQL, Schedule;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("กำหนดการณ์ภายในงาน" +"  "+ TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        key_id = getIntent().getExtras().getString("key_id");

        lbl_schedule = (TextView) findViewById(R.id.lbl_schedule);

        AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
        asyncTaskGetData.execute(key_id);
    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            lbl_schedule.setText(Schedule);
        }

        @Override
        protected Void doInBackground(String... params) { // เป็นเมธอร์ดที่ไว้สำหรับทำงานหลังบ้าน protected Void doInBackground

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("key_id", params[0]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/getSchedule.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    State = jsonObject.getString("State");
                    SQL = jsonObject.getString("SQL");
                    Schedule = jsonObject.getString("Schedule");
                    System.out.println(SQL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }
    }

}
