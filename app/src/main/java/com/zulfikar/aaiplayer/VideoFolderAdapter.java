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

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<VideoFiles> folderVideoFiles;
    View view;

    public VideoFolderAdapter(Context mContext, ArrayList<VideoFiles> folderVideoFiles) {
        this.mContext = mContext;
        VideoFolderAdapter.folderVideoFiles = folderVideoFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileName.setText(folderVideoFiles.get(position).getTitle());
        holder.videoDuration.setText(getDuration(holder, position));
        Glide.with(mContext).load(new File(folderVideoFiles.get(position).getPath())).into(holder.thumbnail);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PlayerActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("sender", "VideoFolder");
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return folderVideoFiles.size();
    }

    private String getDuration(@NonNull MyViewHolder holder, int position) {
        long duration = Long.parseLong(folderVideoFiles.get(position).getDuration()) / 1000;
        long hour = duration / 3600;
        long minute = duration % 3600 / 60;
        long seconds = duration % 3600 % 60;
        String durationFormatted = String.format(Locale.US, "%02d:%02d:%02d", hour, minute, seconds);
        if (hour == 0) durationFormatted = String.format(Locale.ENGLISH, "%02d:%02d", minute, seconds);
        return durationFormatted;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menuMore;
        TextView fileName, videoDuration;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnailImage);
            menuMore = itemView.findViewById(R.id.menu_more);
            fileName = itemView.findViewById(R.id.video_file_name);
            videoDuration = itemView.findViewById(R.id.thumbnailDuration);
        }
    }
}
