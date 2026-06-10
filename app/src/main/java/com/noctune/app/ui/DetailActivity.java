package com.noctune.app.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.noctune.app.R;
import com.noctune.app.database.DatabaseContract;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.LyricsResponse;
import com.noctune.app.model.Playlist;
import com.noctune.app.model.Track;
import com.noctune.app.network.LyricsClient;
import com.noctune.app.network.LyricsService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private MusicHelper musicHelper;
    private Track track;
    private Button btnFave;
    private boolean isFavorite = false;

    // Lyrics
    private TextView tvLyrics;
    private ProgressBar pbLyrics;
    private LyricsService lyricsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
        Button btnYoutube = findViewById(R.id.btn_youtube);
        Button btnAddPlaylist = findViewById(R.id.btn_add_playlist);
        btnAddPlaylist.setOnClickListener(v -> showAddToPlaylistDialog());
        tvLyrics = findViewById(R.id.tv_lyrics);
        pbLyrics = findViewById(R.id.pb_lyrics);

        // Init SQLite
        musicHelper = MusicHelper.getInstance(getApplicationContext());
        musicHelper.open();

        // Init Lyrics API
        lyricsService = LyricsClient.getClient().create(LyricsService.class);

        if (track != null) {
            tvTrackName.setText(track.getName());

            if (track.getArtist() != null) {
                tvArtistName.setText(track.getArtist().getName());
            }

            tvListeners.setText(formatCount(track.getListeners()));
            tvPlaycount.setText(formatCount(track.getPlaycount()));
            tvDuration.setText(formatDuration(track.getDuration()));

            String imageUrl = track.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivImage);
            }

            checkFavoriteStatus();

            // Load lirik otomatis
            loadLyrics();
        }

        // Tombol back
        tvBack.setOnClickListener(v -> finish());

        // Tombol favorit
        btnFave.setOnClickListener(v -> toggleFavorite());

        // Tombol YouTube — buka browser
        btnYoutube.setOnClickListener(v -> openYoutube());
    }

    private void loadLyrics() {
        if (track.getArtist() == null) {
            tvLyrics.setText("Artist info not available");
            return;
        }

        // Tampilkan loading
        pbLyrics.setVisibility(View.VISIBLE);
        tvLyrics.setText("");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<LyricsResponse> call = lyricsService.getLyrics(
                    track.getArtist().getName(),
                    track.getName()
            );

            call.enqueue(new Callback<LyricsResponse>() {
                @Override
                public void onResponse(@NonNull Call<LyricsResponse> call,
                                       @NonNull Response<LyricsResponse> response) {
                    handler.post(() -> {
                        pbLyrics.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().getLyrics() != null
                                && !response.body().getLyrics().isEmpty()) {
                            tvLyrics.setText(response.body().getLyrics());
                        } else {
                            tvLyrics.setText("Lyrics not found for this track.");
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<LyricsResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        pbLyrics.setVisibility(View.GONE);
                        tvLyrics.setText("Could not load lyrics.\nCheck your connection.");
                    });
                }
            });
        });
    }

    private void openYoutube() {
        if (track == null) return;

        String artistName = track.getArtist() != null ?
                track.getArtist().getName() : "";
        String query = artistName + " " + track.getName();

        // Encode spasi jadi + untuk URL
        String encodedQuery = query.trim().replace(" ", "+");
        String youtubeUrl = "https://www.youtube.com/results?search_query=" + encodedQuery;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
        startActivity(intent);
    }

    // ---- Method lainnya tetap sama seperti sebelumnya ----

    private void checkFavoriteStatus() {
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
            btnFave.setText("♥ SAVED");
            btnFave.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#E63329")));
        } else {
            btnFave.setText("♥ SAVE");
            btnFave.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#FFD600")));
        }
    }

    private String formatCount(String countStr) {
        try {
            long count = Long.parseLong(countStr);
            if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000.0);
            else if (count >= 1_000) return String.format("%.1fK", count / 1_000.0);
            return String.valueOf(count);
        } catch (Exception e) {
            return countStr;
        }
    }

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

    private void showAddToPlaylistDialog() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            musicHelper.open();
            android.database.Cursor cursor = musicHelper.queryAllPlaylists();
            ArrayList<Playlist> playlists =
                    com.noctune.app.database.MappingHelper.mapCursorToPlaylists(cursor);
            cursor.close();

            handler.post(() -> {
                if (playlists.isEmpty()) {
                    Toast.makeText(this,
                            "No playlists yet! Create one in Faves tab.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Buat array nama playlist untuk dialog
                String[] names = new String[playlists.size()];
                for (int i = 0; i < playlists.size(); i++) {
                    names[i] = playlists.get(i).getName();
                }

                new AlertDialog.Builder(this)
                        .setTitle("ADD TO PLAYLIST")
                        .setItems(names, (dialog, which) -> {
                            addTrackToPlaylist(playlists.get(which));
                        })
                        .show();
            });
        });
    }

    private void addTrackToPlaylist(com.noctune.app.model.Playlist playlist) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ContentValues values = new ContentValues();
            values.put(com.noctune.app.database.DatabaseContract
                    .PlaylistTrackColumns.PLAYLIST_ID, playlist.getId());
            values.put(com.noctune.app.database.DatabaseContract
                    .PlaylistTrackColumns.TRACK_NAME, track.getName());
            values.put(com.noctune.app.database.DatabaseContract
                            .PlaylistTrackColumns.ARTIST_NAME,
                    track.getArtist() != null ? track.getArtist().getName() : "");
            values.put(com.noctune.app.database.DatabaseContract
                    .PlaylistTrackColumns.DURATION, track.getDuration());
            values.put(com.noctune.app.database.DatabaseContract
                    .PlaylistTrackColumns.IMAGE_URL, track.getImageUrl());

            long result = musicHelper.insertTrackToPlaylist(values);

            handler.post(() -> {
                if (result > 0) {
                    Toast.makeText(this,
                            "Added to " + playlist.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}