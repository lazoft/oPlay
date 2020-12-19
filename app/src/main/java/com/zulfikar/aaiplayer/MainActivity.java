package com.zulfikar.aaiplayer;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements ActivityUtility {

    private static final int REQUEST_CODE_PERMISSION = 123;
//    private static final String TAG = "MAIN_ACTIVITY_LOG";

    BottomNavigationView bottomNav;
    FloatingActionButton btnRefresh;
    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;

    Snackbar videoLoadingSnackBar;

    Handler mainActivityHandler = new Handler();

    boolean onPause;
    int themeId = 0;
    long backPressedRecord;

    static FragmentManager fragmentManager;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        outState.putSerializable("folderFragment", folderFragment);
//        outState.putSerializable("filesFragment", filesFragment);
        onPause = true;
        outState.putInt("bottomNavSelectedItem", bottomNav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    public static ArrayList<VideoFiles> videoFiles;
    public static ArrayList<String> folderList;

    @Override
    protected void onResume() {
        super.onResume();
        onPause = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) savedInstanceState = getIntent().getBundleExtra("saved_state");
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        themeId = Theme.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int selectedBottomNaveItemId = -1;
        if (savedInstanceState != null) {
            selectedBottomNaveItemId = savedInstanceState.getInt("bottomNavSelectedItem");
        }
        bottomNav = findViewById(R.id.bottomNavViewAM);
        btnRefresh = findViewById(R.id.btnRefresh);
        if (fragmentManager == null) fragmentManager = getSupportFragmentManager();

        addBottomNavListener();

        btnRefresh.setOnClickListener(v -> {
//            int selectedId = bottomNav.getSelectedItemId();
//            Fragment fragment = selectedId == R.id.folderList? FolderVideoFragment.isLoaded()? FolderVideoFragment.getInstance() : FolderFragment.getInstance() : selectedId == R.id.filesList? FilesFragment.getInstance() : new SettingsFragment();
            load(); //load(getSupportFragmentManager().beginTransaction(), fragment);
//            videoFiles = loadLibrary();
//            FilesFragment.reload();
//            FolderFragment.requestLoad();
//            FolderVideoFragment.requestLoad();
        });
        if (selectedBottomNaveItemId >= 0) bottomNav.setSelectedItemId(selectedBottomNaveItemId);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        permission(savedInstanceState);
    }

    private void permission(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
//            videoFiles = loadLibrary();
//            if (videoFiles == null) loadFolders();

            if (savedInstanceState == null) {
                if (videoFiles == null) load(); //load(getSupportFragmentManager().beginTransaction(), FolderFragment.getInstance());
                loadFragment(getSupportFragmentManager().beginTransaction(), FolderFragment.getInstance());
            } else {
//                int selectedId = bottomNav.getSelectedItemId();
//                Fragment fragment = selectedId == R.id.folderList? FolderVideoFragment.isLoaded()? FolderVideoFragment.getInstance() : FolderFragment.getInstance() : selectedId == R.id.filesList? FilesFragment.getInstance() : new SettingsFragment();
                if (videoFiles == null) load(); // load(getSupportFragmentManager().beginTransaction(), fragment);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                videoFiles = loadLibrary();
//                if (videoFiles == null) loadFolders();
                if (videoFiles == null) load(); //load(getSupportFragmentManager().beginTransaction(), FolderFragment.getInstance());
                loadFragment(getSupportFragmentManager().beginTransaction(), FolderFragment.getInstance());
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (FolderVideoFragment.isLoaded() && bottomNav.getSelectedItemId() == R.id.folderList) {
            FolderVideoFragment.requestUnload();
            loadFragment(getSupportFragmentManager().beginTransaction(), FolderFragment.getInstance());
        } else {
            if (System.currentTimeMillis() - backPressedRecord <= 2100) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            }
            backPressedRecord = System.currentTimeMillis();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (themeId != Theme.currentThemeId) {
            new Thread(() -> {
                Snackbar.make(MainActivity.this.bottomNav, "Applying theme. Please wait...", Snackbar.LENGTH_SHORT).show();
                mainActivityHandler.postDelayed(() -> Theme.recreate(MainActivity.this, MainActivity.this), 300);
            }).start();

        }
    }

    private void load() {
        videoLoadingSnackBar = Snackbar.make(bottomNav, "Loading. Please wait...", BaseTransientBottomBar.LENGTH_INDEFINITE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoLoadingSnackBar.getView().getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        videoLoadingSnackBar.show();
        new Thread(() -> {
            Pair<ArrayList<VideoFiles>, ArrayList<String>> data = refreshLibraryAsync(MainActivity.this);
            mainActivityHandler.post(() -> {
                videoLoadingSnackBar.dismiss();
                videoFiles = data.first;
                folderList = data.second;
                Collections.sort(folderList);
                FolderFragment.requestLoad();
                FilesFragment.requestLoad();
                if (FolderVideoFragment.isLoaded()) FolderVideoFragment.requestLoad();
                setBottomNavItemOnClick(true, bottomNav.getMenu().findItem(bottomNav.getSelectedItemId()));
            });
        }).start();
    }

    public Pair<ArrayList<VideoFiles>, ArrayList<String>> refreshLibraryAsync(Context context) {
        ArrayList<VideoFiles> videoFiles = new ArrayList<>();
        ArrayList<String> folderList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME};
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
                String duration = "0";
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

    private void loadFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        if (!onPause) {
            fragmentTransaction.replace(R.id.mainFragment, fragment);
            fragmentTransaction.commit();
        } else {
            mainActivityHandler.post(() -> loadFragment(fragmentTransaction, fragment));
        }
    }

    private void addBottomNavListener() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            setBottomNavItemOnClick(false, item);
            return item.setChecked(true) == null;
        });
    }

    private void setBottomNavItemOnClick(boolean refreshCall, MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (item.getItemId() == R.id.folderList) {
            if (FolderVideoFragment.isLoaded() && (refreshCall || !item.isChecked())) {
                loadFragment(fragmentTransaction, FolderVideoFragment.getInstance());
            } else {
                FolderVideoFragment.requestUnload();
                loadFragment(fragmentTransaction, FolderFragment.getInstance());
            }
        } else if (item.getItemId() == R.id.filesList) {
            loadFragment(fragmentTransaction, FilesFragment.getInstance());
        } else if (item.getItemId() == R.id.btnNavSettings) {
            loadFragment(fragmentTransaction, new SettingsFragment());
        }
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        onSaveInstanceState(bundle);
    }
}

// <editor-fold defaultstate="collapsed" desc="Deprecated V1.0">
/*
    private void loadFolders() {
        (videoLoadingSnackBar = Snackbar.make(bottomNav, "Loading videos. Please wait...", BaseTransientBottomBar.LENGTH_INDEFINITE)).show();
        HashSet<String> folderNames = (HashSet<String>) sharedPreferences.getStringSet("folderNames", null);
        if (folderNames != null) {
            folderList = new ArrayList<>(folderNames);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<VideoFiles> videos = getAllVideos(MainActivity.this, folderList);
                    mainActivityHandler.post(() -> {
                        videoFiles = videos;
                        FilesFragment.requestLoad();
                        if (videoLoadingSnackBar.isShown()) videoLoadingSnackBar.dismiss();
                    });
                }

                public ArrayList<VideoFiles> getAllVideos(Context context, ArrayList<String> folderNames) {
                    ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
                    Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME};
                    String selection = MediaStore.Video.Media.DATA + " LIKE?";
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    for (String folderName : folderNames) {
                        String[] selectionArgs = new String[]{"%" + folderName + "%"};
                        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                String id = cursor.getString(0);
                                String path = cursor.getString(1);
                                String title = cursor.getString(2);
                                String size = cursor.getString(3);
                                String dateAdded = cursor.getString(4);
                                String fileName = cursor.getString(5);
                                String duration = "0";
                                try {
                                    retriever.setDataSource(path);
                                } catch (Exception e) {
                                    retriever = null;
                                }
                                if (retriever != null)
                                    duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration, folderName);
                                tempVideoFiles.add(videoFiles);
                            }
                            cursor.close();
                        }
                    }
                    return  tempVideoFiles;
                }
            }).start();
        } else {
            videoFiles = loadLibrary();
        }

    }

    public ArrayList<VideoFiles> loadLibrary() {
        if (null == null) return refreshLibrary(MainActivity.this);
        long start = System.currentTimeMillis();
        ArrayList<VideoFiles> videoFilesArray = new ArrayList<>();
        HashSet<String> videoPaths = (HashSet<String>) sharedPreferences.getStringSet("videoPaths", null);
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
        ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        HashSet<String> videoPaths = new HashSet<>();
        if (cursor != null) {
            while(cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String fileName = cursor.getString(5);
                String duration = "0";
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
//                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), folderName);
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration, folderName);
                tempVideoFiles.add(videoFiles);
                videoPaths.add(path);
            }
            cursor.close();
        }

        editor = sharedPreferences.edit();
        editor.putStringSet("videoPaths", videoPaths);
        HashSet<String> folderNames = new HashSet<String>(folderList);
        editor.putStringSet("folderNames", folderNames);
        editor.apply();
        return tempVideoFiles;
    }
*/
// </editor-fold>