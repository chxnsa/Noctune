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
import android.widget.Toast;
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

    private final String[] chipColors = {
            "#E63329", "#FFD600", "#000000", "#2D6A4F",
            "#9C27B0", "#1565C0", "#FF6B35", "#424242"
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

        // Setup RecyclerView artists — horizontal
        rvArtists.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL, false));
        artistAdapter = new ArtistHorizontalAdapter(getActivity());
        rvArtists.setAdapter(artistAdapter);

        // Setup RecyclerView tracks — vertical
        rvTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        trackAdapter = new TrackAdapter(getActivity());
        rvTracks.setAdapter(trackAdapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        buildGenreChips();

        btnRetry.setOnClickListener(v -> loadExploreData(selectedGenre));

        loadExploreData(selectedGenre);
    }

    private void buildGenreChips() {
        for (int i = 0; i < genres.length; i++) {
            final String genre = genres[i];
            final String color = chipColors[i];

            TextView chip = new TextView(getActivity());
            chip.setText(genre.toUpperCase());
            chip.setTextColor(Color.WHITE);
            chip.setBackgroundColor(Color.parseColor(color));
            chip.setTextSize(12f);
            chip.setPadding(24, 12, 24, 12);
            chip.setTypeface(android.graphics.Typeface.MONOSPACE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 12, 0);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                selectedGenre = genre;
                loadExploreData(genre);
            });

            llGenreChips.addView(chip);
        }
    }

    private void loadExploreData(String genre) {
        if (!NetworkUtils.isConnected(getActivity())) {
            btnRetry.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),
                    "No internet connection", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(),
                                "Error loading artists",
                                Toast.LENGTH_SHORT).show();
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
                            trackAdapter.setTracks(
                                    response.body().getTracks().getTrack()
                            );
                        } else {
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopTracksResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        Toast.makeText(getActivity(),
                                "Error loading tracks",
                                Toast.LENGTH_SHORT).show();
                        btnRetry.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
    }
}