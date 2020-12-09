package com.zulfikar.aaiplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import static com.zulfikar.aaiplayer.MainActivity.videoFiles;

public class FilesFragment extends Fragment implements Serializable {

    transient RecyclerView recyclerView;
    transient View view;
    transient VideoAdapter videoAdapter;

    public FilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_files, container, false);
        recyclerView = view.findViewById(R.id.filesRV);
        if (videoFiles != null && videoFiles.size() > 0) {
            videoAdapter = new VideoAdapter(getContext(), videoFiles);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}