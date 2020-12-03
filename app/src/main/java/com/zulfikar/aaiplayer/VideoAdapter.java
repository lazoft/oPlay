package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<VideoFiles> videoFiles;
    View view;

    public VideoAdapter(Context mContext, ArrayList<VideoFiles> videoFiles) {
        this.mContext = mContext;
        this.videoFiles = videoFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileName.setText(videoFiles.get(position).getTitle());
        holder.videoDuration.setText(getDuration(holder, position));
        Glide.with(mContext).load(new File(videoFiles.get(position).getPath())).into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("sender", "Video");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    private String getDuration(@NonNull MyViewHolder holder, int position) {
        long duration = Long.parseLong(videoFiles.get(position).getDuration()) / 1000;
        long hour = duration / 3600;
        long minute = duration % 3600 / 60;
        long seconds = duration % 3600 % 60;
        return String.format("%02d:%02d:%02d", hour, minute, seconds);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menuMore;
        TextView fileName, videoDuration;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            menuMore = itemView.findViewById(R.id.menu_more);
            fileName = itemView.findViewById(R.id.video_file_name);
            videoDuration = itemView.findViewById(R.id.video_duration);
        }
    }
}
