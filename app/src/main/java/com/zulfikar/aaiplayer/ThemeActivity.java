package com.zulfikar.aaiplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
//import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class ThemeActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    protected void onCreate(Bundle savedInstanceState){
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        Button toggle = findViewById(R.id.tasniaThemeBtn);
        if(useDarkTheme) {
            setTheme(R.style.TasniaTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        //Switch toggle = (Switch)findViewById()

    }

    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent intent = getIntent();
        finish();

        startActivity(intent);
    }

}
