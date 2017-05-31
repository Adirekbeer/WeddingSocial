package com.example.android.weddingsocial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class FeedEventActivity extends AppCompatActivity {

    private static final String TAG = "FeedEventActivity";
    private static final String URL_GET_EVENT_DETAILS = "http://weddingsocial.org/beer/getEventDetails.php";
    private static final String PLEASE_WAIT_MESSAGE = "Loading....";
    OkHttpClient okHttpClient = new OkHttpClient();
    String State, sql, event, user_id;
    int count = 0;

    private RecyclerView recyclerView;
    private ContentAdapter adapter;
    private List<FeedDataHelper> feedsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("งานที่ท่านเคยเข้าร่วม");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user_id = getIntent().getExtras().getString("user_id");

        recyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GetEventDetail getEventDetail = new GetEventDetail(this);
        getEventDetail.execute(user_id);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(FeedEventActivity.this, login.class);
        it.putExtra("user_id", user_id);
        startActivity(it);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // เป็นเมธอร์ทการทำงานของปุ่มเพิ่มคีย์หน้า FeedEventActivity
        switch (item.getItemId()) {
            case R.id.manu_event:
                Bundle b = new Bundle();
                b.putString("user_id", user_id);
                Intent intent = new Intent(this, Key.class).putExtras(b);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder { // ผูกกับส่วนไหนของการ์ดวิว
        public ImageView picture;
        public TextView name;
        public TextView description;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card_event, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            name = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);
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
                CustomViewHolder holder = (CustomViewHolder) view.getTag();
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
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //แก้ปัญหาเรื่องความกว้างของ Card
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_event, viewGroup, false);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
            //เป็นการผูกข้อมูลรวมกันจากคลาส feedDataHelper
            //Handle click event on both title and image click
            customViewHolder.textView_title.setOnClickListener(clickListener);
            customViewHolder.imageView.setOnClickListener(clickListener);

            customViewHolder.textView_title.setTag(customViewHolder);
            customViewHolder.imageView.setTag(customViewHolder);
//            customViewHolder.action_button.setTag(customViewHolder);

            FeedDataHelper feedDataHelper = feedDataHelperList.get(i);

            //Download image using picasso library
            Picasso.with(mContext).load(feedDataHelper.getBanner())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);

            customViewHolder.textView_card_text.setText(feedDataHelper.getDate());
            customViewHolder.textView_title.setText(feedDataHelper.getTitle());

        }

        @Override
        public int getItemCount() {
            return (null != feedDataHelperList ? feedDataHelperList.size() : 0); // เอาไว้นับค่าเริ่มต้น ถ้าไม่มีก็เท่ากับ 0
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imageView;
            protected TextView textView_title;
            protected TextView textView_card_text;

            public CustomViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.card_image);
                this.textView_title = (TextView) view.findViewById(R.id.card_title);
                this.textView_card_text = (TextView) view.findViewById(R.id.card_text);
            }
        }
    }

    public class GetEventDetail extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;
        Context context;

        public GetEventDetail(Context context) {
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
            Log.d(TAG, sql);

            adapter = new ContentAdapter(recyclerView.getContext(), feedsList);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("user_id", params[0]);
            RequestBody body = builder.build();
            Request request = new Request.Builder().url(URL_GET_EVENT_DETAILS).post(body).build();
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
                    jsonArray = jsonObject.getJSONArray("event");

                    try {

                        jsonObject = new JSONObject(result);
                        State = jsonObject.getString("state");
                        count = jsonObject.getInt("count");
                        sql = jsonObject.getString("sql");
                        event = jsonObject.getString("event");
                        System.out.println(State);
                        System.out.print("sql" + sql);
                        System.out.print("event" + event);

                        if (count >= 1) {
                            feedsList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                FeedDataHelper item = new FeedDataHelper();
                                item.setTitle(object.getString("title"));
                                item.setDate(object.getString("date"));
                                item.setBanner(object.getString("banner"));
                                item.setKey_id(object.getString("key_id"));
                                item.setUser_id(object.getString("user_id"));
                                feedsList.add(item);
                            }
                        } else {
                            System.out.println("ไม่เคยเข้าร่วมงาน");

                            Intent it = new Intent(FeedEventActivity.this, Key.class);
                            it.putExtra("user_id", user_id);
                            startActivity(it);
                            System.exit(0);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    Intent it = new Intent(FeedEventActivity.this, Key.class);
                    it.putExtra("user_id", user_id);
                    startActivity(it);
                    System.exit(0);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
