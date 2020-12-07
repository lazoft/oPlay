package com.zulfikar.aaiplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zulfikar.aaiplayer.VideoAdapter.videoFiles;
import static com.zulfikar.aaiplayer.VideoFolderAdapter.folderVideoFiles;

public class PlayerActivity extends AppCompatActivity {

    DefaultTimeBar timeBar;
    Handler playerHandler = new Handler();
    ImageView btnBackward, btnForward, btnPlay, btnPause;
    LinearLayout playbackController, controlLabelLayout;
    PlayerView playerView;
    RelativeLayout customController, timeBarLayout;
    SimpleExoPlayer simpleExoPlayer;
    String sender, path;
    TextView controlLabel;

    boolean controllerVisible = true;
    int forwardJumpTime, backwardJumpTime, position = -1;

    static HashMap<String, Long> lastPlayed = new HashMap<>();
    static long duration = 0;

    volatile TextView videoPosition, videoDuration;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        Objects.requireNonNull(getSupportActionBar()).hide();
        playerView = findViewById(R.id.exoplayer_movie);
        customController = findViewById(R.id.cloneCustomController);
        position = getIntent().getIntExtra("position", -1);
        sender = getIntent().getStringExtra("sender");
        path = sender.equals("Video") ? videoFiles.get(position).getPath() : sender.equals("VideoFolder") ? folderVideoFiles.get(position).getPath() : path;
        duration = Objects.requireNonNull(lastPlayed.getOrDefault(path, 0l));
        forwardJumpTime = Integer.parseInt(getResources().getString(R.string.default_forward_playback_time));
        backwardJumpTime = Integer.parseInt(getResources().getString(R.string.default_backward_playback_time));
        Log.e("forwardJumpTime", forwardJumpTime + "");
        startVideo(duration);
        functioningCustomController(customController);
    }

    private void startVideo(long duration) {
        if (path != null) {
            Uri uri = Uri.parse(path);
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Olosh Player"));
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

    private void functioningCustomController(RelativeLayout customController) {
        playbackController = customController.findViewById(R.id.playbackControl);
        timeBarLayout = customController.findViewById(R.id.time_bar_layout);
        controlLabelLayout = customController.findViewById(R.id.control_label_layout);
        controlLabel = customController.findViewById(R.id.control_label);
        btnBackward = customController.findViewById(R.id.exo_rew);
        btnPlay = customController.findViewById(R.id.exo_play);
        btnPause = customController.findViewById(R.id.exo_pause);
        btnForward = customController.findViewById(R.id.exo_ffwd);
        timeBar = customController.findViewById(R.id.exo_progress);
        videoPosition = customController.findViewById(R.id.exo_position);
        videoDuration = customController.findViewById(R.id.exo_duration);

        new Thread(new CustomTimeBar()).start();

        customController.setOnClickListener(v -> {

            if (controllerVisible) {
                v.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                playbackController.setVisibility(View.INVISIBLE);
                timeBarLayout.setVisibility(View.INVISIBLE);
                controllerVisible = false;
            } else {
                v.setBackgroundColor(getResources().getColor(R.color.customControllerBackground));
                playbackController.setVisibility(View.VISIBLE);
                timeBarLayout.setVisibility(View.VISIBLE);
                controllerVisible = true;
            }
        });

        btnBackward.setOnTouchListener(new View.OnTouchListener() {
            float y;
            int seek;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    y = event.getRawY();
                    controlLabelLayout.setVisibility(View.VISIBLE);
                    controlLabel.setText("Backward " + backwardJumpTime + " Sec");
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getRawY() < y) seek = (int) (backwardJumpTime * ((1 + (11 - (int) (11 / y * event.getRawY())) / 10.0) % 2.1));
                    else if (event.getRawY() > y) seek = (int) (backwardJumpTime * (11 - 11 / y * (event.getRawY() - y)) / 10);

                    controlLabel.setText(String.format("Backward %d Sec", seek));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    controlLabelLayout.setVisibility(View.INVISIBLE);
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - TimeUnit.SECONDS.toMillis(seek));
                    videoPosition.setText(getDurationFormat(simpleExoPlayer.getCurrentPosition()));
                    return true;
                }
                return false;
            }
        });

        btnPlay.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                controlLabelLayout.setVisibility(View.VISIBLE);
                controlLabel.setText("Play");
                return true;
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                controlLabelLayout.setVisibility(View.INVISIBLE);
                simpleExoPlayer.setPlayWhenReady(true);
                return true;
            }
            return false;
        });

        btnPause.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                controlLabelLayout.setVisibility(View.VISIBLE);
                controlLabel.setText("Pause");
                return true;
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                controlLabelLayout.setVisibility(View.INVISIBLE);
                simpleExoPlayer.setPlayWhenReady(false);
                return true;
            }
            return false;
        });

        btnForward.setOnTouchListener(new View.OnTouchListener() {
            float y;
            int seek;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    y = event.getRawY();
                    controlLabelLayout.setVisibility(View.VISIBLE);
                    controlLabel.setText("Forward " + forwardJumpTime + " Sec");
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getRawY() < y) seek = (int) (backwardJumpTime * ((1 + (11 - (int) (11 / y * event.getRawY())) / 10.0) % 2.1));
                    else if (event.getRawY() > y) seek = (int) (backwardJumpTime * (11 - 11 / y * (event.getRawY() - y)) / 10);

                    controlLabel.setText(String.format("Forward %d Sec", seek));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    controlLabelLayout.setVisibility(View.INVISIBLE);
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + TimeUnit.SECONDS.toMillis(seek));
                    videoPosition.setText(getDurationFormat(simpleExoPlayer.getCurrentPosition()));
                    return true;
                }
                return false;
            }
        });

        timeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                simpleExoPlayer.seekTo(position);
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {

            }
        });
    }

    private String getDurationFormat(long duration) {
        long hour = TimeUnit.MILLISECONDS.toHours(duration);
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hour));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.HOURS.toMillis(hour) - TimeUnit.MINUTES.toMillis(minute));

        String durationFormatted = String.format(Locale.ENGLISH, "%02d:%02d:%02d", roundValue(String.valueOf(hour)), roundValue(String.valueOf(minute)), roundValue(String.valueOf(seconds)));
        return durationFormatted;
    }

    private int roundValue(String val) {
        if (val.length() > 10) {
            return 0;
        }

        if (val.length() > 2) {
            if (Character.isDigit(val.charAt(2))) {
                if (val.charAt(2) > '5') {
                    return Integer.parseInt(val.charAt(0) + String.valueOf((char) (val.charAt(1) + 1)));
                } else {
                    return Integer.parseInt(val.substring(0, 2));
                }
            }
        }

        return Integer.parseInt(val);
    }

    private class CustomTimeBar implements Runnable {
        long durationEnd = -1;
        long durationCurrent = 0;

        @Override
        public void run() {
            while (durationEnd < 0) {
                playerHandler.post(() -> durationEnd = simpleExoPlayer.getDuration());
//                try {
//                    playerHandler.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                durationEnd = 45;
            }

            playerHandler.post(() -> {
                videoDuration.setText(getDurationFormat(durationEnd));
                timeBar.setDuration(durationEnd);
            });

            while (durationCurrent < durationEnd) {
                try {
                    playerHandler.post(() -> durationCurrent = simpleExoPlayer.getCurrentPosition());
                    //playerHandler.wait();
                    playerHandler.post(() -> {
                        videoPosition.setText(getDurationFormat(durationCurrent));
                        timeBar.setPosition(durationCurrent);
                    });
                    //playerHandler.wait();
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Log.e("threadMessage", e.getMessage());
                }
            }
        }
    }
}