package com.example.lenovo.awstreams_task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Ezz on 26/03/2016.
 */
public class VideosRecyclerAdapter extends RecyclerView.Adapter<VideosRecyclerAdapter.CustomViewHolder> {

    private ArrayList<VideoItem> videosList;
    private Context mContext;

    public VideosRecyclerAdapter(Context context, ArrayList<VideoItem> videoItems) {
        this.videosList = videoItems;
        this.mContext = context;

    }


    @Override
    public VideosRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideosRecyclerAdapter.CustomViewHolder holder, int position) {
        VideoItem videoItem = videosList.get(position);
        if (videoItem != null) {
            holder.textView.setText(videoItem.getTitle());
            Glide.with(mContext)
                    .load(videoItem.getThumbnail())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title_tv);

        }
    }
}
