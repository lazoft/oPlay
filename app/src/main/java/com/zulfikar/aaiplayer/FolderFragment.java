package com.zulfikar.aaiplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.zulfikar.aaiplayer.MainActivity.folderList;
import static com.zulfikar.aaiplayer.MainActivity.videoFiles;

public class FolderFragment extends Fragment {

    FolderAdapter folderAdapter;
    RecyclerView recyclerView;

    static boolean loaded;
    static FolderFragment me;

    public FolderFragment() {
        me = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        recyclerView = view.findViewById(R.id.folderRV);
        if (folderList != null && videoFiles != null && folderList.size() > 0) {
            folderAdapter = new FolderAdapter(folderList, videoFiles, getActivity());
            recyclerView.setAdapter(folderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}