package com.zulfikar.aaiplayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

public class pipActivity extends AppCompatActivity {


    private static final String TAG = "PIP_TAG";
    private VideoView videoView;
    private ImageButton pipButton;

    private ActionBar actionBar;
    private PictureInPictureParams.Builder pictureInPictureParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pip);

        actionBar = getSupportActionBar();

        videoView = findViewById(R.id.videoView);
        pipButton = findViewById(R.id.pipBtn);
        setVideoView(getIntent());

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            pictureInPictureParams = new PictureInPictureParams.Builder();
        }

        pipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void pictureInPictureMode(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d(TAG,"pictureInPictureMode : Supports PIP");

            Rational aspectRatio = new Rational(videoView.getWidth(),videoView.getHeight());
            pictureInPictureParams.setAspectRatio(aspectRatio).build();
            enterPictureInPictureMode(pictureInPictureParams.build());
        }
        else {
            Log.d(TAG,"Picture in Picture mode doesnt supports PIP");
        }
    }

    @Override
    protected void onUserLeaveHint(){
        super.onUserLeaveHint();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            if(!isInPictureInPictureMode()){
                Log.d(TAG,"onUserLeaveHint is not in PIP");
                pictureInPictureMode();
            }
            else{
                Log.d(TAG,"onUserLeaveHint already in PIP");
            }

        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if(isInPictureInPictureMode){
            Log.d(TAG, "onPictureInPictureModeChanged: Entering PIP");
            pipButton.setVisibility(View.GONE);
            actionBar.hide();
        }
        else{
            Log.d(TAG, "onPictureInPictureModeChanged: Exited PIP");
            pipButton.setVisibility(View.VISIBLE);
            actionBar.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: Play New Video");
        setVideoView(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    private void setVideoView(Intent intent) {
        String videoList = intent.getStringExtra("");
    }
}