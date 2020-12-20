package com.zulfikar.aaiplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    LinearLayout rowSohanRockstar;
    ImageView oloshLogo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        rowSohanRockstar = findViewById(R.id.rowSohanRockStarAA);
        oloshLogo = findViewById(R.id.imgOloshLogoAA);

        rowSohanRockstar.setVisibility(View.GONE);
        oloshLogo.setOnClickListener(new View.OnClickListener() {
            int clickCount;
            long lastClick;
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastClick < 1000) {
                    clickCount++;
                } else {
                    clickCount = 0;
                }
                lastClick = System.currentTimeMillis();
                if (clickCount == 5) {
                    if (rowSohanRockstar.getVisibility() == View.GONE) {
                        rowSohanRockstar.setVisibility(View.VISIBLE);
                        Toast.makeText(AboutActivity.this, "Developer mode activated", Toast.LENGTH_SHORT).show();
                    } else {
                        rowSohanRockstar.setVisibility(View.GONE);
                        Toast.makeText(AboutActivity.this, "Developer mode deactivated", Toast.LENGTH_SHORT).show();
                    }
                    clickCount = 0;
                }
            }
        });
    }
}