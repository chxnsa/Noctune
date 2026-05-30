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
import com.noctune.app.adapter.ArtistAdapter;
import com.noctune.app.model.TopArtistsResponse;
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

    private RecyclerView rvExplore;
    private ArtistAdapter artistAdapter;
    private Button btnRetry;
    private TextView tvLabel;
    private LinearLayout llGenreChips;
    private ApiService apiService;
    private String selectedGenre = "pop";

    // Daftar genre
    private final String[] genres = {
            "pop", "rock", "hip-hop", "jazz",
            "electronic", "classical", "indie", "metal"
    };

    // Warna chip per genre — sesuai desain Noctune
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

        rvExplore = view.findViewById(R.id.rv_explore);
        btnRetry = view.findViewById(R.id.btn_retry_explore);
        tvLabel = view.findViewById(R.id.tv_explore_label);
        llGenreChips = view.findViewById(R.id.ll_genre_chips);

        // Setup RecyclerView
        rvExplore.setLayoutManager(new LinearLayoutManager(getActivity()));
        artistAdapter = new ArtistAdapter(getActivity());
        rvExplore.setAdapter(artistAdapter);

        // Init Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Buat genre chips secara dinamis
        buildGenreChips();

        // Tombol retry
        btnRetry.setOnClickListener(v -> loadArtistsByGenre(selectedGenre));

        // Load default genre
        loadArtistsByGenre(selectedGenre);
    }

    private void buildGenreChips() {
        for (int i = 0; i < genres.length; i++) {
            final String genre = genres[i];
            final String color = chipColors[i];

            // Buat chip sebagai TextView
            TextView chip = new TextView(getActivity());
            chip.setText(genre.toUpperCase());
            chip.setTextColor(Color.WHITE);
            chip.setBackgroundColor(Color.parseColor(color));
            chip.setTextSize(12f);
            chip.setPadding(24, 12, 24, 12);

            // Margin antar chip
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 12, 0);
            chip.setLayoutParams(params);

            // Klik chip → load artis genre tersebut
            chip.setOnClickListener(v -> {
                selectedGenre = genre;
                tvLabel.setText("TOP ARTISTS — " + genre.toUpperCase());
                loadArtistsByGenre(genre);
            });

            llGenreChips.addView(chip);
        }
    }

    private void loadArtistsByGenre(String genre) {
        if (!NetworkUtils.isConnected(getActivity())) {
            btnRetry.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),
                    "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRetry.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<TopArtistsResponse> call = apiService.getArtistsByTag(
                    genre, Constants.API_KEY, 20
            );

            call.enqueue(new Callback<TopArtistsResponse>() {
                @Override
                public void onResponse(@NonNull Call<TopArtistsResponse> call,
                                       @NonNull Response<TopArtistsResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getTopartists() != null) {
                            artistAdapter.setArtists(
                                    response.body().getTopartists().getArtist(),
                                    genre
                            );
                        } else {
                            Toast.makeText(getActivity(),
                                    "Failed to load artists",
                                    Toast.LENGTH_SHORT).show();
                            btnRetry.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<TopArtistsResponse> call,
                                      @NonNull Throwable t) {
                    handler.post(() -> {
                        Toast.makeText(getActivity(),
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        btnRetry.setVisibility(View.VISIBLE);
                    });
                }
            });
        });
    }
}