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

public class edit_profile extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    EditText firstname, lastname, email;
    Button save;
    OkHttpClient okHttpClient = new OkHttpClient();
    String user_id, key_id;

    ImageView profie_picture;
    String filePath, fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("โปรไฟล์ส่วนตัวผู้ใช้งาน");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user_id = getIntent().getExtras().getString("user_id");
        key_id = getIntent().getExtras().getString("key_id");


        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        profie_picture = (ImageView) findViewById(R.id.imageView_edit);

        save = (Button) findViewById(R.id.btn_saveEditProfile);
        save.setOnClickListener(this);

        profie_picture.setOnClickListener(this);

    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
        }

        @Override
        protected Void doInBackground(String... params) {

            FormBody.Builder builder = new FormBody.Builder();

            builder.add("user_id", params[0]);
            builder.add("firstname", params[1]);
            builder.add("lastname", params[2]);
            builder.add("email", params[3]);
            RequestBody body = builder.build();

            Request request = new Request.Builder().url("http://weddingsocial.org/beer/edit_profile.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();

            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageView_edit:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;

            case R.id.btn_saveEditProfile:

                String _firstname = firstname.getText().toString();
                String _lastname = lastname.getText().toString();
                String _email = email.getText().toString();

                AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
                asyncTaskGetData.execute(user_id, _firstname, _lastname, _email);

                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                it.putExtra("user_id", user_id);
                it.putExtra("key_id", key_id);
                startActivity(it);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profie_picture.setImageURI(selectedImage);
            //Get your Image Path
            String[] filePathColumn = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        }
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }
}