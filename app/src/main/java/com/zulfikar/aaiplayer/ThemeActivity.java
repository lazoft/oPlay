package com.zulfikar.aaiplayer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
//import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class ThemeActivity extends AppCompatActivity {

    int themeId;

    Button btnChangeTheme;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_THEME_TAS = "testTheme";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        ThemeActivity.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        btnChangeTheme = findViewById(R.id.btnChangeTheme);
        btnChangeTheme.setOnClickListener(view -> toggleTheme());
    }

    private void toggleTheme() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        themeId = sharedPreferences.getInt(PREFS_THEME_TAS, R.style.AppTheme);

        if (themeId == R.style.AppTheme) themeId = R.style.TasniaTheme;
        else if (themeId == R.style.TasniaTheme) themeId = R.style.RimiTheme;
        else if (themeId == R.style.RimiTheme) themeId = R.style.SoulMateTheme;
        else if (themeId == R.style.SoulMateTheme) themeId = R.style.InLawsTheme;
        else if (themeId == R.style.InLawsTheme) themeId = R.style.AppTheme;
        else themeId = R.style.AppTheme;

        editor.putInt(PREFS_THEME_TAS, themeId);
        editor.apply();

        applyTheme(this);
        recreate();
    }

    public static void applyTheme(Activity activity){
        int themeId = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(PREFS_THEME_TAS, R.style.AppTheme);

        activity.setTheme(themeId);
    }

}
