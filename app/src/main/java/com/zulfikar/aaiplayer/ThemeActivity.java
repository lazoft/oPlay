package com.zulfikar.aaiplayer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class ThemeActivity extends AppCompatActivity {

    Button tasniaThemeBtn, rimiThemeBtn, soulmateThemeBtn, inlawsThemeBtn, aaiThemeBtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_THEME_TAS = "testTheme";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        ThemeActivity.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        tasniaThemeBtn = findViewById(R.id.tasniaThemeBtn);
        rimiThemeBtn = findViewById(R.id.rimiThemeBtn);
        soulmateThemeBtn = findViewById(R.id.soulmateThemeBtn);
        inlawsThemeBtn = findViewById(R.id.inlawsThemeBtn);
        aaiThemeBtn = findViewById(R.id.aaiThemeBtn);

        changeTheme(tasniaThemeBtn, R.style.TasniaTheme);
        changeTheme(rimiThemeBtn, R.style.RimiTheme);
        changeTheme(soulmateThemeBtn, R.style.SoulMateTheme);
        changeTheme(inlawsThemeBtn, R.style.InLawsTheme);
        changeTheme(aaiThemeBtn, R.style.AppTheme);
    }

    private void changeTheme(@NotNull Button themeBtn, int themeId) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        themeBtn.setOnClickListener(v -> {
            editor.putInt(PREFS_THEME_TAS, themeId);
            editor.apply();
            recreate();
        });
    }

    public static int applyTheme(@NotNull Activity activity){
        int themeId = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(PREFS_THEME_TAS, R.style.AppTheme);

        activity.setTheme(themeId);
        return Theme.currentThemeId = themeId;
    }

}
