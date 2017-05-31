package com.example.android.weddingsocial;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FeedPostboxActivity extends AppCompatActivity{

    private static final String TAG = "FeedPostboxActivity";
    private static final String URL_GET_POSTBOX_POST = "http://weddingsocial.org/beer/getPostboxList.php";
    private static final String PLEASE_WAIT_MESSAGE = "Loading....";
    OkHttpClient okHttpClient = new OkHttpClient();
    String State, sql, postbox, key_id, user_id;
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
        setContentView(R.layout.activity_feed_postbox);

        SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String TitleApp = sp.getString("Title", null);
        System.out.println(TitleApp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("โพสต์ภายในงาน" + "  " + TitleApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        key_id = getIntent().getExtras().getString("key_id");
        user_id = getIntent().getExtras().getString("user_id");

        Log.d(TAG, "key_id " + key_id);
        Log.d(TAG, "user_id " + user_id);

        recyclerView = (RecyclerView) findViewById(R.id.postbox_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GetPostboxDetail GetPostboxDetail = new GetPostboxDetail(this);
        GetPostboxDetail.execute(key_id, user_id);
    }

    public void onBackPressed() {
        Intent it = new Intent(FeedPostboxActivity.this, Post_box.class);
        it.putExtra("key_id",key_id);
        it.putExtra("user_id", user_id);
        startActivity(it);
        finish();
    }

    public class ViewHolder extends RecyclerView.ViewHolder { // ผูกกับส่วนไหนของการ์ดวิว
        public ImageView imageToUpload_profile_postlist;
        public ImageView imageToUpload_postlist;
        public TextView post_text;
        public TextView post_username;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list_postboxlist, parent, false));
            imageToUpload_profile_postlist = (ImageView) itemView.findViewById(R.id.imageToUpload_profile_postlist);
            imageToUpload_postlist = (ImageView) itemView.findViewById(R.id.imageToUpload_postlist);
            post_text = (TextView) itemView.findViewById(R.id.post_text);
            post_username = (TextView) itemView.findViewById(R.id.post_username);
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_postboxlist, viewGroup, false);

           ContentAdapter.CustomViewHolder viewHolder = new ContentAdapter.CustomViewHolder(view);
            return viewHolder;
        }



        @Override
        public void onBindViewHolder(ContentAdapter.CustomViewHolder customViewHolder, int i) {
            //เป็นการผูกข้อมูลรวมกันจากคลาส feedDataHelper
            //Handle click event on both title and image click
            customViewHolder.post_text.setOnClickListener(clickListener);
            customViewHolder.post_username.setOnClickListener(clickListener);
            customViewHolder.imageToUpload_profile_postlist.setOnClickListener(clickListener);
            customViewHolder.imageToUpload_postlist.setOnClickListener(clickListener);


            customViewHolder.post_text.setTag(customViewHolder);
            customViewHolder.post_username.setTag(customViewHolder);
            customViewHolder.imageToUpload_profile_postlist.setTag(customViewHolder);
            customViewHolder.imageToUpload_postlist.setTag(customViewHolder);


            final FeedDataHelper feedDataHelper = feedDataHelperList.get(i);

            //Download image using picasso library
            Picasso.with(mContext).load(feedDataHelper.getThumbnail())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageToUpload_profile_postlist);
            Log.d("Feed", feedDataHelper.getThumbnail());

            Picasso.with(mContext).load(feedDataHelper.getThumbnail2())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageToUpload_postlist);
            Log.d("Feed", feedDataHelper.getThumbnail2());

            //Setting text view title
            customViewHolder.post_username.setText(Html.fromHtml(feedDataHelper.getFirstname() + " " + feedDataHelper.getLastname()));
            customViewHolder.post_text.setText(feedDataHelper.getPostbox_post());

            customViewHolder.delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(FeedPostboxActivity.this);
                    builder.setMessage("คุณต้องการลบโพสต์ใช่หรือไม่?");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setCancelable(true);
                    builder.setPositiveButton("ลบข้อมูล", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DeletePost deletePost = new DeletePost();
                            deletePost.execute(feedDataHelper.getPostbox_id(), user_id, key_id);
                            Log.d(TAG, "SS"+feedDataHelper.getPostbox_id());
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
            protected ImageView imageToUpload_profile_postlist;
            protected ImageView imageToUpload_postlist;
            protected TextView post_text;
            protected TextView post_username;
            protected ImageButton delete_button;

            public CustomViewHolder(View view) {
                super(view);
                this.imageToUpload_profile_postlist = (ImageView) view.findViewById(R.id.imageToUpload_profile_postlist);
                this.imageToUpload_postlist = (ImageView) view.findViewById(R.id.imageToUpload_postlist);
                this.post_text = (TextView) view.findViewById(R.id.post_text);
                this.post_username = (TextView) view.findViewById(R.id.post_username);
                this.delete_button = (ImageButton) view.findViewById(R.id.delete_post);
            }
        }
    }

    public class GetPostboxDetail extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;
        Context context;

        public GetPostboxDetail(Context context) {
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
            Request request = new Request.Builder().url(URL_GET_POSTBOX_POST).post(body).build();
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
                    jsonArray = jsonObject.getJSONArray("postbox");

                    try {
                        jsonObject = new JSONObject(result);
                        State = jsonObject.getString("state");
                        sql = jsonObject.getString("sql");
                        count = jsonObject.getInt("count");
                        postbox = jsonObject.getString("postbox");
                        System.out.println(State);
                        System.out.print("sql" + sql);



                        if (count >= 1) {
                            feedsList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                FeedDataHelper item = new FeedDataHelper();
                                item.setFirstname(object.optString("Firstname"));
                                item.setLastname(object.optString("Lastname"));
                                item.setThumbnail(object.optString("Picture_Profile"));
                                item.setThumbnail2(object.optString("Picture_Photo"));
                                item.setPostbox_post(object.getString("postbox_post"));
                                item.setPostbox_id(object.getString("postbox_id"));
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

    public class DeletePost extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute() method running" + SQL);
            if(StatusCode.equals("OK")){
                Toast.makeText(getApplicationContext(), ("Deleted"),Toast.LENGTH_SHORT).show();
                Intent it = new Intent(FeedPostboxActivity.this, FeedPostboxActivity.class);
                it.putExtra("key_id",key_id);
                it.putExtra("user_id", user_id);
                startActivity(it);
            }

        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("postbox_id", params[0]);
            builder.add("user_id", params[1]);
            builder.add("key_id", params[2]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url(URL_GET_POSTBOX_POST).post(body).build();
            Response response;
            try {
                response = okHttpClient.newCall(request).execute();
                String result = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    StatusCode = jsonObject.getString("state");
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
        Intent it = new Intent(FeedPostboxActivity.this, Post_box.class);
        it.putExtra("key_id",key_id);
        it.putExtra("user_id", user_id);
        it.putExtra("goback", "yes");
        startActivity(it);
    }
}











