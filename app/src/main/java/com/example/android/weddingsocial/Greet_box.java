package com.example.android.weddingsocial;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Greet_box extends AppCompatActivity implements View.OnClickListener{

    String user_id, key_id, ref_greetbox_post;
    EditText greetbox_post;
    String State, goback;
    Button btn_greetbox, btn_ToListGreetbox;
    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greetbox);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("กล่องอวยพรงาน" +"  "+ TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user_id = getIntent().getExtras().getString("user_id");
        key_id = getIntent().getExtras().getString("key_id");

        try {
            goback = getIntent().getExtras().getString("goback");
            if(goback.equals("yes")){
                showAlert();
            }
        } catch (NullPointerException e) {

        }

        greetbox_post = (EditText) findViewById(R.id.txt_greetbox);

        btn_greetbox = (Button) findViewById(R.id.btn_greetbox);
        btn_greetbox.setOnClickListener(this);

        btn_ToListGreetbox = (Button) findViewById(R.id.btn_ToListGreetbox);
        btn_ToListGreetbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), FeedGreetboxActivity.class);
                it.putExtra("key_id", key_id);
                it.putExtra("user_id", user_id);
                startActivity(it);
            }
        });
    }

    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Greet_box.this, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("ยังไม่มีข้อความที่อวยพรภายในงาน");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(true);
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {

        ref_greetbox_post = greetbox_post.getText().toString().trim();

        switch (v.getId()){
            case R.id.btn_greetbox:
                if (greetbox_post.getText().length()==0){
                    greetbox_post.setError("กรุณาระบุข้อความที่ท่านต้องการอวยพร");
                }
                else {

                    AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
                    asyncTaskGetData.execute(user_id, key_id, ref_greetbox_post);

                    Intent it = new Intent(Greet_box.this, FeedGreetboxActivity.class);
                    it.putExtra("key_id",key_id);
                    it.putExtra("user_id", user_id);
                    startActivity(it);
                }
            break;
        }
    }

    public void onBackPressed() {
        Intent it = new Intent(Greet_box.this, MainActivity.class);
        it.putExtra("key_id",key_id);
        it.putExtra("user_id", user_id);
        startActivity(it);
        finish();
    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
        }

        @Override
        protected Void doInBackground(String... params) {

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("user_id", params[0]);
            builder.add("key_id", params[1]);
            builder.add("greetbox_post", params[2]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/saveGreetbox.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();

                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(result);

                    State = jsonObject.getString("State");
                    String SQL = jsonObject.getString("SQL");
                    System.out.println(State);
                    System.out.println("SQL:" + SQL);

                    if (State.equals("OK")) {
                        System.out.println("เพิ่มข้อความอวยพรสำเร็จ");
                    }
                    else {
                        System.out.println("เพิ่มข้อความอวยพรไม่สำเร็จ");
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
