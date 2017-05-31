package com.example.android.weddingsocial;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by adirek on 4/8/2016.
 */
public class register extends AppCompatActivity implements View.OnClickListener {
    String Strtextga1, Strtextga2, Strtextga3, Strtextga4, Strtextga5;
    EditText register_username, register_password, verify_password, firstname, lastname, email;
    Button ref_btn_register;
    OkHttpClient okHttpClient = new OkHttpClient();
    String State;

    private static final int RESULT_LOAD_IMAGE = 1;
    final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    ImageView imageToUpload;
    String filePath, fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        register_username = (EditText) findViewById(R.id.editText);
        register_password = (EditText) findViewById(R.id.editText2);
        verify_password = (EditText) findViewById(R.id.editText7);
        firstname = (EditText) findViewById(R.id.editText3);
        lastname = (EditText) findViewById(R.id.editText4);
        email = (EditText) findViewById(R.id.editText5);

        ref_btn_register = (Button) findViewById(R.id.btn_register);
        ref_btn_register.setOnClickListener(this);

        imageToUpload = (ImageView) findViewById(R.id.imageViewToUpload);
        imageToUpload.setOnClickListener(this);

    }

    public void onClick(View v) {

        Strtextga1 = register_username.getText().toString().trim();
        Strtextga2 = register_password.getText().toString().trim();
        Strtextga3 = firstname.getText().toString().trim();
        Strtextga4 = lastname.getText().toString().trim();
        Strtextga5 = email.getText().toString().trim();

        switch (v.getId()) {
            case R.id.imageViewToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;

            case R.id.btn_register:
                if(register_username.getText().length()==0){
                    register_username.setError("กรุณาระบุชื่อผู้ใช้งาน");
                }
                else if(register_password.getText().length()<=5){
                    register_password.setError("กรุณาระบุรหัสผ่านมากกว่า 6 ตัวอักษร");
                }
                else if (!register_password.getText().toString().trim().equals(verify_password.getText().toString().trim())) {
                    verify_password.setError("รหัสผ่านไม่ตรงกับที่ระบุข้างต้น");
                }
                else if(firstname.getText().length()==0){
                    firstname.setError("กรุณาระบุชื่อ");
                }
                else if(lastname.getText().length()==0){
                    lastname.setError("กรุณาระบุนามสกุล");
                }
                else if(email.getText().length()==0){
                    email.setError("กรุณาระบุอีเมล์");
                }
                else{
                    AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData();
                    asyncTaskGetData.execute(Strtextga1, Strtextga2, Strtextga3, Strtextga4, Strtextga5);
                }

                break;

            default: {
                Intent it = new Intent(getApplicationContext(),login.class);
                startActivity(it);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
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
        protected Void doInBackground(String... params) {
            File sourceFile = new File(filePath);
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", params[0])
                    .addFormDataPart("password", params[1])
                    .addFormDataPart("firstname", params[2])
                    .addFormDataPart("lastname", params[3])
                    .addFormDataPart("email", params[4])
                    .addFormDataPart("sImage", fileName, RequestBody.create(MEDIA_TYPE_JPG, sourceFile)).build();
            Request request = new Request.Builder().url("http://weddingsocial.org/beer/register.php").post(body).build();
            Response response;
            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    State = jsonObject.getString("State");
                    String sql = jsonObject.getString("sql");
                    System.out.println(State);
                    System.out.println("SQL:" + sql);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        /*
        @Override
        protected Void doInBackground(String... params) { // เป็นเมธอร์ดที่ไว้สำหรับทำงานหลังบ้าน protected Void doInBackground

            File sourceFile = new File(filePath);

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", params[0])
                    .addFormDataPart("password", params[1])
                    .addFormDataPart("firstname", params[2])
                    .addFormDataPart("lastname", params[3])
                    .addFormDataPart("email", params[4])
                    .addFormDataPart("sImage", fileName, RequestBody.create(MEDIA_TYPE_JPG, sourceFile)).build();

            Request request = new Request.Builder().url("http://weddingsocial.org/beer/register.php").post(body).build();
            Response response;

            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();

                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(result);
                    State = jsonObject.getString("State");
                    String sql = jsonObject.getString("sql");
                    System.out.println(State);
                    System.out.println("SQL:" + sql);

                    if (State.equals("OK")) { //equals คือเท่ากับ ใช้กับอ็อปเจ็กซ์
                        System.out.println("สมัครสมาชิกสำเร็จ");

                        Intent it = new Intent(register.this, login.class);
                        startActivity(it);
                        System.exit(0);
                    } else {
                        System.out.println("สมัครสมาชิกไม่สำเร็จ");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }
        */
    }
}
