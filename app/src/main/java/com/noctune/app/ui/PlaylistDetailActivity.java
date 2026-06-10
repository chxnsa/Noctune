package com.noctune.app.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.PlaylistTrackAdapter;
import com.noctune.app.database.MappingHelper;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.model.Playlist;
import com.noctune.app.model.PlaylistTrack;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistDetailActivity extends AppCompatActivity
        implements PlaylistTrackAdapter.OnTrackRemovedListener {

    private RecyclerView rvTracks;
    private PlaylistTrackAdapter adapter;
    private TextView tvEmpty, tvInfo, tvTitle;
    private MusicHelper musicHelper;
    private Playlist playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        playlist = getIntent().getParcelableExtra("playlist");

        tvTitle = findViewById(R.id.tv_playlist_title);
        tvInfo = findViewById(R.id.tv_playlist_info);
        tvEmpty = findViewById(R.id.tv_playlist_empty);
        rvTracks = findViewById(R.id.rv_playlist_tracks);

        TextView tvBack = findViewById(R.id.tv_back_playlist);
        tvBack.setOnClickListener(v -> finish());

        if (playlist != null) {
            tvTitle.setText(playlist.getName().toUpperCase());
        }

        rvTracks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistTrackAdapter(this, this);
        rvTracks.setAdapter(adapter);

        musicHelper = MusicHelper.getInstance(getApplicationContext());

        loadTracks();
    }

    private void loadTracks() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            musicHelper.open();
            Cursor cursor = musicHelper.queryTracksByPlaylist(
                    String.valueOf(playlist.getId()));
            ArrayList<PlaylistTrack> tracks =
                    MappingHelper.mapCursorToPlaylistTracks(cursor);
            cursor.close();

            handler.post(() -> {
                tvInfo.setText(tracks.size() + " TRACKS IN PLAYLIST");

                if (tracks.size() > 0) {
                    adapter.setTracks(tracks);
                    tvEmpty.setVisibility(View.GONE);
                    rvTracks.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvTracks.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public void onTrackRemoved() {
        // Refresh info count saat track dihapus
        loadTracks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicHelper != null) musicHelper.close();
    }
}