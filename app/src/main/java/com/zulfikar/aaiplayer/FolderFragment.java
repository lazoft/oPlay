package com.zulfikar.aaiplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class FolderFragment extends Fragment implements Serializable {

    transient FolderAdapter folderAdapter;
    transient RecyclerView recyclerView;

    private static boolean loaded;
    @SuppressLint("StaticFieldLeak")
    private static FolderFragment me;
    private int paddingTop, paddingBottom, saveP, saveQ;

    View titleBar, bottomNavBar;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        recyclerView = view.findViewById(R.id.folderRV);
        if (MainActivity.folderList != null && MainActivity.folderList.size() > 0) {
            folderAdapter = new FolderAdapter(MainActivity.folderList, getActivity(), titleBar, bottomNavBar);
            recyclerView.setAdapter(folderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        } else if (HomeActivity.folderList != null && HomeActivity.folderList.size() > 0) {
            folderAdapter = new FolderAdapter(HomeActivity.folderList, getActivity(), titleBar, bottomNavBar);
            recyclerView.setAdapter(folderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }

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

        return view;
    }

    public FolderFragment setBars(View titleBar, View bottomNavBar) {
        this.titleBar = titleBar;
        this.bottomNavBar = bottomNavBar;
        return this;
    }

    public FolderFragment setInitialPaddingTop(int paddingTop, int paddingBottom) {
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        return this;
    }
}