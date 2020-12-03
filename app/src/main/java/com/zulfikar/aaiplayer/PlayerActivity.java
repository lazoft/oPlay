package com.zulfikar.aaiplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;

import static com.zulfikar.aaiplayer.VideoAdapter.videoFiles;
import static com.zulfikar.aaiplayer.VideoFolderAdapter.folderVideoFiles;

public class PlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    String path;
    static HashMap<String, Long> lastPlayed = new HashMap<>();
    int position = -1;

    static long duration = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        playerView = findViewById(R.id.exoplayer_movie);
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender.equals("Video")) {
            path = videoFiles.get(position).getPath();
        } else if (sender.equals("VideoFolder")) {
            path = folderVideoFiles.get(position).getPath();
        }
        duration = lastPlayed.getOrDefault(path, (long) 0);
        startVideo(duration);
    }

    private void startVideo(long duration) {
        if (path != null) {
            Uri uri = Uri.parse(path);
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "AAI Player"));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri);
            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);
            simpleExoPlayer.prepare(mediaSource, false, true);
            simpleExoPlayer.seekTo(duration);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        lastPlayed.put(path, simpleExoPlayer.getContentPosition());
        simpleExoPlayer.stop();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}