package com.zulfikar.aaiplayer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;

import static com.zulfikar.aaiplayer.VideoAdapter.videoFiles;
import static com.zulfikar.aaiplayer.VideoFolderAdapter.folderVideoFiles;

public class PlayerActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    DefaultTimeBar timeBar;
    Handler playerHandler = new Handler();
    ImageViewButton btnBackward, btnForward, btnPlayPause, btnCamera;
    LinearLayout playbackController, controlLabelLayout;
    PlayerView playerView;
    RelativeLayout customController, timeBarLayout;
    SimpleExoPlayer simpleExoPlayer;
    String sender, path;
    TextView controlLabel;
    Thread playbackControllerThread;

//    private String filename = "oloshsnap-";
    private static final int quality = 100;

    boolean recordingClip, controllerVisible = true;
    int forwardJumpTime, backwardJumpTime, position = -1;
    long pauseTime;

    private static final String PLAYBACK_JUMPER_PREFERENCE = "playback_jumper_preferences";
    static HashMap<String, Long> lastPlayed = new HashMap<>();
    static Long duration = 0L;

    volatile TextView videoPosition, videoDuration;
    volatile boolean exitPlayer;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (System.currentTimeMillis() - pauseTime < 100) {
            btnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_circle_outline));
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onPause() {
        pauseTime = System.currentTimeMillis();
        if(!sharedPreferences.getBoolean(SettingsFragment.BACKGROUND_PLAYBACK_STATE, true)){
            if(simpleExoPlayer.getPlayWhenReady()) {
                btnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_circle_outline));
                simpleExoPlayer.setPlayWhenReady(false);
            }
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        sharedPreferences = getSharedPreferences(PLAYBACK_JUMPER_PREFERENCE, MODE_PRIVATE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        playerView = findViewById(R.id.exoplayer_movie);
        customController = findViewById(R.id.cloneCustomController);
        position = getIntent().getIntExtra("position", -1);
        sender = getIntent().getStringExtra("sender");
        path = sender.equals("Video") ? videoFiles.get(position).getPath() : sender.equals("VideoFolder") ? folderVideoFiles.get(position).getPath() : path;
        duration = lastPlayed.get(path);
        if (duration == null) duration = 0L;
        backwardJumpTime = Integer.parseInt(sharedPreferences.getString("backward_jumper_time", "10"));
        forwardJumpTime = Integer.parseInt(sharedPreferences.getString("forward_jumper_time", "10"));
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
        exitPlayer = true;
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void functioningCustomController(RelativeLayout customController) {
        playbackController = customController.findViewById(R.id.playbackControl);
        timeBarLayout = customController.findViewById(R.id.time_bar_layout);
        controlLabelLayout = customController.findViewById(R.id.control_label_layout);
        controlLabel = customController.findViewById(R.id.control_label);
        btnBackward = customController.findViewById(R.id.exo_rew);
        btnPlayPause = customController.findViewById(R.id.exo_play);
        btnCamera = customController.findViewById(R.id.btnCapture);
        btnForward = customController.findViewById(R.id.exo_ffwd);
        timeBar = customController.findViewById(R.id.exo_progress);
        videoPosition = customController.findViewById(R.id.exo_position);
        videoDuration = customController.findViewById(R.id.exo_duration);

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
            int seek = backwardJumpTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    y = event.getRawY();
                    controlLabelLayout.setVisibility(View.VISIBLE);
                    controlLabel.setText(String.format(Locale.US,"Backward %d Sec", backwardJumpTime));
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getRawY() < y) seek = (int) (backwardJumpTime * ((1 + (11 - (int) (11 / y * event.getRawY())) / 10.0) % 2.1));
                    else if (event.getRawY() > y) seek = (int) (backwardJumpTime * (11 - 11 / y * (event.getRawY() - y)) / 10);

                    controlLabel.setText(String.format(Locale.US,"Backward %d Sec", seek));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    controlLabelLayout.setVisibility(View.INVISIBLE);
                    long seekPosition = simpleExoPlayer.getCurrentPosition() - TimeUnit.SECONDS.toMillis(seek);
                    if (seekPosition < 0) seekPosition = 0;
                    simpleExoPlayer.seekTo(seekPosition);
                    timeBar.setPosition(simpleExoPlayer.getCurrentPosition());
                    videoPosition.setText(getDurationFormat(simpleExoPlayer.getCurrentPosition()));
                    seek = backwardJumpTime;
                    v.performClick();
                    return true;
                }
                return false;
            }
        });

        btnPlayPause.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                controlLabelLayout.setVisibility(View.VISIBLE);
                controlLabel.setText(simpleExoPlayer.getPlayWhenReady()? R.string.text_pause : R.string.text_play);
                return true;
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                controlLabelLayout.setVisibility(View.INVISIBLE);
                simpleExoPlayer.setPlayWhenReady(!simpleExoPlayer.getPlayWhenReady());
                btnPlayPause.setImageDrawable(simpleExoPlayer.getPlayWhenReady()? getResources().getDrawable(R.drawable.ic_baseline_pause_circle_outline) : getResources().getDrawable(R.drawable.ic_baseline_play_circle_outline));
                v.performClick();
                return true;
            }
            return false;
        });

        btnCamera.setOnTouchListener(new View.OnTouchListener() {
            float y;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (recordingClip) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        controlLabelLayout.setVisibility(View.VISIBLE);
                        controlLabel.setText(R.string.stop_clip_recording_label);
                        return true;
                    } else if (e.getAction() == MotionEvent.ACTION_UP) {
                        controlLabelLayout.setVisibility(View.INVISIBLE);
                        btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.button_camera_normal));
                        recordClip(recordingClip = false);
                        return true;
                    }
                } else {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        y = e.getRawY();
                        controlLabelLayout.setVisibility(View.VISIBLE);
                        controlLabel.setText(R.string.capture_label);
                        btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.button_camera_pressed));
//                        snapFrame();
                        return true;
                    } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                        if (e.getRawY() > y && 1.0 * e.getRawY() / y > 1.15) {
                            btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.button_camera_record_start));
                            controlLabel.setText(R.string.start_clip_record_label);
                        } else if (e.getRawY() < y && y / e.getRawY() > 2) {
                            controlLabel.setText(R.string.capture_label);
                            btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.button_camera_pressed));
                        } else {
                            controlLabel.setText(R.string.capture_label);
                            btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.button_camera_pressed));
                        }
                    } else if (e.getAction() == MotionEvent.ACTION_UP) {
                        controlLabelLayout.setVisibility(View.INVISIBLE);
                        if (e.getRawY() > y && 1.0 * e.getRawY() / y > 1.15) {
                            btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.button_camera_record_start));
                            recordClip(recordingClip = true);
                        } else {
                            TextureView textureView = (TextureView)playerView.getVideoSurfaceView();
                            btnCamera.setImageDrawable(getResources().getDrawable(R.drawable.button_camera_normal));
                            snapFrame(textureView);
                        }
                        v.performClick();
                        return true;
                    }
                }
                return false;
            }
        });

        btnForward.setOnTouchListener(new View.OnTouchListener() {
            float y;
            int seek = forwardJumpTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    y = event.getRawY();
                    controlLabelLayout.setVisibility(View.VISIBLE);
                    controlLabel.setText(String.format(Locale.US, "Forward %d Sec", forwardJumpTime));
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getRawY() < y) seek = (int) (backwardJumpTime * ((1 + (11 - (int) (11 / y * event.getRawY())) / 10.0) % 2.1));
                    else if (event.getRawY() > y) seek = (int) (backwardJumpTime * (11 - 11 / y * (event.getRawY() - y)) / 10);
                    controlLabel.setText(String.format(Locale.US, "Forward %d Sec", seek));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    controlLabelLayout.setVisibility(View.INVISIBLE);
                    long seekPosition = simpleExoPlayer.getCurrentPosition() + TimeUnit.SECONDS.toMillis(seek);
                    if (seekPosition > simpleExoPlayer.getDuration()) seekPosition = simpleExoPlayer.getDuration();
                    simpleExoPlayer.seekTo(seekPosition);
                    timeBar.setPosition(simpleExoPlayer.getCurrentPosition());
                    videoPosition.setText(getDurationFormat(simpleExoPlayer.getCurrentPosition()));
                    seek = forwardJumpTime;
                    v.performClick();
                    return true;
                }
                return false;
            }
        });

        timeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(@NotNull TimeBar timeBar, long position) {
                controlLabelLayout.setVisibility(View.VISIBLE);
                controlLabel.setText(String.format(Locale.US, "Seek to: %s", getDurationFormat(position)));
            }

            @Override
            public void onScrubMove(@NotNull TimeBar timeBar, long position) {
                controlLabel.setText(String.format(Locale.US, "Seek to: %s", getDurationFormat(position)));
            }

            @Override
            public void onScrubStop(@NotNull TimeBar timeBar, long position, boolean canceled) {
                simpleExoPlayer.seekTo(position);
                videoPosition.setText(getDurationFormat(position));
                controlLabelLayout.setVisibility(View.INVISIBLE);
            }
        });

        (playbackControllerThread = new Thread(new CustomTimeBar())).start();
    }

    private String getDurationFormat(long duration) {
        long hour = TimeUnit.MILLISECONDS.toHours(duration);
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hour));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration - TimeUnit.HOURS.toMillis(hour) - TimeUnit.MINUTES.toMillis(minute));

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", roundValue(String.valueOf(hour)), roundValue(String.valueOf(minute)), roundValue(String.valueOf(seconds)));
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

    private void snapFrame(TextureView textureView) {
        Date date = new Date();
        CharSequence time = android.text.format.DateFormat.format("yy-MM-dd_hh:mm:ss", date);
        String dirPath = Environment.getExternalStorageDirectory().toString()+"/Pictures";
        File fileDirectory = new File(dirPath);
        if (!fileDirectory.exists()) {
            boolean dir = fileDirectory.mkdir();
            if (!dir) Toast.makeText(PlayerActivity.this, "Screenshot Feature Will fail", Toast.LENGTH_SHORT).show();
        }
        try {

            String path = dirPath + "/" + "snapshot-" + "-" + time + ".jpeg";
            Bitmap bitmap = textureView.getBitmap();
            File imageFile = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(PlayerActivity.this, "Screenshot Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO: Sohan's code to record video clip
    /*
    * If action is `true` then start recording the video
    * Else if action if `false` then stop recording the video and save the video to phone
    */
    private void recordClip(boolean action) {

    }

    private class CustomTimeBar implements Runnable {
        long durationEnd = -1;
        long durationCurrent = 0;

        @Override
        public void run() {
            try {
                while (durationEnd < 0) {
                    TimeUnit.MILLISECONDS.sleep(125);
                    playerHandler.post(() -> durationEnd = simpleExoPlayer.getDuration());
    //                durationEnd = simpleExoPlayer.getDuration();
                }

                playerHandler.post(() -> {
                    videoDuration.setText(getDurationFormat(durationEnd));
                    timeBar.setDuration(durationEnd);
                });

                while (!exitPlayer) {
//                    durationCurrent = simpleExoPlayer.getCurrentPosition();
                    playerHandler.post(() -> durationCurrent = simpleExoPlayer.getCurrentPosition());
                    playerHandler.post(() -> {
                        videoPosition.setText(getDurationFormat(durationCurrent));
                        timeBar.setPosition(durationCurrent);
                    });
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                Log.e("threadMessage", Objects.requireNonNull(e.getMessage()));
            }
        }
    }
}