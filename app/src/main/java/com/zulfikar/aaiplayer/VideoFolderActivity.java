package com.zulfikar.aaiplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;

public class VideoFolderActivity extends AppCompatActivity implements ActivityUtility {

    RecyclerView recyclerView;
    VideoFolderAdapter videoFolderAdapter;
    String myFolderName;
    ArrayList<VideoFiles> videoFilesArrayList = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Theme.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_folder);
        recyclerView = findViewById(R.id.FolderVideoRV);
        myFolderName = getIntent().getStringExtra("folderName");
        if (myFolderName != null) {
            videoFilesArrayList = getAllVideos(this, myFolderName);
        }
        if (videoFilesArrayList.size() > 0) {
            videoFolderAdapter = new VideoFolderAdapter(this, videoFilesArrayList);
            recyclerView.setAdapter(videoFolderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<VideoFiles> getAllVideos(Context context, String myFolderName) {
        ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DISPLAY_NAME};
        String selection = MediaStore.Video.Media.DATA + " LIKE?";
        String[] selectionArgs = new String[]{"%" + myFolderName + "%"};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String duration = cursor.getString(5);
                String fileName = cursor.getString(6);
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration, myFolderName);

                tempVideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return  tempVideoFiles;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Theme.recreate(this, this);
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        onSaveInstanceState(bundle);
    }
}