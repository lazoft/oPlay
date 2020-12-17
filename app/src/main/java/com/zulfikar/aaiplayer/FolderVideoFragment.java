package com.zulfikar.aaiplayer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_video, container, false);

        recyclerView = view.findViewById(R.id.FolderVideoRV);
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
}