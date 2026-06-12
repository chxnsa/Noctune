package com.noctune.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.noctune.app.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply dark/light theme sebelum setContentView
        applyTheme();
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splash);

        // Sembunyikan action bar di splash
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Pakai Handler untuk delay — sesuai materi Background Thread
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pindah ke MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // tutup SplashActivity agar tidak bisa back
            }
        }, SPLASH_DELAY);
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("noctune_prefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", true);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}