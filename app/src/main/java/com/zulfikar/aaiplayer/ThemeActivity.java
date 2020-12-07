package com.zulfikar.aaiplayer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
//import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class ThemeActivity extends AppCompatActivity {

    int themeId;
    Button toggle;

    private static final String PREFS_NAME = "prefs", PREFS_THEME_TAS = "testTheme";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        ThemeActivity.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        toggle = findViewById(R.id.btnChangeTheme);
        toggle.setOnClickListener(view -> toggleTheme());
    }

    private void toggleTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        themeId = preferences.getInt(PREFS_THEME_TAS, R.style.AppTheme);

        if (themeId == R.style.AppTheme) themeId = R.style.TasniaTheme;
        else if (themeId == R.style.TasniaTheme) themeId = R.style.RimiTheme;
        else if (themeId == R.style.RimiTheme) themeId = R.style.AppTheme;
        else themeId = R.style.AppTheme;

        editor.putInt(PREFS_THEME_TAS, themeId);
        editor.apply();
        applyTheme(this);
        recreate();
    }

    public static void applyTheme(Activity activity){
        SharedPreferences preferences = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int themeId = preferences.getInt(PREFS_THEME_TAS, R.style.AppTheme);

        activity.setTheme(themeId);
    }

}
