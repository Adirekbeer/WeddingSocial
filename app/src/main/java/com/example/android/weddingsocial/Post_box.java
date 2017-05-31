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
import android.widget.ImageButton;
import android.widget.ImageView;

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

public class Post_box extends AppCompatActivity implements View.OnClickListener{

    String user_id, key_id, ref_postbox_post;
    EditText postbox_post;
    String State;
    Button btn_postbox, btn_ToListpostbox;
    OkHttpClient okHttpClient = new OkHttpClient();

    private static final int RESULT_LOAD_IMAGE = 1;
    final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    ImageView imageView_postbox;
    ImageButton imageButton;
    String filePath, fileName;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postbox);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title" , null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("โพสต์งาน" +"  "+ TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user_id = getIntent().getExtras().getString("user_id");
        key_id = getIntent().getExtras().getString("key_id");

        postbox_post = (EditText) findViewById(R.id.txt_postbox);

        btn_postbox = (Button) findViewById(R.id.btn_postbox);
        btn_postbox.setOnClickListener(this);

        imageView_postbox = (ImageView) findViewById(R.id.imageView_postbox);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);

//        btn_ToListpostbox = (Button) findViewById(R.id.btn_ToListpostbox);
//        btn_ToListpostbox.setOnClickListener(this);

        btn_ToListpostbox = (Button) findViewById(R.id.btn_ToListpostbox);
        btn_ToListpostbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), FeedPostboxActivity.class);
                it.putExtra("key_id", key_id);
                it.putExtra("user_id", user_id);
                startActivity(it);
            }
        });
    }

    @Override
    public void onClick(View v) {

        ref_postbox_post = postbox_post.getText().toString().trim();

        switch (v.getId()){
            case R.id.imageButton:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;

            case R.id.btn_postbox:
                if (postbox_post.getText().length()==0){
                    postbox_post.setError("กรุณาระบุข้อความที่ท่านต้องการโพสต์");
                }
                else {

                    AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
                    asyncTaskGetData.execute(user_id, key_id, ref_postbox_post);

                    Intent it = new Intent(Post_box.this, FeedPostboxActivity.class);
                    it.putExtra("key_id", key_id);
                    it.putExtra("user_id", user_id);
                    startActivity(it);
                }
            break;
        }
    }

    public void onBackPressed() {
        Intent it = new Intent(Post_box.this, MainActivity.class);
        it.putExtra("key_id",key_id);
        it.putExtra("user_id", user_id);
        startActivity(it);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageView_postbox.setImageURI(selectedImage);
            //Get your Image Path
            String[] filePathColumn = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

            System.out.println("filePath" + filePath);

            cursor.close();
        }
    }

    public class AsyncTaskGetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
        }

        @Override
        protected Void doInBackground(String... params) { // เป็นเมธอร์ดที่ไว้สำหรับทำงานหลังบ้าน protected Void doInBackground

            File sourceFile = new File(filePath);

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", params[0])
                    .addFormDataPart("key_id", params[1])
                    .addFormDataPart("postbox_post", params[2])
                    .addFormDataPart("sImage", fileName, RequestBody.create(MEDIA_TYPE_JPG, sourceFile)).build();

            Request request = new Request.Builder().url("http://weddingsocial.org/beer/savePostbox.php").post(body).build();
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
                    System.out.println(ref_postbox_post);

                    if (State.equals("OK")) { //equals คือเท่ากับ ใช้กับอ็อปเจ็กซ์
                        System.out.println("เพิ่มโพสต์สำเร็จ");
                    }
                    else {
                        System.out.println("เพิ่มโพสต์ไม่สำเร็จ");
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
