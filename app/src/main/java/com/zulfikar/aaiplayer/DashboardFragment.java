package com.zulfikar.aaiplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class DashboardFragment extends Fragment {

    ScrollView scrollView;

    View titleBar, bottomNavBar;
    float titleBarPosY = -1;
    float bottomNavBarPosY = -1;

    int paddingTop;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        scrollView = view.findViewById(R.id.scrollViewFD);

        scrollView.setPadding(0, paddingTop, 0, 0);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            float y;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    y = event.getRawY();
                    if (bottomNavBarPosY < 0) bottomNavBarPosY = bottomNavBar.getY();
                    if (titleBarPosY < 0) titleBarPosY = titleBar.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int r = 30;
                    int p = 30;
                    int q = 30;
                    if (event.getRawY() - y < 0) {
                        if (scrollView.getPaddingTop() > 0) scrollView.setPadding(0, scrollView.getPaddingTop() - r, 0, 0);
                        if (titleBar.getY() > titleBarPosY - 300) titleBar.setY(titleBar.getY() - p);
                        if (bottomNavBar.getY() < bottomNavBarPosY + 300) bottomNavBar.setY(bottomNavBar.getY() + q);
                    } else if (event.getRawY() - y > 0) {
                        if (scrollView.getPaddingTop() < 300) scrollView.setPadding(0, scrollView.getPaddingTop() + r, 0, 0);
                        if (titleBar.getY() < titleBarPosY) titleBar.setY(titleBar.getY() + p);
                        if (bottomNavBar.getY() > bottomNavBarPosY) bottomNavBar.setY(bottomNavBar.getY() - q);
                    }
                }
                Log.e("TAG", "onTouch: " + bottomNavBarPosY);
                return false;
            }
        });

//        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                try {
//                    int y = 15;
//                    int p = 23;
//                    int q = 30;
//                    if (oldScrollY - scrollY < 0) {
//                        if (scrollView.getPaddingTop() > 0) {
//                            scrollView.setPadding(0, scrollView.getPaddingTop() - y, 0, 0);
//                            titleBar.setY(scrollView.getPaddingTop() - 266);
//                        }
//                        if (bottomNavBar.getY() < 2190) bottomNavBar.setY(bottomNavBar.getY() + q);
////                        titleBar.setVisibility(View.INVISIBLE);
//                        Log.e("TAG", "onScrollChange+: " + String.format("%d %d %d %d", scrollX, scrollY, oldScrollX, oldScrollY)  + " -- " + titleBar.getY() + " <> " + titleBar.getHeight() + " ## " + scrollView.getPaddingTop());
//                    } else if (oldScrollY - scrollY > 0) {
//                        Log.e("TAG", "onScrollChange+: " + String.format("%d %d %d %d", scrollX, scrollY, oldScrollX, oldScrollY)  + " -- " + titleBar.getY() + " <> " + titleBar.getHeight() + " ## " + scrollView.getPaddingTop());
//                        if (scrollView.getPaddingTop() < 300) {
//                            scrollView.setPadding(0, scrollView.getPaddingTop() + y, 0, 0);
////                            titleBar.setY(scrollY);
//                            titleBar.setY(scrollView.getPaddingTop() - 266);
//                        }
//                        if (bottomNavBar.getY() > 1784) bottomNavBar.setY(bottomNavBar.getY() - q);
////                        bottomNavBar.setVisibility(View.VISIBLE);
//                    }
//                } catch (Exception e) {e.printStackTrace();}
//            }
//        });

        return view;
    }

    public DashboardFragment setBars(View titleBar, View bottomNavBar) {
        this.titleBar = titleBar;
        this.bottomNavBar = bottomNavBar;
        return this;
    }

    public DashboardFragment setInitialPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }
}