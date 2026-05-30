package com.noctune.app.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.noctune.app.R;
import com.noctune.app.database.DatabaseContract;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.Track;
import com.squareup.picasso.Picasso;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private MusicHelper musicHelper;
    private Track track;
    private Button btnFave;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Sembunyikan action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Terima data Track dari Intent — sesuai syarat Intent tugas
        track = getIntent().getParcelableExtra("track");

        // Init views
        ImageView ivImage = findViewById(R.id.iv_detail_image);
        TextView tvTrackName = findViewById(R.id.tv_detail_track_name);
        TextView tvArtistName = findViewById(R.id.tv_detail_artist_name);
        TextView tvListeners = findViewById(R.id.tv_listeners);
        TextView tvPlaycount = findViewById(R.id.tv_detail_playcount);
        TextView tvDuration = findViewById(R.id.tv_duration);
        TextView tvBack = findViewById(R.id.tv_back);
        btnFave = findViewById(R.id.btn_fave);

        // Init SQLite — sesuai modul
        musicHelper = MusicHelper.getInstance(getApplicationContext());
        musicHelper.open();

        // Isi data ke views
        if (track != null) {
            tvTrackName.setText(track.getName());

            if (track.getArtist() != null) {
                tvArtistName.setText(track.getArtist().getName());
            }

            tvListeners.setText(formatCount(track.getListeners()));
            tvPlaycount.setText(formatCount(track.getPlaycount()));
            tvDuration.setText(formatDuration(track.getDuration()));

            // Load gambar dengan Picasso
            String imageUrl = track.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivImage);
            }

            // Cek apakah sudah difavoritkan
            checkFavoriteStatus();
        }

        // Tombol back
        tvBack.setOnClickListener(v -> finish());

        // Tombol favorit
        btnFave.setOnClickListener(v -> toggleFavorite());
    }

    private void checkFavoriteStatus() {
        // Cek di background thread — sesuai syarat Background Thread tugas
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String artistName = track.getArtist() != null ?
                    track.getArtist().getName() : "";
            isFavorite = musicHelper.isFavorite(track.getName(), artistName);

            handler.post(() -> updateFaveButton());
        });
    }

    private void toggleFavorite() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (isFavorite) {
                // Sudah favorit → hapus
                // Tidak langsung pakai deleteById karena kita tidak punya _id
                // Jadi kita query dulu
                android.database.Cursor cursor = musicHelper.queryAll();
                int idToDelete = -1;
                String artistName = track.getArtist() != null ?
                        track.getArtist().getName() : "";

                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(
                            DatabaseContract.FavColumns.TRACK_NAME));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(
                            DatabaseContract.FavColumns.ARTIST_NAME));

                    if (name.equals(track.getName()) && artist.equals(artistName)) {
                        idToDelete = cursor.getInt(cursor.getColumnIndexOrThrow(
                                DatabaseContract.FavColumns._ID));
                        break;
                    }
                }
                cursor.close();

                if (idToDelete != -1) {
                    musicHelper.deleteById(String.valueOf(idToDelete));
                    isFavorite = false;
                }

            } else {
                // Belum favorit → simpan
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.FavColumns.TRACK_NAME, track.getName());
                values.put(DatabaseContract.FavColumns.ARTIST_NAME,
                        track.getArtist() != null ? track.getArtist().getName() : "");
                values.put(DatabaseContract.FavColumns.PLAYCOUNT, track.getPlaycount());
                values.put(DatabaseContract.FavColumns.LISTENERS, track.getListeners());
                values.put(DatabaseContract.FavColumns.DURATION, track.getDuration());
                values.put(DatabaseContract.FavColumns.IMAGE_URL, track.getImageUrl());

                long result = musicHelper.insert(values);
                if (result > 0) isFavorite = true;
            }

            handler.post(() -> {
                updateFaveButton();
                Toast.makeText(DetailActivity.this,
                        isFavorite ? "Added to Favorites!" : "Removed from Favorites",
                        Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void updateFaveButton() {
        if (isFavorite) {
            btnFave.setText("♥ SAVED TO FAVORITES");
            btnFave.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#E63329")));
        } else {
            btnFave.setText("♥ SAVE TO FAVORITES");
            btnFave.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#FFD600")));
        }
    }

    // Format angka
    private String formatCount(String countStr) {
        try {
            long count = Long.parseLong(countStr);
            if (count >= 1_000_000) {
                return String.format("%.1fM", count / 1_000_000.0);
            } else if (count >= 1_000) {
                return String.format("%.1fK", count / 1_000.0);
            }
            return String.valueOf(count);
        } catch (Exception e) {
            return countStr;
        }
    }

    // Format durasi detik → menit:detik
    private String formatDuration(String durationStr) {
        try {
            int seconds = Integer.parseInt(durationStr);
            int minutes = seconds / 60;
            int secs = seconds % 60;
            return String.format("%d:%02d", minutes, secs);
        } catch (Exception e) {
            return "0:00";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicHelper != null) {
            musicHelper.close();
        }
    }
}