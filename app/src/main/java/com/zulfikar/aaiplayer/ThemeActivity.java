package com.zulfikar.aaiplayer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.jetbrains.annotations.NotNull;

public class ThemeActivity extends AppCompatActivity implements ActivityUtility {

//    Button tasniaThemeBtn, rimiThemeBtn, soulmateThemeBtn, inlawsThemeBtn, aaiThemeBtn, bangladeshBtn, saudiArabiaTheme, japanTheme;
    CardView btnDefaultTheme, btnTasniaTheme, btnRimiTheme, btnSoulmateTheme, btnInlawsTheme, btnBangladeshTheme, btnSaudiTheme, btnJapanTheme;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ScrollView layoutButton;

    private final String TAG = "THEME_ACTIVITY_DEBUG";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scroll_y", layoutButton.getScrollY());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        if (savedInstanceState == null) savedInstanceState = getIntent().getBundleExtra("saved_state");
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Theme.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        /*
        tasniaThemeBtn = findViewById(R.id.tasniaThemeBtn);
        rimiThemeBtn = findViewById(R.id.rimiThemeBtn);
        soulmateThemeBtn = findViewById(R.id.soulmateThemeBtn);
        inlawsThemeBtn = findViewById(R.id.inlawsThemeBtn);
        aaiThemeBtn = findViewById(R.id.aaiThemeBtn);
        bangladeshBtn = findViewById(R.id.bangladeshThemeBtn);
        saudiArabiaTheme = findViewById(R.id.saudiArabiaThemeBtn);
        japanTheme = findViewById(R.id.japanThemeBtn);
        layoutButton = findViewById(R.id.themeButtonOuterLayoutTA);

        changeTheme(tasniaThemeBtn, R.style.TasniaTheme);
        changeTheme(rimiThemeBtn, R.style.RimiTheme);
        changeTheme(soulmateThemeBtn, R.style.SoulMateTheme);
        changeTheme(inlawsThemeBtn, R.style.InLawsTheme);
        changeTheme(aaiThemeBtn, R.style.AppTheme);
        changeTheme(bangladeshBtn, R.style.BangladeshTheme);
        changeTheme(saudiArabiaTheme, R.style.SaudiArabiaTheme);
        changeTheme(japanTheme, R.style.JapanTheme);
        */

        btnDefaultTheme = findViewById(R.id.btnDefaultTheme);
        btnTasniaTheme = findViewById(R.id.btnTasniaTheme);
        btnRimiTheme = findViewById(R.id.btnRimiTheme);
        btnSoulmateTheme = findViewById(R.id.btnSoulmateTheme);
        btnInlawsTheme = findViewById(R.id.btnInlawsTheme);
        btnBangladeshTheme = findViewById(R.id.btnBangladeshTheme);
        btnSaudiTheme = findViewById(R.id.btnSaudiTheme);
        btnJapanTheme = findViewById(R.id.btnJapanTheme);
        layoutButton = findViewById(R.id.themeButtonOuterLayoutTA);

        changeTheme(btnDefaultTheme, R.style.AppTheme);
        changeTheme(btnTasniaTheme, R.style.TasniaTheme);
        changeTheme(btnRimiTheme, R.style.RimiTheme);
        changeTheme(btnSoulmateTheme, R.style.SoulMateTheme);
        changeTheme(btnInlawsTheme, R.style.InLawsTheme);
        changeTheme(btnBangladeshTheme, R.style.BangladeshTheme);
        changeTheme(btnSaudiTheme, R.style.SaudiArabiaTheme);
        changeTheme(btnJapanTheme, R.style.JapanTheme);

        if (savedInstanceState != null) {
            int y = savedInstanceState.getInt("scroll_y");
            layoutButton.post(() -> layoutButton.scrollTo(0, y));
        }
    }

    private void changeTheme(@NotNull Button themeBtn, int themeId) {
        if (themeId == Theme.currentThemeId) return;

        sharedPreferences = getSharedPreferences(Theme.PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        themeBtn.setOnClickListener(v -> {
            editor.putInt(Theme.PREFS_THEME_TAS, Theme.currentThemeId = themeId);
            editor.apply();

            Theme.recreate(this, this);
        });
    }

    private void changeTheme(@NotNull CardView themeBtn, int themeId) {
        sharedPreferences = getSharedPreferences(Theme.PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        themeBtn.setOnClickListener(v -> {
            if (themeId == Theme.currentThemeId) return;

            editor.putInt(Theme.PREFS_THEME_TAS, Theme.currentThemeId = themeId);
            editor.apply();

            Theme.recreate(this, this);
        });
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        onSaveInstanceState(bundle);
    }
}
