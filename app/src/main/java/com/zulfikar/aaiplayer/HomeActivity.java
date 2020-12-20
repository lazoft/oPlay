package com.zulfikar.aaiplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.zulfikar.aaiplayer.R.anim;

public class HomeActivity extends AppCompatActivity implements ActivityUtility {

    private static final String TAG = "Home Activity Debug";
    private final Handler homeActivityHolder = new Handler();

    private int currentThemeId, selectedItem;
    private CardView titleBar, bottomNavBar;
    private FrameLayout mainFragment;
    private ImageView btnDashboard, btnVideos, btnPlaylist, btnFolder, imgUser;
    private TextView txtLabel;

    @Override
    protected void onRestart() {
        super.onRestart();
        if (currentThemeId != Theme.currentThemeId) {
            new Thread(() -> {
                Snackbar.make(HomeActivity.this.btnPlaylist, "Applying theme. Please wait...", Snackbar.LENGTH_SHORT).show();
                homeActivityHolder.postDelayed(() -> Theme.recreate(HomeActivity.this, HomeActivity.this), 300);
            }).start();
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedItem == 3 && FolderVideoFragment.isLoaded()) {
            FolderVideoFragment.requestUnload();
            txtLabel.setText("BROWSE");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(anim.from_left, anim.to_right, anim.from_left, anim.to_right);
            fragmentTransaction.replace(R.id.mainFragmentAH, FolderFragment.getInstance());
            fragmentTransaction.commit();
            FolderFragment.getInstance().setBars(titleBar, bottomNavBar).setInitialPaddingTop(250);;
            selectedItem = 3;
        } else {
            finish();
            overridePendingTransition(anim.from_down, anim.to_up);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentThemeId = Theme.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnDashboard = findViewById(R.id.btnDashboard);
        btnVideos = findViewById(R.id.btnVideos);
        btnPlaylist = findViewById(R.id.btnPlaylist);
        btnFolder = findViewById(R.id.btnFolder);
        txtLabel = findViewById(R.id.txtLabelAH);
        imgUser = findViewById(R.id.imgUserAH);

        mainFragment = findViewById(R.id.mainFragmentAH);
        titleBar = findViewById(R.id.titleBarAH);
        bottomNavBar = findViewById(R.id.bottomNavBarAH);

        btnDashboard.setOnClickListener(v -> {
            itemCheck(btnDashboard.getId());
            txtLabel.setText("DASHBOARD");
            DashboardFragment dashboardFragment = new DashboardFragment();
            dashboardFragment.setBars(titleBar, bottomNavBar).setInitialPaddingTop(250);;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(anim.from_left, anim.to_right, anim.from_left, anim.to_right);
            fragmentTransaction.replace(R.id.mainFragmentAH, dashboardFragment);
            fragmentTransaction.commit();
            selectedItem = 0;
        });

        btnVideos.setOnClickListener(v -> {
            itemCheck(btnVideos.getId());
            txtLabel.setText("VIDEOS");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (selectedItem < 1) fragmentTransaction.setCustomAnimations(anim.from_right, anim.to_left, anim.from_right, anim.to_left);
            else if (selectedItem > 1) fragmentTransaction.setCustomAnimations(anim.from_left, anim.to_right, anim.from_left, anim.to_right);
            fragmentTransaction.replace(R.id.mainFragmentAH, FilesFragment.getInstance());
            fragmentTransaction.commit();
            FilesFragment.getInstance().setBars(titleBar, bottomNavBar).setInitialPaddingTop(250);
            selectedItem = 1;
        });

        btnPlaylist.setOnClickListener(v -> {
            itemCheck(btnPlaylist.getId());
            txtLabel.setText("PLAYLIST");
            PlaylistFragment playlistFragment = new PlaylistFragment();
            playlistFragment.setBars(titleBar, bottomNavBar).setInitialPaddingTop(250);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (selectedItem < 2) fragmentTransaction.setCustomAnimations(anim.from_right, anim.to_left, anim.from_right, anim.to_left);
            else if (selectedItem > 2) fragmentTransaction.setCustomAnimations(anim.from_left, anim.to_right, anim.from_left, anim.to_right);
            fragmentTransaction.replace(R.id.mainFragmentAH, playlistFragment);
            fragmentTransaction.commit();
            selectedItem = 2;
        });

        btnFolder.setOnClickListener(v -> {
            itemCheck(btnFolder.getId());
            txtLabel.setText("BROWSE");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (selectedItem < 3) fragmentTransaction.setCustomAnimations(anim.from_right, anim.to_left, anim.from_right, anim.to_left);
            else if (selectedItem == 3) fragmentTransaction.setCustomAnimations(anim.from_left, anim.to_right, anim.from_left, anim.to_right);
            fragmentTransaction.replace(R.id.mainFragmentAH, FolderFragment.getInstance());
            fragmentTransaction.commit();
            FolderFragment.getInstance().setBars(titleBar, bottomNavBar).setInitialPaddingTop(250);;
            selectedItem = 3;
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userSettingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                userSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(userSettingsIntent);
                overridePendingTransition(anim.from_right, anim.to_left);
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onLoad();
    }

    private void onLoad() {
        itemCheck(btnDashboard.getId());
        txtLabel.setText("DASHBOARD");
        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setBars(titleBar, bottomNavBar).setInitialPaddingTop(300);;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentAH, dashboardFragment);
        fragmentTransaction.commit();
    }

    private void itemCheck(int itemId) {
        TypedValue attrItemUnchecked = new TypedValue();
        TypedValue attrItemChecked = new TypedValue();
        TypedValue attrBottomNavBackground = new TypedValue();
        getTheme().resolveAttribute(R.attr.bottomNavItemUnchecked, attrItemUnchecked, true);
        getTheme().resolveAttribute(R.attr.bottomNavItemChecked, attrItemChecked, true);
        getTheme().resolveAttribute(R.attr.bottomNavBackground, attrBottomNavBackground, true);
        int itemUncheckedColor = attrItemUnchecked.data;
        int itemCheckedColor = attrItemChecked.data;
        int bottomNavBackgroundColor = attrBottomNavBackground.data;
        btnDashboard.setBackgroundColor(bottomNavBackgroundColor);
        btnVideos.setBackgroundColor(bottomNavBackgroundColor);
        btnPlaylist.setBackgroundColor(bottomNavBackgroundColor);
        btnFolder.setBackgroundColor(bottomNavBackgroundColor);

//        btnDashboard.setColorFilter(itemCheckedColor);
//        btnVideos.setColorFilter(itemCheckedColor);
//        btnPlaylist.setColorFilter(itemCheckedColor);
//        btnFolder.setColorFilter(itemCheckedColor);
        if (itemId == R.id.btnDashboard) {
            btnDashboard.setBackgroundColor(itemCheckedColor);
//            btnDashboard.setColorFilter(itemUncheckedColor);
        } else if (itemId == R.id.btnVideos) {
            btnVideos.setBackgroundColor(itemCheckedColor);
//            btnVideos.setColorFilter(itemUncheckedColor);
        } else if (itemId == R.id.btnPlaylist) {
            btnPlaylist.setBackgroundColor(itemCheckedColor);
//            btnPlaylist.setColorFilter(itemUncheckedColor);
        } else if (itemId == R.id.btnFolder) {
            btnFolder.setBackgroundColor(itemCheckedColor);
//            btnFolder.setColorFilter(itemUncheckedColor);
        }
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        onSaveInstanceState(bundle);
    }
}