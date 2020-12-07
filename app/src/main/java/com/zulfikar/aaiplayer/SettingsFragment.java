package com.zulfikar.aaiplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    //RecyclerView recyclerView;
    TextView button;
//    private static final String PREFS_NAME = "prefs";
//    private static final String PREF_DARK_THEME = "dark_theme";
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//        View view = super.onCreateView(inflater, container, savedInstanceState);
        button = view.findViewById(R.id.themeBtn);
        button.setOnClickListener(view1 -> openThemeActivity());
        return view;
    }

    public void openThemeActivity() {
        Intent intent = new Intent(getActivity(), ThemeActivity.class);
        startActivity(intent);
    }

//    private void toggleTheme(boolean darkTheme) {
//        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
//        editor.putBoolean(PREF_DARK_THEME, darkTheme);
//        editor.apply();
//
//        Intent intent = getIntent();
//        finish();
//
//        startActivity(intent);
}