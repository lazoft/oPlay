package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;


public class SettingsFragment extends Fragment {

    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    PickerWheel txtForwardPlayback, txtBackwardPlayback;
    TextView btnMyAccount, btnPlaylist, btnChangeTheme, btnChangeLanguage, btnWifiShare, btnVideoSnaps, btnVideoRecordedClips, btnResetSettings, btnSendFeedback, btnUpdate, btnAbout, btnExit;
    SwitchCompat switchBackgroundPlayback;

    private static final String PLAYBACK_JUMPER_PREFERENCE = "playback_jumper_preferences";
    public static final String BACKGROUND_PLAYBACK_STATE = "background_playback_state";
//    private static final String TAG = "Settings Fragment DEBUG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = requireActivity().getSharedPreferences(PLAYBACK_JUMPER_PREFERENCE, Context.MODE_PRIVATE);
        btnChangeTheme = view.findViewById(R.id.btnChangeThemeFS);
        btnMyAccount = view.findViewById(R.id.btnMyAccountFS);
        btnPlaylist = view.findViewById(R.id.btnPlaylistFS);
        btnChangeLanguage = view.findViewById(R.id.btnChangeLanguage);
        btnWifiShare = view.findViewById(R.id.btnWifiShareFS);
        btnVideoSnaps = view.findViewById(R.id.btnVideoSnapsFS);
        btnVideoRecordedClips = view.findViewById(R.id.btnVideoRecordedClipsFS);
        btnResetSettings = view.findViewById(R.id.btnResetSettingsFS);
        btnSendFeedback = view.findViewById(R.id.btnFeedbackFS);
        btnUpdate = view.findViewById(R.id.btnUpdateFS);
        btnAbout = view.findViewById(R.id.btnAboutFS);
        btnExit = view.findViewById(R.id.btnExitFS);
        txtBackwardPlayback = view.findViewById(R.id.txtBackwardPlaybackFS);
        txtForwardPlayback = view.findViewById(R.id.txtForwardPlaybackFS);
        switchBackgroundPlayback = view.findViewById(R.id.switchBackgroundPlayback);
        switchBackgroundPlayback.setChecked(sharedPreferences.getBoolean(BACKGROUND_PLAYBACK_STATE,true));
//        txtBackwardPlayback.setText(sharedPreferences.getString("backward_jumper_time", "10"));
//        txtForwardPlayback.setText(sharedPreferences.getString("forward_jumper_time", "10"));

        txtBackwardPlayback.setValue(sharedPreferences.getInt("backward_jumper_time", 10));
        txtForwardPlayback.setValue(sharedPreferences.getInt("forward_jumper_time", 10));

        addListeners();
        setOnClickForUpcomingButtons(btnChangeLanguage, btnWifiShare, btnResetSettings, btnSendFeedback, btnUpdate);
        return view;
    }

    public void setOnClickForUpcomingButtons(TextView... buttons) {
        for (TextView button : buttons) button.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UnderMaintenanceActivity.class));
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        });
    }

    private void addListeners() {
        btnChangeTheme.setOnClickListener(view1 -> {
            Intent themeActivityIntent = new Intent(getActivity(), ThemeActivity.class);
            themeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(themeActivityIntent);
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        });
//        txtBackwardPlayback.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                editor = sharedPreferences.edit();
//                editor.putString("backward_jumper_time", txtBackwardPlayback.getText().toString());
//                editor.apply();
//            }
//        });
//        txtForwardPlayback.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                editor = sharedPreferences.edit();
//                editor.putString("forward_jumper_time", txtForwardPlayback.getText().toString());
//                editor.apply();
//            }
//        });

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
        btnAbout.setOnClickListener(v -> startActivity(new Intent(getActivity(), AboutActivity.class)));
        switchBackgroundPlayback.setOnCheckedChangeListener((compoundButton, b) -> {
            editor = sharedPreferences.edit();
            editor.putBoolean(BACKGROUND_PLAYBACK_STATE, b);
            editor.apply();
        });

        btnVideoRecordedClips.setOnClickListener(v -> {
            Snackbar snack = Snackbar.make(btnMyAccount,  "Olosh Clips are being saved on\n\n/storage/emulated/0/DCIM/Olosh Player/Olosh Clips/", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("CLOSE", v1 -> {});
            snack.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
            ((TextView) snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setLines(4);
            snack.show();
        });

        btnVideoSnaps.setOnClickListener(v -> {
            Snackbar snack = Snackbar.make(btnMyAccount,  "Olosh Clips are being saved on\n\n/storage/emulated/0/DCIM/Olosh Player/Olosh Snaps/", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("CLOSE", v1 -> {});
            snack.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
            ((TextView) snack.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setLines(4);
            snack.show();
        });

        btnPlaylist.setOnClickListener(v -> {
            Intent playlistChooserIntent = new Intent(getActivity(), PlaylistChooserActivity.class);
            playlistChooserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(playlistChooserIntent);
            getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
        });

        btnMyAccount.setOnClickListener(v -> {
            Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            getActivity().overridePendingTransition(R.anim.from_up, R.anim.to_down);
        });

        btnExit.setOnClickListener(v -> System.exit(0));
    }
}

