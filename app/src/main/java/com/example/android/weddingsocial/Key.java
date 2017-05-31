package com.example.android.weddingsocial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Key extends AppCompatActivity {
    private static final String TAG = "Key";
    EditText key;
    Button submit, cancel;
    String State, user_id, sql, key_id, title, wedding_id;

    OkHttpClient okHttpClient = new OkHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ระบุคีย์ที่ท่านต้องการเพิ่มงาน");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user_id = getIntent().getExtras().getString("user_id");

        key = (EditText) findViewById(R.id.txt_key);
        submit = (Button) findViewById(R.id.btn_submit);
        cancel = (Button) findViewById(R.id.btn_cancel);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(key.getText().toString());
                String val1 = user_id;
                String val2 = key.getText().toString();
                System.out.println("val1" + val1);

                switch (v.getId()){
                    case R.id.btn_submit:
                        if (key.getText().length()==0){
                            key.setError("กรุณาระบุคีย์ไหม่อีกครั้ง");
                        }
                        else {
                            AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
                            asyncTaskGetData.execute(val1, val2);
                        }
                    break;
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            if(State.equals("Denied")){
                Toast.makeText(getApplicationContext(), ("คีย์ไม่ถูกต้อง"),Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(String... params) {

            Log.d(TAG, "Hello from the othe side");

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("user_id", params[0]);
            builder.add("key_id", params[1]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/validateKey.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();

                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(result);

                    State = jsonObject.getString("State");
                    user_id = jsonObject.getString("user_id");
                    key_id = jsonObject.getString("key_id");
                    sql = jsonObject.getString("sql");
                    title = jsonObject.getString("title");
                    wedding_id = jsonObject.getString("wedding_id");
                    System.out.print("wwwwwwwwwwwwww"+ wedding_id);
                    System.out.println(sql);


                    if (State.equals("OK")) {

                        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Title", title);
                        editor.commit();

                        Intent Key = new Intent(com.example.android.weddingsocial.Key.this, MainActivity.class);
                        Key.putExtra("user_id", user_id);
                        Key.putExtra("key_id", key_id);
                        startActivity(Key);
                        System.exit(0);
                    } else {
                        System.out.println(State);
                    }

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
