package com.zulfikar.aaiplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.zulfikar.aaiplayer.R.anim;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity implements ActivityUtility {

    private static final String TAG = "Home Activity Debug";
    private final Handler homeActivityHolder = new Handler();

    private int currentThemeId, selectedItem;
    private CardView titleBar, bottomNavBar;
    private FrameLayout mainFragment;
    private ImageView btnDashboard, btnVideos, btnPlaylist, btnFolder, imgUser;
    private TextView txtLabel;

    private static final int REQUEST_CODE_PERMISSION = 123;
//    public final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.tick);
//    public final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.tick);;
//    private static final String TAG = "MAIN_ACTIVITY_LOG";

    BottomNavigationView bottomNav;

    Snackbar videoLoadingSnackBar;

    Handler mainActivityHandler = new Handler();

    public static ArrayList<VideoFiles> videoFiles;
    public static ArrayList<String> folderList;

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
            FolderFragment.getInstance().setBars(titleBar, bottomNavBar).setInitialPaddingTop(240, 200);;
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
            dashboardFragment.setBars(titleBar, bottomNavBar).setInitialPaddingTop(240, 200);;
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
            FilesFragment.getInstance().setBars(titleBar, bottomNavBar).setInitialPaddingTop(240, 200);
            selectedItem = 1;
        });

        btnPlaylist.setOnClickListener(v -> {
            itemCheck(btnPlaylist.getId());
            txtLabel.setText("PLAYLIST");
            PlaylistFragment playlistFragment = new PlaylistFragment();
            playlistFragment.setBars(titleBar, bottomNavBar).setInitialPaddingTop(240, 200);
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
            FolderFragment.getInstance().setBars(titleBar, bottomNavBar).setInitialPaddingTop(240, 200);;
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

        permission(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onLoad();
    }

    private void permission(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            if (videoFiles == null) load();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (videoFiles == null) load();
            }
        }
    }

    private void load() {
        videoLoadingSnackBar = Snackbar.make(bottomNav, "Loading. Please wait...", BaseTransientBottomBar.LENGTH_INDEFINITE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoLoadingSnackBar.getView().getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        videoLoadingSnackBar.show();
        new Thread(() -> {
            android.util.Pair<ArrayList<VideoFiles>, ArrayList<String>> data = refreshLibraryAsync(HomeActivity.this);
            mainActivityHandler.post(() -> {
                videoLoadingSnackBar.dismiss();
                videoFiles = data.first;
                folderList = data.second;
                Collections.sort(folderList);
            });
        }).start();
    }

    public android.util.Pair<ArrayList<VideoFiles>, ArrayList<String>> refreshLibraryAsync(Context context) {
        ArrayList<VideoFiles> videoFiles = new ArrayList<>();
        ArrayList<String> folderList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        //TRYING TO FIX ERRORS FOR API 24
//        String [] projection = makeProjection(Build.VERSION.SDK_INT, Build.VERSION_CODES.Q);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION};
//        } else {
//            projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME};
//        }

//        String [] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION};

        String [] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (cursor != null) {
            while(cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String fileName = cursor.getString(5);
                String duration = cursor.getString(6);
                try { retriever.setDataSource(path); }
                catch (Exception e) { retriever = null; }
                if (retriever != null) duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                int index = subString.lastIndexOf("/");
                String folderName = subString.substring((index + 1));
                if (!folderList.contains(folderName)) {
                    folderList.add(folderName);
                }

                videoFiles.add(new VideoFiles(id, path, title, fileName, size, dateAdded, duration, folderName));
            }
            cursor.close();
        }

        return new Pair<>(videoFiles, folderList);
    }

    private void onLoad() {
        itemCheck(btnDashboard.getId());
        txtLabel.setText("DASHBOARD");
        DashboardFragment dashboardFragment = new DashboardFragment();
        dashboardFragment.setBars(titleBar, bottomNavBar).setInitialPaddingTop(240, 200);;
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