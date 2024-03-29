package com.zulfikar.aaiplayer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.zulfikar.aaiplayer.VideoAdapter.videoFiles;
import static com.zulfikar.aaiplayer.VideoFolderAdapter.folderVideoFiles;

public class PlayerActivity extends AppCompatActivity {

    private static final int QUALITY = 100;
    private static final String PLAYBACK_JUMPER_PREFERENCE = "playback_jumper_preferences";
    private static final HashMap<String, Long> lastPlayed = new HashMap<>();
    private static final String TAG = "PLAYER ACTIVITY DEBUG";

    SharedPreferences sharedPreferences;
    DefaultTimeBar timeBar;
    Handler playerHandler = new Handler();
    ImageViewButton btnBackward, btnForward, btnPlayPause, btnCamera, btnRotate, btnPip;
    LinearLayout playbackController, controlLabelLayout;
    PlayerView playerView;
    RelativeLayout customController, timeBarLayout;
    SimpleExoPlayer simpleExoPlayer;
    String title, sender, path, recordingProcessStatus;
    TextView controlLabel, movieName;

    boolean  recordingClip, recordingClipProcessing, controllerVisible = true, orientation = true, pip = false;
    int forwardJumpTime, backwardJumpTime, position = -1;
    long pauseTime;
    volatile TextView videoPosition, videoDuration;
    volatile boolean exitPlayer;

    long startRecord;
    Long duration;

    volatile long durationEnd = -1;
    volatile long durationCurrent = 0;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (System.currentTimeMillis() - pauseTime < 100) {
            btnPlayPause.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_baseline_pause_circle_outline));
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {
        pauseTime = System.currentTimeMillis();
        if(!sharedPreferences.getBoolean(SettingsFragment.BACKGROUND_PLAYBACK_STATE, true)){
            if(simpleExoPlayer.getPlayWhenReady()) {
                btnPlayPause.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_baseline_play_circle_outline));
                simpleExoPlayer.setPlayWhenReady(false);
                enterPictureInPictureMode();
                Log.e("msg", pip+" pip " );
            }
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Theme.applyTheme(this);
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences(PLAYBACK_JUMPER_PREFERENCE, MODE_PRIVATE);
        playerView = findViewById(R.id.exoplayer_movie);
        customController = findViewById(R.id.cloneCustomController);
//        btnPip = findViewById(R.id.btnPiP);
        movieName = findViewById(R.id.movieName);

        title = getIntent().getStringExtra("title");
        position = getIntent().getIntExtra("position", -1);
        sender = getIntent().getStringExtra("sender");
        path = sender.equals("Video") ? videoFiles.get(position).getPath() : sender.equals("VideoFolder") ? folderVideoFiles.get(position).getPath() : path;

        duration = lastPlayed.get(path);
        if (duration == null) duration = 0L;

        backwardJumpTime = sharedPreferences.getInt("backward_jumper_time", 10);
        forwardJumpTime = sharedPreferences.getInt("forward_jumper_time", 10);

        playVideo(duration);
        functioningCustomController(customController);
//        enterPictureInPictureMode();


    }

    private void playVideo(long duration) {
        if (path != null) {
            Uri uri = Uri.parse(path);
//            int index = path.lastIndexOf("\\");
            movieName.setText(path.substring( path.lastIndexOf("/") + 1));

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
//        if (simpleExoPlayer.getCurrentPosition() >= durationEnd) lastPlayed.put(path, 0L);
//        else lastPlayed.put(path, simpleExoPlayer.getContentPosition());
        lastPlayed.put(path, simpleExoPlayer.getContentPosition());
        simpleExoPlayer.stop();
        exitPlayer = true;
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        btnRotate = customController.findViewById(R.id.btnRotate);
        btnPip = customController.findViewById(R.id.btnPiP);


        simpleExoPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onTimelineChanged(@NotNull EventTime eventTime, int reason) {
                if (simpleExoPlayer.getDuration() > 0) {
                    durationEnd = simpleExoPlayer.getDuration();
                    videoDuration.setText(getDurationFormat(durationEnd));
                    timeBar.setDuration(durationEnd);
                    if (duration >= durationEnd) simpleExoPlayer.seekTo(0);
                }
            }

            @Override
            public void onIsPlayingChanged(@NotNull EventTime eventTime, boolean isPlaying) {
                btnPlayPause.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, isPlaying? R.drawable.ic_baseline_pause_circle_outline : R.drawable.ic_baseline_play_circle_outline));
            }
        });

        customController.setOnClickListener(v -> {
            if (controllerVisible) {
                v.setBackgroundColor(ContextCompat.getColor(PlayerActivity.this, R.color.colorTransparent));
                playbackController.setVisibility(View.INVISIBLE);
                timeBarLayout.setVisibility(View.INVISIBLE);
                controlLabelLayout.setVisibility(View.INVISIBLE);
                movieName.setVisibility(View.INVISIBLE);
                controllerVisible = false;
            } else {
                v.setBackgroundColor(ContextCompat.getColor(PlayerActivity.this, R.color.customControllerBackground));
                playbackController.setVisibility(View.VISIBLE);
                timeBarLayout.setVisibility(View.VISIBLE);
                movieName.setVisibility(View.VISIBLE);
                if (recordingClipProcessing) controlLabelLayout.setVisibility(View.VISIBLE);
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
                    if (!recordingClipProcessing) controlLabelLayout.setVisibility(View.INVISIBLE);
                    else controlLabel.setText(recordingProcessStatus);
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

        //pip method, works for now, requires some management
        btnPip.setOnClickListener(view -> {
            try {
                enterPictureInPictureMode();
            } catch (IllegalStateException e) {
                Toast.makeText(PlayerActivity.this, "Doesn't support Picture in Picture", Toast.LENGTH_SHORT).show();
            }
            customController.setBackgroundColor(ContextCompat.getColor(PlayerActivity.this, R.color.colorTransparent));
            playbackController.setVisibility(View.INVISIBLE);
            timeBarLayout.setVisibility(View.INVISIBLE);
            controlLabelLayout.setVisibility(View.INVISIBLE);
            movieName.setVisibility(View.INVISIBLE);
//                btnPlayPause.setVisibility(View.VISIBLE);
            controllerVisible = false;
            Log.e("msg", pip+" pip " );

        });

        //Screen rotation method, has some issues
       btnRotate.setOnTouchListener((view, motionEvent) -> {
            if (orientation){
                orientation = false;
            } else {
                orientation = true;
                return false;
            }
//                simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
           int i = getRequestedOrientation();
           Log.e("orientation in method ", i+"");
           if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
               setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                   i = PlayerActivity.this.getRequestedOrientation();
               Log.e("orientation in if ", i+"");
           }
           else {
               setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                   i = PlayerActivity.this.getRequestedOrientation();
               Log.e("orientation in else ", i+"");

           }

           view.performClick();
           return true;
       });

//        btnRotate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                getRequestedOrientation();
//                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//        });

        btnPlayPause.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                controlLabelLayout.setVisibility(View.VISIBLE);
                controlLabel.setText(simpleExoPlayer.getPlayWhenReady()? R.string.text_pause : R.string.text_play);
                return true;
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                controlLabel.setText(simpleExoPlayer.getPlayWhenReady()? R.string.text_pause : R.string.text_play);
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                if (!recordingClipProcessing) controlLabelLayout.setVisibility(View.INVISIBLE);
                else controlLabel.setText(recordingProcessStatus);
                if (durationCurrent >= durationEnd) {
                    if (simpleExoPlayer.getPlayWhenReady()) simpleExoPlayer.seekTo(0);
                } else {
                    simpleExoPlayer.setPlayWhenReady(!simpleExoPlayer.getPlayWhenReady());
                }
//                btnPlayPause.setImageDrawable(simpleExoPlayer.getPlayWhenReady()? ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_baseline_pause_circle_outline) : ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_baseline_play_circle_outline));
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
                    } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                        controlLabel.setText(R.string.stop_clip_recording_label);
                    } else if (e.getAction() == MotionEvent.ACTION_UP) {
                        if (!recordingClipProcessing) controlLabelLayout.setVisibility(View.INVISIBLE);
                        recordingProcessStatus = "Clipping in progress 0%";
                        controlLabel.setText(recordingProcessStatus);
                        btnCamera.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.button_camera_normal));
                        toggleClip(recordingClip = false);
                        return true;
                    }
                } else {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        y = e.getRawY();
                        controlLabelLayout.setVisibility(View.VISIBLE);
                        controlLabel.setText(R.string.capture_label);
                        btnCamera.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.button_camera_pressed));
//                        snapFrame();
                        return true;
                    } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                        if (e.getRawY() > y && 1.0 * e.getRawY() / y > 1.15) {
                            btnCamera.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.button_camera_record_start));
                            controlLabel.setText(R.string.start_clip_record_label);
                        } else if (e.getRawY() < y && y / e.getRawY() > 2) {
                            controlLabel.setText(R.string.capture_label);
                            btnCamera.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.button_camera_pressed));
                        } else {
                            controlLabel.setText(R.string.capture_label);
                            btnCamera.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.button_camera_pressed));
                        }
                    } else if (e.getAction() == MotionEvent.ACTION_UP) {
                        if (!recordingClipProcessing) controlLabelLayout.setVisibility(View.INVISIBLE);
                        else controlLabel.setText(recordingProcessStatus);
                        if (e.getRawY() > y && 1.0 * e.getRawY() / y > 1.15) {
                            btnCamera.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.button_camera_record_start));
                            toggleClip(recordingClip = true);
                        } else {
                            btnCamera.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.button_camera_normal));
//                            TextureView textureView = (TextureView) playerView.getVideoSurfaceView();
//                            toggleSnap(textureView);
                            toggleSnap((TextureView)playerView.getVideoSurfaceView());
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
                    if (!recordingClipProcessing) controlLabelLayout.setVisibility(View.INVISIBLE);
                    else controlLabel.setText(recordingProcessStatus);
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
                if (!recordingClipProcessing) controlLabelLayout.setVisibility(View.INVISIBLE);
            }
        });

        (new Thread(new CustomTimeBar())).start();
    }

    private void toggleSnap(TextureView textureView) {
        Date date = new Date();
        CharSequence time = android.text.format.DateFormat.format("yy-MM-dd_hh:mm:ss", date);
        String dirPath = "/storage/emulated/0/DCIM/Olosh Player/Olosh Snaps/";
        File fileDirectory = new File(dirPath);
        if (fileDirectory.exists() || fileDirectory.mkdirs()) {
            try {
                String title = this.title.length() > 5? this.title.substring(0, 5) : this.title;
                String path = dirPath + "/" + "Snapshot (" + title + ") - " + time + ".jpeg";
                Bitmap bitmap = textureView.getBitmap();
                File imageFile = new File(path);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                Thread updateImageMediaStoreThread = new Thread(() -> {
                    try { MediaStore.Images.Media.insertImage(getContentResolver(), path, null, null); }
                    catch (IOException e) { e.printStackTrace(); }
                });
                updateImageMediaStoreThread.start();

                Toast.makeText(PlayerActivity.this, "Snapshot saved", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(PlayerActivity.this, "Failed due to storage access", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(PlayerActivity.this, "Failed due to storage access", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleClip(boolean action) {
        if (action) startRecord = simpleExoPlayer.getCurrentPosition();
        else executeCutVideoCommand(startRecord, simpleExoPlayer.getCurrentPosition(), path);
    }

    private void executeCutVideoCommand(long startMs, long endMs, String sourcePath) {
        if (recordingClipProcessing) {
            Snackbar.make(timeBar, "Another clipping is in process. Please wait...", BaseTransientBottomBar.LENGTH_SHORT).show();
            return;
        }
        Log.e(TAG, "executeCutVideoCommand: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        String destPath = "/storage/emulated/0/DCIM/Olosh Player/Olosh Clips/";
        File externalStoragePublicDirectory = new File(destPath);
        if (externalStoragePublicDirectory.exists() || externalStoragePublicDirectory.mkdirs()) {
            String title = this.title.length() > 5? this.title.substring(0, 5) : this.title;
            String destFileName = "Olosh clip (" + title + ")";
            File destinationPath =  new File(externalStoragePublicDirectory, destFileName + ".mp4");
            for (int fileNo = 2; destinationPath.exists(); fileNo++) {
                destinationPath = new File(externalStoragePublicDirectory, destFileName + " - " + fileNo + ".mp4");
            }

            if (endMs < startMs) startMs += endMs - (endMs = startMs);

            String[] trimCommand = {"-ss", String.valueOf(startMs / 1000), "-y", "-i", sourcePath, "-t", String.valueOf((endMs - startMs) / 1000),
                    "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", destinationPath.getAbsolutePath()};

            executeTrimCommand(trimCommand, endMs - startMs, "Olosh clip (" + title + ")", destinationPath);
        }
    }

    private void executeTrimCommand(String[] trimCommand, long trimDuration, String title, File output) {
        recordingClipProcessing = true;
        controlLabelLayout.setVisibility(View.VISIBLE);
        FFmpeg.executeAsync(trimCommand, (executionId, rc) -> {
            if (rc == RETURN_CODE_SUCCESS) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Video.Media.TITLE, title);
                contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                contentValues.put(MediaStore.Video.Media.DATA, output.getAbsolutePath());

                ContentResolver contentResolver = getContentResolver();
                contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                contentValues.clear();

                Log.e("msg1", "Command execution completed successfully.");

                Toast.makeText(PlayerActivity.this, "Clip Saved", Toast.LENGTH_SHORT).show();
            } else if (rc == RETURN_CODE_CANCEL) {
                Log.e("msg2", "Command execution cancelled by user.");
                Toast.makeText(PlayerActivity.this, "Clipping Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("msg3", String.format("Command execution failed with rc=%d and the output below.", rc));
                Toast.makeText(PlayerActivity.this, "Clipping failed", Toast.LENGTH_SHORT).show();
                Config.printLastCommandOutput(Log.INFO);
            }
            recordingClipProcessing = false;
            controlLabelLayout.setVisibility(View.INVISIBLE);
        });

        Config.enableStatisticsCallback(new StatisticsCallback() {
            boolean firstRun = true;
            public void apply(Statistics newStatistics) {
                if (firstRun && !(firstRun = false)) return;
                int position = newStatistics.getTime();
                int percentage = (int) (position * 100 / trimDuration);

                recordingProcessStatus = String.format(Locale.ENGLISH, "Clipping in progress %-5s", "(" + percentage + "%)");

                playerHandler.post(() -> controlLabel.setText(recordingProcessStatus));
            }
        });
    }

    private String getDurationFormat(long durationMs) {
        long hour = TimeUnit.MILLISECONDS.toHours(durationMs);
        long minute = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;

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

    private class CustomTimeBar implements Runnable {
        @Override
        public void run() {
            try {
                while (!exitPlayer) {
                    playerView.post(() -> {
                        if (simpleExoPlayer.isPlaying()) {
                            durationCurrent = simpleExoPlayer.getCurrentPosition();
                            videoPosition.setText(getDurationFormat(durationCurrent));
                            timeBar.setPosition(durationCurrent);
                        }
                    });
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (InterruptedException e) {
                Log.e("threadMessage", Objects.requireNonNull(e.getMessage()));
            }
        }
    }
}

// <editor-fold defaultstate="collapsed" desc="DEPRECIATED V1.0">
/*
ffmpeg = FFmpeg.getInstance(PlayerActivity.this);

        try {
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

        private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Toast.makeText(PlayerActivity.this, "Failed due to storage access", Toast.LENGTH_SHORT).show();
                    Log.d("", "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Toast.makeText(PlayerActivity.this, "Clip Saved", Toast.LENGTH_SHORT).show();
                    Log.d("", "SUCCESS with output : " + s);

                }

                @Override
                public void onProgress(String s) {
                    Log.d("Progress command", "" + s);

                }

                @Override
                public void onStart() {
                    Log.d("Started command", "");

                }

                @Override
                public void onFinish() {
                    Log.d("Finished command", "");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

 */
// </editor-fold>

//<editor-fold defaultstate="collapsed" desc="DEPRECATED V2.0">
/*
/final String[] s = {"ffmpeg","-ss", "0" ,"-i", sourcePath, startMs / 1000 + "", "-to", endMs / 1000 + "", "-c:v",destinationPath.getAbsolutePath()};

//            execFFmpegBinary(complexCommand);
            int rc = FFmpeg.execute(complexCommand);
//            Toast.makeText(PlayerActivity.this, "Cilpping Started", Toast.LENGTH_SHORT).show();
            if (rc == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "Command execution completed successfully.");
                Log.e("msg1", "Command execution completed successfully.");
                Toast.makeText(PlayerActivity.this, "Clip Saved", Toast.LENGTH_SHORT).show();
            } else if (rc == RETURN_CODE_CANCEL) {
//                Log.i(Config.TAG, "Command execution cancelled by user.");
                Log.e("msg2", "Command execution cancelled by user.");
            } else {
                Log.e("msg3", String.format("Command execution failed with rc=%d and the output below.", rc));
                Config.printLastCommandOutput(Log.INFO);
            }

//            long executionid = FFmpeg.executeAsync(complexCommand, new ExecuteCallback() {
//                @Override
//                public void apply(long executionId, int rc) {
//                    Toast.makeText(PlayerActivity.this, "Cilpping Started", Toast.LENGTH_SHORT).show();
//                    if (rc == RETURN_CODE_SUCCESS) {
////                Log.i(Config.TAG, "Command execution completed successfully.");
//                        Log.e("msg1", "Command execution completed successfully.");
//                        Toast.makeText(PlayerActivity.this, "Clip Saved", Toast.LENGTH_SHORT).show();
//                    } else if (rc == RETURN_CODE_CANCEL) {
////                Log.i(Config.TAG, "Command execution cancelled by user.");
//                        Log.e("msg2", "Command execution cancelled by user.");
//                        Toast.makeText(PlayerActivity.this, "Cilpping Cancelled", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.e("msg3", String.format("Command execution failed with rc=%d and the output below.", rc));
//                        Toast.makeText(PlayerActivity.this, "Clipping failed", Toast.LENGTH_SHORT).show();
//                        Config.printLastCommandOutput(Log.INFO);
//                    }
//
//                }
//            });
 */


//</editor-fold>

// <editor-fold defaultstate="collapsed" desc=" DEPRECATED V3.0 ">
/*

//    private void executeTrimCommand(String[] trimCommand, long trimDuration) {
//        try {
//            ffmpeg.execute(trimCommand, new FFmpegExecuteResponseHandler() {
//                int colorRed = Color.parseColor("#7CC30808");
//                int colorWhite = Color.parseColor("#FFFFFF");
//                int[] color = {colorRed, colorWhite};
//                int flag;
//                int i = 0;
//                @Override
//                public void onSuccess(String message) {
//                    Log.e("Clip saved", message);
//                    Toast.makeText(PlayerActivity.this, "Clip saved", Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    Log.e("Clip progress", message);
//                    String dot = "";
//                    long position = getDurationMs(message);
//                    int percentage = (int) (position * 100 / trimDuration);
////                    if (controlLabelLayout.getVisibility() == View.INVISIBLE) controlLabelLayout.setVisibility(View.VISIBLE);
////                    controlLabel.setTextColor(color[(flag = ++flag % 2)]);
////                    controlLabel.setBackgroundColor(color[(flag = ++flag % 2)]);
//                    recordingProcessStatus = String.format(Locale.ENGLISH, "Clipping in progress %-5s", "(" + percentage + "%)");
//                    controlLabel.setText(recordingProcessStatus);
////                    i = ++i % 4;
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    Log.e("Clip failed to save", message);
//                    Toast.makeText(PlayerActivity.this, "Clip failed: " + message, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onStart() {
//                    //noinspection ImplicitArrayToString
//                    Log.e("Clip started", "" + trimCommand);
//                    recordingClipProcessing = true;
//                    controlLabelLayout.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onFinish() {
//                    //noinspection ImplicitArrayToString
//                    Log.e("Clip trim finished", trimCommand + "");
//                    recordingClipProcessing = false;
//                    controlLabelLayout.setVisibility(View.INVISIBLE);
//                }
//
//                private long getDurationMs(String msg) {
//                    int indexOfTime = msg.indexOf("time");
//                    int indexOfBitrate = msg.indexOf("bitrate");
//                    if (indexOfBitrate <= 0 || indexOfTime <= 0) return 0;
//                    String timeStr = msg.substring(indexOfTime + 5, indexOfBitrate - 1);
//                    String[] timeSplit = timeStr.split(":|\\.");
//                    int hours = Integer.parseInt(timeSplit[0]);
//                    int minutes = Integer.parseInt(timeSplit[1]);
//                    int seconds = Integer.parseInt(timeSplit[2]);
//                    int milliseconds = Integer.parseInt(timeSplit[3]);
//                    long time = TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds) + milliseconds;
//
//                    return time;
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void loadFFMpegBinary() {
//        try {
//            if (ffmpeg == null) {
////                Log.d(TAG, "ffmpeg : era nulo");
//                ffmpeg = FFmpeg.getInstance(this);
//            }
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                @Override
//                public void onFailure() {
//                    showUnsupportedExceptionDialog();
//                }
//
//                @Override
//                public void onSuccess() {
//                    //////////Log.d(TAG, "ffmpeg : correct Loaded");
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            showUnsupportedExceptionDialog();
//        } catch (Exception e) {
////            Log.d(TAG, "EXception no controlada : " + e);
//        }
//    }


    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(PlayerActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Not Supported")
                .setMessage("Device Not Supported")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlayerActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

 */

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="DEPRECATED V3.5">

//            final String[] complexCommand = {"-i", sourcePath, "-ss",startMs/1000 + "","-c:v","copy", "-t", ((endMs-startMs)/1000) + "", destinationPath.getAbsolutePath()};

// </editor-fold>