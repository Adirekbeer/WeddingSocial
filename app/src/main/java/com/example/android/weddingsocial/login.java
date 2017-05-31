package com.example.android.weddingsocial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.*;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends Activity implements View.OnClickListener {

    String StrUsername, StrPassword;
    EditText username, password;
    TextView rege;
    Button btlogin;
    OkHttpClient okHttpClient = new OkHttpClient();
    String State;

    String user_id, sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btlogin = (Button) findViewById(R.id.button);
        rege = (TextView) findViewById(R.id.rege);

        rege.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(getApplicationContext(), register.class);
                startActivity(it);
            }
        });

        btlogin.setOnClickListener(this);

    }

    public void onBackPressed() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(login.this);

        alertDialog.setTitle("ออกจากแอปพลิเคชั่น...");
        alertDialog.setMessage("ต้องการออกจากแอปพลิเคชั่นใช่หรือไม่ ?");
        //alertDialog.setIcon(R.drawable.bgblood);

        alertDialog.setPositiveButton("ใช่",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //คลิกใช่ ออกจากโปรแกรม
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            }
        });

        alertDialog.setNegativeButton("ไม่ใช่",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //คลิกไม่ cancel dialog
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    public void onClick(View v) {

        StrUsername = username.getText().toString().trim();
        StrPassword = password.getText().toString().trim();

        switch (v.getId()) {
            case R.id.button:
                if(username.getText().length()==0){
                    username.setError("กรุณาระบุชื่อผู้ใช้งาน");
                }
                else if(password.getText().length()==0){
                    password.setError("กรุณาระบุรหัสผ่าน");
                }
                else{
                    AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
                    asyncTaskGetData.execute( StrUsername, StrPassword);

                }
        }

        Bundle b = new Bundle();
        b.putString("username", StrUsername);
        b.putString("password", StrPassword);
    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
        }

        @Override
        protected Void doInBackground(String... params) {

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("username", params[0]);
            builder.add("password", params[1]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/login.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();

                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(result);

                    State = jsonObject.getString("State");
                    user_id = jsonObject.getString("user_id");
                    sql = jsonObject.getString("sql");
                    System.out.println(sql);


                    if (State.equals("OK")) {
                        System.out.println("รหัสผ่านและชื่อผู้ใช้งานถูกต้อง");

                        Intent it = new Intent(login.this, FeedEventActivity.class);
                        it.putExtra("user_id", user_id);
                        startActivity(it);
                    } else {

                        System.out.println("รหัสผ่านและชื่อผู้ไม่ใช้งานถูกต้อง");
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