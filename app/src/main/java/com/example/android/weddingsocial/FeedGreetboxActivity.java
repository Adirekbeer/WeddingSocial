package com.example.android.weddingsocial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedGreetboxActivity extends AppCompatActivity {

    private static final String TAG = "FeedGreetboxActivity";
    private static final String URL_GET_GREETBOX_POST = "http://weddingsocial.org/beer/getGreetboxList.php";
    private static final String URL_DELETE_GREETBOX = "http://weddingsocial.org/beer/deleteGreetbox.php";//deleteGreetbox
    private static final String PLEASE_WAIT_MESSAGE = "Loading....";
    OkHttpClient okHttpClient = new OkHttpClient();
    String state, sql, greetbox, key_id, user_id;
    String StatusCode, SQL;
    int count = 0;
    CoordinatorLayout coordinatorLayout;
    String isNull = "NO";

    private RecyclerView recyclerView;
    private ContentAdapter adapter;
    private List<FeedDataHelper> feedsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_greetbox);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title", null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("คำอวยพรงาน" + "  " + TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        key_id = getIntent().getExtras().getString("key_id");
        user_id = getIntent().getExtras().getString("user_id");

        Log.d(TAG, "key_id " + key_id);
        Log.d(TAG, "user_id " + user_id);

        recyclerView = (RecyclerView) findViewById(R.id.greetbox_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GetGreetboxDetail getGreetboxDetail = new GetGreetboxDetail(this);
        getGreetboxDetail.execute(key_id, user_id);
    }


    public void onBackPressed() {
        Intent it = new Intent(FeedGreetboxActivity.this, Greet_box.class);
        it.putExtra("key_id",key_id);
        it.putExtra("user_id", user_id);
        startActivity(it);
        finish();
    }

    public class ViewHolder extends RecyclerView.ViewHolder { // ผูกกับส่วนไหนของการ์ดวิว
        public ImageView imageToUpload_greetlist;
        public TextView greet_text;
        public TextView greet_username;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list_greetboxlist, parent, false));
            imageToUpload_greetlist = (ImageView) itemView.findViewById(R.id.imageToUpload_greetlist);
            greet_text = (TextView) itemView.findViewById(R.id.greet_text);
            greet_username = (TextView) itemView.findViewById(R.id.greet_username);
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.CustomViewHolder> {

        private List<FeedDataHelper> feedDataHelperList;
        private Context mContext;

        public ContentAdapter(Context context, List<FeedDataHelper> feedDataHelperList) {
            this.feedDataHelperList = feedDataHelperList;
            this.mContext = context;
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentAdapter.CustomViewHolder holder = (ContentAdapter.CustomViewHolder) view.getTag();
                int position = holder.getAdapterPosition();

                FeedDataHelper feedDataHelper = feedDataHelperList.get(position);
                Bundle b = new Bundle();
                b.putString("user_id", feedDataHelper.getUser_id());
                b.putString("key_id", feedDataHelper.getKey_id());

                SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Title", feedDataHelper.getTitle());
                editor.commit();

                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class).putExtras(b));
            }
        };

        @Override
        public ContentAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //แก้ปัญหาเรื่องความกว้างของ Card
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_greetboxlist, viewGroup, false);

            ContentAdapter.CustomViewHolder viewHolder = new ContentAdapter.CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ContentAdapter.CustomViewHolder customViewHolder, int i) {
            //เป็นการผูกข้อมูลรวมกันจากคลาส feedDataHelper
            //Handle click event on both title and image click
            customViewHolder.greet_text.setOnClickListener(clickListener);
            customViewHolder.greet_username.setOnClickListener(clickListener);
            customViewHolder.imageToUpload_greetlist.setOnClickListener(clickListener);

            customViewHolder.greet_text.setTag(customViewHolder);
            customViewHolder.greet_username.setTag(customViewHolder);
            customViewHolder.imageToUpload_greetlist.setTag(customViewHolder);

            final FeedDataHelper feedDataHelper = feedDataHelperList.get(i);

            //Download image using picasso library
            Picasso.with(mContext).load(feedDataHelper.getThumbnail())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageToUpload_greetlist);
            Log.d("Feed", feedDataHelper.getThumbnail());

            //Setting text view title
            customViewHolder.greet_username.setText(Html.fromHtml(feedDataHelper.getFirstname() + " " + feedDataHelper.getLastname()));
            customViewHolder.greet_text.setText(feedDataHelper.getGreetbox_post());

            customViewHolder.delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(FeedGreetboxActivity.this);
                    builder.setMessage("คุณต้องการลบข้อความอวยพรใช่หรือไม่?");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setCancelable(true);
                    builder.setPositiveButton("ลบข้อความ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DeleteGreetbox deleteGreetbox = new DeleteGreetbox();
                            deleteGreetbox.execute(feedDataHelper.getGreetbox_id(), user_id, key_id); // ส่งไปที่เมธอร์ด DeleteGreetbox
                            Log.d(TAG, "SS"+feedDataHelper.getGreetbox_id());
                            Log.d(TAG, "SS"+key_id);
                            Log.d(TAG, "SS"+user_id);
                        }
                    });
                    builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != feedDataHelperList ? feedDataHelperList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imageToUpload_greetlist;
            protected TextView greet_text;
            protected TextView greet_username;
            protected ImageButton delete_button;

            public CustomViewHolder(View view) {
                super(view);
                this.imageToUpload_greetlist = (ImageView) view.findViewById(R.id.imageToUpload_greetlist);
                this.greet_text = (TextView) view.findViewById(R.id.greet_text);
                this.greet_username = (TextView) view.findViewById(R.id.greet_username);
                this.delete_button = (ImageButton) view.findViewById(R.id.imageButton3);
            }
        }
    }

    public class GetGreetboxDetail extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;
        Context context;

        public GetGreetboxDetail(Context context) {
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
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute() method running");
//            Log.d(TAG, sql);

            adapter = new ContentAdapter(recyclerView.getContext(), feedsList);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            progressDialog.dismiss();

            if(isNull == "YES"){ // If cannot get any data from JSON. It will go back to Post_box activity
                Intent();
            }

        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("key_id", params[0]);
            builder.add("user_id", params[1]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url(URL_GET_GREETBOX_POST).post(body).build();
            Response response = null;
            try {
                try {
                    response = okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String result = response.body().string();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray;

                try {
                    jsonArray = jsonObject.getJSONArray("greetbox");

                    try {
                        jsonObject = new JSONObject(result);
                        state = jsonObject.getString("state");
                        sql = jsonObject.getString("sql");
                        count = jsonObject.getInt("count");
                        greetbox = jsonObject.getString("greetbox");
                        System.out.println(state);
                        System.out.print("sql" + sql);
                        System.out.print("greetbox" + greetbox);
                        System.out.print("kkkkkkkkkkkk" + user_id);
                        System.out.print("oooooooooooo" + count);


                        if (count >= 1) {
                            feedsList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                FeedDataHelper item = new FeedDataHelper();
                                item.setFirstname(object.optString("Firstname"));
                                item.setLastname(object.optString("Lastname"));
                                item.setThumbnail(object.optString("Picture"));
                                item.setGreetbox_post(object.getString("greetbox_post"));
                                item.setGreetbox_id(object.getString("greetbox_id"));
                                item.setUser_id(object.getString("user_id"));
                                feedsList.add(item);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Can not convert json");
                    isNull = "YES";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DeleteGreetbox extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute() method running" + SQL);
            if(StatusCode.equals("OK")){
                Toast.makeText(getApplicationContext(), ("ลบข้อความอวยพรสำเร็จ"),Toast.LENGTH_SHORT).show();
                Intent it = new Intent(FeedGreetboxActivity.this, FeedGreetboxActivity.class);
                it.putExtra("key_id",key_id);
                it.putExtra("user_id", user_id);
                startActivity(it);
            }
            else if (StatusCode.equals("Denied")){
                Toast.makeText(getApplicationContext(), ("ไม่สามารถลบข้อความของแขกท่านอื่นได้"),Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("greetbox_id", params[0]);
            builder.add("user_id", params[1]);
            builder.add("key_id", params[2]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url(URL_DELETE_GREETBOX).post(body).build();
            Response response;
            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(result);
                    StatusCode = jsonObject.getString("state");
                    System.out.print("sssssssss"+StatusCode);
                    SQL = jsonObject.getString("sql");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void Intent(){
        Intent it = new Intent(FeedGreetboxActivity.this, Greet_box.class);
        it.putExtra("key_id",key_id);
        it.putExtra("user_id", user_id);
        it.putExtra("goback", "yes");
        startActivity(it);
    }
}
