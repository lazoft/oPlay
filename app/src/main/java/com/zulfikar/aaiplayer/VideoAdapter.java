package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.collection.LruCache;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private final Context mContext;
    static ArrayList<VideoFiles> videoFiles;
    View view;
    /////////private static View snap;
    private static Bitmap img;
    private static final int quality = 100;

    public VideoAdapter(Context mContext, ArrayList<VideoFiles> videoFiles) {
        this.mContext = mContext;
        VideoAdapter.videoFiles = videoFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
//        img = getScreenshot();
        //getScreenshot(view, "oloshanp_");
        //getScreenshotFromRecyclerView(view);
        ////////img = getScreenshotFromRecyclerView((RecyclerView)LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false));
        /////////snap = view;
        ////////snap = getWindow().getDecorView().getRootView();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileName.setText(videoFiles.get(position).getTitle());
        holder.videoDuration.setText(getDuration(position));
        Glide.with(mContext).load(new File(videoFiles.get(position).getPath())).into(holder.thumbnail);
        holder.menuMore.setOnClickListener(v -> {});
        holder.itemView.setOnClickListener(v -> startPlayerActivity(position));
    }

    public void startPlayerActivity(int position) {
        Intent intent = new Intent(mContext, PlayerActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("sender", "Video");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    private String getDuration(int position) {
        long duration = Long.parseLong(videoFiles.get(position).getDuration());
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
//            img = getScreenshot(itemView);
        }
    }

//    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
//        RecyclerView.Adapter adapter = view.getAdapter();
//        Bitmap bigBitmap = null;
//        if (adapter != null) {
//            int size = adapter.getItemCount();
//            int height = 0;
//            Paint paint = new Paint();
//            int iHeight = 0;
//            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//
//            // Use 1/8th of the available memory for this memory cache.
//            final int cacheSize = maxMemory / 8;
//            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
//            for (int i = 0; i < size; i++) {
//                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
//                adapter.onBindViewHolder(holder, i);
//                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
//                holder.itemView.setDrawingCacheEnabled(true);
//                holder.itemView.buildDrawingCache();
//                Bitmap drawingCache = holder.itemView.getDrawingCache();
//                if (drawingCache != null) {
//
//                    bitmaCache.put(String.valueOf(i), drawingCache);
//                }
////                holder.itemView.setDrawingCacheEnabled(false);
////                holder.itemView.destroyDrawingCache();
//                height += holder.itemView.getMeasuredHeight();
//            }
//
//            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
//            Canvas bigCanvas = new Canvas(bigBitmap);
//            bigCanvas.drawColor(Color.WHITE);
//
//            for (int i = 0; i < size; i++) {
//                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
//                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
//                iHeight += bitmap.getHeight();
//                bitmap.recycle();
//            }
//
//        }
//        return bigBitmap;
//    }
//
//    public static Bitmap getBitmap() {
//        return img;
//    }
//
//    public static Bitmap getScreenshot(View view){
//        View view = this.view;
//        Date date = new Date();
//        CharSequence time = android.text.format.DateFormat.format("yy:MM:dd_hh:mm:ss", date);
//        String dirPath = Environment.getExternalStorageDirectory().toString()+"/OloshPlayer";
//        File filedir = new File(dirPath);
//        if (!filedir.exists()) filedir.mkdir();
//        try {
//            String path = dirPath + "/" + filename + "-" + time + ".jpeg";
//            view.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
//            view.setDrawingCacheEnabled(false);

//            File imageFile = new File(path);

//            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG,quality, fileOutputStream);
//            fileOutputStream.flush();
//            fileOutputStream.close();
//            return bitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}
