package com.zulfikar.aaiplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UnderMaintenanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Theme.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_maintainance);
    }
}