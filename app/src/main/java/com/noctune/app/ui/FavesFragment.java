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
import android.widget.ImageView;
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
import android.widget.Button;
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

    public interface OnConfirmListener {
        void onConfirmed();
    }

    private android.net.Uri selectedImageUri = null;
    private ImageView ivCoverPreview; // Akses global agar bisa diganti setelah user memilih gambar

    // Launcher untuk membuka Galeri Gambar kustom
    private final androidx.activity.result.ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedImageUri = uri;
                            if (ivCoverPreview != null) {
                                ivCoverPreview.setPadding(0, 0, 0, 0); // Hilangkan padding icon bawaan
                                ivCoverPreview.setImageURI(uri); // Tampilkan gambar pilihan user
                            }
                        }
                    });

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

        // Ambil TextView secara paksa berdasarkan index anak ke-0 di dalam LinearLayout Tab
        TextView tvTabFaves = (TextView) tabFaves.getChildAt(0);
        TextView tvTabPlaylists = (TextView) tabPlaylists.getChildAt(0);

        if (showFaves) {
            viewFaves.setVisibility(View.VISIBLE);
            viewPlaylists.setVisibility(View.GONE);

            // Set Background Tab
            tabFaves.setBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.brand_yellow));
            tabPlaylists.setBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.bg_surface));

            // Set Warna Teks Tab (Aktif = Hitam, Tidak Aktif = Putih/Text Primary bawaan)
            if (tvTabFaves != null) tvTabFaves.setTextColor(android.graphics.Color.BLACK);
            if (tvTabPlaylists != null) tvTabPlaylists.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary));

            loadFavorites();
        } else {
            viewFaves.setVisibility(View.GONE);
            viewPlaylists.setVisibility(View.VISIBLE);

            // Set Background Tab
            tabFaves.setBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.bg_surface));
            tabPlaylists.setBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.brand_yellow));

            // Set Warna Teks Tab (Tidak Aktif = Putih/Text Primary bawaan, Aktif = Hitam)
            if (tvTabFaves != null) tvTabFaves.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary));
            if (tvTabPlaylists != null) tvTabPlaylists.setTextColor(android.graphics.Color.BLACK);

            loadPlaylists();
        }
    }

    private void showCreatePlaylistDialog() {
        if (getActivity() == null) return;

        // Reset URI gambar setiap kali dialog baru dibuka
        selectedImageUri = null;

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_playlist, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Init komponen kustom baru
        ivCoverPreview = dialogView.findViewById(R.id.iv_playlist_cover_preview);
        TextView btnPickCover = dialogView.findViewById(R.id.btn_pick_cover);
        EditText etPlaylistName = dialogView.findViewById(R.id.et_playlist_name);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnCreate = dialogView.findViewById(R.id.btn_create);

        // Aksi saat tombol [CHOOSE_COVER_IMAGE] ditekan
        btnPickCover.setOnClickListener(v -> {
            // Membuka galeri sistem dan menyaring hanya file gambar saja
            pickImageLauncher.launch("image/*");
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnCreate.setOnClickListener(v -> {
            String name = etPlaylistName.getText().toString().trim();
            if (!name.isEmpty()) {
                // Ambil string URI jika ada, jika tidak kosongkan
                String coverStr = (selectedImageUri != null) ? selectedImageUri.toString() : "";
                createPlaylist(name, coverStr);
                dialog.dismiss();
            } else {
                etPlaylistName.setHintTextColor(getResources().getColor(R.color.brand_red));
                etPlaylistName.setHint("NAME_REQUIRED!");
            }
        });

        dialog.show();
    }

    private void createPlaylist(String name, String coverUri) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            musicHelper.open();

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.PlaylistColumns.PLAYLIST_NAME, name);
            values.put(DatabaseContract.PlaylistColumns.CREATED_AT,
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()));
            values.put(DatabaseContract.PlaylistColumns.PLAYLIST_COVER, coverUri); // Simpan URI Gambar ke SQLite

            musicHelper.insertPlaylist(values);

            handler.post(() -> {
                if (isAdded()) {
                    loadPlaylists();
                }
            });
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
            if (cursor != null) cursor.close();

            handler.post(() -> {
                if (!isAdded()) return; // Jika fragment sudah lepas, hentikan manipulasi UI

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
            if (cursor != null) cursor.close();

            // Hitung jumlah track per playlist
            for (Playlist p : playlists) {
                int count = musicHelper.countTracksInPlaylist(String.valueOf(p.getId()));
                p.setTrackCount(count);
            }

            handler.post(() -> {
                if (!isAdded()) return; // Proteksi crash ketiadaan konteks UI

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

    private void showConfirmDeleteDialog(String message, OnConfirmListener listener) {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_delete, null);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvMessage = dialogView.findViewById(R.id.tv_confirm_message);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_delete);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_delete);

        tvMessage.setText(message.toUpperCase());
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmed();
            }
            dialog.dismiss();
        });

        dialog.show();
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
        // HAPUS MUSICHELPER.CLOSE() DI SINI AGAR DATABASE TIDAK MATI MENDADAK SAAT PERPINDAHAN TAB/MODE
    }
}