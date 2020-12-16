package com.zulfikar.aaiplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

public class FolderFragment extends Fragment implements Serializable {

    transient FolderAdapter folderAdapter;
    transient RecyclerView recyclerView;

    private static boolean loaded;
    private static FolderFragment me;

    public FolderFragment() {
        me = this;
        loaded = true;
    }

    public static FolderFragment requestLoad() {
        return new FolderFragment();
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static FolderFragment getInstance() {
        if (me == null) me = new FolderFragment();
        return me;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        recyclerView = view.findViewById(R.id.folderRV);
        if (MainActivity.folderList != null && MainActivity.folderList.size() > 0) {
            folderAdapter = new FolderAdapter(MainActivity.folderList, getActivity());
            recyclerView.setAdapter(folderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}