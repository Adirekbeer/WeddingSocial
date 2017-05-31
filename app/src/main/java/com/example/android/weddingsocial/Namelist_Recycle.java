//package com.example.android.weddingsocial;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.OkHttpClient;
//
//public class Namelist_Recycle extends AppCompatActivity {
//
//    private static final String TAG = "Namelist_Recycle";
//    private RecyclerView recyclerView;
//    private FeedNamelistRecyclerAdapter adapter;
//    private List<FeedNamelistItem> feedsList;
//
//    boolean showResult = true;
//
//    String key_id,user_id;
//    OkHttpClient okHttpClient = new OkHttpClient();
//    ArrayList<String> name = new ArrayList<>();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_feed_namelist);
//
//        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
//        String TitleApp = sp.getString("Title" , null);
//        System.out.println(TitleApp);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("รายชื่อแขกงาน" +"  "+ TitleApp);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        key_id = getIntent().getExtras().getString("key_id");
//        user_id = getIntent().getExtras().getString("user_id");
//
//        final String url = "http://weddingsocial.org/beer/getNameList.php?key_id=" + key_id;
//        new AsyncHttpTask().execute(url);
//
//        recyclerView = (RecyclerView) findViewById(R.id.namelist_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        TextView txtView = (TextView) findViewById(R.id.textIntro);
//    }
//
//    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
//
//        @Override
//        protected Integer doInBackground(String... params) {
//            Integer result = 0;
//            HttpURLConnection urlConnection;
//            try {
//                URL url = new URL(params[0]);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                int statusCode = urlConnection.getResponseCode();
//
//                if (statusCode == 200) {
//                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = r.readLine()) != null) {
//                        response.append(line);
//                    }
//                    parseResult(response.toString());
//                    result = 1;
//                } else {
//                    result = 0;
//                    showResult = false;
//                }
//            } catch (Exception e) {
//                Log.d(TAG, e.getLocalizedMessage());
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Integer result) {
//
//            if (result == 1) {
//                adapter = new FeedNamelistRecyclerAdapter(Namelist_Recycle.this, feedsList);
//                recyclerView.setAdapter(adapter);
//            } else {
////                Toast.makeText(Namelist_Recycle.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void parseResult(String result) {
//
//        try {
//
//            JSONObject response = new JSONObject(result);
//            JSONArray jsonArray = response.optJSONArray("Data");
//
//            feedsList = new ArrayList<>();
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject object = jsonArray.optJSONObject(i);
//                FeedNamelistItem item = new FeedNamelistItem();
//                item.setUserID(object.optString("user_id"));
//                item.setFirstname(object.optString("Firstname"));
//                item.setLastname(object.optString("Lastname"));
//                item.setThumbnail(object.optString("Picture"));
//                feedsList.add(item);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
