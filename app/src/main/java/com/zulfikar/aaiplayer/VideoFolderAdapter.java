package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.MyViewHolder> {
    private final Context mContext;
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
        holder.videoDuration.setText(getDuration(position));
        Glide.with(mContext).load(new File(folderVideoFiles.get(position).getPath())).into(holder.thumbnail);
        holder.itemView.setOnClickListener(v -> startPlayerActivity(position));
        holder.menuMore.setOnClickListener(v -> {});
    }

    public void startPlayerActivity(int position) {
        Intent intent = new Intent(mContext, PlayerActivity.class);
        intent.putExtra("title", folderVideoFiles.get(position).getTitle());
//        intent.putExtra("videoDuration", folderVideoFiles.get(position).getDuration());
        intent.putExtra("position", position);
        intent.putExtra("sender", "VideoFolder");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return folderVideoFiles.size();
    }

    private String getDuration(int position) {
        long duration = Long.parseLong(folderVideoFiles.get(position).getDuration());
        long hour = TimeUnit.MILLISECONDS.toHours(duration);
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        if (hour == 0) return String.format(Locale.ENGLISH, "%02d:%02d", minute, seconds);
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hour, minute, seconds);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
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
