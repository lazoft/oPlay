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

public class FilesFragment extends Fragment implements Serializable {

    transient RecyclerView recyclerView;
    transient View view;
    transient VideoAdapter videoAdapter;

    private static boolean loaded;
    private static FilesFragment me;

    private View titleBar, bottomNavBar;

    private int paddingTop, saveP, saveQ;

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

    public static FilesFragment getInstance() {
        if (me == null) requestLoad();
        return me;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_files, container, false);
        recyclerView = view.findViewById(R.id.filesRV);
        if (MainActivity.videoFiles != null && MainActivity.videoFiles.size() > 0) {
            videoAdapter = new VideoAdapter(getContext(), MainActivity.videoFiles);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        } else if (HomeActivity.videoFiles != null && HomeActivity.videoFiles.size() > 0) {
            videoAdapter = new VideoAdapter(getContext(), HomeActivity.videoFiles);
            recyclerView.setAdapter(videoAdapter);
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

//        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                try {
//                    int y = 30;
//                    int p = 30;
//                    int q = 30;
//                if (oldScrollY < 0) {
//                    if (recyclerView.getPaddingTop() > 0) recyclerView.setPadding(0, recyclerView.getPaddingTop() - y, 0, 0);
//                    if (titleBar.getY() + titleBar.getHeight() > 0) { titleBar.setY(titleBar.getY() - p); saveY -= p; }
//                    if (bottomNavBar.getY() <  300) bottomNavBar.setY(bottomNavBar.getY() + q);
//                    Log.e("TAG", "onScrollChange+: " + bottomNavBar.getY() );
//                } else {
//                    if (saveY < 0) { titleBar.setY(titleBar.getY() + p); saveY += p; }
//                }
//                } catch (Exception e) {e.printStackTrace();}
//                Log.e("TAG", "onScrollChange-: " + titleBar.getY() + " <<< >>> " + (titleBar.getY() + titleBar.getHeight()));
//            }
//        });

        return view;
    }

    public FilesFragment setBars(View titleBar, View bottomNavBar) {
        this.titleBar = titleBar;
        this.bottomNavBar = bottomNavBar;
        return this;
    }

    public FilesFragment setInitialPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }
}