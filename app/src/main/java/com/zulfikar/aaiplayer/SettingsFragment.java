package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    EditText txtForwardPlayback, txtBackwardPlayback;
    TextView btnMyAccount, btnPlaylist, btnChangeTheme, btnWifiShare, btnVideoSnaps, btnVideoRecordedClips, btnResetSettings, btnSendFeedback, btnExit;

    private static final String PLAYBACK_JUMPER_PREFERENCE = "playback_jumper_preferences";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = getActivity().getSharedPreferences(PLAYBACK_JUMPER_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        btnChangeTheme = view.findViewById(R.id.btnChangeThemeFS);
        btnMyAccount = view.findViewById(R.id.btnMyAccountFS);
        btnPlaylist = view.findViewById(R.id.btnPlaylistFS);
        btnWifiShare = view.findViewById(R.id.btnWifiShareFS);
        btnVideoSnaps = view.findViewById(R.id.btnVideoSnapsFS);
        btnVideoRecordedClips = view.findViewById(R.id.btnVideoRecordedClipsFS);
        btnResetSettings = view.findViewById(R.id.btnResetSettingsFS);
        btnSendFeedback = view.findViewById(R.id.btnFeedbackFS);
        btnExit = view.findViewById(R.id.btnExitFS);
        txtBackwardPlayback = view.findViewById(R.id.txtBackwardPlaybackFS);
        txtForwardPlayback = view.findViewById(R.id.txtForwardPlaybackFS);
        txtBackwardPlayback.setText(sharedPreferences.getString("backward_jumper_time", "10"));
        txtForwardPlayback.setText(sharedPreferences.getString("forward_jumper_time", "10"));
        btnChangeTheme.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), ThemeActivity.class)));
        txtBackwardPlayback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("backward_jumper_time", txtBackwardPlayback.getText().toString());
                editor.apply();
            }
        });
        txtForwardPlayback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("forward_jumper_time", txtForwardPlayback.getText().toString());
                editor.apply();
            }
        });
        setOnClickForUpcomingButtons(btnMyAccount, btnPlaylist, btnWifiShare, btnVideoSnaps, btnVideoRecordedClips, btnResetSettings, btnSendFeedback, btnExit);
        return view;
    }

    public void setOnClickForUpcomingButtons(TextView... buttons) {
        for (TextView button : buttons) button.setOnClickListener(v -> startActivity(new Intent(getActivity(), UnderMaintenanceActivity.class)));
    }
}

