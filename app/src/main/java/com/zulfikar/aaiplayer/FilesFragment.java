package com.zulfikar.aaiplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

public class FilesFragment extends Fragment implements Serializable {

    transient RecyclerView recyclerView;
    transient View view;
    transient VideoAdapter videoAdapter;

    private static boolean loaded;
    private static FilesFragment me;

    public FilesFragment() {
        me = this;
        loaded = true;
    }

    public static void reload() {
        requestLoad();
    }

    public static void requestLoad() {
        me = new FilesFragment();
    }

    public static void requestUnload() {
        me = null;
        loaded = false;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static Fragment getInstance() {
        if (me == null) requestLoad();
        return me;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_files, container, false);
        recyclerView = view.findViewById(R.id.filesRV);
        if (MainActivity.videoFiles != null && MainActivity.videoFiles.size() > 0) {
            videoAdapter = new VideoAdapter(getContext(), MainActivity.videoFiles);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}