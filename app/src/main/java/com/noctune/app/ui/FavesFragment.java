package com.noctune.app.ui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.FavesAdapter;
import com.noctune.app.adapter.PlaylistAdapter;
import com.noctune.app.database.MappingHelper;
import com.noctune.app.database.MusicHelper;
import com.noctune.app.database.DatabaseContract;
import com.noctune.app.model.FavoriteTrack;
import com.noctune.app.model.Playlist;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavesFragment extends Fragment {

    // Views
    private RecyclerView rvFaves, rvPlaylists;
    private FavesAdapter favesAdapter;
    private PlaylistAdapter playlistAdapter;
    private TextView tvEmpty, tvEmptyPlaylist;
    private LinearLayout tabFaves, tabPlaylists;
    private View viewFaves, viewPlaylists;

    private MusicHelper musicHelper;
    private boolean isFavesTab = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faves, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        rvFaves = view.findViewById(R.id.rv_faves);
        rvPlaylists = view.findViewById(R.id.rv_playlists);
        tvEmpty = view.findViewById(R.id.tv_empty);
        tvEmptyPlaylist = view.findViewById(R.id.tv_empty_playlist);
        tabFaves = view.findViewById(R.id.tab_faves);
        tabPlaylists = view.findViewById(R.id.tab_playlists);
        viewFaves = view.findViewById(R.id.view_faves_content);
        viewPlaylists = view.findViewById(R.id.view_playlists_content);

        TextView btnNewPlaylist = view.findViewById(R.id.btn_new_playlist);

        // Setup RecyclerViews
        rvFaves.setLayoutManager(new LinearLayoutManager(getActivity()));
        favesAdapter = new FavesAdapter(getActivity());
        rvFaves.setAdapter(favesAdapter);

        rvPlaylists.setLayoutManager(new LinearLayoutManager(getActivity()));
        playlistAdapter = new PlaylistAdapter(getActivity());
        rvPlaylists.setAdapter(playlistAdapter);

        musicHelper = MusicHelper.getInstance(getActivity().getApplicationContext());

        // Tab listener
        tabFaves.setOnClickListener(v -> switchTab(true));
        tabPlaylists.setOnClickListener(v -> switchTab(false));

        // Buat playlist baru
        btnNewPlaylist.setOnClickListener(v -> showCreatePlaylistDialog());

        // Default tab
        switchTab(true);
        loadFavorites();
        loadPlaylists();
    }

    private void switchTab(boolean showFaves) {
        isFavesTab = showFaves;

        if (showFaves) {
            viewFaves.setVisibility(View.VISIBLE);
            viewPlaylists.setVisibility(View.GONE);
            tabFaves.setBackgroundColor(0xFFFFD600);
            tabPlaylists.setBackgroundColor(0xFF000000);
        } else {
            viewFaves.setVisibility(View.GONE);
            viewPlaylists.setVisibility(View.VISIBLE);
            tabFaves.setBackgroundColor(0xFF000000);
            tabPlaylists.setBackgroundColor(0xFFFFD600);
            loadPlaylists();
        }
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("NEW PLAYLIST");

        // Input field
        EditText input = new EditText(getActivity());
        input.setHint("Playlist name...");
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        builder.setPositiveButton("CREATE", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                createPlaylist(name);
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createPlaylist(String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            musicHelper.open();

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.PlaylistColumns.PLAYLIST_NAME, name);
            values.put(DatabaseContract.PlaylistColumns.CREATED_AT,
                    new SimpleDateFormat("dd MMM yyyy",
                            Locale.getDefault()).format(new Date()));

            musicHelper.insertPlaylist(values);

            handler.post(() -> loadPlaylists());
        });
    }

    private void loadFavorites() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            musicHelper.open();
            Cursor cursor = musicHelper.queryAll();
            ArrayList<FavoriteTrack> favorites =
                    MappingHelper.mapCursorToArrayList(cursor);
            cursor.close();

            handler.post(() -> {
                if (favorites.size() > 0) {
                    favesAdapter.setFavorites(favorites);
                    tvEmpty.setVisibility(View.GONE);
                    rvFaves.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvFaves.setVisibility(View.GONE);
                }
            });
        });
    }

    private void loadPlaylists() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            musicHelper.open();
            Cursor cursor = musicHelper.queryAllPlaylists();
            ArrayList<Playlist> playlists =
                    MappingHelper.mapCursorToPlaylists(cursor);
            cursor.close();

            // Hitung jumlah track per playlist
            for (Playlist p : playlists) {
                int count = musicHelper.countTracksInPlaylist(
                        String.valueOf(p.getId()));
                p.setTrackCount(count);
            }

            handler.post(() -> {
                if (playlists.size() > 0) {
                    playlistAdapter.setPlaylists(playlists);
                    tvEmptyPlaylist.setVisibility(View.GONE);
                    rvPlaylists.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyPlaylist.setVisibility(View.VISIBLE);
                    rvPlaylists.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
        loadPlaylists();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (musicHelper != null) musicHelper.close();
    }
}