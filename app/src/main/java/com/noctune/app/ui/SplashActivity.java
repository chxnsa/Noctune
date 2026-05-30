package com.noctune.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.noctune.app.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply dark/light theme sebelum setContentView
        applyTheme();

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
        // Baca preferensi tema dari SharedPreferences
        // Sesuai materi SharedPreferences di modul
        SharedPreferences prefs = getSharedPreferences("noctune_prefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", true); // default dark

        if (isDarkMode) {
            setTheme(R.style.Theme_Noctune_Dark);
        } else {
            setTheme(R.style.Theme_Noctune_Light);
        }
    }
}