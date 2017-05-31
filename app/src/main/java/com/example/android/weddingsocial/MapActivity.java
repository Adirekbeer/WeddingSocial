package com.example.android.weddingsocial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String key_id;
    Double lat_id, lon_id;
    OkHttpClient okHttpClient = new OkHttpClient();
    String State, SQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(TitleApp);

        key_id = getIntent().getExtras().getString("key_id");

        AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
        asyncTaskGetData.execute(key_id);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//
//        LatLng sydney = new LatLng(0, 0);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("lat_id, lon_id"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
//            MapActivity.setText(MapActivity);
            LatLng sydney = new LatLng(lat_id, lon_id);
//            mMap.addMarker(new MarkerOptions().position(sydney).title("พิกัดปัจจุบัน" + lat_id + ", " + lon_id));
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat_id, lon_id)).title("พิกัดปัจจุบัน" + lat_id + ", " + lon_id));
            marker.showInfoWindow();
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat_id, lon_id)).zoom(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        @Override
        protected Void doInBackground(String... params) { // เป็นเมธอร์ดที่ไว้สำหรับทำงานหลังบ้าน protected Void doInBackground

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("key_id", params[0]);
            //builder.add("lat_id", params[1]);
            //builder.add("lon_id", params[2]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/map.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    State = jsonObject.getString("State");
                    SQL = jsonObject.getString("SQL");
                    lat_id = jsonObject.getDouble("lat_id");
                    lon_id = jsonObject.getDouble("lon_id");
                    System.out.println(SQL);
                    System.out.println("lat_id"+lat_id);
                    System.out.println("lon_id"+lon_id);


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
