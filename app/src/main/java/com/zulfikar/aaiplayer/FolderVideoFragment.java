package com.zulfikar.aaiplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FolderVideoFragment extends Fragment {

    RecyclerView recyclerView;
    VideoFolderAdapter videoFolderAdapter;
    String myFolderName;
    ArrayList<VideoFiles> videoFilesArrayList = new ArrayList<>();

    private static boolean loaded;
    private static FolderVideoFragment me;
    private int paddingTop, saveP, saveQ;

    View titleBar, bottomNavBar;

    public FolderVideoFragment() {
        this.myFolderName = FolderAdapter.folderTitle;
        loaded = true;
        me = this;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void requestUnload() {
        loaded = false;
        me = null;
    }

    public static FolderVideoFragment requestLoad() {
        return new FolderVideoFragment();
    }

    public static FolderVideoFragment getInstance() {
        if (me == null) me = new FolderVideoFragment();
        return me;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_video, container, false);

        recyclerView = view.findViewById(R.id.FolderVideoRV);

        recyclerView.setPadding(0, paddingTop, 0, 0);

//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//            float y;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (titleBar == null || bottomNavBar == null) return false;
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    int r = 25;
//                    int p = 30;
//                    int q = 30;
//                    if (event.getRawY() - y < 0) {
//                        if (recyclerView.getPaddingTop() > 0) recyclerView.setPadding(0, recyclerView.getPaddingTop() - r, 0, 0);
//                        if (titleBar.getY() + titleBar.getHeight() > 0) { titleBar.setY(titleBar.getY() - p); saveP--; }
//                        if (bottomNavBar.getY() < recyclerView.getBottom()) { bottomNavBar.setY(bottomNavBar.getY() + q); saveQ--; }
//                        Log.e("TAG", "onTouch+: " + (bottomNavBar.getY()) + " <<>> " + recyclerView.getBottom());
//                    } else if (event.getRawY() - y > 0) {
//                        if (recyclerView.getPaddingTop() < 250) recyclerView.setPadding(0, recyclerView.getPaddingTop() + r, 0, 0);
//                        if (saveP < 0) { titleBar.setY(titleBar.getY() + p); saveP++; }
//                        if (saveQ < 0) { bottomNavBar.setY(bottomNavBar.getY() - q); saveQ++; }
//                        Log.e("TAG", "onTouch-: " + (bottomNavBar.getY()) + " <<>> " + recyclerView.getBottom());
//                    }
//                    y = event.getRawY();
//                }
//                return false;
//            }
//        });

        if (myFolderName != null) {
//            videoFilesArrayList = getAllVideos(requireContext(), myFolderName);
            videoFilesArrayList = getVideos(myFolderName);
        }
        if (videoFilesArrayList.size() > 0) {
            videoFolderAdapter = new VideoFolderAdapter(getActivity(), videoFilesArrayList);
            recyclerView.setAdapter(videoFolderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }
        return view;
    }

    public ArrayList<VideoFiles> getVideos(String folderName) {
        ArrayList<VideoFiles> videoFiles = new ArrayList<>();

        for (VideoFiles videoFile : MainActivity.videoFiles) if (videoFile.getFolderName().equals(folderName)) videoFiles.add(videoFile);

        return videoFiles;
    }

    public FolderVideoFragment setBars(View titleBar, View bottomNavBar) {
        this.titleBar = titleBar;
        this.bottomNavBar = bottomNavBar;
        return this;
    }

    public FolderVideoFragment setInitialPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }
}