package com.zulfikar.aaiplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

public class Theme {

    public static final String PREFS_NAME = "prefs";
    public static final String PREFS_THEME_TAS = "testTheme";
    public static int currentThemeId;

    public static int applyTheme(@NotNull Activity activity){
        int themeId = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(PREFS_THEME_TAS, R.style.AppTheme);

        activity.setTheme(themeId);
        return Theme.currentThemeId = themeId;
    }

    public  static void recreate(Activity activity, ActivityUtility utility) {
        Bundle bundle = new Bundle();
        utility.saveInstanceState(bundle);
        Intent intent = new Intent(activity, activity.getClass());
        intent.putExtra("saved_state", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
