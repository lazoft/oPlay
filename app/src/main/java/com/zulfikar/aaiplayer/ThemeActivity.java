package com.zulfikar.aaiplayer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
//import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class ThemeActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "prefs";
    public static final String PREFS_THEME_TAS = "testTheme";
    int themeId;
    Button toggle;



    protected void onCreate(Bundle savedInstanceState){
        ThemeActivity.applyTheme(this);
        //SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //themeId = preferences.getInt(PREF_DARK_THEME, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        toggle = findViewById(R.id.tasniaThemeBtn);
        toggle.setOnClickListener(view -> toggleTheme());
    }


    private void toggleTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        this.themeId = preferences.getInt(PREFS_THEME_TAS, R.style.AppTheme);

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
        //Log.d("Themeid", themeId+": "+R.style.TasniaTheme+": "+R.style.AppTheme);
        activity.setTheme(themeId);
    }

}
