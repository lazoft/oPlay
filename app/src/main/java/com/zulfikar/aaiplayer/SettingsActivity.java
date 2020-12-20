package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity implements ActivityUtility {
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    PickerWheel txtForwardPlayback, txtBackwardPlayback;
    TextView btnPlaylist, btnChangeTheme, btnChangeLanguage, btnWifiShare, btnVideoSnaps, btnVideoRecordedClips, btnResetSettings, btnSendFeedback, btnUpdate, btnAbout, btnExit;
    SwitchCompat switchBackgroundPlayback;

    Handler settingsActivityHolder = new Handler();

    private int currentThemeId;

    private static final String PLAYBACK_JUMPER_PREFERENCE = "playback_jumper_preferences";
    public static final String BACKGROUND_PLAYBACK_STATE = "background_playback_state";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentThemeId = Theme.applyTheme(this);
//        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_out_right);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(PLAYBACK_JUMPER_PREFERENCE, Context.MODE_PRIVATE);
        btnChangeTheme = findViewById(R.id.btnChangeThemeAS);
        btnPlaylist = findViewById(R.id.btnPlaylistAS);
        btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        btnWifiShare = findViewById(R.id.btnWifiShareAS);
        btnVideoSnaps = findViewById(R.id.btnVideoSnapsAS);
        btnVideoRecordedClips = findViewById(R.id.btnVideoRecordedClipsAS);
        btnResetSettings = findViewById(R.id.btnResetSettingsAS);
        btnSendFeedback = findViewById(R.id.btnFeedbackAS);
        btnUpdate = findViewById(R.id.btnUpdateAS);
        btnAbout = findViewById(R.id.btnAboutAS);
        btnExit = findViewById(R.id.btnExitAS);
        txtBackwardPlayback = findViewById(R.id.txtBackwardPlaybackAS);
        txtForwardPlayback = findViewById(R.id.txtForwardPlaybackAS);
        switchBackgroundPlayback = findViewById(R.id.switchBackgroundPlaybackAS);
        switchBackgroundPlayback.setChecked(sharedPreferences.getBoolean(BACKGROUND_PLAYBACK_STATE,true));

        txtBackwardPlayback.setValue(sharedPreferences.getInt("backward_jumper_time", 10));
        txtForwardPlayback.setValue(sharedPreferences.getInt("forward_jumper_time", 10));

        addListeners();
        setOnClickForUpcomingButtons(btnChangeLanguage, btnWifiShare, btnResetSettings, btnSendFeedback, btnUpdate);
    }

    public void setOnClickForUpcomingButtons(TextView... buttons) {
        for (TextView button : buttons) button.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, UnderMaintenanceActivity.class));
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (currentThemeId != Theme.currentThemeId) {
            new Thread(() -> {
                Snackbar.make(SettingsActivity.this.btnPlaylist, "Applying theme. Please wait...", Snackbar.LENGTH_SHORT).show();
                settingsActivityHolder.postDelayed(() -> Theme.recreate(SettingsActivity.this, SettingsActivity.this), 300);
            }).start();
        }
    }

    private void addListeners() {
        btnChangeTheme.setOnClickListener(view1 -> {
            Intent themeActivityIntent = new Intent(SettingsActivity.this, ThemeActivity.class);
            themeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(themeActivityIntent);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        });

        txtForwardPlayback.setOnValueChangedListener((picker, oldVal, newVal) -> {
            editor = sharedPreferences.edit();
            editor.putInt("forward_jumper_time", txtForwardPlayback.getValue());
            editor.apply();
        });
        txtBackwardPlayback.setOnValueChangedListener((picker, oldVal, newVal) -> {
            editor = sharedPreferences.edit();
            editor.putInt("backward_jumper_time", txtBackwardPlayback.getValue());
            editor.apply();
        });
        btnAbout.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, AboutActivity.class)));
        switchBackgroundPlayback.setOnCheckedChangeListener((compoundButton, b) -> {
            editor = sharedPreferences.edit();
            editor.putBoolean(BACKGROUND_PLAYBACK_STATE, b);
            editor.apply();
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        });

        btnVideoRecordedClips.setOnClickListener(v -> {
            Snackbar snack = Snackbar.make(btnPlaylist,  "Olosh Clips are being saved on\n\n/storage/emulated/0/DCIM/Olosh Player/Olosh Clips/", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("CLOSE", v1 -> {});
            snack.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
            ((TextView) snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setLines(4);
            snack.show();
        });

        btnVideoSnaps.setOnClickListener(v -> {
            Snackbar snack = Snackbar.make(btnPlaylist,  "Olosh Clips are being saved on\n\n/storage/emulated/0/DCIM/Olosh Player/Olosh Snaps/", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("CLOSE", v1 -> {});
            snack.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
            ((TextView) snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setLines(4);
            snack.show();
        });

        btnPlaylist.setOnClickListener(v -> {
            Intent playlistChooserIntent = new Intent(SettingsActivity.this, PlaylistChooserActivity.class);
            playlistChooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(playlistChooserIntent);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        });

        btnExit.setOnClickListener(v -> System.exit(0));
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        onSaveInstanceState(bundle);
    }
}