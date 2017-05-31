package com.example.android.weddingsocial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String key_id,user_id;
    private static final String TAG = "Namelist_Recycle";
    private static final String PLEASE_WAIT_MESSAGE = "Loading....";
    private RecyclerView recyclerView;
    private FeedNamelistRecyclerAdapter adapter;
    private List<FeedNamelistItem> feedsList;

    boolean showResult = true;
    OkHttpClient okHttpClient = new OkHttpClient();
    ArrayList<String> name = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("รายชื่อแขกงาน"+" "+TitleApp);

        key_id = getIntent().getExtras().getString("key_id");
        user_id = getIntent().getExtras().getString("user_id");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final String url = "http://weddingsocial.org/beer/getNameList.php?key_id=" + key_id;
        new AsyncHttpTask(MainActivity.this).execute(url);

        recyclerView = (RecyclerView) findViewById(R.id.namelist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView txtView = (TextView) findViewById(R.id.textIntro);


    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;
        Context context;

        public AsyncHttpTask(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage(PLEASE_WAIT_MESSAGE);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1;
                } else {
                    result = 0;
                    showResult = false;
                }
            } catch (Exception e) {
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
//                adapter = new FeedNamelistRecyclerAdapter(MainActivity.this, feedsList);
//                recyclerView.setAdapter(adapter);
                Log.d(TAG, "onPostExecute() method running");
                adapter = new FeedNamelistRecyclerAdapter(MainActivity.this, feedsList);
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                progressDialog.dismiss();

            } else {
//                Toast.makeText(Namelist_Recycle.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String result) {

        try {

            JSONObject response = new JSONObject(result);
            JSONArray jsonArray = response.optJSONArray("Data");

            feedsList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                FeedNamelistItem item = new FeedNamelistItem();
                item.setUserID(object.optString("user_id"));
                item.setFirstname(object.optString("Firstname"));
                item.setLastname(object.optString("Lastname"));
                item.setThumbnail(object.optString("Picture"));
                feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(MainActivity.this, FeedEventActivity.class);
        it.putExtra("key_id",key_id);
        it.putExtra("user_id", user_id);
        startActivity(it);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.person) {
            Bundle b = new Bundle();
            b.putString("key_id", key_id);
            b.putString("user_id", user_id);
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + key_id);
            startActivity(new Intent(MainActivity.this, MainActivity.class).putExtras(b));
        }
        if (id == R.id.profile) {
            Bundle b = new Bundle();
            b.putString("user_id", user_id);
            startActivity(new Intent(this, edit_profile.class).putExtras(b));
        }
        if (id == R.id.greetbox) {
            Bundle b = new Bundle();
            b.putString("user_id", user_id);
            b.putString("key_id", key_id);
            startActivity(new Intent(this, Greet_box.class).putExtras(b));
        }
        if (id == R.id.postbox) {
            Bundle b = new Bundle();
            b.putString("user_id", user_id);
            b.putString("key_id", key_id);
            startActivity(new Intent(this, Post_box.class).putExtras(b));
        }
        if (id == R.id.activity_abount){
            Bundle b = new Bundle();
            b.putString("key_id", key_id);
            b.putString("user_id", user_id);
            startActivity(new Intent(this, Abount_activity.class).putExtras(b));
        }
        if (id == R.id.abount_event){
            Bundle b = new Bundle();
            b.putString("key_id", key_id);
            b.putString("user_id", user_id);
            startActivity(new Intent(this, Abount_event.class).putExtras(b));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
