package com.noctune.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noctune.app.R;
import com.noctune.app.adapter.TrackAdapter;
import com.noctune.app.model.ArtistInfoResponse;
import com.noctune.app.model.TopTracksResponse;
import com.noctune.app.network.ApiService;
import com.noctune.app.network.RetrofitClient;
import com.noctune.app.utils.Constants;
import com.noctune.app.utils.ImageLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistDetailActivity extends AppCompatActivity {

    private ApiService apiService;
    private String artistName;

    private ImageView ivArtist;
    private TextView tvName, tvListeners, tvPlaycount, tvBio;
    private RecyclerView rvTracks;
    private TrackAdapter trackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        artistName = getIntent().getStringExtra("artist_name");

        ivArtist = findViewById(R.id.iv_artist_image);
        tvName = findViewById(R.id.tv_artist_name);
        tvListeners = findViewById(R.id.tv_listeners);
        tvPlaycount = findViewById(R.id.tv_playcount);
        tvBio = findViewById(R.id.tv_bio);
        rvTracks = findViewById(R.id.rv_artist_tracks);

        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());

        rvTracks.setLayoutManager(new LinearLayoutManager(this));
        trackAdapter = new TrackAdapter(this);
        rvTracks.setAdapter(trackAdapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        tvName.setText(artistName != null ? artistName.toUpperCase() : "");

        loadArtistInfo();
        loadArtistTracks();
    }

    private void loadArtistInfo() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<ArtistInfoResponse> call = apiService.getArtistInfo(
                    artistName, Constants.API_KEY);

            call.enqueue(new Callback<ArtistInfoResponse>() {
                @Override
                public void onResponse(Call<ArtistInfoResponse> call,
                                       Response<ArtistInfoResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getArtist() != null) {

                            var artist = response.body().getArtist();

                            // Stats
                            if (artist.getStats() != null) {
                                tvListeners.setText(formatCount(
                                        artist.getStats().getListeners()));
                                tvPlaycount.setText(formatCount(
                                        artist.getStats().getPlaycount()));
                            }

                            // Bio — strip HTML tags
                            if (artist.getBio() != null
                                    && artist.getBio().getSummary() != null) {
                                String bioText = Html.fromHtml(
                                        artist.getBio().getSummary(),
                                        Html.FROM_HTML_MODE_LEGACY
                                ).toString();

                                // Hapus link "Read more on Last.fm" di akhir
                                int cutIndex = bioText.indexOf("Read more");
                                if (cutIndex != -1) {
                                    bioText = bioText.substring(0, cutIndex).trim();
                                }
                                tvBio.setText(bioText);
                            }

                            // Image dengan fallback iTunes
                            ImageLoader.loadArtistImage(
                                    ivArtist, artist.getImageUrl(), artistName);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ArtistInfoResponse> call, Throwable t) {
                    handler.post(() -> {
                        ImageLoader.loadArtistImage(ivArtist, "", artistName);
                    });
                }
            });
        });
    }

    private void loadArtistTracks() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Call<TopTracksResponse> call = apiService.getArtistTopTracks(
                    artistName, Constants.API_KEY, 20);

            call.enqueue(new Callback<TopTracksResponse>() {
                @Override
                public void onResponse(Call<TopTracksResponse> call,
                                       Response<TopTracksResponse> response) {
                    handler.post(() -> {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getTracks() != null) {
                            trackAdapter.setTracks(
                                    response.body().getTracks().getTrack());
                        } else {
                            Toast.makeText(ArtistDetailActivity.this,
                                    "No tracks found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<TopTracksResponse> call, Throwable t) {
                    handler.post(() -> {
                        Toast.makeText(ArtistDetailActivity.this,
                                "Error loading tracks",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
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
}