package com.noctune.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.ArtistHorizontalAdapter;
import com.noctune.app.adapter.TrackAdapter;
import com.noctune.app.model.TopArtistsResponse;
import com.noctune.app.model.TopTracksResponse;
import com.noctune.app.model.Track;
import com.noctune.app.network.ApiService;
import com.noctune.app.network.RetrofitClient;
import com.noctune.app.utils.Constants;
import com.noctune.app.utils.NetworkUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private RecyclerView rvArtists, rvTracks;
    private ArtistHorizontalAdapter artistAdapter;
    private TrackAdapter trackAdapter;
    private Button btnRetry;
    private TextView tvArtistsLabel, tvTracksLabel;
    private LinearLayout llGenreChips;
    private ApiService apiService;
    private String selectedGenre = "pop";

    private final String[] genres = {
            "pop", "rock", "hip-hop", "jazz",
            "electronic", "classical", "indie", "metal"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvArtists = view.findViewById(R.id.rv_explore_artists);
        rvTracks = view.findViewById(R.id.rv_explore_tracks);
        btnRetry = view.findViewById(R.id.btn_retry_explore);
        tvArtistsLabel = view.findViewById(R.id.tv_artists_label);
        tvTracksLabel = view.findViewById(R.id.tv_tracks_label);
        llGenreChips = view.findViewById(R.id.ll_genre_chips);

        rvArtists.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL, false));
        artistAdapter = new ArtistHorizontalAdapter(getActivity());
        rvArtists.setAdapter(artistAdapter);

        rvTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        trackAdapter = new TrackAdapter(getActivity());
        rvTracks.setAdapter(trackAdapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        buildGenreChips();

        btnRetry.setOnClickListener(v -> loadExploreData(selectedGenre));

        loadExploreData(selectedGenre);
    }

    private void buildGenreChips() {
        llGenreChips.removeAllViews();
        for (int i = 0; i < genres.length; i++) {
            final String genre = genres[i];

            TextView chip = new TextView(getActivity());
            chip.setText(genre.toUpperCase());
            chip.setTextSize(12f);
            chip.setPadding(32, 16, 32, 16);
            chip.setTypeface(android.graphics.Typeface.MONOSPACE);
            chip.setGravity(android.view.Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 16, 0);
            chip.setLayoutParams(params);

            updateChipStyle(chip, genre.equals(selectedGenre));

            chip.setOnClickListener(v -> {
                selectedGenre = genre;
                refreshChipStyles();
                loadExploreData(genre);
            });

            llGenreChips.addView(chip);
        }
    }

    private void refreshChipStyles() {
        for (int i = 0; i < llGenreChips.getChildCount(); i++) {
            TextView chip = (TextView) llGenreChips.getChildAt(i);
            String genre = genres[i];
            updateChipStyle(chip, genre.equals(selectedGenre));
        }
    }

    private void updateChipStyle(TextView chip, boolean isActive) {
        if (isActive) {
            // Active Style: Kuning Menyala Khas Noctune
            chip.setBackgroundColor(Color.parseColor("#FFD600"));
            chip.setTextColor(Color.BLACK);
            // Tambahkan border kaku jika didukung layout atau biarkan solid brutalist
        } else {
            // Inactive Style: Menyelam ke latar belakang gelap, tidak putih lagi
            chip.setBackgroundColor(Color.parseColor("#1E1E1E")); // Menyesuaikan dengan bg_surface
            chip.setTextColor(Color.parseColor("#757575")); // Abu-abu diredam
        }
    }

    private void loadExploreData(String genre) {
        if (!NetworkUtils.isConnected(getActivity())) {
            btnRetry.setVisibility(View.VISIBLE);
            ToastHelper.showError(getActivity(), "[ERROR: NO INTERNET CONNECTION]");
            return;
        }

        btnRetry.setVisibility(View.GONE);
        tvArtistsLabel.setText("TOP ARTISTS — " + genre.toUpperCase());
        tvTracksLabel.setText("TOP TRACKS — " + genre.toUpperCase());

        loadArtists(genre);
        loadTracks(genre);
    }

    private void loadArtists(String genre) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<TopArtistsResponse> call = apiService.getArtistsByTag(
                    genre, Constants.API_KEY, 10
            );

            call.enqueue(new Callback<TopArtistsResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopArtistsResponse> call,
                                       @NonNull Response<TopArtistsResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getTopartists() != null) {
                            artistAdapter.setArtists(
                                    response.body().getTopartists().getArtist()
                            );
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopArtistsResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        ToastHelper.showError(getActivity(), "[ERROR: FAILED TO LOAD ARTISTS]");
                    });
                }
            });
        });
    }

    private void loadTracks(String genre) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<TopTracksResponse> call = apiService.getTracksByTag(
                    genre, Constants.API_KEY, 15
            );

            call.enqueue(new Callback<TopTracksResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopTracksResponse> call,
                                       @NonNull Response<TopTracksResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getTracks() != null) {

                            java.util.List<Track> trackList = response.body().getTracks().getTrack();

                            // --- PERBAIKAN: GENERATE DATA EMULASI DINAMIS UNTUK GENRE HUB ---
                            if (trackList != null) {
                                for (int i = 0; i < trackList.size(); i++) {
                                    Track t = trackList.get(i);

                                    // Karena API Last.fm Genre tidak memberikan data statistik,
                                    // kita buat simulasi angka brutalist menurun berdasarkan urutan rank (i)
                                    // agar UI tetap terasa hidup dan responsif.
                                    long simulatedListeners = 1_500_000L - (i * 75_000L);
                                    long simulatedPlays = simulatedListeners * 3; // Plays dibuat lebih besar dari pendengar

                                    if (t.getListeners() == null || t.getListeners().isEmpty() || t.getListeners().equals("0")) {
                                        t.setListeners(String.valueOf(simulatedListeners));
                                    }
                                    if (t.getPlaycount() == null || t.getPlaycount().isEmpty() || t.getPlaycount().equals("0")) {
                                        // Ganti ini jika kamu punya setter, atau pastikan setPlaycount sudah ada di Track.java
                                        t.setPlaycount(String.valueOf(simulatedPlays));
                                    }
                                }
                            }

                            trackAdapter.setTracks(trackList);
                        } else {
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopTracksResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        ToastHelper.showError(getActivity(), "[ERROR: FAILED TO LOAD TRACKS]");
                        btnRetry.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
    }
}