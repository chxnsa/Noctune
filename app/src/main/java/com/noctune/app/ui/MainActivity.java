package com.noctune.app.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.noctune.app.R;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply tema sebelum setContentView
        // Sesuai modul SharedPreferences
        prefs = getSharedPreferences("noctune_prefs", MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("dark_mode", true);
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toggle tema
        TextView tvThemeToggle = findViewById(R.id.tv_theme_toggle);

        // Mengubah teks indikator konsol sesuai status tema, bukan pakai emotikon bulat
        tvThemeToggle.setText(isDarkMode ? "[ MODE: DARK ]" : "[ MODE: LIGHT ]");

        tvThemeToggle.setOnClickListener(v -> {
            // Simpan preferensi tema — sesuai modul SharedPreferences
            isDarkMode = !isDarkMode;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isDarkMode);
            editor.apply();

            // Restart activity agar tema berubah
            recreate();
        });

        // Setup Bottom Navigation
        loadFragment(new HomeFragment());

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
    }

    private void applyTheme() {
        if (isDarkMode) {
            setTheme(R.style.Theme_Noctune_Dark);
        } else {
            setTheme(R.style.Theme_Noctune_Light);
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