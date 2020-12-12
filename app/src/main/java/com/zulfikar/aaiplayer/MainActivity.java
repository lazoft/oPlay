package com.zulfikar.aaiplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 123;

    BottomNavigationView bottomNav;
    FolderFragment folderFragment;
    FilesFragment filesFragment;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int themeId = 0;

    static FragmentManager fragmentManager;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("folderFragment", folderFragment);
        outState.putSerializable("filesFragment", filesFragment);
    }

    static ArrayList<VideoFiles> videoFiles = new ArrayList<>();
    static ArrayList<String> folderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeId = ThemeActivity.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavViewAM);
        if (savedInstanceState != null) {
            folderFragment = (FolderFragment) savedInstanceState.getSerializable("folderFragment");
            filesFragment = (FilesFragment) savedInstanceState.getSerializable("filesFragment");
        }
        if (fragmentManager == null) fragmentManager = getSupportFragmentManager();
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.folderList) {
                FragmentTransaction folderFragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (FolderVideoFragment.loaded) {
                    folderFragmentTransaction.replace(R.id.mainFragment, FolderVideoFragment.me);
                } else {
                    if (folderFragment == null) folderFragment = new FolderFragment();
                    folderFragmentTransaction.replace(R.id.mainFragment, folderFragment);
                }
                folderFragmentTransaction.commit();
            } else if (item.getItemId() == R.id.filesList) {
                FragmentTransaction fileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (filesFragment == null) filesFragment = new FilesFragment();
                fileFragmentTransaction.replace(R.id.mainFragment, filesFragment);
                fileFragmentTransaction.commit();
            } else if (item.getItemId() == R.id.btnNavSettings) {
                FragmentTransaction settingsFragmentTransaction = getSupportFragmentManager().beginTransaction();
                settingsFragmentTransaction.replace(R.id.mainFragment, new SettingsFragment());
                settingsFragmentTransaction.commit();
            }
            item.setChecked(true);
            return false;
        });
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        permission(savedInstanceState);
    }

    private void permission(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            videoFiles = loadLibrary();
            if (savedInstanceState == null) {
                loadFolderFragment();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                videoFiles = loadLibrary();
                FragmentTransaction folderFragmentTransaction = getSupportFragmentManager().beginTransaction();
                folderFragmentTransaction.replace(R.id.mainFragment, new FolderFragment());
                folderFragmentTransaction.commit();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (FolderVideoFragment.loaded && bottomNav.getSelectedItemId() == R.id.folderList) {
            FolderVideoFragment.loaded = false;
            loadFolderFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (themeId != Theme.currentThemeId) {
            recreate();
        }
    }

    public ArrayList<VideoFiles> loadLibrary() {
        long start = System.currentTimeMillis();
        ArrayList<VideoFiles> videoFilesArray = new ArrayList<>();
        HashSet<String> videoPaths = (HashSet<String>) sharedPreferences.getStringSet("videoPaths", null);
        if (null == null) return refreshLibrary(MainActivity.this);
        int i = 0;
        for (String path : videoPaths) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String id = String.valueOf(i);
            File file = new File(path);
            String fileName = file.getName();
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            Log.e("loadLibraryFileName", fileName + " | " + title);
            String dateAdded = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int slashFirstIndex = path.lastIndexOf("/");
            String subString = path.substring(0, slashFirstIndex);
            int index = subString.lastIndexOf("/");
            String folderName = subString.substring((index + 1));
            if (!folderList.contains(folderName)) {
                folderList.add(folderName);
            }
            VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, "0", dateAdded, duration, folderName);
            videoFilesArray.add(videoFiles);
        }
        Log.e("loadLibraryTest", "running " + VideoFiles.getDurationFormat(System.currentTimeMillis() - start));
        return videoFilesArray;
    }

    public ArrayList<VideoFiles> refreshLibrary(Context context) {
        long start = System.currentTimeMillis();
        ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        HashSet<String> videoPaths = new HashSet<>();
        HashSet<String> folderPaths = new HashSet<>();
        if (cursor != null) {
            while(cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String fileName = cursor.getString(5);
                retriever.setDataSource(path);
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                int index = subString.lastIndexOf("/");
                String folderName = subString.substring((index + 1));
                if (!folderList.contains(folderName)) {
                    folderList.add(folderName);
                }
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), folderName);
                tempVideoFiles.add(videoFiles);
                videoPaths.add(path);
            }
            cursor.close();
        }

        Log.e("refreshLibraryTest", "running " + VideoFiles.getDurationFormat(System.currentTimeMillis() - start));
        editor.putStringSet("videoPaths", videoPaths);
        editor.apply();
        return tempVideoFiles;
    }

    private void loadFolderFragment() {
        FragmentTransaction folderFragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (folderFragment == null) folderFragment = new FolderFragment();
        folderFragmentTransaction.replace(R.id.mainFragment, folderFragment);
        folderFragmentTransaction.commit();
    }
}