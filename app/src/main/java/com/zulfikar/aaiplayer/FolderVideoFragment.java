package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FolderVideoFragment extends Fragment {

    RecyclerView recyclerView;
    VideoFolderAdapter videoFolderAdapter;
    String myFolderName;
    ArrayList<VideoFiles> videoFilesArrayList = new ArrayList<>();

    static boolean loaded;
    static FolderVideoFragment me;

    public FolderVideoFragment() {
        this.myFolderName = FolderAdapter.folderTitle;
        loaded = true;
        me = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_video, container, false);

        recyclerView = view.findViewById(R.id.FolderVideoRV);
        if (myFolderName != null) {
            videoFilesArrayList = getAllVideos(getContext(), myFolderName);
        }
        if (videoFilesArrayList.size() > 0) {
            videoFolderAdapter = new VideoFolderAdapter(getActivity(), videoFilesArrayList);
            recyclerView.setAdapter(videoFolderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }
        return view;
    }

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
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration);

                tempVideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return  tempVideoFiles;
    }


}