package com.example.android.weddingsocial;
//เป็นคลาสที่ใช้ดึงข้อมูลใส่ในการ์ดวิว

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedNamelistRecyclerAdapter extends RecyclerView.Adapter<FeedNamelistRecyclerAdapter.CustomViewHolder> {
    private List<FeedNamelistItem> feedNamelistItemList;
    private Context mContext;

    public FeedNamelistRecyclerAdapter(Context context, List<FeedNamelistItem> feedNamelistItemList) {
        this.feedNamelistItemList = feedNamelistItemList;
        this.mContext = context;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomViewHolder holder = (CustomViewHolder) view.getTag();
            int position = holder.getPosition();

            FeedNamelistItem feedNamelistItem = feedNamelistItemList.get(position);
        }
    };

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_namlist, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        customViewHolder.namelist.setOnClickListener(clickListener);
        customViewHolder.imageToUpload_namelist.setOnClickListener(clickListener);

        customViewHolder.namelist.setTag(customViewHolder);
        customViewHolder.imageToUpload_namelist.setTag(customViewHolder);

        FeedNamelistItem feedNamelistItem = feedNamelistItemList.get(i);

        Picasso.with(mContext).load(feedNamelistItem.getThumbnail())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(customViewHolder.imageToUpload_namelist);

        customViewHolder.namelist.setText(Html.fromHtml(feedNamelistItem.getFirstname() + " " + feedNamelistItem.getLastname()));
    }

    @Override
    public int getItemCount() {
        return (null != feedNamelistItemList ? feedNamelistItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageToUpload_namelist;
        protected TextView namelist;

        public CustomViewHolder(View view) {
            super(view);
            this.imageToUpload_namelist = (ImageView) view.findViewById(R.id.imageToUpload_namelist);
            this.namelist = (TextView) view.findViewById(R.id.namelist);
        }
    }
}
