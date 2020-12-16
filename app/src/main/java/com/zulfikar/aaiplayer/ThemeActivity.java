package com.zulfikar.aaiplayer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class ThemeActivity extends AppCompatActivity implements ActivityUtility {

    Button tasniaThemeBtn, rimiThemeBtn, soulmateThemeBtn, inlawsThemeBtn, aaiThemeBtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Theme.applyTheme(this);
        super.onCreate(savedInstanceState != null ? savedInstanceState : getIntent().getBundleExtra("saved_state"));
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
        sharedPreferences = getSharedPreferences(Theme.PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        themeBtn.setOnClickListener(v -> {
            editor.putInt(Theme.PREFS_THEME_TAS, themeId);
            editor.apply();

            Theme.recreate(this, this);
        });
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        onSaveInstanceState(bundle);
    }
}
