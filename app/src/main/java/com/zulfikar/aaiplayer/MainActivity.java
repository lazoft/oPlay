package com.zulfikar.aaiplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 123;
    BottomNavigationView bottomNav;
    static ArrayList<VideoFiles> videoFiles = new ArrayList<>();
    static ArrayList<String> folderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeActivity.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();
        bottomNav = findViewById(R.id.bottomNavViewAM);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.folderList) {
                FragmentTransaction folderFragmentTransaction = getSupportFragmentManager().beginTransaction();
                folderFragmentTransaction.replace(R.id.mainFragment, new FolderFragment());
                folderFragmentTransaction.commit();
            } else if (item.getItemId() == R.id.filesList) {
                FragmentTransaction fileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                fileFragmentTransaction.replace(R.id.mainFragment, new FilesFragment());
                fileFragmentTransaction.commit();
            } else if (item.getItemId() == R.id.btnNavSettings) {
                FragmentTransaction settingsFragmentTransaction = getSupportFragmentManager().beginTransaction();
                settingsFragmentTransaction.replace(R.id.mainFragment, new SettingsFragment());
                settingsFragmentTransaction.commit();
            }
            item.setChecked(true);
            return false;
        });
    }


    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            videoFiles = getAllVideos(MainActivity.this);
            FragmentTransaction folderFragmentTransaction = getSupportFragmentManager().beginTransaction();
            folderFragmentTransaction.replace(R.id.mainFragment, new FolderFragment());
            folderFragmentTransaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                videoFiles = getAllVideos(MainActivity.this);
                FragmentTransaction folderFragmentTransaction = getSupportFragmentManager().beginTransaction();
                folderFragmentTransaction.replace(R.id.mainFragment, new FolderFragment());
                folderFragmentTransaction.commit();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    public ArrayList<VideoFiles> getAllVideos(Context context) {
        ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, "duration", MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String duration = cursor.getString(5);
                String fileName = cursor.getString(6);
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration);
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                int index = subString.lastIndexOf("/");
                String folderName = subString.substring((index + 1));
                if (!folderList.contains(folderName)) {
                    folderList.add(folderName);
                }
                tempVideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return tempVideoFiles;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ThemeActivity.applyTheme(this);
        this.recreate();
    }

}