package com.example.android.weddingsocial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NameList;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Adirek on 9/11/2559.
 */

public class RandomUser extends AppCompatActivity {
    String key_id, user_id;
    TextView lbl_random_user;
    Button btn_random_user;
    ImageView imageView_random;

    OkHttpClient okHttpClient = new OkHttpClient();
    String State, SQL, user_id_random, name_random;
    String firstname, lastname, picture;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_user);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("สุ่มผู้โชคดีงาน" +"  "+ TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        key_id = getIntent().getExtras().getString("key_id");
        user_id = getIntent().getExtras().getString("user_id");

        lbl_random_user = (TextView) findViewById(R.id.lbl_random_user);

        btn_random_user = (Button) findViewById(R.id.btn_random_user);
        btn_random_user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
                asyncTaskGetData.execute(key_id, user_id);
            }
        });

        Random _Random = new Random();
        _Random.execute(key_id);
    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        protected void onPostExecute(Void aVoid) {

            System.out.println("AsyncTaskGetData");

            lbl_random_user.setText((firstname)+"    "+(lastname));

            imageView_random = (ImageView) findViewById(R.id.imageView_random);

            Picasso.with(getApplicationContext()).load(picture)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView_random);
        }

        @Override
        protected Void doInBackground(String... params) { // เป็นเมธอร์ดที่ไว้สำหรับทำงานหลังบ้าน protected Void doInBackground

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("key_id", params[0]);
            builder.add("user_id", params[1]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/getNameRandom.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);

                    State = jsonObject.getString("State");
                    SQL = jsonObject.getString("SQL");
                    firstname = jsonObject.getString("firstname");
                    lastname = jsonObject.getString("lastname");
                    picture = jsonObject.getString("picture");
                    name_random = jsonObject.getString("name_random");

                    System.out.print("name_random" + name_random);
                    System.out.print(SQL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class Random extends  AsyncTask<String, Void, Void>{

        protected void onPostExecute(Void aVoid) {

            System.out.println("RandomRandomRandomRandomRandomRandomRandomRandomRandomRandom");

            lbl_random_user.setText((firstname)+"    "+(lastname));

            imageView_random = (ImageView) findViewById(R.id.imageView_random);

            Picasso.with(getApplicationContext()).load(picture)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView_random);
        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("key_id", params[0]);
//            builder.add("user_id", params[1]);
            //builder.add("user_id_random", params[2]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/getNameRandomShow.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);

                    State = jsonObject.getString("State");
                    SQL = jsonObject.getString("SQL");
                    firstname = jsonObject.getString("firstname");
                    lastname = jsonObject.getString("lastname");
                    picture = jsonObject.getString("picture");
                    //user_id_random = jsonObject.getString("user_id_random");

                    System.out.print("user_id_random" + user_id_random);
                    System.out.print(SQL);
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




