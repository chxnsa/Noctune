package com.noctune.app.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.noctune.app.R;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private boolean isDarkMode;

    private static final String KEY_LAST_TAB = "last_tab";

    private TextView tvModeLight, tvModeDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("noctune_prefs", MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("dark_mode", true);
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== TOGGLE TEMA — Square LIGHT/DARK =====
        tvModeLight = findViewById(R.id.tv_mode_light);
        tvModeDark = findViewById(R.id.tv_mode_dark);

        updateToggleUI();

        tvModeLight.setOnClickListener(v -> {
            if (isDarkMode) { // hanya proses kalau beda dari current
                isDarkMode = false;
                saveThemeAndRecreate();
            }
        });

        tvModeDark.setOnClickListener(v -> {
            if (!isDarkMode) {
                isDarkMode = true;
                saveThemeAndRecreate();
            }
        });

        // ===== BURGER MENU =====
        findViewById(R.id.tv_menu).setOnClickListener(v -> {
            Toast.makeText(this,
                    "NOCTUNE — Music Discovery App",
                    Toast.LENGTH_SHORT).show();
        });

        // ===== SEARCH ICON =====
        findViewById(R.id.tv_search_icon).setOnClickListener(v -> {
            BottomNavigationView nav = findViewById(R.id.bottom_navigation);
            if (nav.getSelectedItemId() != R.id.nav_home) {
                nav.setSelectedItemId(R.id.nav_home);
            }
            Toast.makeText(this,
                    "Use the search bar to find tracks",
                    Toast.LENGTH_SHORT).show();
        });

        // ===== BOTTOM NAVIGATION =====
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_explore) {
                selectedFragment = new ExploreFragment();
            } else if (id == R.id.nav_faves) {
                selectedFragment = new FavesFragment();
            } else if (id == R.id.nav_charts) {
                selectedFragment = new ChartsFragment();
            }

            return loadFragment(selectedFragment);
        });

        // ===== RESTORE TAB SETELAH RECREATE =====
        int lastTabId = prefs.getInt(KEY_LAST_TAB, R.id.nav_home);
        if (lastTabId != R.id.nav_home) {
            bottomNav.setSelectedItemId(lastTabId);
            prefs.edit().putInt(KEY_LAST_TAB, R.id.nav_home).apply();
        } else {
            loadFragment(new HomeFragment());
        }
    }

    // Update warna toggle sesuai mode aktif
    private void updateToggleUI() {
        if (isDarkMode) {
            // DARK aktif → highlight kuning
            tvModeDark.setBackgroundColor(Color.parseColor("#FFD600"));
            tvModeDark.setTextColor(Color.BLACK);

            tvModeLight.setBackgroundColor(Color.TRANSPARENT);
            tvModeLight.setTextColor(Color.parseColor("#666666"));
        } else {
            // LIGHT aktif → highlight kuning
            tvModeLight.setBackgroundColor(Color.parseColor("#FFD600"));
            tvModeLight.setTextColor(Color.BLACK);

            tvModeDark.setBackgroundColor(Color.TRANSPARENT);
            tvModeDark.setTextColor(Color.parseColor("#666666"));
        }
    }

    private void saveThemeAndRecreate() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dark_mode", isDarkMode);
        editor.putInt(KEY_LAST_TAB, getCurrentTabId());
        editor.apply();
        recreate();
    }

    private int getCurrentTabId() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        return nav.getSelectedItemId();
    }

    private void applyTheme() {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}